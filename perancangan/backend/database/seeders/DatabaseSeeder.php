<?php

namespace Database\Seeders;

use App\Models\Customer;
use App\Models\ServicePrice;
use App\Models\ServiceType;
use App\Models\SpecialItemCategory;
use App\Models\User;
use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\Hash;

class DatabaseSeeder extends Seeder
{
    /**
     * Seed the application's database.
     */
    public function run(): void
    {
        User::query()->firstOrCreate(
            ['email' => 'admin@laundryr.local'],
            [
                'name' => 'Admin Laundryr',
                'phone' => '081111111111',
                'role' => 'admin',
                'password' => Hash::make('password123'),
            ]
        );

        User::query()->firstOrCreate(
            ['email' => 'petugas@laundryr.local'],
            [
                'name' => 'Petugas Laundryr',
                'phone' => '082222222222',
                'role' => 'staff',
                'password' => Hash::make('password123'),
            ]
        );

        $customerUser = User::query()->firstOrCreate(
            ['email' => 'pelanggan@laundryr.local'],
            [
                'name' => 'Pelanggan Demo',
                'phone' => '083333333333',
                'role' => 'customer',
                'password' => Hash::make('password123'),
            ]
        );

        Customer::query()->firstOrCreate(
            ['user_id' => $customerUser->id],
            [
                'name' => $customerUser->name,
                'phone' => $customerUser->phone ?? '083333333333',
                'address' => 'Alamat pelanggan demo',
            ]
        );

        $full = ServiceType::query()->firstOrCreate(
            ['code' => 'FULL_LAUNDRY'],
            [
                'name' => 'Full Laundry',
                'workflow' => ['DITERIMA', 'DICUCI', 'DIJEMUR', 'DISETRIKA', 'SELESAI', 'DIAMBIL'],
                'is_active' => true,
            ]
        );

        $iron = ServiceType::query()->firstOrCreate(
            ['code' => 'SETRIKA_SAJA'],
            [
                'name' => 'Setrika Saja',
                'workflow' => ['DITERIMA', 'DISETRIKA', 'SELESAI', 'DIAMBIL'],
                'is_active' => true,
            ]
        );

        ServicePrice::query()->firstOrCreate(
            ['service_type_id' => $full->id, 'effective_from' => now()->toDateString()],
            ['price_per_kg' => 7000]
        );

        ServicePrice::query()->firstOrCreate(
            ['service_type_id' => $iron->id, 'effective_from' => now()->toDateString()],
            ['price_per_kg' => 6000]
        );

        $categories = [
            ['name' => 'Selimut', 'unit' => 'buah', 'price_per_item' => 18000],
            ['name' => 'Bed cover', 'unit' => 'buah', 'price_per_item' => 25000],
            ['name' => 'Sprei', 'unit' => 'lembar', 'price_per_item' => 15000],
            ['name' => 'Gorden', 'unit' => 'lembar', 'price_per_item' => 20000],
            ['name' => 'Sepatu', 'unit' => 'pasang', 'price_per_item' => 30000],
            ['name' => 'Tas', 'unit' => 'buah', 'price_per_item' => 22000],
            ['name' => 'Boneka', 'unit' => 'buah', 'price_per_item' => 16000],
            ['name' => 'Jaket Tebal', 'unit' => 'buah', 'price_per_item' => 17000],
            ['name' => 'Karpet Kecil', 'unit' => 'buah', 'price_per_item' => 35000],
        ];

        foreach ($categories as $category) {
            SpecialItemCategory::query()->firstOrCreate(['name' => $category['name']], $category + ['is_active' => true]);
        }
    }
}
