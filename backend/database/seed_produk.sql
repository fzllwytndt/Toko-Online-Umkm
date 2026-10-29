-- Melengkapi katalog menu pada tabel `products`.
--
-- Sebelumnya kolom `image` dan `description` masih kosong untuk seluruh produk,
-- dan kategori "Promo Spesial" belum memiliki satu pun menu. Berkas ini mengisi
-- keduanya sehingga aplikasi Android menampilkan foto dan deskripsi yang sebenarnya
-- berasal dari database, bukan nilai yang ditulis di dalam kode.
--
-- Cara menjalankan:
--   mysql -u root db_toko_online < backend/database/seed_produk.sql
--
-- Berkas ini aman dijalankan berulang kali:
-- UPDATE memakai ID yang sudah ada, dan INSERT memakai nama menu sebagai penanda
-- supaya menu promo tidak terduplikasi.
--
-- Nilai kolom `image` berupa path relatif terhadap folder `backend/public`,
-- sama seperti pola yang dipakai foto profil pada Tugas 3. Contoh alamat lengkapnya:
--   http://10.0.2.2:3000/uploads/produk/miso-ramen.jpg

-- ---------------------------------------------------------------------------
-- 1. Foto dan deskripsi untuk menu yang sudah ada
-- ---------------------------------------------------------------------------

UPDATE products SET
    image = 'uploads/produk/paket-chiken-katsu.jpg',
    description = 'Potongan ayam katsu berbalut tepung panko yang digoreng sampai renyah, disajikan bersama nasi hangat dan saus katsu khas EZPICK.'
WHERE id = 1;

UPDATE products SET
    image = 'uploads/produk/paket-ayam-geprek.jpg',
    description = 'Ayam goreng tepung yang digeprek bersama sambal bawang pedas, lengkap dengan nasi hangat dan lalapan segar.'
WHERE id = 2;

UPDATE products SET
    image = 'uploads/produk/miso-ramen.jpg',
    description = 'Ramen berkuah miso yang gurih dengan topping telur rebus, jagung manis, dan irisan daun bawang.'
WHERE id = 3;

UPDATE products SET
    image = 'uploads/produk/teh-tawar.jpg',
    description = 'Teh seduh tanpa gula yang disajikan hangat, cocok menemani menu bercita rasa pedas.'
WHERE id = 4;

UPDATE products SET
    image = 'uploads/produk/es-teh-manis.jpg',
    description = 'Teh manis dingin yang diseduh setiap hari, disajikan dengan es batu dan tingkat kemanisan yang pas.'
WHERE id = 5;

UPDATE products SET
    image = 'uploads/produk/coffe-latte.jpg',
    description = 'Espresso yang dipadukan dengan susu segar, menghasilkan rasa kopi yang lembut dan tidak terlalu pahit.'
WHERE id = 6;

UPDATE products SET
    image = 'uploads/produk/katsu-curry.jpg',
    description = 'Nasi hangat dengan siraman kari Jepang yang kental dan potongan katsu renyah di atasnya.'
WHERE id = 7;

UPDATE products SET
    image = 'uploads/produk/katsu-sandwich.jpg',
    description = 'Roti tawar lembut berisi katsu renyah, selada segar, dan saus mayones gurih. Praktis untuk dibawa.'
WHERE id = 8;

-- ---------------------------------------------------------------------------
-- 2. Menu untuk kategori "Promo Spesial"
-- ---------------------------------------------------------------------------
-- category_id diambil dari tabel categories supaya tidak bergantung pada ID tetap.

INSERT INTO products (category_id, name, price, image, is_best_seller, is_available, description)
SELECT c.id,
       'Paket Hemat Berdua',
       35000.00,
       'uploads/produk/paket-hemat-berdua.jpg',
       0,
       1,
       'Dua Paket Ayam Geprek dan dua Es Teh Manis dalam satu paket, pas untuk makan berdua.'
FROM categories c
WHERE c.name = 'Promo Spesial'
  AND NOT EXISTS (SELECT 1 FROM products p WHERE p.name = 'Paket Hemat Berdua');

INSERT INTO products (category_id, name, price, image, is_best_seller, is_available, description)
SELECT c.id,
       'Promo Geprek + Es Teh',
       18000.00,
       'uploads/produk/promo-geprek-es-teh.jpg',
       0,
       1,
       'Satu Paket Ayam Geprek dan satu Es Teh Manis dengan harga lebih hemat dibanding membeli terpisah.'
FROM categories c
WHERE c.name = 'Promo Spesial'
  AND NOT EXISTS (SELECT 1 FROM products p WHERE p.name = 'Promo Geprek + Es Teh');

INSERT INTO products (category_id, name, price, image, is_best_seller, is_available, description)
SELECT c.id,
       'Paket Katsu Komplit',
       28000.00,
       'uploads/produk/paket-katsu-komplit.jpg',
       0,
       1,
       'Paket Chiken Katsu lengkap dengan Es Teh Manis, pilihan praktis untuk sekali makan.'
FROM categories c
WHERE c.name = 'Promo Spesial'
  AND NOT EXISTS (SELECT 1 FROM products p WHERE p.name = 'Paket Katsu Komplit');
