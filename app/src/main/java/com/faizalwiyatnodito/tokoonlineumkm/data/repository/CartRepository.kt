package com.faizalwiyatnodito.tokoonlineumkm.data.repository

import android.util.Log
import com.faizalwiyatnodito.tokoonlineumkm.data.network.ApiConfig
import com.faizalwiyatnodito.tokoonlineumkm.data.network.ApiService
import com.faizalwiyatnodito.tokoonlineumkm.data.network.CartResponse
import com.faizalwiyatnodito.tokoonlineumkm.data.network.SimpleResponse
import retrofit2.Response
import java.io.IOException

// Menangani endpoint keranjang: membaca isi keranjang (GET), menambah (POST),
// mengubah jumlah (PUT), dan menghapus item (DELETE).
class CartRepository {
    private val apiService: ApiService = ApiConfig.getApiService()

    suspend fun ambilKeranjang(idPengguna: Int): CartResponse {
        return try {
            val response = apiService.getCarts(idPengguna)

            if (response.isSuccessful) {
                response.body() ?: CartResponse(false, PesanApi.RESPONS_TIDAK_SESUAI)
            } else {
                val pesan = PesanApi.bacaPesanError(response.errorBody()?.string())
                CartResponse(false, pesan ?: PesanApi.GAGAL_MEMUAT)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Gagal terhubung ke server saat mengambil keranjang", e)
            CartResponse(false, PesanApi.KONEKSI)
        } catch (e: Exception) {
            Log.e(TAG, "Gagal membaca respons keranjang", e)
            CartResponse(false, PesanApi.RESPONS_TIDAK_SESUAI)
        }
    }

    suspend fun tambah(idPengguna: Int, idProduk: Int, jumlah: Int = 1): SimpleResponse =
        jalankan("menambah produk ke keranjang") { apiService.addToCart(idPengguna, idProduk, jumlah) }

    suspend fun ubahJumlah(idItem: Int, jumlah: Int): SimpleResponse =
        jalankan("mengubah jumlah item keranjang") { apiService.updateCart(idItem, jumlah) }

    suspend fun hapus(idItem: Int): SimpleResponse =
        jalankan("menghapus item keranjang") { apiService.deleteCart(idItem) }

    // Ketiga operasi tulis di atas penanganannya sama persis, jadi dikumpulkan di sini
    // supaya kodenya tidak ditulis berulang kali.
    private suspend fun jalankan(
        keterangan: String,
        panggilApi: suspend () -> Response<SimpleResponse>
    ): SimpleResponse {
        return try {
            val response = panggilApi()

            if (response.isSuccessful) {
                response.body() ?: SimpleResponse(false, PesanApi.RESPONS_TIDAK_SESUAI)
            } else {
                val pesan = PesanApi.bacaPesanError(response.errorBody()?.string())
                SimpleResponse(false, pesan ?: PesanApi.GAGAL_MEMUAT)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Gagal terhubung ke server saat $keterangan", e)
            SimpleResponse(false, PesanApi.KONEKSI)
        } catch (e: Exception) {
            Log.e(TAG, "Gagal membaca respons saat $keterangan", e)
            SimpleResponse(false, PesanApi.RESPONS_TIDAK_SESUAI)
        }
    }

    companion object {
        private const val TAG = "CartRepository"
    }
}
