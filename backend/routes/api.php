<?php

require_once __DIR__ . '/../config/response.php';
require_once __DIR__ . '/../controllers/AuthController.php';
require_once __DIR__ . '/../controllers/CategoryController.php';
require_once __DIR__ . '/../controllers/ProductController.php';
require_once __DIR__ . '/../controllers/CartController.php';
require_once __DIR__ . '/../controllers/OrderController.php';
require_once __DIR__ . '/../controllers/UserController.php';

// Daftar seluruh endpoint REST API.
//
// Method  Endpoint                 Fungsi
// GET     /api                     Cek backend hidup
// POST    /api/auth/register       Mendaftarkan akun baru
// POST    /api/auth/login          Masuk dengan akun terdaftar
// GET     /api/categories          Daftar kategori
// GET     /api/products            Daftar produk (filter: category_id, search, best_seller)
// GET     /api/products/{id}       Detail satu produk
// GET     /api/carts               Isi keranjang satu pengguna (wajib ?user_id=)
// POST    /api/carts               Menambah produk ke keranjang
// PUT     /api/carts/{id}          Mengubah jumlah atau catatan item keranjang
// DELETE  /api/carts/{id}          Menghapus item keranjang
// GET     /api/orders              Riwayat pesanan satu pengguna (wajib ?user_id=)
// GET     /api/orders/{id}         Detail satu pesanan
// GET     /api/users/{id}          Data pengguna untuk halaman Profil
function jalankanRute(string $method, array $segmen): void
{
    // $segmen adalah potongan alamat setelah /api, contoh: ['products', '3'].
    $bagian1 = $segmen[0] ?? '';
    $bagian2 = $segmen[1] ?? '';

    if ($bagian1 === '') {
        kirimBerhasil('Backend Toko Online UMKM Semarang siap digunakan.');
    }

    if ($bagian1 === 'auth') {
        $auth = new AuthController();

        if ($bagian2 === 'login' && $method === 'POST') {
            $auth->login();
        }

        if ($bagian2 === 'register' && $method === 'POST') {
            $auth->register();
        }

        tolakMethodAtauEndpoint();
    }

    if ($bagian1 === 'categories' && $bagian2 === '') {
        if ($method !== 'GET') {
            tolakMethod();
        }

        (new CategoryController())->daftar();
    }

    if ($bagian1 === 'products') {
        $product = new ProductController();

        if ($bagian2 === '' && $method === 'GET') {
            $product->daftar();
        }

        if ($bagian2 !== '' && $method === 'GET') {
            $product->detail(bacaId($bagian2));
        }

        tolakMethodAtauEndpoint();
    }

    if ($bagian1 === 'carts') {
        $cart = new CartController();

        if ($bagian2 === '' && $method === 'GET') {
            $cart->daftar();
        }

        if ($bagian2 === '' && $method === 'POST') {
            $cart->tambah();
        }

        if ($bagian2 !== '' && $method === 'PUT') {
            $cart->ubah(bacaId($bagian2));
        }

        if ($bagian2 !== '' && $method === 'DELETE') {
            $cart->hapus(bacaId($bagian2));
        }

        tolakMethodAtauEndpoint();
    }

    if ($bagian1 === 'orders') {
        $order = new OrderController();

        if ($bagian2 === '' && $method === 'GET') {
            $order->daftar();
        }

        if ($bagian2 !== '' && $method === 'GET') {
            $order->detail(bacaId($bagian2));
        }

        tolakMethodAtauEndpoint();
    }

    if ($bagian1 === 'users' && $bagian2 !== '') {
        if ($method !== 'GET') {
            tolakMethod();
        }

        (new UserController())->detail(bacaId($bagian2));
    }

    kirimGagal('Endpoint tidak ditemukan.', 404);
}

// Memastikan parameter {id} pada alamat benar-benar berupa angka.
function bacaId(string $nilai): int
{
    if (!ctype_digit($nilai) || (int) $nilai < 1) {
        kirimGagal('ID pada alamat harus berupa angka.', 422);
    }

    return (int) $nilai;
}

function tolakMethod(): void
{
    kirimGagal('Method HTTP tidak diizinkan untuk endpoint ini.', 405);
}

function tolakMethodAtauEndpoint(): void
{
    kirimGagal('Endpoint atau method HTTP tidak sesuai.', 404);
}
