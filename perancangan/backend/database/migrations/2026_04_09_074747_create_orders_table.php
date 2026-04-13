<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::create('orders', function (Blueprint $table) {
            $table->id();
            $table->string('invoice_number')->unique();
            $table->foreignId('customer_id')->constrained()->cascadeOnDelete();
            $table->foreignId('created_by')->constrained('users')->cascadeOnDelete();
            $table->foreignId('service_type_id')->constrained()->cascadeOnDelete();
            $table->decimal('laundry_weight_kg', 8, 2)->default(0);
            $table->unsignedInteger('price_per_kg_snapshot')->default(0);
            $table->unsignedInteger('subtotal_kg')->default(0);
            $table->unsignedInteger('subtotal_special')->default(0);
            $table->unsignedInteger('grand_total')->default(0);
            $table->enum('payment_status', ['belum_lunas', 'lunas'])->default('belum_lunas');
            $table->string('current_status')->default('DITERIMA');
            $table->timestamp('completed_at')->nullable();
            $table->timestamp('picked_up_at')->nullable();
            $table->text('notes')->nullable();
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('orders');
    }
};
