package com.faizalwiyatnodito.tokoonlineumkm.data.repository

import com.faizalwiyatnodito.tokoonlineumkm.data.network.SimpleResponse
import com.google.gson.Gson

// Kumpulan pesan dan pembaca error yang dipakai bersama oleh seluruh Repository,
// supaya pesan yang muncul di aplikasi selalu sama bentuknya.
object PesanApi {
    const val KONEKSI =
        "Koneksi internet bermasalah atau server tidak dapat diakses. Silakan coba lagi."
    const val RESPONS_TIDAK_SESUAI =
        "Respons dari server tidak sesuai. Silakan coba beberapa saat lagi."
    const val GAGAL_MEMUAT = "Data gagal dimuat. Silakan coba lagi."

    private val gson = Gson()

    // Backend selalu mengirim { "status": false, "message": "..." } ketika gagal,
    // jadi pesan aslinya dibaca dari sana supaya yang tampil bukan pesan buatan aplikasi.
    fun bacaPesanError(errorBody: String?): String? {
        if (errorBody.isNullOrBlank()) return null

        return try {
            gson.fromJson(errorBody, SimpleResponse::class.java)?.message?.takeIf { it.isNotBlank() }
        } catch (e: Exception) {
            null
        }
    }
}
