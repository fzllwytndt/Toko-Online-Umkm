<?php

require_once __DIR__ . '/../config/database.php';

// Model untuk tabel `products`.
// Kolom tabel: id, category_id, name, price, image, is_best_seller, created_at, is_available, description.
class Product
{
    private PDO $db;

    public function __construct()
    {
        $this->db = koneksiDatabase();
    }

    // Daftar produk, bisa disaring per kategori, kata kunci, atau hanya yang best seller.
    public function semua(?int $idKategori, string $kataKunci, bool $hanyaBestSeller): array
    {
        $sql = 'SELECT p.id, p.category_id, c.name AS category_name, p.name, p.price,
                       p.image, p.is_best_seller, p.is_available, p.description
                FROM products p
                LEFT JOIN categories c ON c.id = p.category_id
                WHERE 1 = 1';

        $nilai = [];

        if ($idKategori !== null) {
            $sql .= ' AND p.category_id = ?';
            $nilai[] = $idKategori;
        }

        if ($kataKunci !== '') {
            $sql .= ' AND p.name LIKE ?';
            $nilai[] = '%' . $kataKunci . '%';
        }

        if ($hanyaBestSeller) {
            $sql .= ' AND p.is_best_seller = 1';
        }

        $sql .= ' ORDER BY p.id ASC';

        $statement = $this->db->prepare($sql);
        $statement->execute($nilai);

        return $statement->fetchAll();
    }

    public function cariBerdasarkanId(int $id): ?array
    {
        $sql = 'SELECT p.id, p.category_id, c.name AS category_name, p.name, p.price,
                       p.image, p.is_best_seller, p.is_available, p.description, p.created_at
                FROM products p
                LEFT JOIN categories c ON c.id = p.category_id
                WHERE p.id = ?
                LIMIT 1';

        $statement = $this->db->prepare($sql);
        $statement->execute([$id]);

        $baris = $statement->fetch();

        return $baris === false ? null : $baris;
    }

    public function ada(int $id): bool
    {
        $statement = $this->db->prepare('SELECT id FROM products WHERE id = ? LIMIT 1');
        $statement->execute([$id]);

        return $statement->fetch() !== false;
    }
}
