package com.faizalwiyatnodito.tokoonlineumkm.ui.recommendation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.faizalwiyatnodito.tokoonlineumkm.data.network.Product
import com.faizalwiyatnodito.tokoonlineumkm.data.repository.ProductRepository
import com.faizalwiyatnodito.tokoonlineumkm.ui.common.BarAtasViewModel
import kotlinx.coroutines.launch

// Mengatur dua bagian halaman Rekomendasi:
// - "Best Seller" memakai GET /api/products?best_seller=1
// - "Harga Terjangkau" diurutkan dari data GET /api/products yang sama,
//   bukan daftar buatan di dalam kode.
class RecommendationViewModel : BarAtasViewModel() {
    private val productRepository = ProductRepository()

    private val _bestSeller = MutableLiveData<List<Product>>()
    val bestSeller: LiveData<List<Product>> = _bestSeller

    private val _hargaTerjangkau = MutableLiveData<List<Product>>()
    val hargaTerjangkau: LiveData<List<Product>> = _hargaTerjangkau

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private var kataKunci = ""

    fun muatRekomendasi() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val responseBestSeller = productRepository.ambilProduk(
                kataKunci = kataKunci,
                hanyaBestSeller = true
            )
            val responseSemua = productRepository.ambilProduk(kataKunci = kataKunci)

            if (responseBestSeller.status && responseSemua.status) {
                _bestSeller.value = responseBestSeller.data
                _hargaTerjangkau.value = pilihTermurah(responseSemua.data)
            } else {
                _errorMessage.value =
                    if (!responseBestSeller.status) responseBestSeller.message
                    else responseSemua.message
                _bestSeller.value = emptyList()
                _hargaTerjangkau.value = emptyList()
            }

            _isLoading.value = false
        }
    }

    fun cari(kata: String) {
        if (kataKunci == kata) return

        kataKunci = kata
        muatRekomendasi()
    }

    // Menu termurah yang stoknya masih ada dan belum tampil pada bagian Best Seller.
    private fun pilihTermurah(semuaProduk: List<Product>): List<Product> {
        return semuaProduk
            .filter { it.isAvailable && !it.isBestSeller }
            .sortedBy { it.price }
            .take(BATAS_TERJANGKAU)
    }

    companion object {
        private const val BATAS_TERJANGKAU = 4
    }
}
