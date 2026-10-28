<?php

// Pintu masuk tunggal backend REST API Toko Online UMKM Semarang.
// Semua request masuk ke file ini, lalu diteruskan ke routes/api.php.

require_once __DIR__ . '/../config/response.php';
require_once __DIR__ . '/../routes/api.php';

header('Content-Type: application/json; charset=utf-8');

// Pesan error PHP tidak ditampilkan ke pengguna supaya informasi internal server tidak bocor.
ini_set('display_errors', '0');
error_reporting(E_ALL);

$method = $_SERVER['REQUEST_METHOD'] ?? 'GET';

// Mengambil bagian alamat saja, tanpa query string. Contoh: /api/products/3
$alamat = parse_url($_SERVER['REQUEST_URI'] ?? '/', PHP_URL_PATH) ?? '/';

$segmen = array_values(array_filter(explode('/', $alamat), fn ($bagian) => $bagian !== ''));

// Seluruh endpoint diawali /api. Kalau tidak, berarti alamatnya salah.
if (($segmen[0] ?? '') !== 'api') {
    kirimGagal('Endpoint tidak ditemukan. Seluruh alamat API diawali dengan /api.', 404);
}

array_shift($segmen);

try {
    jalankanRute($method, $segmen);
} catch (PDOException $e) {
    // Gagal terhubung ke MySQL atau query bermasalah.
    error_log('Database error: ' . $e->getMessage());
    kirimGagal('Server tidak dapat terhubung ke database. Pastikan MySQL sudah berjalan.', 503);
} catch (Throwable $e) {
    error_log('Server error: ' . $e->getMessage());
    kirimGagal('Terjadi kesalahan pada server.', 500);
}
