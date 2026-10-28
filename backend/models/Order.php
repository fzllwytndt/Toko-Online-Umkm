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

    // Membuat satu pesanan baru beserta satu barangnya (checkout).
    // Harga diambil dari parameter supaya nilainya mengikuti data produk yang sebenarnya.
    // Seluruh penyimpanan dibungkus transaksi agar pesanan dan barangnya konsisten.
    public function buat(array $data, int $idProduk, int $jumlah, float $harga): int
    {
        $this->db->beginTransaction();

        try {
            $totalHarga = $harga * $jumlah;
            $uangMuka = $data['down_payment'];
            $sisa = $totalHarga - $uangMuka;
            if ($sisa < 0) {
                $sisa = 0;
            }

            $sql = 'INSERT INTO orders
                        (user_id, order_type, notes, payment_method, total_amount, status,
                         pickup_time_type, pickup_time_value, pickup_location,
                         account_name, down_payment, remaining_amount)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)';

            $statement = $this->db->prepare($sql);
            $statement->execute([
                $data['user_id'],
                $data['order_type'],
                $data['notes'],
                $data['payment_method'],
                $totalHarga,
                'Diproses',
                $data['pickup_time_type'],
                $data['pickup_time_value'],
                $data['pickup_location'],
                $data['account_name'],
                $uangMuka,
                $sisa,
            ]);

            $idPesanan = (int) $this->db->lastInsertId();

            $sqlItem = 'INSERT INTO order_items (order_id, product_id, quantity, price, subtotal)
                        VALUES (?, ?, ?, ?, ?)';
            $statementItem = $this->db->prepare($sqlItem);
            $statementItem->execute([$idPesanan, $idProduk, $jumlah, $harga, $totalHarga]);

            $this->db->commit();

            return $idPesanan;
        } catch (Throwable $e) {
            $this->db->rollBack();
            throw $e;
        }
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
