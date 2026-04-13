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
        Schema::create('payment_logs', function (Blueprint $table) {
            $table->id();
            $table->foreignId('order_id')->constrained()->cascadeOnDelete();
            $table->enum('previous_status', ['belum_lunas', 'lunas']);
            $table->enum('new_status', ['belum_lunas', 'lunas']);
            $table->unsignedInteger('amount_paid');
            $table->foreignId('verified_by')->constrained('users')->cascadeOnDelete();
            $table->timestamp('verified_at');
            $table->text('notes')->nullable();
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('payment_logs');
    }
};
