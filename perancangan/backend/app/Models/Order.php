<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Order extends Model
{
    protected $fillable = [
        'invoice_number',
        'customer_id',
        'created_by',
        'service_type_id',
        'laundry_weight_kg',
        'price_per_kg_snapshot',
        'subtotal_kg',
        'subtotal_special',
        'grand_total',
        'payment_status',
        'current_status',
        'completed_at',
        'picked_up_at',
        'notes',
    ];

    protected function casts(): array
    {
        return [
            'laundry_weight_kg' => 'decimal:2',
            'completed_at' => 'datetime',
            'picked_up_at' => 'datetime',
        ];
    }

    public function customer()
    {
        return $this->belongsTo(Customer::class);
    }

    public function createdBy()
    {
        return $this->belongsTo(User::class, 'created_by');
    }

    public function serviceType()
    {
        return $this->belongsTo(ServiceType::class);
    }

    public function specialItems()
    {
        return $this->hasMany(OrderSpecialItem::class);
    }

    public function statusLogs()
    {
        return $this->hasMany(OrderStatusLog::class);
    }

    public function paymentLogs()
    {
        return $this->hasMany(PaymentLog::class);
    }
}
