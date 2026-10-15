package com.faizalwiyatnodito.tokoonlineumkm.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.faizalwiyatnodito.tokoonlineumkm.data.network.ApiConfig
import com.faizalwiyatnodito.tokoonlineumkm.data.network.ApiService
import com.faizalwiyatnodito.tokoonlineumkm.data.network.LoginResponse
import com.faizalwiyatnodito.tokoonlineumkm.data.network.RegisterResponse
import com.faizalwiyatnodito.tokoonlineumkm.data.network.User
import com.google.gson.Gson
import java.io.IOException

/**
 * REPOSITORY
 * Penghubung antara ViewModel dan REST API.
 *
 * Tugasnya :
 * 1. Mengirim permintaan Login dan Register ke API.
 * 2. Menangkap kesalahan (koneksi putus, server bermasalah, format respons salah).
 * 3. Meneruskan hasilnya ke ViewModel dalam bentuk yang sudah rapi.
 * 4. Menyimpan dan membaca sesi login pada SharedPreferences.
 *
 * Dengan cara ini ViewModel tidak perlu berhubungan langsung dengan API.
 */
class UserRepository(context: Context) {

    private val apiService: ApiService = ApiConfig.getApiService()
    private val gson = Gson()

    private val preferences: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    // ---------------------------------------------------------------------
    // Bagian REST API
    // ---------------------------------------------------------------------

    /**
     * Mengirim permintaan Login ke API.
     * Jika terjadi kesalahan, hasilnya tetap berupa LoginResponse
     * dengan status false dan pesan yang mudah dipahami.
     */
    suspend fun login(username: String, password: String): LoginResponse {
        return try {
            val response = apiService.login(username, password)

            if (response.isSuccessful) {
                // Respons berhasil (kode 200)
                response.body() ?: LoginResponse(false, PESAN_RESPONS_TIDAK_SESUAI)
            } else {
                // Respons gagal (kode 400 / 401), pesan asli server dibaca dari errorBody
                val pesan = bacaPesanError(response.errorBody()?.string())
                LoginResponse(false, pesan ?: PESAN_LOGIN_GAGAL)
            }
        } catch (e: IOException) {
            // Tidak ada internet atau server tidak dapat dijangkau
            LoginResponse(false, PESAN_KONEKSI)
        } catch (e: Exception) {
            // Format respons tidak sesuai atau kesalahan lain
            LoginResponse(false, PESAN_RESPONS_TIDAK_SESUAI)
        }
    }

    /**
     * Mengirim permintaan Register ke API.
     * Field yang dikirim disesuaikan dengan kebutuhan endpoint backend :
     * username, email, password, dan no_tlp.
     */
    suspend fun register(
        username: String,
        email: String,
        password: String,
        noTlp: String
    ): RegisterResponse {
        return try {
            val response = apiService.register(username, email, password, noTlp)

            if (response.isSuccessful) {
                response.body() ?: RegisterResponse(false, PESAN_RESPONS_TIDAK_SESUAI)
            } else {
                val pesan = bacaPesanError(response.errorBody()?.string())
                RegisterResponse(false, pesan ?: PESAN_REGISTER_GAGAL)
            }
        } catch (e: IOException) {
            RegisterResponse(false, PESAN_KONEKSI)
        } catch (e: Exception) {
            RegisterResponse(false, PESAN_RESPONS_TIDAK_SESUAI)
        }
    }

    /**
     * Membaca pesan kesalahan dari body respons API.
     * Mengembalikan null bila body kosong atau bukan JSON yang diharapkan.
     */
    private fun bacaPesanError(errorBody: String?): String? {
        if (errorBody.isNullOrBlank()) return null
        return try {
            val hasil = gson.fromJson(errorBody, RegisterResponse::class.java)
            hasil?.message?.takeIf { it.isNotBlank() }
        } catch (e: Exception) {
            null
        }
    }

    // ---------------------------------------------------------------------
    // Bagian sesi login (SharedPreferences)
    // ---------------------------------------------------------------------

    /**
     * Menyimpan sesi login setelah autentikasi berhasil.
     * Password pengguna tidak pernah disimpan di sini.
     */
    fun simpanSesi(user: User) {
        preferences.edit()
            .putBoolean(KEY_IS_LOGIN, true)
            .putInt(KEY_ID, user.id)
            .putString(KEY_USERNAME, user.username)
            .putString(KEY_EMAIL, user.email)
            .putString(KEY_NO_TLP, user.noTlp)
            .apply()
    }

    /**
     * Memeriksa apakah sesi login masih berlaku.
     * Sesi dianggap tidak valid bila penandanya false atau data pengguna hilang.
     */
    fun sudahLogin(): Boolean {
        val isLogin = preferences.getBoolean(KEY_IS_LOGIN, false)
        val username = preferences.getString(KEY_USERNAME, "").orEmpty()
        val id = preferences.getInt(KEY_ID, 0)
        return isLogin && username.isNotBlank() && id != 0
    }

    /** Mengambil data pengguna yang tersimpan pada sesi. */
    fun ambilUser(): User {
        return User(
            id = preferences.getInt(KEY_ID, 0),
            username = preferences.getString(KEY_USERNAME, "").orEmpty(),
            email = preferences.getString(KEY_EMAIL, "").orEmpty(),
            noTlp = preferences.getString(KEY_NO_TLP, "").orEmpty()
        )
    }

    /** Menghapus sesi login, dipakai ketika pengguna keluar dari aplikasi. */
    fun hapusSesi() {
        preferences.edit().clear().apply()
    }

    companion object {
        private const val PREF_NAME = "sesi_toko_online"
        private const val KEY_IS_LOGIN = "is_login"
        private const val KEY_ID = "id"
        private const val KEY_USERNAME = "username"
        private const val KEY_EMAIL = "email"
        private const val KEY_NO_TLP = "no_tlp"

        private const val PESAN_KONEKSI =
            "Koneksi internet bermasalah atau server tidak dapat diakses. Silakan coba lagi."
        private const val PESAN_RESPONS_TIDAK_SESUAI =
            "Respons dari server tidak sesuai. Silakan coba beberapa saat lagi."
        private const val PESAN_LOGIN_GAGAL = "Login gagal. Periksa kembali username dan password."
        private const val PESAN_REGISTER_GAGAL = "Registrasi gagal. Silakan coba lagi."
    }
}
