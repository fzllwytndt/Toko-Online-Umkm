<?php

require_once __DIR__ . '/../config/response.php';
require_once __DIR__ . '/../models/Product.php';

class ProductController
{
    private Product $product;

    public function __construct()
    {
        $this->product = new Product();
    }

    // GET /api/products?category_id=1&search=katsu&best_seller=1
    public function daftar(): void
    {
        $idKategori = null;

        if (isset($_GET['category_id']) && $_GET['category_id'] !== '') {
            if (!ctype_digit((string) $_GET['category_id'])) {
                kirimGagal('Parameter category_id harus berupa angka.', 422);
            }

            $idKategori = (int) $_GET['category_id'];
        }

        $kataKunci = isset($_GET['search']) ? trim((string) $_GET['search']) : '';
        $hanyaBestSeller = isset($_GET['best_seller']) && $_GET['best_seller'] === '1';

        $produk = $this->product->semua($idKategori, $kataKunci, $hanyaBestSeller);

        foreach ($produk as $urutan => $satuProduk) {
            $produk[$urutan] = $this->rapikan($satuProduk);
        }

        kirimBerhasil('Daftar produk berhasil diambil.', $produk);
    }

    // GET /api/products/{id}
    public function detail(int $id): void
    {
        $produk = $this->product->cariBerdasarkanId($id);

        if ($produk === null) {
            kirimGagal('Produk tidak ditemukan.', 404);
        }

        kirimBerhasil('Detail produk ditemukan.', $this->rapikan($produk));
    }

    // MySQL mengirim angka dalam bentuk teks. Diubah dulu supaya tipe data JSON-nya benar.
    private function rapikan(array $produk): array
    {
        $produk['id'] = (int) $produk['id'];
        $produk['category_id'] = $produk['category_id'] === null ? null : (int) $produk['category_id'];
        $produk['price'] = (float) $produk['price'];
        $produk['is_best_seller'] = (bool) $produk['is_best_seller'];
        $produk['is_available'] = (bool) $produk['is_available'];

        return $produk;
    }
}
