package com.faizalwiyatnodito.tokoonlineumkm.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.util.LruCache
import com.faizalwiyatnodito.tokoonlineumkm.data.network.ApiConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Pengunduh gambar sederhana yang dipakai bersama oleh foto menu dan foto profil,
// supaya kode pengunduhan gambar tidak ditulis berulang di beberapa halaman.
//
// Mengembalikan null ketika path kosong atau gambar gagal diambil, sehingga
// halaman pemanggil cukup menampilkan gambar pengganti.
object PemuatGambar {
    private const val TAG = "PemuatGambar"
    private const val UKURAN_CACHE_BYTE = 6 * 1024 * 1024

    // Gambar yang sudah diunduh disimpan sementara supaya daftar menu
    // tidak mengunduh ulang setiap kali baris digulirkan.
    private val cache = object : LruCache<String, Bitmap>(UKURAN_CACHE_BYTE) {
        override fun sizeOf(key: String, value: Bitmap): Int = value.byteCount
    }

    // Menyusun URL penuh dari nilai yang dikirim API. API boleh mengirim
    // URL penuh ("http://...") maupun path relatif ("uploads/products/a.png").
    fun urlPenuh(path: String?): String? {
        if (path.isNullOrBlank()) return null
        return if (path.startsWith("http")) path else ApiConfig.SERVER_URL + path.trimStart('/')
    }

    suspend fun muat(path: String?): Bitmap? {
        val url = urlPenuh(path) ?: return null

        cache.get(url)?.let { return it }

        return withContext(Dispatchers.IO) {
            try {
                val response = ApiConfig.getApiService().unduhGambar(url)
                val bytes = response.body()?.bytes()

                if (!response.isSuccessful || bytes == null) {
                    null
                } else {
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        ?.also { cache.put(url, it) }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Gagal mengunduh gambar: $url", e)
                null
            }
        }
    }
}
