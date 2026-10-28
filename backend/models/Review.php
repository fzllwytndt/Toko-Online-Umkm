<?php

require_once __DIR__ . '/../config/database.php';

// Model untuk tabel `reviews`.
// Kolom tabel: id, order_id, user_id, product_id, rating, comment, tags, created_at.
class Review
{
    private PDO $db;

    public function __construct()
    {
        $this->db = koneksiDatabase();
    }

    // Mencari ulasan yang sudah ada untuk kombinasi pesanan + pengguna + produk.
    public function cari(int $idPesanan, int $idPengguna, int $idProduk): ?array
    {
        $statement = $this->db->prepare(
            'SELECT id FROM reviews WHERE order_id = ? AND user_id = ? AND product_id = ? LIMIT 1'
        );
        $statement->execute([$idPesanan, $idPengguna, $idProduk]);

        $baris = $statement->fetch();

        return $baris === false ? null : $baris;
    }

    public function tambah(int $idPesanan, int $idPengguna, int $idProduk, int $rating, ?string $komentar, ?string $tags): int
    {
        $sql = 'INSERT INTO reviews (order_id, user_id, product_id, rating, comment, tags) VALUES (?, ?, ?, ?, ?, ?)';
        $statement = $this->db->prepare($sql);
        $statement->execute([$idPesanan, $idPengguna, $idProduk, $rating, $komentar, $tags]);

        return (int) $this->db->lastInsertId();
    }

    public function perbarui(int $id, int $rating, ?string $komentar, ?string $tags): void
    {
        $sql = 'UPDATE reviews SET rating = ?, comment = ?, tags = ? WHERE id = ?';
        $statement = $this->db->prepare($sql);
        $statement->execute([$rating, $komentar, $tags, $id]);
    }

    public function cariBerdasarkanId(int $id): ?array
    {
        $statement = $this->db->prepare('SELECT * FROM reviews WHERE id = ? LIMIT 1');
        $statement->execute([$id]);

        $baris = $statement->fetch();

        return $baris === false ? null : $baris;
    }
}
