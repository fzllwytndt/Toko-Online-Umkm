<?php

require_once __DIR__ . '/../config/response.php';
require_once __DIR__ . '/../models/Notification.php';

class NotificationController
{
    private Notification $notification;

    public function __construct()
    {
        $this->notification = new Notification();
    }

    // GET /api/notifications?user_id=1
    public function daftar(): void
    {
        if (!isset($_GET['user_id']) || !ctype_digit((string) $_GET['user_id'])) {
            kirimGagal('Parameter user_id wajib diisi dan harus berupa angka.', 422);
        }

        $isi = $this->notification->milikPengguna((int) $_GET['user_id']);

        foreach ($isi as $urutan => $baris) {
            $isi[$urutan]['id'] = (int) $baris['id'];
        }

        kirimBerhasil('Daftar notifikasi berhasil diambil.', $isi);
    }
}
