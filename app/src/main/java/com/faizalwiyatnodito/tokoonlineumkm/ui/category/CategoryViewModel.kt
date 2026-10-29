package com.faizalwiyatnodito.tokoonlineumkm.ui.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faizalwiyatnodito.tokoonlineumkm.data.network.Product
import com.faizalwiyatnodito.tokoonlineumkm.data.repository.ProductRepository
import kotlinx.coroutines.launch

// Mengambil menu pada satu kategori lewat GET /api/products?category_id=.
class CategoryViewModel : ViewModel() {
    private val productRepository = ProductRepository()

    private val _daftarProduk = MutableLiveData<List<Product>>()
    val daftarProduk: LiveData<List<Product>> = _daftarProduk

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun muatProduk(idKategori: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val response = productRepository.ambilProduk(idKategori = idKategori)

            if (response.status) {
                _daftarProduk.value = response.data
            } else {
                _errorMessage.value = response.message
                _daftarProduk.value = emptyList()
            }

            _isLoading.value = false
        }
    }
}
