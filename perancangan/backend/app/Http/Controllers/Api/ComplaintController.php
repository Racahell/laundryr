<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Complaint;
use App\Models\Order;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Storage;

class ComplaintController extends Controller
{
    public function index(Request $request): JsonResponse
    {
        $user = $request->user();

        $query = Complaint::query()->with(['order', 'customer']);

        if ($user->role === 'customer') {
            $customerId = $user->customerProfile?->id;
            $query->where('customer_id', $customerId);
        }

        return response()->json($query->latest()->get());
    }

    public function store(Request $request): JsonResponse
    {
        $user = $request->user();

        if ($user->role !== 'customer') {
            return response()->json(['message' => 'Hanya pelanggan yang dapat membuat komplain'], 403);
        }

        $customer = $user->customerProfile;
        if (!$customer) {
            return response()->json(['message' => 'Profil customer tidak ditemukan'], 422);
        }

        $data = $request->validate([
            'invoice_number' => ['required', 'string'],
            'item_name' => ['required', 'string', 'max:255'],
            'damage_type' => ['required', 'string', 'max:255'],
            'description' => ['nullable', 'string'],
            'photo' => ['nullable', 'image', 'max:3072'],
        ]);

        $order = Order::query()
            ->where('invoice_number', $data['invoice_number'])
            ->where('customer_id', $customer->id)
            ->first();

        if (!$order) {
            return response()->json(['message' => 'Invoice tidak ditemukan atau bukan milik Anda'], 404);
        }

        $photoUrl = null;
        if ($request->hasFile('photo')) {
            $path = $request->file('photo')->store('complaints', 'public');
            $photoUrl = Storage::disk('public')->url($path);
        }

        $complaint = Complaint::create([
            'order_id' => $order->id,
            'customer_id' => $customer->id,
            'item_name' => $data['item_name'],
            'damage_type' => $data['damage_type'],
            'description' => $data['description'] ?? null,
            'photo_url' => $photoUrl,
            'status' => 'submitted',
        ]);

        return response()->json($complaint, 201);
    }
}
