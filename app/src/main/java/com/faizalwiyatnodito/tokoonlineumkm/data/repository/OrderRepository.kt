package com.faizalwiyatnodito.tokoonlineumkm.data.repository

import android.util.Log
import com.faizalwiyatnodito.tokoonlineumkm.data.network.ApiConfig
import com.faizalwiyatnodito.tokoonlineumkm.data.network.ApiService
import com.faizalwiyatnodito.tokoonlineumkm.data.network.OrderListResponse
import java.io.IOException

// Menangani endpoint riwayat pesanan.
class OrderRepository {
    private val apiService: ApiService = ApiConfig.getApiService()

    suspend fun ambilRiwayat(idPengguna: Int): OrderListResponse {
        return try {
            val response = apiService.getOrders(idPengguna)

            if (response.isSuccessful) {
                response.body() ?: OrderListResponse(false, PesanApi.RESPONS_TIDAK_SESUAI)
            } else {
                val pesan = PesanApi.bacaPesanError(response.errorBody()?.string())
                OrderListResponse(false, pesan ?: PesanApi.GAGAL_MEMUAT)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Gagal terhubung ke server saat mengambil riwayat pesanan", e)
            OrderListResponse(false, PesanApi.KONEKSI)
        } catch (e: Exception) {
            Log.e(TAG, "Gagal membaca respons riwayat pesanan", e)
            OrderListResponse(false, PesanApi.RESPONS_TIDAK_SESUAI)
        }
    }

    companion object {
        private const val TAG = "OrderRepository"
    }
}
