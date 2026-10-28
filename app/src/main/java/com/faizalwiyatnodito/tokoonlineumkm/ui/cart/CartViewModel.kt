package com.faizalwiyatnodito.tokoonlineumkm.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faizalwiyatnodito.tokoonlineumkm.data.network.CartData
import com.faizalwiyatnodito.tokoonlineumkm.data.repository.CartRepository
import kotlinx.coroutines.launch

class CartViewModel : ViewModel() {
    private val repository = CartRepository()

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _keranjang = MutableLiveData<CartData>()
    val keranjang: LiveData<CartData> = _keranjang

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _pesanAksi = MutableLiveData<String?>()
    val pesanAksi: LiveData<String?> = _pesanAksi

    fun muatKeranjang(idPengguna: Int) {
        viewModelScope.launch {
            _isLoading.value = true

            val response = repository.ambilKeranjang(idPengguna)

            if (response.status && response.data != null) {
                _keranjang.value = response.data
                _errorMessage.value = null
            } else {
                _keranjang.value = CartData()
                _errorMessage.value = response.message
            }

            _isLoading.value = false
        }
    }

    // PUT /api/carts/{id} lalu daftar dimuat ulang supaya total ikut diperbarui.
    fun ubahJumlah(idPengguna: Int, idItem: Int, jumlahBaru: Int) {
        viewModelScope.launch {
            val response = repository.ubahJumlah(idItem, jumlahBaru)

            if (response.status) {
                muatKeranjang(idPengguna)
            } else {
                _pesanAksi.value = response.message
            }
        }
    }

    // DELETE /api/carts/{id}
    fun hapusItem(idPengguna: Int, idItem: Int) {
        viewModelScope.launch {
            val response = repository.hapus(idItem)
            _pesanAksi.value = response.message

            if (response.status) {
                muatKeranjang(idPengguna)
            }
        }
    }

    fun pesanAksiSudahDitampilkan() {
        _pesanAksi.value = null
    }
}
