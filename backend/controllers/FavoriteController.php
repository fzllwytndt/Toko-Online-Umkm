<?php

require_once __DIR__ . '/../config/response.php';
require_once __DIR__ . '/../models/Favorite.php';
require_once __DIR__ . '/../models/Product.php';

class FavoriteController
{
    private Favorite $favorite;
    private Product $product;

    public function __construct()
    {
        $this->favorite = new Favorite();
        $this->product = new Product();
    }

    // GET /api/favorites?user_id=1
    public function daftar(): void
    {
        if (!isset($_GET['user_id']) || !ctype_digit((string) $_GET['user_id'])) {
            kirimGagal('Parameter user_id wajib diisi dan harus berupa angka.', 422);
        }

        $isi = $this->favorite->milikPengguna((int) $_GET['user_id']);

        foreach ($isi as $urutan => $baris) {
            $isi[$urutan]['id'] = (int) $baris['id'];
            $isi[$urutan]['product_id'] = (int) $baris['product_id'];
            $isi[$urutan]['price'] = (float) $baris['price'];
            $isi[$urutan]['is_available'] = (bool) $baris['is_available'];
            $isi[$urutan]['is_best_seller'] = (bool) $baris['is_best_seller'];
        }

        kirimBerhasil('Daftar favorit berhasil diambil.', $isi);
    }

    // POST /api/favorites
    public function tambah(): void
    {
        $input = ambilInput();
        $idPengguna = ambilTeks($input, 'user_id');
        $idProduk = ambilTeks($input, 'product_id');

        if (!ctype_digit($idPengguna) || !ctype_digit($idProduk)) {
            kirimGagal('user_id dan product_id wajib diisi dan harus berupa angka.', 422);
        }

        if (!$this->product->ada((int) $idProduk)) {
            kirimGagal('Produk tidak ditemukan.', 404);
        }

        // Kalau sudah difavoritkan, tidak dibuat baris ganda. Tetap dianggap berhasil
        // supaya pemanggilan berulang tidak menimbulkan error.
        $sudahAda = $this->favorite->cari((int) $idPengguna, (int) $idProduk);

        if ($sudahAda !== null) {
            kirimBerhasil('Produk sudah ada di daftar favorit.', ['id' => (int) $sudahAda['id']]);
        }

        $idBaru = $this->favorite->tambah((int) $idPengguna, (int) $idProduk);

        kirimBerhasil('Produk ditambahkan ke favorit.', ['id' => $idBaru], 201);
    }

    // DELETE /api/favorites?user_id=1&product_id=2
    public function hapus(): void
    {
        $idPengguna = $_GET['user_id'] ?? '';
        $idProduk = $_GET['product_id'] ?? '';

        if (!ctype_digit((string) $idPengguna) || !ctype_digit((string) $idProduk)) {
            kirimGagal('user_id dan product_id wajib diisi dan harus berupa angka.', 422);
        }

        // Dibuat idempoten: menghapus favorit yang memang tidak ada tetap dianggap berhasil.
        $this->favorite->hapus((int) $idPengguna, (int) $idProduk);

        kirimBerhasil('Produk dihapus dari favorit.');
    }
}
