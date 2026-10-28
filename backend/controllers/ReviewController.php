<?php

require_once __DIR__ . '/../config/response.php';
require_once __DIR__ . '/../models/Review.php';
require_once __DIR__ . '/../models/Order.php';
require_once __DIR__ . '/../models/Product.php';

class ReviewController
{
    private Review $review;
    private Order $order;
    private Product $product;

    public function __construct()
    {
        $this->review = new Review();
        $this->order = new Order();
        $this->product = new Product();
    }

    // POST /api/reviews
    public function buat(): void
    {
        $input = ambilInput();

        $idPesanan = ambilTeks($input, 'order_id');
        $idPengguna = ambilTeks($input, 'user_id');
        $idProduk = ambilTeks($input, 'product_id');
        $rating = ambilTeks($input, 'rating');
        $komentar = ambilTeks($input, 'comment');
        $tags = ambilTeks($input, 'tags');

        if (!ctype_digit($idPesanan) || !ctype_digit($idPengguna) || !ctype_digit($idProduk)) {
            kirimGagal('order_id, user_id, dan product_id wajib diisi dan harus berupa angka.', 422);
        }

        if (!ctype_digit($rating) || (int) $rating < 1 || (int) $rating > 5) {
            kirimGagal('Rating wajib diisi dengan angka 1 sampai 5.', 422);
        }

        if ($this->order->cariBerdasarkanId((int) $idPesanan) === null) {
            kirimGagal('Pesanan tidak ditemukan.', 404);
        }

        if (!$this->product->ada((int) $idProduk)) {
            kirimGagal('Produk tidak ditemukan.', 404);
        }

        $komentar = $komentar === '' ? null : $komentar;
        $tags = $tags === '' ? null : $tags;

        // Satu pesanan+produk cukup punya satu ulasan. Kalau sudah ada, ulasannya diperbarui
        // supaya pengujian berulang tidak menumpuk data.
        $sudahAda = $this->review->cari((int) $idPesanan, (int) $idPengguna, (int) $idProduk);

        if ($sudahAda !== null) {
            $this->review->perbarui((int) $sudahAda['id'], (int) $rating, $komentar, $tags);
            kirimBerhasil('Ulasan berhasil diperbarui.', $this->rapikan((int) $sudahAda['id']));
        }

        $idBaru = $this->review->tambah((int) $idPesanan, (int) $idPengguna, (int) $idProduk, (int) $rating, $komentar, $tags);

        kirimBerhasil('Ulasan berhasil dikirim.', $this->rapikan($idBaru), 201);
    }

    private function rapikan(int $id): array
    {
        $ulasan = $this->review->cariBerdasarkanId($id);
        $ulasan['id'] = (int) $ulasan['id'];
        $ulasan['order_id'] = (int) $ulasan['order_id'];
        $ulasan['user_id'] = (int) $ulasan['user_id'];
        $ulasan['product_id'] = (int) $ulasan['product_id'];
        $ulasan['rating'] = (int) $ulasan['rating'];

        return $ulasan;
    }
}
