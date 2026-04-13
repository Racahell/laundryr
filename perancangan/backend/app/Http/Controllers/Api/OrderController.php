<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Customer;
use App\Models\Order;
use App\Models\OrderSpecialItem;
use App\Models\OrderStatusLog;
use App\Models\ServicePrice;
use App\Models\ServiceType;
use App\Models\SpecialItemCategory;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class OrderController extends Controller
{
    public function index(Request $request): JsonResponse
    {
        $user = $request->user();

        $query = Order::query()->with(['customer', 'serviceType', 'specialItems']);

        if ($user->role === 'customer') {
            $customerId = $user->customerProfile?->id;
            $query->where('customer_id', $customerId);
        }

        return response()->json($query->latest()->get()->map(fn (Order $order) => $this->transformOrder($order)));
    }

    public function show(Request $request, Order $order): JsonResponse
    {
        $this->ensureOrderReadable($request, $order);

        $order->load(['customer', 'serviceType', 'specialItems', 'statusLogs', 'paymentLogs']);

        return response()->json($this->transformOrder($order));
    }

    public function store(Request $request): JsonResponse
    {
        $data = $request->validate([
            'service_type_id' => ['required', 'exists:service_types,id'],
            'laundry_weight_kg' => ['nullable', 'numeric', 'min:0'],
            'notes' => ['nullable', 'string'],
            'customer_id' => ['nullable', 'exists:customers,id'],
            'customer.name' => ['required_without:customer_id', 'string', 'max:255'],
            'customer.phone' => ['required_without:customer_id', 'string', 'max:25'],
            'customer.address' => ['nullable', 'string'],
            'special_items' => ['nullable', 'array'],
            'special_items.*.special_item_category_id' => ['required', 'exists:special_item_categories,id'],
            'special_items.*.quantity' => ['required', 'integer', 'min:1'],
        ]);

        $serviceType = ServiceType::query()->findOrFail($data['service_type_id']);

        $price = ServicePrice::query()
            ->where('service_type_id', $serviceType->id)
            ->where('effective_from', '<=', now()->toDateString())
            ->orderByDesc('effective_from')
            ->first();

        $pricePerKg = $price?->price_per_kg ?? ($serviceType->code === 'SETRIKA_SAJA' ? 6000 : 7000);
        $weight = (float) ($data['laundry_weight_kg'] ?? 0);
        $subtotalKg = (int) round($weight * $pricePerKg);

        return DB::transaction(function () use ($data, $request, $serviceType, $pricePerKg, $weight, $subtotalKg) {
            $customer = isset($data['customer_id'])
                ? Customer::query()->findOrFail($data['customer_id'])
                : Customer::create([
                    'name' => $data['customer']['name'],
                    'phone' => $data['customer']['phone'],
                    'address' => $data['customer']['address'] ?? null,
                ]);

            $order = Order::create([
                'invoice_number' => $this->generateInvoiceNumber(),
                'customer_id' => $customer->id,
                'created_by' => $request->user()->id,
                'service_type_id' => $serviceType->id,
                'laundry_weight_kg' => $weight,
                'price_per_kg_snapshot' => $pricePerKg,
                'subtotal_kg' => $subtotalKg,
                'subtotal_special' => 0,
                'grand_total' => $subtotalKg,
                'payment_status' => 'belum_lunas',
                'current_status' => 'DITERIMA',
                'notes' => $data['notes'] ?? null,
            ]);

            $specialSubtotal = 0;

            foreach (($data['special_items'] ?? []) as $item) {
                $category = SpecialItemCategory::query()->findOrFail($item['special_item_category_id']);
                $subtotal = $category->price_per_item * (int) $item['quantity'];
                $specialSubtotal += $subtotal;

                OrderSpecialItem::create([
                    'order_id' => $order->id,
                    'special_item_category_id' => $category->id,
                    'category_name_snapshot' => $category->name,
                    'unit_snapshot' => $category->unit,
                    'price_per_item_snapshot' => $category->price_per_item,
                    'quantity' => (int) $item['quantity'],
                    'subtotal' => $subtotal,
                ]);
            }

            $order->update([
                'subtotal_special' => $specialSubtotal,
                'grand_total' => $subtotalKg + $specialSubtotal,
            ]);

            OrderStatusLog::create([
                'order_id' => $order->id,
                'previous_status' => null,
                'new_status' => 'DITERIMA',
                'changed_by' => $request->user()->id,
                'changed_at' => now(),
            ]);

            $order->load(['customer', 'serviceType', 'specialItems']);

            return response()->json($this->transformOrder($order), 201);
        });
    }

    public function customerInvoice(Request $request, string $invoiceNumber): JsonResponse
    {
        $user = $request->user();

        if ($user->role !== 'customer') {
            return response()->json(['message' => 'Endpoint ini khusus pelanggan'], 403);
        }

        $customerId = $user->customerProfile?->id;
        if (!$customerId) {
            return response()->json(['message' => 'Profil customer tidak ditemukan'], 422);
        }

        $order = Order::query()
            ->with(['customer', 'serviceType', 'specialItems', 'statusLogs'])
            ->where('invoice_number', $invoiceNumber)
            ->where('customer_id', $customerId)
            ->first();

        if (!$order) {
            return response()->json(['message' => 'Invoice tidak ditemukan'], 404);
        }

        return response()->json($this->transformOrder($order));
    }

    private function ensureOrderReadable(Request $request, Order $order): void
    {
        $user = $request->user();
        if ($user->role !== 'customer') {
            return;
        }

        $customerId = $user->customerProfile?->id;
        abort_if($customerId !== $order->customer_id, 403, 'Anda tidak memiliki akses ke order ini');
    }

    private function generateInvoiceNumber(): string
    {
        $prefix = 'INV-'.now()->format('Ymd');
        $countToday = Order::query()->whereDate('created_at', now()->toDateString())->count() + 1;

        return sprintf('%s-%04d', $prefix, $countToday);
    }

    private function transformOrder(Order $order): array
    {
        return [
            'id' => $order->id,
            'invoice_number' => $order->invoice_number,
            'customer' => $order->customer,
            'service_type' => $order->serviceType,
            'laundry_weight_kg' => $order->laundry_weight_kg,
            'subtotal_kg' => $order->subtotal_kg,
            'subtotal_special' => $order->subtotal_special,
            'grand_total' => $order->grand_total,
            'payment_status' => $order->payment_status,
            'current_status' => $order->current_status,
            'completed_at' => $order->completed_at,
            'picked_up_at' => $order->picked_up_at,
            'overdue_unclaimed' => $order->completed_at && !$order->picked_up_at
                ? $order->completed_at->diffInDays(now()) > 30
                : false,
            'special_items' => $order->specialItems,
            'notes' => $order->notes,
            'created_at' => $order->created_at,
            'updated_at' => $order->updated_at,
        ];
    }
}
