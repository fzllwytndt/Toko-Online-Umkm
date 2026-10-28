<?php

require_once __DIR__ . '/../config/database.php';

// Model untuk tabel `orders` dan `order_items`.
// Kolom `orders`: id, user_id, order_type, dine_capacity, notes, payment_method, total_amount,
//                 created_at, status, pickup_time_type, pickup_time_value, pickup_location,
//                 account_name, down_payment, remaining_amount.
// Kolom `order_items`: id, order_id, product_id, quantity, price, subtotal.
class Order
{
    private PDO $db;

    public function __construct()
    {
        $this->db = koneksiDatabase();
    }

    public function milikPengguna(int $idPengguna): array
    {
        $sql = 'SELECT id, user_id, order_type, payment_method, total_amount, status,
                       pickup_location, notes, created_at
                FROM orders
                WHERE user_id = ?
                ORDER BY created_at DESC, id DESC';

        $statement = $this->db->prepare($sql);
        $statement->execute([$idPengguna]);

        $pesanan = $statement->fetchAll();

        // Setiap pesanan dilengkapi daftar barangnya supaya aplikasi cukup memanggil satu endpoint.
        foreach ($pesanan as $urutan => $satuPesanan) {
            $pesanan[$urutan]['items'] = $this->daftarBarang((int) $satuPesanan['id']);
        }

        return $pesanan;
    }

    public function cariBerdasarkanId(int $id): ?array
    {
        $statement = $this->db->prepare('SELECT * FROM orders WHERE id = ? LIMIT 1');
        $statement->execute([$id]);

        $pesanan = $statement->fetch();

        if ($pesanan === false) {
            return null;
        }

        $pesanan['items'] = $this->daftarBarang($id);

        return $pesanan;
    }

    private function daftarBarang(int $idPesanan): array
    {
        $sql = 'SELECT oi.id, oi.product_id, oi.quantity, oi.price, oi.subtotal, p.name AS product_name
                FROM order_items oi
                INNER JOIN products p ON p.id = oi.product_id
                WHERE oi.order_id = ?
                ORDER BY oi.id ASC';

        $statement = $this->db->prepare($sql);
        $statement->execute([$idPesanan]);

        return $statement->fetchAll();
    }
}
