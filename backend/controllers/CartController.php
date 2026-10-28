<?php

require_once __DIR__ . '/../config/response.php';
require_once __DIR__ . '/../models/Cart.php';
require_once __DIR__ . '/../models/Product.php';

class CartController
{
    private Cart $cart;
    private Product $product;

    public function __construct()
    {
        $this->cart = new Cart();
        $this->product = new Product();
    }

    // GET /api/carts?user_id=1
    public function daftar(): void
    {
        if (!isset($_GET['user_id']) || !ctype_digit((string) $_GET['user_id'])) {
            kirimGagal('Parameter user_id wajib diisi dan harus berupa angka.', 422);
        }

        $isi = $this->cart->milikPengguna((int) $_GET['user_id']);
        $totalHarga = 0.0;

        foreach ($isi as $urutan => $satuBaris) {
            $isi[$urutan] = $this->rapikan($satuBaris);
            $totalHarga += $isi[$urutan]['subtotal'];
        }

        kirimBerhasil('Isi keranjang berhasil diambil.', [
            'items' => $isi,
            'total_item' => count($isi),
            'total_harga' => $totalHarga,
        ]);
    }

    // POST /api/carts
    public function tambah(): void
    {
        $input = ambilInput();

        $idPengguna = ambilTeks($input, 'user_id');
        $idProduk = ambilTeks($input, 'product_id');
        $jumlah = ambilTeks($input, 'quantity');
        $catatan = ambilTeks($input, 'notes');

        if (!ctype_digit($idPengguna) || !ctype_digit($idProduk)) {
            kirimGagal('user_id dan product_id wajib diisi dan harus berupa angka.', 422);
        }

        // Kalau quantity tidak dikirim, dianggap 1 mengikuti nilai bawaan kolomnya.
        $jumlah = $jumlah === '' ? 1 : (int) $jumlah;

        if ($jumlah < 1) {
            kirimGagal('Jumlah minimal 1.', 422);
        }

        if (!$this->product->ada((int) $idProduk)) {
            kirimGagal('Produk tidak ditemukan.', 404);
        }

        // Produk yang sama tidak dibuat dua baris, cukup jumlahnya yang bertambah.
        $sudahAda = $this->cart->cariProdukDiKeranjang((int) $idPengguna, (int) $idProduk);

        if ($sudahAda !== null) {
            $jumlahBaru = (int) $sudahAda['quantity'] + $jumlah;
            $this->cart->ubah((int) $sudahAda['id'], $jumlahBaru, $catatan === '' ? null : $catatan);

            kirimBerhasil('Jumlah produk di keranjang diperbarui.', [
                'id' => (int) $sudahAda['id'],
                'quantity' => $jumlahBaru,
            ]);
        }

        $idBaru = $this->cart->tambah((int) $idPengguna, (int) $idProduk, $jumlah, $catatan);

        kirimBerhasil('Produk ditambahkan ke keranjang.', ['id' => $idBaru, 'quantity' => $jumlah], 201);
    }

    // PUT /api/carts/{id}
    public function ubah(int $id): void
    {
        $barisKeranjang = $this->cart->cariBerdasarkanId($id);

        if ($barisKeranjang === null) {
            kirimGagal('Item keranjang tidak ditemukan.', 404);
        }

        $input = ambilInput();
        $jumlah = ambilTeks($input, 'quantity');

        if (!ctype_digit($jumlah) || (int) $jumlah < 1) {
            kirimGagal('Jumlah wajib diisi dengan angka minimal 1.', 422);
        }

        // Catatan boleh tidak dikirim. Kalau tidak dikirim, catatan lama dipertahankan.
        if (array_key_exists('notes', $input)) {
            $catatanBaru = trim((string) $input['notes']);
            $catatan = $catatanBaru === '' ? null : $catatanBaru;
        } else {
            $catatan = $barisKeranjang['notes'];
        }

        $this->cart->ubah($id, (int) $jumlah, $catatan);

        kirimBerhasil('Item keranjang berhasil diperbarui.', [
            'id' => $id,
            'quantity' => (int) $jumlah,
            'notes' => $catatan,
        ]);
    }

    // DELETE /api/carts/{id}
    public function hapus(int $id): void
    {
        if ($this->cart->cariBerdasarkanId($id) === null) {
            kirimGagal('Item keranjang tidak ditemukan.', 404);
        }

        $this->cart->hapus($id);

        kirimBerhasil('Item keranjang berhasil dihapus.', ['id' => $id]);
    }

    private function rapikan(array $baris): array
    {
        $baris['id'] = (int) $baris['id'];
        $baris['user_id'] = (int) $baris['user_id'];
        $baris['product_id'] = (int) $baris['product_id'];
        $baris['quantity'] = (int) $baris['quantity'];
        $baris['price'] = (float) $baris['price'];
        $baris['subtotal'] = (float) $baris['subtotal'];
        $baris['is_available'] = (bool) $baris['is_available'];

        return $baris;
    }
}
