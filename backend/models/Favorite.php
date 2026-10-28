<?php

require_once __DIR__ . '/../config/database.php';

// Model untuk tabel `favorites`.
// Kolom tabel: id, user_id, product_id, created_at.
class Favorite
{
    private PDO $db;

    public function __construct()
    {
        $this->db = koneksiDatabase();
    }

    // Daftar favorit satu pengguna, digabung dengan data produknya.
    public function milikPengguna(int $idPengguna): array
    {
        $sql = 'SELECT f.id, f.product_id, p.name AS product_name, p.price,
                       p.image, p.is_available, p.is_best_seller
                FROM favorites f
                INNER JOIN products p ON p.id = f.product_id
                WHERE f.user_id = ?
                ORDER BY f.id DESC';

        $statement = $this->db->prepare($sql);
        $statement->execute([$idPengguna]);

        return $statement->fetchAll();
    }

    public function cari(int $idPengguna, int $idProduk): ?array
    {
        $statement = $this->db->prepare('SELECT id FROM favorites WHERE user_id = ? AND product_id = ? LIMIT 1');
        $statement->execute([$idPengguna, $idProduk]);

        $baris = $statement->fetch();

        return $baris === false ? null : $baris;
    }

    public function tambah(int $idPengguna, int $idProduk): int
    {
        $statement = $this->db->prepare('INSERT INTO favorites (user_id, product_id) VALUES (?, ?)');
        $statement->execute([$idPengguna, $idProduk]);

        return (int) $this->db->lastInsertId();
    }

    // Menghapus favorit berdasarkan pengguna dan produk. Mengembalikan jumlah baris terhapus.
    public function hapus(int $idPengguna, int $idProduk): int
    {
        $statement = $this->db->prepare('DELETE FROM favorites WHERE user_id = ? AND product_id = ?');
        $statement->execute([$idPengguna, $idProduk]);

        return $statement->rowCount();
    }
}
