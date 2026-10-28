<?php

require_once __DIR__ . '/../config/response.php';
require_once __DIR__ . '/../models/User.php';

// Menampilkan data satu pengguna untuk halaman Profil.
class UserController
{
    private User $user;

    public function __construct()
    {
        $this->user = new User();
    }

    // GET /api/users/{id}
    public function detail(int $id): void
    {
        $akun = $this->user->cariBerdasarkanId($id);

        if ($akun === null) {
            kirimGagal('Pengguna tidak ditemukan.', 404);
        }

        $akun['id'] = (int) $akun['id'];

        kirimBerhasil('Data pengguna ditemukan.', $akun);
    }
}
