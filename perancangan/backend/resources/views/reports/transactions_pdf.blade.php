<!doctype html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <title>Laporan Transaksi</title>
    <style>
        body { font-family: DejaVu Sans, sans-serif; font-size: 12px; }
        table { width: 100%; border-collapse: collapse; }
        th, td { border: 1px solid #ddd; padding: 6px; }
        th { background-color: #f3f4f6; }
    </style>
</head>
<body>
<h2>Laporan Transaksi Laundry</h2>
<p>Tanggal cetak: {{ now()->format('d-m-Y H:i') }}</p>
<table>
    <thead>
    <tr>
        <th>Invoice</th>
        <th>Layanan</th>
        <th>Pelanggan</th>
        <th>Status</th>
        <th>Pembayaran</th>
        <th>Total</th>
    </tr>
    </thead>
    <tbody>
    @foreach($orders as $order)
        <tr>
            <td>{{ $order->invoice_number }}</td>
            <td>{{ $order->serviceType?->name }}</td>
            <td>{{ $order->customer?->name }}</td>
            <td>{{ $order->current_status }}</td>
            <td>{{ $order->payment_status }}</td>
            <td>Rp{{ number_format($order->grand_total, 0, ',', '.') }}</td>
        </tr>
    @endforeach
    </tbody>
</table>
</body>
</html>
