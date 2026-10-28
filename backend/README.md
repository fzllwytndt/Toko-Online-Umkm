# Backend REST API — Toko Online UMKM Semarang

Backend REST API yang dijalankan secara lokal (localhost) dan terhubung ke database MySQL **`db_toko_online`** yang sudah tersedia di komputer.

Ditulis memakai **PHP native** tanpa framework dan tanpa dependency apa pun, supaya setiap bagiannya mudah dipelajari. Yang dipakai hanya PHP bawaan XAMPP dan **PDO** untuk koneksi database.

> **Backend ini tidak pernah mengubah struktur database.** Tidak ada perintah `CREATE`, `ALTER`, `DROP`, maupun `TRUNCATE` di dalam kode. Tidak ada migration dan tidak ada seeder. Seluruh query menyesuaikan nama tabel dan nama kolom yang sudah ada.

---

## Struktur Folder

```
backend/
├── config/
│   ├── env.php          Membaca file .env menjadi array
│   ├── database.php     Membuat koneksi PDO ke MySQL
│   └── response.php     Bentuk respons JSON dan pembaca input request
├── models/              Query SQL per tabel (prepared statement)
│   ├── User.php
│   ├── Category.php
│   ├── Product.php
│   ├── Cart.php
│   └── Order.php
├── controllers/         Validasi request lalu memanggil Model
│   ├── AuthController.php
│   ├── UserController.php
│   ├── CategoryController.php
│   ├── ProductController.php
│   ├── CartController.php
│   └── OrderController.php
├── routes/
│   └── api.php          Daftar seluruh endpoint
├── public/
│   ├── index.php        Pintu masuk tunggal, seluruh request lewat sini
│   └── .htaccess        Hanya dipakai kalau dijalankan lewat Apache
├── .env                 Konfigurasi database (tidak diunggah ke GitHub)
└── .env.example         Contoh isi .env
```

Alur satu request:

```
Request  →  public/index.php  →  routes/api.php  →  Controller  →  Model  →  MySQL
                                                                      ↓
Response JSON  ←───────────────────────────────────────────────── hasil query
```

---

## Konfigurasi Database

Salin `.env.example` menjadi `.env`, lalu sesuaikan dengan pengaturan MySQL di komputer sendiri:

```
DB_HOST=localhost
DB_PORT=3306
DB_NAME=db_toko_online
DB_USER=root
DB_PASSWORD=
APP_PORT=3000
```

File `.env` sudah didaftarkan pada `.gitignore`, jadi kredensial asli tidak ikut terunggah ke GitHub.

---

## Cara Menjalankan

1. Nyalakan **MySQL** lewat XAMPP Control Panel dan pastikan database `db_toko_online` tersedia.
2. Buka terminal pada folder project, lalu jalankan:

   ```bash
   php -S 0.0.0.0:3000 -t backend/public backend/public/index.php
   ```

   Kalau `php` belum terdaftar pada PATH, pakai PHP bawaan XAMPP:

   ```bash
   C:\xampp\php\php.exe -S 0.0.0.0:3000 -t backend/public backend/public/index.php
   ```

   Alamat `0.0.0.0` dipakai supaya backend bisa diakses dari komputer itu sendiri maupun dari HP Android pada jaringan yang sama. Kalau hanya perlu diakses dari komputer sendiri, ganti dengan `localhost:3000`.

3. Cek backend sudah hidup dengan membuka `http://localhost:3000/api` di browser. Respons yang benar:

   ```json
   { "status": true, "message": "Backend Toko Online UMKM Semarang siap digunakan." }
   ```

### Alternatif: lewat Apache XAMPP

Backend juga bisa dijalankan lewat Apache dengan menambahkan Alias pada `httpd.conf` yang mengarah ke folder `backend/public`. File `.htaccess` di dalamnya sudah menyiapkan rewrite ke `index.php`. Cara `php -S` di atas lebih disarankan karena tidak perlu mengubah konfigurasi Apache.

---

## Alamat yang Dipakai

| Diakses dari | Alamat |
|---|---|
| Postman / browser di komputer yang sama | `http://localhost:3000` |
| Android Emulator | `http://10.0.2.2:3000` |
| HP Android fisik (satu jaringan Wi-Fi) | `http://<IP-lokal-komputer>:3000` |

