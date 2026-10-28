<?php

require_once __DIR__ . '/../config/response.php';
require_once __DIR__ . '/../models/Order.php';
require_once __DIR__ . '/../models/Product.php';

class OrderController
{
    private Order $order;
    private Product $product;

    public function __construct()
    {
        $this->order = new Order();
        $this->product = new Product();
    }

    // GET /api/orders?user_id=1
    public function daftar(): void
    {
        if (!isset($_GET['user_id']) || !ctype_digit((string) $_GET['user_id'])) {
            kirimGagal('Parameter user_id wajib diisi dan harus berupa angka.', 422);
        }

        $pesanan = $this->order->milikPengguna((int) $_GET['user_id']);

        foreach ($pesanan as $urutan => $satuPesanan) {
            $pesanan[$urutan] = $this->rapikan($satuPesanan);
        }

        kirimBerhasil('Riwayat pesanan berhasil diambil.', $pesanan);
    }

    // GET /api/orders/{id}
    public function detail(int $id): void
    {
        $pesanan = $this->order->cariBerdasarkanId($id);

        if ($pesanan === null) {
            kirimGagal('Pesanan tidak ditemukan.', 404);
        }

        kirimBerhasil('Detail pesanan ditemukan.', $this->rapikan($pesanan));
    }

    // POST /api/orders  — membuat pesanan baru (checkout) untuk satu produk.
    public function checkout(): void
    {
        $input = ambilInput();

        $idPengguna = ambilTeks($input, 'user_id');
        $idProduk = ambilTeks($input, 'product_id');
        $jumlah = ambilTeks($input, 'quantity');
        $jenis = ambilTeks($input, 'order_type');
        $pembayaran = ambilTeks($input, 'payment_method');

        if (!ctype_digit($idPengguna) || !ctype_digit($idProduk)) {
            kirimGagal('user_id dan product_id wajib diisi dan harus berupa angka.', 422);
        }

        if (!ctype_digit($jumlah) || (int) $jumlah < 1) {
            kirimGagal('Jumlah wajib diisi dengan angka minimal 1.', 422);
        }

        // order_type mengikuti pilihan yang tersedia pada kolom enum tabel orders.
        $jenis = $jenis === '' ? 'Diambil' : $jenis;
        if (!in_array($jenis, ['Diambil', 'Makan Di Tempat'], true)) {
            kirimGagal('order_type harus "Diambil" atau "Makan Di Tempat".', 422);
        }

        if ($pembayaran === '') {
            kirimGagal('payment_method wajib diisi.', 422);
        }

        $produk = $this->product->cariBerdasarkanId((int) $idProduk);
        if ($produk === null) {
            kirimGagal('Produk tidak ditemukan.', 404);
        }

        $uangMuka = ambilTeks($input, 'down_payment');
        $uangMuka = is_numeric($uangMuka) ? (float) $uangMuka : 0.0;

        $data = [
            'user_id' => (int) $idPengguna,
            'order_type' => $jenis,
            'notes' => ambilTeks($input, 'notes') ?: null,
            'payment_method' => $pembayaran,
            'pickup_time_type' => ambilTeks($input, 'pickup_time_type') ?: 'Secepatnya',
            'pickup_time_value' => ambilTeks($input, 'pickup_time_value') ?: null,
            'pickup_location' => ambilTeks($input, 'pickup_location') ?: 'Resto Pusat - Pandanaran',
            'account_name' => ambilTeks($input, 'account_name') ?: null,
            'down_payment' => $uangMuka,
        ];

        $idBaru = $this->order->buat($data, (int) $idProduk, (int) $jumlah, (float) $produk['price']);

        $pesanan = $this->order->cariBerdasarkanId($idBaru);

        kirimBerhasil('Pesanan berhasil dibuat.', $this->rapikan($pesanan), 201);
    }

    private function rapikan(array $pesanan): array
    {
        $pesanan['id'] = (int) $pesanan['id'];
        $pesanan['user_id'] = (int) $pesanan['user_id'];
        $pesanan['total_amount'] = (float) $pesanan['total_amount'];

        foreach ($pesanan['items'] as $urutan => $barang) {
            $pesanan['items'][$urutan]['id'] = (int) $barang['id'];
            $pesanan['items'][$urutan]['product_id'] = (int) $barang['product_id'];
            $pesanan['items'][$urutan]['quantity'] = (int) $barang['quantity'];
            $pesanan['items'][$urutan]['price'] = (float) $barang['price'];
            $pesanan['items'][$urutan]['subtotal'] = (float) $barang['subtotal'];
        }

        return $pesanan;
    }
}
