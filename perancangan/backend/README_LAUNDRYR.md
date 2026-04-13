# Laundryr Backend (Laravel)

Lokasi: `perancangan/backend`

## Database
- Nama database aktif: `laundryr` (SQLite file `database/laundryr.sqlite`)
- Konfigurasi: `.env` menggunakan `DB_CONNECTION=sqlite` dan `DB_DATABASE=database/laundryr.sqlite`

## Menjalankan Backend
```bash
cd perancangan/backend
php artisan serve
```

## Seed Account
- Admin: `admin@laundryr.local` / `password123`
- Petugas: `petugas@laundryr.local` / `password123`
- Pelanggan: `pelanggan@laundryr.local` / `password123`

## Endpoint Utama
- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/logout`
- `GET /api/v1/me`
- `GET /api/v1/orders`
- `POST /api/v1/orders`
- `POST /api/v1/orders/{id}/status-transition`
- `POST /api/v1/orders/{id}/payment-verify`
- `GET /api/v1/customer/invoices/{invoiceNumber}`
- `GET/POST/PUT/DELETE /api/v1/special-item-categories`
- `GET/POST /api/v1/complaints`
- `GET /api/v1/reports/transactions`
- `GET /api/v1/reports/transactions/export-excel`
- `GET /api/v1/reports/transactions/export-pdf`

## Catatan Android
App Android mengakses backend via `http://10.0.2.2:8000/` (emulator) dan cleartext HTTP diaktifkan.
