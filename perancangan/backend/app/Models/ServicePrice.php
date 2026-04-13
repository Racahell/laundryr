<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class ServicePrice extends Model
{
    protected $fillable = [
        'service_type_id',
        'price_per_kg',
        'effective_from',
    ];

    protected function casts(): array
    {
        return [
            'effective_from' => 'date',
        ];
    }

    public function serviceType()
    {
        return $this->belongsTo(ServiceType::class);
    }
}
