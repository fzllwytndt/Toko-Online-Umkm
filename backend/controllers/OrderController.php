<?php

require_once __DIR__ . '/../config/response.php';
require_once __DIR__ . '/../models/Order.php';

class OrderController
{
    private Order $order;

    public function __construct()
    {
        $this->order = new Order();
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