IP lokal komputer bisa dilihat dengan perintah `ipconfig` (cari **IPv4 Address**).

---

## Daftar Endpoint

Seluruh alamat diawali `/api`. Format pertukaran data memakai JSON.

| Method | Endpoint | Fungsi |
|---|---|---|
| GET | `/api` | Cek backend hidup |
| POST | `/api/auth/register` | Mendaftarkan akun baru |
| POST | `/api/auth/login` | Masuk dengan akun terdaftar |
| GET | `/api/categories` | Daftar kategori |
| GET | `/api/products` | Daftar produk |
| GET | `/api/products/{id}` | Detail satu produk |
| GET | `/api/carts?user_id=` | Isi keranjang satu pengguna |
| POST | `/api/carts` | Menambah produk ke keranjang |
| PUT | `/api/carts/{id}` | Mengubah jumlah atau catatan item keranjang |
| DELETE | `/api/carts/{id}` | Menghapus item keranjang |
| GET | `/api/orders?user_id=` | Riwayat pesanan satu pengguna |
| GET | `/api/orders/{id}` | Detail satu pesanan |
| GET | `/api/users/{id}` | Data pengguna untuk halaman Profil |

Parameter tambahan pada `GET /api/products`:

| Parameter | Contoh | Fungsi |
|---|---|---|
| `category_id` | `/api/products?category_id=1` | Menyaring per kategori |
| `search` | `/api/products?search=katsu` | Mencari berdasarkan sebagian nama |
| `best_seller` | `/api/products?best_seller=1` | Hanya produk best seller |

### Mengapa produk hanya bisa dibaca

Tabel `products` sudah berisi data katalog yang disiapkan sebelumnya, dan aturan tugas melarang mengubah data yang sudah ada. Karena itu endpoint produk sengaja dibuat **hanya membaca**.

Method `POST`, `PUT`, dan `DELETE` tetap dibuat lengkap, tetapi ditempatkan pada **keranjang** — fitur yang datanya memang dibuat dan dihapus sendiri oleh pengguna, sehingga tidak ada data lama yang terganggu.

---

## Bentuk Respons

Seluruh endpoint memakai bentuk yang sama, sehingga aplikasi Android hanya perlu membaca satu pola.

Berhasil:

```json
{
  "status": true,
  "message": "Daftar produk berhasil diambil.",
  "data": [ ... ]
}
```

Gagal:

```json
{
  "status": false,
  "message": "Produk tidak ditemukan."
}
```

---

## HTTP Status Code

| Kode | Dipakai ketika |
|---|---|
| `200 OK` | Permintaan berhasil |
| `201 Created` | Data baru berhasil dibuat (register, tambah keranjang) |
| `401 Unauthorized` | Username atau password salah |
| `404 Not Found` | Data atau endpoint tidak ditemukan |
| `405 Method Not Allowed` | Method HTTP tidak sesuai untuk endpoint tersebut |
| `409 Conflict` | Username atau email sudah terdaftar |
| `422 Unprocessable Entity` | Input tidak valid atau wajib diisi |
| `500 Internal Server Error` | Kesalahan tak terduga pada server |
| `503 Service Unavailable` | Backend tidak dapat terhubung ke MySQL |

---

## Penanganan Error

- Setiap request divalidasi lebih dulu sebelum menyentuh database.
- Semua query memakai **prepared statement**, sehingga aman dari SQL injection.
- Kegagalan koneksi MySQL ditangkap sebagai `PDOException` dan dijawab `503` dengan pesan yang mudah dipahami.
- Pesan error asli PHP tidak pernah ditampilkan ke pengguna (`display_errors` dimatikan), hanya dicatat lewat `error_log`. Nama database, username, dan password tidak pernah ikut terkirim pada respons.
- Password disimpan sebagai hash `password_hash()` dan dicocokkan dengan `password_verify()`, mengikuti bentuk data yang sudah ada di tabel `users`.
- Kolom `password` tidak pernah ikut dikirim pada respons mana pun.

---

## Pengujian

Gunakan Collection Postman pada `postman/Toko-Online-UMKM-Semarang.postman_collection.json`. Cara memakainya dijelaskan pada README utama repository.
