<?php

use App\Http\Controllers\Api\AuthController;
use App\Http\Controllers\Api\ComplaintController;
use App\Http\Controllers\Api\OrderController;
use App\Http\Controllers\Api\OrderStatusController;
use App\Http\Controllers\Api\PaymentController;
use App\Http\Controllers\Api\ProfileController;
use App\Http\Controllers\Api\ReportController;
use App\Http\Controllers\Api\SpecialItemCategoryController;
use Illuminate\Support\Facades\Route;

Route::prefix('v1')->group(function () {
    Route::post('/auth/register', [AuthController::class, 'register']);
    Route::post('/auth/login', [AuthController::class, 'login']);

    Route::middleware('auth:sanctum')->group(function () {
        Route::post('/auth/logout', [AuthController::class, 'logout']);
        Route::get('/me', [ProfileController::class, 'me']);

        Route::get('/orders', [OrderController::class, 'index']);
        Route::get('/orders/{order}', [OrderController::class, 'show']);
        Route::get('/customer/invoices/{invoiceNumber}', [OrderController::class, 'customerInvoice'])->middleware('role:customer');

        Route::get('/complaints', [ComplaintController::class, 'index']);
        Route::post('/complaints', [ComplaintController::class, 'store'])->middleware('role:customer');

        Route::middleware('role:admin,staff')->group(function () {
            Route::post('/orders', [OrderController::class, 'store']);
            Route::post('/orders/{order}/status-transition', [OrderStatusController::class, 'transition']);
            Route::post('/orders/{order}/payment-verify', [PaymentController::class, 'verify']);

            Route::get('/special-item-categories', [SpecialItemCategoryController::class, 'index']);
            Route::get('/special-item-categories/{specialItemCategory}', [SpecialItemCategoryController::class, 'show']);
        });

        Route::middleware('role:admin')->group(function () {
            Route::post('/special-item-categories', [SpecialItemCategoryController::class, 'store']);
            Route::put('/special-item-categories/{specialItemCategory}', [SpecialItemCategoryController::class, 'update']);
            Route::delete('/special-item-categories/{specialItemCategory}', [SpecialItemCategoryController::class, 'destroy']);

            Route::get('/reports/transactions', [ReportController::class, 'transactions']);
            Route::get('/reports/transactions/export-excel', [ReportController::class, 'exportExcel']);
            Route::get('/reports/transactions/export-pdf', [ReportController::class, 'exportPdf']);
        });
    });
});
