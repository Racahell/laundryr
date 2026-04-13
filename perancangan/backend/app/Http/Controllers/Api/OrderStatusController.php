<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Order;
use App\Models\OrderStatusLog;
use App\Support\OrderWorkflow;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;

class OrderStatusController extends Controller
{
    public function transition(Request $request, Order $order): JsonResponse
    {
        $data = $request->validate([
            'next_status' => ['required', 'string'],
        ]);

        $serviceCode = $order->serviceType->code;
        $currentStatus = $order->current_status;
        $nextStatus = strtoupper($data['next_status']);

        if (!OrderWorkflow::canTransition($serviceCode, $currentStatus, $nextStatus)) {
            return response()->json([
                'message' => 'Transisi status tidak valid',
                'current_status' => $currentStatus,
                'allowed_flow' => OrderWorkflow::flow($serviceCode),
            ], 422);
        }

        $updates = ['current_status' => $nextStatus];
        if ($nextStatus === 'SELESAI') {
            $updates['completed_at'] = now();
        }
        if ($nextStatus === 'DIAMBIL') {
            $updates['picked_up_at'] = now();
        }

        $order->update($updates);

        OrderStatusLog::create([
            'order_id' => $order->id,
            'previous_status' => $currentStatus,
            'new_status' => $nextStatus,
            'changed_by' => $request->user()->id,
            'changed_at' => now(),
        ]);

        return response()->json([
            'message' => 'Status berhasil diperbarui',
            'order' => $order->fresh(['statusLogs']),
        ]);
    }
}
