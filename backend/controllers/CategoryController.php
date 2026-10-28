<?php

require_once __DIR__ . '/../config/response.php';
require_once __DIR__ . '/../models/Category.php';

class CategoryController
{
    private Category $category;

    public function __construct()
    {
        $this->category = new Category();
    }

    // GET /api/categories
    public function daftar(): void
    {
        $kategori = $this->category->semua();

        foreach ($kategori as $urutan => $satuKategori) {
            $kategori[$urutan]['id'] = (int) $satuKategori['id'];
            $kategori[$urutan]['sort_order'] = (int) $satuKategori['sort_order'];
        }

        kirimBerhasil('Daftar kategori berhasil diambil.', $kategori);
    }
}
