<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\SpecialItemCategory;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;

class SpecialItemCategoryController extends Controller
{
    public function index(): JsonResponse
    {
        return response()->json(
            SpecialItemCategory::query()->orderBy('name')->get()
        );
    }

    public function store(Request $request): JsonResponse
    {
        $data = $request->validate([
            'name' => ['required', 'string', 'max:255', 'unique:special_item_categories,name'],
            'unit' => ['required', 'string', 'max:40'],
            'price_per_item' => ['required', 'integer', 'min:0'],
            'is_active' => ['nullable', 'boolean'],
        ]);

        $item = SpecialItemCategory::create([
            ...$data,
            'is_active' => $data['is_active'] ?? true,
        ]);

        return response()->json($item, 201);
    }

    public function show(SpecialItemCategory $specialItemCategory): JsonResponse
    {
        return response()->json($specialItemCategory);
    }

    public function update(Request $request, SpecialItemCategory $specialItemCategory): JsonResponse
    {
        $data = $request->validate([
            'name' => ['sometimes', 'string', 'max:255', 'unique:special_item_categories,name,'.$specialItemCategory->id],
            'unit' => ['sometimes', 'string', 'max:40'],
            'price_per_item' => ['sometimes', 'integer', 'min:0'],
            'is_active' => ['sometimes', 'boolean'],
        ]);

        $specialItemCategory->update($data);

        return response()->json($specialItemCategory);
    }

    public function destroy(SpecialItemCategory $specialItemCategory): JsonResponse
    {
        $specialItemCategory->delete();

        return response()->json(status: 204);
    }
}
