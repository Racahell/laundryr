<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class OrderSpecialItem extends Model
{
    protected $fillable = [
        'order_id',
        'special_item_category_id',
        'category_name_snapshot',
        'unit_snapshot',
        'price_per_item_snapshot',
        'quantity',
        'subtotal',
    ];

    public function order()
    {
        return $this->belongsTo(Order::class);
    }

    public function category()
    {
        return $this->belongsTo(SpecialItemCategory::class, 'special_item_category_id');
    }
}
