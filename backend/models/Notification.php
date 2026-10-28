<?php

require_once __DIR__ . '/../config/database.php';

// Model untuk tabel `notifications`.
// Kolom tabel: id, user_id, message, created_at.
class Notification
{
    private PDO $db;

    public function __construct()
    {
        $this->db = koneksiDatabase();
    }

    public function milikPengguna(int $idPengguna): array
    {
        $sql = 'SELECT id, message, created_at
                FROM notifications
                WHERE user_id = ?
                ORDER BY created_at DESC, id DESC';

        $statement = $this->db->prepare($sql);
        $statement->execute([$idPengguna]);

        return $statement->fetchAll();
    }
}
