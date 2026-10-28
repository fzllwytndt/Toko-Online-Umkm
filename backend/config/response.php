<?php

// Semua endpoint memakai bentuk respons yang sama:
// { "status": true/false, "message": "...", "data": ... }
function kirimJson(int $kodeHttp, bool $status, string $pesan, mixed $data = null): void
{
    http_response_code($kodeHttp);
    header('Content-Type: application/json; charset=utf-8');

    $isi = ['status' => $status, 'message' => $pesan];

    // Kunci "data" hanya dikirim kalau memang ada isinya.
    if ($data !== null) {
        $isi['data'] = $data;
    }

    echo json_encode($isi, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
    exit;
}

function kirimBerhasil(string $pesan, mixed $data = null, int $kodeHttp = 200): void
{
    kirimJson($kodeHttp, true, $pesan, $data);
}

function kirimGagal(string $pesan, int $kodeHttp = 400): void
{
    kirimJson($kodeHttp, false, $pesan);
}

// Mengambil isi request baik yang dikirim sebagai JSON maupun form biasa.
function ambilInput(): array
{
    if (!empty($_POST)) {
        return $_POST;
    }

    $mentah = file_get_contents('php://input');

    if ($mentah === false || trim($mentah) === '') {
        return [];
    }

    $json = json_decode($mentah, true);

    if (is_array($json)) {
        return $json;
    }

    // PUT dan DELETE dengan body x-www-form-urlencoded tidak otomatis masuk ke $_POST.
    parse_str($mentah, $form);

    return is_array($form) ? $form : [];
}

function ambilTeks(array $input, string $kunci): string
{
    return isset($input[$kunci]) ? trim((string) $input[$kunci]) : '';
}
