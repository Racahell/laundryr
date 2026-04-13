<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class SpecialItemCategory extends Model
{
    protected $fillable = [
        'name',
        'unit',
        'price_per_item',
        'is_active',
    ];

    protected function casts(): array
    {
        return [
            'is_active' => 'boolean',
        ];
    }
}
