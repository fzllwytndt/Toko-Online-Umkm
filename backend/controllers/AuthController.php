<?php

require_once __DIR__ . '/../config/response.php';
require_once __DIR__ . '/../models/User.php';

// Menangani proses masuk dan pendaftaran akun.
class AuthController
{
    private User $user;

    public function __construct()
    {
        $this->user = new User();
    }

    // POST /api/auth/login
    public function login(): void
    {
        $input = ambilInput();
        $username = ambilTeks($input, 'username');
        $password = ambilTeks($input, 'password');

        if ($username === '' || $password === '') {
            kirimGagal('Username dan password wajib diisi.', 422);
        }

        $akun = $this->user->cariBerdasarkanUsername($username);

        // Pesan gagal sengaja disamakan supaya tidak membocorkan username mana yang terdaftar.
        if ($akun === null || !password_verify($password, $akun['password'])) {
            kirimGagal('Username atau password salah.', 401);
        }

        kirimBerhasil('Login berhasil.', [
            'id' => (int) $akun['id'],
            'username' => $akun['username'],
            'email' => $akun['email'],
            'no_tlp' => $akun['no_tlp'],
        ]);
    }

    // POST /api/auth/register
    public function register(): void
    {
        $input = ambilInput();
        $username = ambilTeks($input, 'username');
        $email = ambilTeks($input, 'email');
        $password = ambilTeks($input, 'password');
        $noTlp = ambilTeks($input, 'no_tlp');

        if ($username === '' || $email === '' || $password === '' || $noTlp === '') {
            kirimGagal('Username, email, password, dan nomor telepon wajib diisi.', 422);
        }

        if (strlen($username) < 3) {
            kirimGagal('Username minimal 3 karakter.', 422);
        }

        if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
            kirimGagal('Format email tidak valid.', 422);
        }

        if (strlen($password) < 6) {
            kirimGagal('Password minimal 6 karakter.', 422);
        }

        if (!ctype_digit($noTlp) || strlen($noTlp) < 9) {
            kirimGagal('Nomor telepon minimal 9 angka dan hanya boleh berisi angka.', 422);
        }

        if ($this->user->sudahDipakai($username, $email)) {
            kirimGagal('Username atau email sudah terdaftar.', 409);
        }

        $idBaru = $this->user->tambah($username, $email, $password, $noTlp);

        kirimBerhasil('Registrasi berhasil.', ['id' => $idBaru, 'username' => $username], 201);
    }
}
