package com.faizalwiyatnodito.tokoonlineumkm.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.faizalwiyatnodito.tokoonlineumkm.data.network.Category
import com.faizalwiyatnodito.tokoonlineumkm.data.network.Product
import com.faizalwiyatnodito.tokoonlineumkm.data.repository.FavoriteRepository
import com.faizalwiyatnodito.tokoonlineumkm.data.repository.ProductRepository
import com.faizalwiyatnodito.tokoonlineumkm.ui.common.BarAtasViewModel
import kotlinx.coroutines.launch

// Mengatur seluruh data halaman Beranda: kategori, daftar menu,
// penyaringan per kategori, pencarian, daftar favorit, dan status tampilan.
class HomeViewModel : BarAtasViewModel() {
    private val productRepository = ProductRepository()
    private val favoriteRepository = FavoriteRepository()

    private var idPengguna = 0

    private val _daftarKategori = MutableLiveData<List<Category>>()
    val daftarKategori: LiveData<List<Category>> = _daftarKategori

    private val _daftarProduk = MutableLiveData<List<Product>>()
    val daftarProduk: LiveData<List<Product>> = _daftarProduk

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    // null berarti chip "Semua" yang sedang dipilih.
    private val _idKategoriTerpilih = MutableLiveData<Int?>(null)
    val idKategoriTerpilih: LiveData<Int?> = _idKategoriTerpilih

    private val _modeFavorit = MutableLiveData(false)
    val modeFavorit: LiveData<Boolean> = _modeFavorit

    private val _kataKunci = MutableLiveData("")
    val kataKunci: LiveData<String> = _kataKunci

    // Dipanggil sekali ketika halaman pertama kali dibuka.
    fun mulai(idPengguna: Int) {
        this.idPengguna = idPengguna
        muatKategori()
        muatProduk()
    }

    fun muatKategori() {
        viewModelScope.launch {
            val response = productRepository.ambilKategori()

            // Kategori hanya pelengkap, jadi kegagalannya tidak menutup daftar menu.
            _daftarKategori.value = if (response.status) response.data else emptyList()
        }
    }

    fun pilihKategori(idKategori: Int?) {
        _modeFavorit.value = false
        _idKategoriTerpilih.value = idKategori
        muatProduk()
    }

    fun cari(kata: String) {
        if (_kataKunci.value == kata) return

        _modeFavorit.value = false
        _kataKunci.value = kata
        muatProduk()
    }

    // Tombol "Favorit" menampilkan menu favorit pengguna dari GET /api/favorites.
    fun ubahModeFavorit() {
        val aktifkan = _modeFavorit.value != true

        // Kata kunci dan kategori dibersihkan lebih dulu, baru status favorit diubah.
        // Halaman mengosongkan kolom pencarian begitu status favorit menyala,
        // dan pengosongan itu tidak boleh menghidupkan kembali daftar menu biasa.
        if (aktifkan) {
            _idKategoriTerpilih.value = null
            _kataKunci.value = ""
        }

        _modeFavorit.value = aktifkan
        muatProduk()
    }

    fun muatProduk() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            if (_modeFavorit.value == true) {
                ambilFavorit()
            } else {
                ambilProduk()
            }

            _isLoading.value = false
        }
    }

    private suspend fun ambilProduk() {
        val response = productRepository.ambilProduk(
            idKategori = _idKategoriTerpilih.value,
            kataKunci = _kataKunci.value
        )

        if (response.status) {
            _daftarProduk.value = response.data
        } else {
            _errorMessage.value = response.message
            _daftarProduk.value = emptyList()
        }
    }

    private suspend fun ambilFavorit() {
        val response = favoriteRepository.ambilFavorit(idPengguna)

        if (response.status) {
            // Bentuk data favorit berbeda dari produk, jadi diubah dulu
            // supaya bisa memakai kartu menu yang sama.
            _daftarProduk.value = response.data.map { it.keProduk() }
        } else {
            _errorMessage.value = response.message
            _daftarProduk.value = emptyList()
        }
    }
}
