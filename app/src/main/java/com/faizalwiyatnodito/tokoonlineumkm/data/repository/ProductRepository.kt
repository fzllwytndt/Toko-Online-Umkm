package com.faizalwiyatnodito.tokoonlineumkm.data.repository

import android.util.Log
import com.faizalwiyatnodito.tokoonlineumkm.data.network.ApiConfig
import com.faizalwiyatnodito.tokoonlineumkm.data.network.ApiService
import com.faizalwiyatnodito.tokoonlineumkm.data.network.CategoryListResponse
import com.faizalwiyatnodito.tokoonlineumkm.data.network.ProductListResponse
import java.io.IOException

// Satu-satunya bagian yang berhubungan langsung dengan endpoint produk dan kategori.
// ViewModel cukup memanggil fungsi di sini tanpa tahu urusan Retrofit.
class ProductRepository {
    private val apiService: ApiService = ApiConfig.getApiService()

    suspend fun ambilProduk(idKategori: Int? = null, kataKunci: String? = null): ProductListResponse {
        return try {
            val response = apiService.getProducts(idKategori, kataKunci)

            if (response.isSuccessful) {
                response.body() ?: ProductListResponse(false, PesanApi.RESPONS_TIDAK_SESUAI)
            } else {
                val pesan = PesanApi.bacaPesanError(response.errorBody()?.string())
                ProductListResponse(false, pesan ?: PesanApi.GAGAL_MEMUAT)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Gagal terhubung ke server saat mengambil produk", e)
            ProductListResponse(false, PesanApi.KONEKSI)
        } catch (e: Exception) {
            Log.e(TAG, "Gagal membaca respons produk", e)
            ProductListResponse(false, PesanApi.RESPONS_TIDAK_SESUAI)
        }
    }

    suspend fun ambilKategori(): CategoryListResponse {
        return try {
            val response = apiService.getCategories()

            if (response.isSuccessful) {
                response.body() ?: CategoryListResponse(false, PesanApi.RESPONS_TIDAK_SESUAI)
            } else {
                val pesan = PesanApi.bacaPesanError(response.errorBody()?.string())
                CategoryListResponse(false, pesan ?: PesanApi.GAGAL_MEMUAT)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Gagal terhubung ke server saat mengambil kategori", e)
            CategoryListResponse(false, PesanApi.KONEKSI)
        } catch (e: Exception) {
            Log.e(TAG, "Gagal membaca respons kategori", e)
            CategoryListResponse(false, PesanApi.RESPONS_TIDAK_SESUAI)
        }
    }

    companion object {
        private const val TAG = "ProductRepository"
    }
}
