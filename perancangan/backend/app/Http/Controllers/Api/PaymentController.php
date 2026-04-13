<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Order;
use App\Models\PaymentLog;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;

class PaymentController extends Controller
{
    public function verify(Request $request, Order $order): JsonResponse
    {
        $data = $request->validate([
            'amount_paid' => ['nullable', 'integer', 'min:0'],
            'notes' => ['nullable', 'string'],
        ]);

        if ($order->payment_status === 'lunas') {
            return response()->json(['message' => 'Order sudah lunas'], 422);
        }

        $amountPaid = $data['amount_paid'] ?? $order->grand_total;

        PaymentLog::create([
            'order_id' => $order->id,
            'previous_status' => $order->payment_status,
            'new_status' => 'lunas',
            'amount_paid' => $amountPaid,
            'verified_by' => $request->user()->id,
            'verified_at' => now(),
            'notes' => $data['notes'] ?? null,
        ]);

        $order->update([
            'payment_status' => 'lunas',
        ]);

        return response()->json([
            'message' => 'Pembayaran berhasil diverifikasi',
            'order' => $order->fresh(),
        ]);
    }
}
