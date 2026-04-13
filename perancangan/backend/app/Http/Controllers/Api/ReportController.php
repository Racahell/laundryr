<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Order;
use Barryvdh\DomPDF\Facade\Pdf;
use Illuminate\Database\Eloquent\Builder;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;
use Symfony\Component\HttpFoundation\StreamedResponse;

class ReportController extends Controller
{
    public function transactions(Request $request): JsonResponse
    {
        $orders = $this->filteredOrders($request)->get();

        return response()->json([
            'summary' => [
                'total_orders' => $orders->count(),
                'total_revenue' => (int) $orders->sum('grand_total'),
                'total_unpaid' => (int) $orders->where('payment_status', 'belum_lunas')->sum('grand_total'),
            ],
            'data' => $orders,
        ]);
    }

    public function exportExcel(Request $request): StreamedResponse
    {
        $orders = $this->filteredOrders($request)->get();

        $headers = [
            'Content-Type' => 'text/csv',
            'Content-Disposition' => 'attachment; filename="laporan-transaksi.csv"',
        ];

        $columns = ['invoice_number', 'service', 'customer', 'status', 'payment_status', 'grand_total', 'created_at'];

        return response()->stream(function () use ($orders, $columns) {
            $file = fopen('php://output', 'w');
            fputcsv($file, $columns);

            foreach ($orders as $order) {
                fputcsv($file, [
                    $order->invoice_number,
                    $order->serviceType?->name,
                    $order->customer?->name,
                    $order->current_status,
                    $order->payment_status,
                    $order->grand_total,
                    $order->created_at,
                ]);
            }

            fclose($file);
        }, 200, $headers);
    }

    public function exportPdf(Request $request)
    {
        $orders = $this->filteredOrders($request)->get();

        $html = view('reports.transactions_pdf', ['orders' => $orders])->render();
        $pdf = Pdf::loadHTML($html)->setPaper('a4', 'portrait');

        return $pdf->download('laporan-transaksi.pdf');
    }

    private function filteredOrders(Request $request): Builder
    {
        return Order::query()
            ->with(['customer', 'serviceType'])
            ->when($request->filled('date_from'), fn (Builder $q) => $q->whereDate('created_at', '>=', $request->string('date_from')))
            ->when($request->filled('date_to'), fn (Builder $q) => $q->whereDate('created_at', '<=', $request->string('date_to')))
            ->when($request->filled('service_type_id'), fn (Builder $q) => $q->where('service_type_id', $request->integer('service_type_id')))
            ->when($request->filled('payment_status'), fn (Builder $q) => $q->where('payment_status', $request->string('payment_status')))
            ->when($request->filled('current_status'), fn (Builder $q) => $q->where('current_status', $request->string('current_status')))
            ->latest();
    }
}
