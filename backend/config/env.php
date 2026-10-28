<?php

// Membaca file .env menjadi array sederhana.
// Dipakai supaya username dan password MySQL tidak ditulis langsung di dalam kode.
function bacaEnv(string $lokasiFile): array
{
    $hasil = [];

    if (!is_file($lokasiFile)) {
        return $hasil;
    }

    foreach (file($lokasiFile, FILE_IGNORE_NEW_LINES | FILE_SKIP_EMPTY_LINES) as $baris) {
        $baris = trim($baris);

        // Lewati baris komentar dan baris yang bukan pasangan KUNCI=NILAI.
        if ($baris === '' || str_starts_with($baris, '#') || !str_contains($baris, '=')) {
            continue;
        }

        [$kunci, $nilai] = explode('=', $baris, 2);
        $hasil[trim($kunci)] = trim($nilai);
    }

    return $hasil;
}

function ambilEnv(string $kunci, string $bawaan = ''): string
{
    static $env = null;

    if ($env === null) {
        $env = bacaEnv(__DIR__ . '/../.env');

        // Kalau .env belum dibuat, pakai .env.example supaya backend tetap bisa dicoba.
        if ($env === []) {
            $env = bacaEnv(__DIR__ . '/../.env.example');
        }
    }

    return $env[$kunci] ?? $bawaan;
}
