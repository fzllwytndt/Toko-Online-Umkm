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

    // POST /api/users/{id}
    // Memperbarui profil pengguna: nickname (username) dan foto profil (avatar).
    // Dikirim sebagai multipart/form-data supaya file avatar bisa ikut diunggah.
    public function perbarui(int $id): void
    {
        $akun = $this->user->cariBerdasarkanId($id);

        if ($akun === null) {
            kirimGagal('Pengguna tidak ditemukan.', 404);
        }

        $username = ambilTeks(ambilInput(), 'username');

        // Validasi username di sisi server, tidak hanya mengandalkan aplikasi.
        if ($username === '') {
            kirimGagal('Username wajib diisi.', 422);
        }

        if (mb_strlen($username) < 3) {
            kirimGagal('Username minimal 3 karakter.', 422);
        }

        if ($this->user->usernameDipakaiPenggunaLain($username, $id)) {
            kirimGagal('Username sudah dipakai pengguna lain.', 409);
        }

        // Avatar opsional. Kalau tidak ada file terkirim, foto lama dipertahankan.
        $avatarBaru = null;

        if (isset($_FILES['avatar']) && $_FILES['avatar']['error'] !== UPLOAD_ERR_NO_FILE) {
            $avatarBaru = $this->simpanAvatar($_FILES['avatar'], $id);
        }

        $this->user->perbarui($id, $username, $avatarBaru);

        $terbaru = $this->user->cariBerdasarkanId($id);
        $terbaru['id'] = (int) $terbaru['id'];

        kirimBerhasil('Profil berhasil diperbarui.', $terbaru);
    }

    // Menyimpan file avatar ke folder public/uploads/avatars dan mengembalikan
    // path relatifnya (mis. "uploads/avatars/avatar_1_1699999999.png") untuk disimpan di database.
    private function simpanAvatar(array $file, int $idPengguna): string
    {
        if ($file['error'] !== UPLOAD_ERR_OK) {
            kirimGagal('Gagal mengunggah foto profil. Silakan coba lagi.', 422);
        }

        // Batas ukuran 2 MB supaya unggahan tidak membebani server.
        if ($file['size'] > 2 * 1024 * 1024) {
            kirimGagal('Ukuran foto profil maksimal 2 MB.', 422);
        }

        // Memastikan file benar-benar gambar sekaligus mengambil ekstensinya.
        $info = @getimagesize($file['tmp_name']);

        if ($info === false) {
            kirimGagal('File yang diunggah bukan gambar yang valid.', 422);
        }

        $ekstensiPerType = [
            IMAGETYPE_JPEG => 'jpg',
            IMAGETYPE_PNG => 'png',
            IMAGETYPE_WEBP => 'webp',
        ];

        if (!isset($ekstensiPerType[$info[2]])) {
            kirimGagal('Format foto profil harus JPG, PNG, atau WEBP.', 422);
        }

        $ekstensi = $ekstensiPerType[$info[2]];
        $folder = __DIR__ . '/../public/uploads/avatars';

        if (!is_dir($folder) && !mkdir($folder, 0755, true) && !is_dir($folder)) {
            kirimGagal('Server gagal menyiapkan folder penyimpanan foto.', 500);
        }

        $namaFile = 'avatar_' . $idPengguna . '_' . time() . '.' . $ekstensi;
        $tujuan = $folder . '/' . $namaFile;

        if (!move_uploaded_file($file['tmp_name'], $tujuan)) {
            kirimGagal('Server gagal menyimpan foto profil.', 500);
        }

        return 'uploads/avatars/' . $namaFile;
    }
}
