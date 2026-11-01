<?php

// Pintu masuk tunggal backend REST API Toko Online UMKM Semarang.
// Semua request masuk ke file ini, lalu diteruskan ke routes/api.php.

// Ketika dijalankan memakai server bawaan PHP sebagai router
// (php -S 0.0.0.0:3000 -t backend/public backend/public/index.php),
// seluruh request ikut masuk ke file ini, termasuk permintaan berkas statis
// seperti /uploads/produk/miso-ramen.jpg. Tanpa pemeriksaan di bawah, berkas
// tersebut akan ditolak sebagai "endpoint tidak ditemukan" karena alamatnya
// tidak diawali /api, sehingga foto menu dan foto profil tidak pernah tampil.
//
// Mengembalikan false memberitahu server bawaan PHP untuk melayani sendiri
// berkas yang memang ada di folder public. Di luar server bawaan (misalnya
// Apache), bagian ini dilewati karena berkas statis sudah dilayani web server.
if (PHP_SAPI === 'cli-server') {
    $jalur = parse_url($_SERVER['REQUEST_URI'] ?? '/', PHP_URL_PATH) ?? '/';
    $berkas = realpath(__DIR__ . $jalur);

    // realpath dipakai supaya alamat seperti /../config/.env tidak bisa
    // menembus keluar dari folder public. Pemisah folder ikut disertakan pada
    // pembanding, supaya folder tetangga yang namanya berawalan sama (misalnya
    // public_lama) tidak ikut lolos lewat alamat seperti /../public_lama/rahasia.txt.
    $folderPublic = realpath(__DIR__) . DIRECTORY_SEPARATOR;

    if ($berkas !== false && is_file($berkas) && str_starts_with($berkas, $folderPublic)) {
        return false;
    }
}

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
