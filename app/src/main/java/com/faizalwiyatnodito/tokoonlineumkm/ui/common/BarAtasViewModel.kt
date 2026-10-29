package com.faizalwiyatnodito.tokoonlineumkm.ui.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faizalwiyatnodito.tokoonlineumkm.data.repository.CartRepository
import kotlinx.coroutines.launch

// ViewModel dasar untuk halaman yang memakai bar atas.
// Jumlah item keranjang dibutuhkan halaman Beranda dan Rekomendasi,
// jadi pengambilannya ditulis satu kali di sini.
abstract class BarAtasViewModel : ViewModel() {
    private val cartRepository = CartRepository()

    private val _jumlahKeranjang = MutableLiveData(0)
    val jumlahKeranjang: LiveData<Int> = _jumlahKeranjang

    // Kegagalan tidak ditampilkan sebagai pesan error karena jumlah keranjang
    // hanyalah keterangan tambahan pada bar atas, bukan isi utama halaman.
    fun muatJumlahKeranjang(idPengguna: Int) {
        if (idPengguna <= 0) return

        viewModelScope.launch {
            val response = cartRepository.ambilKeranjang(idPengguna)
            _jumlahKeranjang.value = if (response.status) response.data?.totalItem ?: 0 else 0
        }
    }
}
