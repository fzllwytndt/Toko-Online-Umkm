package com.faizalwiyatnodito.tokoonlineumkm.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faizalwiyatnodito.tokoonlineumkm.data.network.Order
import com.faizalwiyatnodito.tokoonlineumkm.data.repository.OrderRepository
import kotlinx.coroutines.launch

class HistoryViewModel : ViewModel() {
    private val repository = OrderRepository()

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _daftarPesanan = MutableLiveData<List<Order>>()
    val daftarPesanan: LiveData<List<Order>> = _daftarPesanan

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun muatRiwayat(idPengguna: Int) {
        viewModelScope.launch {
            _isLoading.value = true

            val response = repository.ambilRiwayat(idPengguna)

            if (response.status) {
                _daftarPesanan.value = response.data
                _errorMessage.value = null
            } else {
                _daftarPesanan.value = emptyList()
                _errorMessage.value = response.message
            }

            _isLoading.value = false
        }
    }
}
