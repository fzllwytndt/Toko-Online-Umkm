package com.faizalwiyatnodito.tokoonlineumkm.ui.product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faizalwiyatnodito.tokoonlineumkm.data.network.Product
import com.faizalwiyatnodito.tokoonlineumkm.data.repository.CartRepository
import com.faizalwiyatnodito.tokoonlineumkm.data.repository.ProductRepository
import kotlinx.coroutines.launch

// Mengatur pengambilan satu produk berdasarkan ID yang dipilih pengguna,
// beserta status loading, berhasil, gagal, dan produk tidak ditemukan.
class ProductDetailViewModel : ViewModel() {
    private val productRepository = ProductRepository()
    private val cartRepository = CartRepository()

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _produk = MutableLiveData<Product?>()
    val produk: LiveData<Product?> = _produk

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _pesanKeranjang = MutableLiveData<String?>()
    val pesanKeranjang: LiveData<String?> = _pesanKeranjang

    fun muatDetail(idProduk: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val response = productRepository.ambilDetailProduk(idProduk)

            if (response.status && response.data != null) {
                _produk.value = response.data
            } else {
                // Pesan diambil dari API, termasuk "Produk tidak ditemukan."
                _produk.value = null
                _errorMessage.value = response.message
            }

            _isLoading.value = false
        }
    }

    // POST /api/carts memakai ID produk yang sedang dibuka.
    fun tambahKeKeranjang(idPengguna: Int) {
        val produkTampil = _produk.value ?: return

        viewModelScope.launch {
            val response = cartRepository.tambah(idPengguna, produkTampil.id)
            _pesanKeranjang.value = response.message
        }
    }

    fun pesanKeranjangSudahDitampilkan() {
        _pesanKeranjang.value = null
    }
}
