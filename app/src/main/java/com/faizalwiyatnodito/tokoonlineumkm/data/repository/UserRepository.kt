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

class UserRepository(context: Context) {
    private val apiService: ApiService = ApiConfig.getApiService()
    private val gson = Gson()

    private val preferences: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    suspend fun login(username: String, password: String): LoginResponse {
        return try {
            val response = apiService.login(username, password)

            if (response.isSuccessful) {
                response.body() ?: LoginResponse(false, PESAN_RESPONS_TIDAK_SESUAI)
            } else {
                val pesan = bacaPesanError(response.errorBody()?.string())
                LoginResponse(false, pesan ?: PESAN_LOGIN_GAGAL)
            }
        } catch (e: IOException) {
            LoginResponse(false, PESAN_KONEKSI)
        } catch (e: Exception) {
            LoginResponse(false, PESAN_RESPONS_TIDAK_SESUAI)
        }
    }

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

    private fun bacaPesanError(errorBody: String?): String? {
        if (errorBody.isNullOrBlank()) return null
        return try {
            val hasil = gson.fromJson(errorBody, RegisterResponse::class.java)
            hasil?.message?.takeIf { it.isNotBlank() }
        } catch (e: Exception) {
            null
        }
    }

    fun simpanSesi(user: User) {
        preferences.edit()
            .putBoolean(KEY_IS_LOGIN, true)
            .putInt(KEY_ID, user.id)
            .putString(KEY_USERNAME, user.username)
            .putString(KEY_EMAIL, user.email)
            .putString(KEY_NO_TLP, user.noTlp)
            .apply()
    }

    fun sudahLogin(): Boolean {
        val isLogin = preferences.getBoolean(KEY_IS_LOGIN, false)
        val username = preferences.getString(KEY_USERNAME, "").orEmpty()
        val id = preferences.getInt(KEY_ID, 0)
        return isLogin && username.isNotBlank() && id != 0
    }

    fun ambilUser(): User {
        return User(
            id = preferences.getInt(KEY_ID, 0),
            username = preferences.getString(KEY_USERNAME, "").orEmpty(),
            email = preferences.getString(KEY_EMAIL, "").orEmpty(),
            noTlp = preferences.getString(KEY_NO_TLP, "").orEmpty()
        )
    }

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
