<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class ServiceType extends Model
{
    protected $fillable = [
        'code',
        'name',
        'workflow',
        'is_active',
    ];

    protected function casts(): array
    {
        return [
            'workflow' => 'array',
            'is_active' => 'boolean',
        ];
    }

    public function prices()
    {
        return $this->hasMany(ServicePrice::class);
    }
}
