# Toko-Online-Umkm

Aplikasi **Toko Online UMKM Semarang (EZPICK)** — Tugas 1 : Implementasi fitur **Login** dan
**Register** menggunakan **REST API** dengan arsitektur **MVVM** dan **Repository Pattern**.

## Fitur yang Dibuat

- **Login** menggunakan REST API (username dan password).
- **Register** akun baru (username, email, password, konfirmasi password, dan nomor telepon).
- **Validasi form** pada halaman Login dan Register, pesan kesalahan tampil tepat di bawah kolom
  yang bermasalah.
- **Indikator loading** selama permintaan API berlangsung. Tombol dinonaktifkan sementara agar
  tidak terjadi pengiriman berulang.
- **Penanganan error** menggunakan try-catch : akun salah, email atau username sudah terdaftar,
  koneksi internet bermasalah, server tidak dapat diakses, dan respons API tidak sesuai format.
- **Penyimpanan sesi login** menggunakan SharedPreferences. Password pengguna tidak disimpan.
- **Navigasi** Splash → Login / Register → Dashboard, termasuk keluar (logout) dari Dashboard.

## Arsitektur

Alur program mengikuti MVVM dan Repository Pattern :

```
View (Activity)  ->  ViewModel  ->  Repository  ->  REST API / Backend
     (form)          (validasi &      (permintaan      (memeriksa akun /
                      status)          autentikasi)     membuat akun)
```

Hasil respons dikembalikan melalui Repository dan ViewModel untuk diperbarui pada tampilan.
Activity tidak pernah memanggil API secara langsung.

## Struktur Folder

```
com.faizalwiyatnodito.tokoonlineumkm
├── data
│   ├── network
│   │   ├── ApiConfig.kt          konfigurasi Retrofit dan BASE_URL
│   │   ├── ApiService.kt         daftar endpoint auth/login dan auth/register
│   │   └── ResponseModels.kt     model User, LoginResponse, RegisterResponse
│   └── repository
│       └── UserRepository.kt     penghubung ViewModel <-> API + sesi SharedPreferences
├── ui
│   ├── login
│   │   ├── LoginActivity.kt
│   │   └── LoginViewModel.kt
│   ├── register
│   │   ├── RegisterActivity.kt
│   │   └── RegisterViewModel.kt
│   └── dashboard
│       └── DashboardActivity.kt  halaman tujuan setelah Login berhasil
└── MainActivity.kt               halaman Splash sekaligus pemeriksa sesi login

res/layout
├── activity_main.xml       tampilan Splash
├── activity_login.xml      tampilan Login
├── activity_register.xml   tampilan Register
└── activity_dashboard.xml  tampilan Dashboard
```

## REST API

Base URL :

```
https://pasarrakyatbangetayukulon.my.id/toko_online/
```

Data dikirim dalam bentuk form (`x-www-form-urlencoded`).

| Endpoint        | Method | Field yang dikirim                        |
|-----------------|--------|-------------------------------------------|
| `auth/login`    | POST   | `username`, `password`                    |
| `auth/register` | POST   | `username`, `email`, `password`, `no_tlp` |

Contoh respons Login berhasil :

```json
{
  "status": true,
  "message": "Login berhasil.",
  "data": { "id": 8, "username": "ezpicktest1", "email": "ezpicktest1@example.com", "no_tlp": "081234567890" }
}
```

Contoh respons gagal :

```json
{ "status": false, "message": "Username atau Password salah." }
```

Respons gagal dikirim dengan kode HTTP 400 atau 401, sehingga pesannya dibaca dari `errorBody()`
di dalam `UserRepository`.

## Alur Aplikasi

**Login**

1. Aplikasi dibuka, halaman Splash tampil.
2. Sesi login dibaca dari SharedPreferences.
   Sesi masih aktif → langsung ke Dashboard. Belum login → ke halaman Login.
3. Pengguna mengisi username dan password, lalu input divalidasi.
4. ViewModel memanggil Repository untuk menjalankan Login melalui REST API, loading ditampilkan.
5. Berhasil → sesi disimpan lalu pindah ke Dashboard.
   Gagal → pesan kesalahan ditampilkan dan pengguna tetap di halaman Login.

**Register**

1. Pengguna membuka halaman Register dan mengisi seluruh kolom.
2. Input diperiksa kelengkapan dan validitasnya.
3. ViewModel memanggil Repository untuk mengirim data ke REST API, loading ditampilkan.
4. Berhasil → pesan ditampilkan lalu kembali ke halaman Login.
   Gagal → pesan kesalahan ditampilkan, misalnya email sudah digunakan.

## Ketentuan Validasi

| Kolom               | Ketentuan                                      |
|---------------------|------------------------------------------------|
| Username (Login)    | tidak boleh kosong                              |
| Password (Login)    | tidak boleh kosong                              |
| Username (Register) | tidak boleh kosong, minimal 3 karakter          |
| Email               | tidak boleh kosong, format email harus benar    |
| Password            | tidak boleh kosong, minimal 6 karakter          |
| Konfirmasi Password | tidak boleh kosong, harus sama dengan password  |
| Nomor telepon       | tidak boleh kosong, minimal 9 angka             |

## Library yang Digunakan

- Retrofit 2 + Converter Gson — komunikasi dengan REST API
- OkHttp Logging Interceptor — melihat permintaan dan respons API di Logcat
- AndroidX Lifecycle (ViewModel + LiveData) — penerapan MVVM
- Material Components — komponen tampilan
- View Binding — mengakses komponen layout

## Cara Menjalankan

1. Buka project pada Android Studio.
2. Jalankan Gradle Sync agar seluruh library terunduh.
3. Jalankan aplikasi pada emulator atau perangkat Android (minimal Android 7.0 / API 24).
