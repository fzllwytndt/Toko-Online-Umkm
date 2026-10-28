<?php

require_once __DIR__ . '/../config/database.php';

// Model untuk tabel `categories`.
// Kolom tabel: id, name, icon, sort_order.
class Category
{
    private PDO $db;

    public function __construct()
    {
        $this->db = koneksiDatabase();
    }

    public function semua(): array
    {
        $sql = 'SELECT id, name, icon, sort_order FROM categories ORDER BY sort_order ASC, id ASC';

        return $this->db->query($sql)->fetchAll();
    }
}
