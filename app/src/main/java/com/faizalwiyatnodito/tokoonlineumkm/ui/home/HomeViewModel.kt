package com.faizalwiyatnodito.tokoonlineumkm.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faizalwiyatnodito.tokoonlineumkm.data.network.Product
import com.faizalwiyatnodito.tokoonlineumkm.data.repository.CartRepository
import com.faizalwiyatnodito.tokoonlineumkm.data.repository.ProductRepository
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val productRepository = ProductRepository()
    private val cartRepository = CartRepository()

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _daftarProduk = MutableLiveData<List<Product>>()
    val daftarProduk: LiveData<List<Product>> = _daftarProduk

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _pesanKeranjang = MutableLiveData<String?>()
    val pesanKeranjang: LiveData<String?> = _pesanKeranjang

    fun muatProduk() {
        viewModelScope.launch {
            _isLoading.value = true

            val response = productRepository.ambilProduk()

            if (response.status) {
                _daftarProduk.value = response.data
                _errorMessage.value = null
            } else {
                _daftarProduk.value = emptyList()
                _errorMessage.value = response.message
            }

            _isLoading.value = false
        }
    }

    // Menambahkan produk ke keranjang lewat endpoint POST /api/carts.
    fun tambahKeKeranjang(idPengguna: Int, produk: Product) {
        viewModelScope.launch {
            val response = cartRepository.tambah(idPengguna, produk.id)
            _pesanKeranjang.value = response.message
        }
    }

    fun pesanKeranjangSudahDitampilkan() {
        _pesanKeranjang.value = null
    }
}
