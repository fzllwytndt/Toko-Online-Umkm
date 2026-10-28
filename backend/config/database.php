<?php

require_once __DIR__ . '/env.php';

// Membuat koneksi ke MySQL memakai PDO.
// PDO dipilih karena mendukung prepared statement, sehingga aman dari SQL injection.
function koneksiDatabase(): PDO
{
    static $koneksi = null;

    if ($koneksi instanceof PDO) {
        return $koneksi;
    }

    $host = ambilEnv('DB_HOST', 'localhost');
    $port = ambilEnv('DB_PORT', '3306');
    $nama = ambilEnv('DB_NAME', 'db_toko_online');
    $user = ambilEnv('DB_USER', 'root');
    $sandi = ambilEnv('DB_PASSWORD', '');

    $dsn = "mysql:host={$host};port={$port};dbname={$nama};charset=utf8mb4";

    $koneksi = new PDO($dsn, $user, $sandi, [
        PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
        PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
        PDO::ATTR_EMULATE_PREPARES => false,
    ]);

    return $koneksi;
}
