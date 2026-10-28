<?php

require_once __DIR__ . '/../config/database.php';

// Model untuk tabel `carts`.
// Kolom tabel: id, user_id, product_id, quantity, created_at, notes.
class Cart
{
    private PDO $db;

    public function __construct()
    {
        $this->db = koneksiDatabase();
    }

    // Isi keranjang milik satu pengguna, sudah digabung dengan data produknya.
    public function milikPengguna(int $idPengguna): array
    {
        $sql = 'SELECT k.id, k.user_id, k.product_id, k.quantity, k.notes, k.created_at,
                       p.name AS product_name, p.price, p.image, p.is_available,
                       (p.price * k.quantity) AS subtotal
                FROM carts k
                INNER JOIN products p ON p.id = k.product_id
                WHERE k.user_id = ?
                ORDER BY k.id ASC';

        $statement = $this->db->prepare($sql);
        $statement->execute([$idPengguna]);

        return $statement->fetchAll();
    }

    public function cariBerdasarkanId(int $id): ?array
    {
        $statement = $this->db->prepare('SELECT id, user_id, product_id, quantity, notes FROM carts WHERE id = ? LIMIT 1');
        $statement->execute([$id]);

        $baris = $statement->fetch();

        return $baris === false ? null : $baris;
    }

    public function cariProdukDiKeranjang(int $idPengguna, int $idProduk): ?array
    {
        $statement = $this->db->prepare('SELECT id, quantity FROM carts WHERE user_id = ? AND product_id = ? LIMIT 1');
        $statement->execute([$idPengguna, $idProduk]);

        $baris = $statement->fetch();

        return $baris === false ? null : $baris;
    }

    public function tambah(int $idPengguna, int $idProduk, int $jumlah, string $catatan): int
    {
        $sql = 'INSERT INTO carts (user_id, product_id, quantity, notes) VALUES (?, ?, ?, ?)';

        $statement = $this->db->prepare($sql);
        $statement->execute([$idPengguna, $idProduk, $jumlah, $catatan === '' ? null : $catatan]);

        return (int) $this->db->lastInsertId();
    }

    public function ubah(int $id, int $jumlah, ?string $catatan): void
    {
        $sql = 'UPDATE carts SET quantity = ?, notes = ? WHERE id = ?';

        $statement = $this->db->prepare($sql);
        $statement->execute([$jumlah, $catatan, $id]);
    }

    public function hapus(int $id): void
    {
        $statement = $this->db->prepare('DELETE FROM carts WHERE id = ?');
        $statement->execute([$id]);
    }
}
