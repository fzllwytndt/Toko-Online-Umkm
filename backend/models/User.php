<?php

require_once __DIR__ . '/../config/database.php';

// Model untuk tabel `users`.
// Kolom tabel: id, username, email, password, no_tlp, created_at, avatar.
class User
{
    private PDO $db;

    public function __construct()
    {
        $this->db = koneksiDatabase();
    }

    public function cariBerdasarkanUsername(string $username): ?array
    {
        $sql = 'SELECT id, username, email, password, no_tlp, avatar FROM users WHERE username = ? LIMIT 1';

        $statement = $this->db->prepare($sql);
        $statement->execute([$username]);

        $baris = $statement->fetch();

        return $baris === false ? null : $baris;
    }

    public function cariBerdasarkanId(int $id): ?array
    {
        $sql = 'SELECT id, username, email, no_tlp, avatar, created_at FROM users WHERE id = ? LIMIT 1';

        $statement = $this->db->prepare($sql);
        $statement->execute([$id]);

        $baris = $statement->fetch();

        return $baris === false ? null : $baris;
    }

    public function sudahDipakai(string $username, string $email): bool
    {
        $sql = 'SELECT id FROM users WHERE username = ? OR email = ? LIMIT 1';

        $statement = $this->db->prepare($sql);
        $statement->execute([$username, $email]);

        return $statement->fetch() !== false;
    }

    // Memeriksa apakah username sudah dipakai oleh pengguna LAIN (selain dirinya sendiri).
    // Dipakai saat memperbarui profil supaya username tetap unik.
    public function usernameDipakaiPenggunaLain(string $username, int $idSekarang): bool
    {
        $sql = 'SELECT id FROM users WHERE username = ? AND id <> ? LIMIT 1';

        $statement = $this->db->prepare($sql);
        $statement->execute([$username, $idSekarang]);

        return $statement->fetch() !== false;
    }

    // Memperbarui profil pengguna. Hanya username dan avatar yang bisa diubah,
    // mengikuti field yang disediakan halaman Edit Profil.
    // $avatar bernilai null berarti foto profil tidak diganti (nilai lama dipertahankan).
    public function perbarui(int $id, string $username, ?string $avatar): void
    {
        if ($avatar === null) {
            $sql = 'UPDATE users SET username = ? WHERE id = ?';
            $statement = $this->db->prepare($sql);
            $statement->execute([$username, $id]);
        } else {
            $sql = 'UPDATE users SET username = ?, avatar = ? WHERE id = ?';
            $statement = $this->db->prepare($sql);
            $statement->execute([$username, $avatar, $id]);
        }
    }

    public function tambah(string $username, string $email, string $password, string $noTlp): int
    {
        $sql = 'INSERT INTO users (username, email, password, no_tlp) VALUES (?, ?, ?, ?)';

        $statement = $this->db->prepare($sql);
        $statement->execute([
            $username,
            $email,
            // Password disimpan dalam bentuk hash, mengikuti data yang sudah ada di database.
            password_hash($password, PASSWORD_BCRYPT),
            $noTlp,
        ]);

        return (int) $this->db->lastInsertId();
    }
}
