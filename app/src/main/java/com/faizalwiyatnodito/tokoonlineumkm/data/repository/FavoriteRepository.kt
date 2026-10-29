package com.faizalwiyatnodito.tokoonlineumkm.data.repository

import android.util.Log
import com.faizalwiyatnodito.tokoonlineumkm.data.network.ApiConfig
import com.faizalwiyatnodito.tokoonlineumkm.data.network.ApiService
import com.faizalwiyatnodito.tokoonlineumkm.data.network.FavoriteListResponse
import java.io.IOException

// Menangani endpoint favorit, dipakai tombol "Favorit" pada halaman Beranda.
class FavoriteRepository {
    private val apiService: ApiService = ApiConfig.getApiService()

    // GET /api/favorites?user_id=
    suspend fun ambilFavorit(idPengguna: Int): FavoriteListResponse {
        return try {
            val response = apiService.getFavorites(idPengguna)

            if (response.isSuccessful) {
                response.body() ?: FavoriteListResponse(false, PesanApi.RESPONS_TIDAK_SESUAI)
            } else {
                val pesan = PesanApi.bacaPesanError(response.errorBody()?.string())
                FavoriteListResponse(false, pesan ?: PesanApi.GAGAL_MEMUAT)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Gagal terhubung ke server saat mengambil favorit", e)
            FavoriteListResponse(false, PesanApi.KONEKSI)
        } catch (e: Exception) {
            Log.e(TAG, "Gagal membaca respons favorit", e)
            FavoriteListResponse(false, PesanApi.RESPONS_TIDAK_SESUAI)
        }
    }

    companion object {
        private const val TAG = "FavoriteRepository"
    }
}
