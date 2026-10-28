package com.faizalwiyatnodito.tokoonlineumkm.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.faizalwiyatnodito.tokoonlineumkm.data.network.ApiConfig
import com.faizalwiyatnodito.tokoonlineumkm.data.network.ApiService
import com.faizalwiyatnodito.tokoonlineumkm.data.network.LoginResponse
import com.faizalwiyatnodito.tokoonlineumkm.data.network.RegisterResponse
import com.faizalwiyatnodito.tokoonlineumkm.data.network.User
import com.faizalwiyatnodito.tokoonlineumkm.data.network.UserResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class UserRepository(context: Context) {
    private val apiService: ApiService = ApiConfig.getApiService()

    private val preferences: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    suspend fun login(username: String, password: String): LoginResponse {
        return try {
            val response = apiService.login(username, password)

            if (response.isSuccessful) {
                response.body() ?: LoginResponse(false, PesanApi.RESPONS_TIDAK_SESUAI)
            } else {
                val pesan = PesanApi.bacaPesanError(response.errorBody()?.string())
                LoginResponse(false, pesan ?: PESAN_LOGIN_GAGAL)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Login gagal terhubung ke server", e)
            LoginResponse(false, PesanApi.KONEKSI)
        } catch (e: Exception) {
            Log.e(TAG, "Login gagal membaca respons server", e)
            LoginResponse(false, PesanApi.RESPONS_TIDAK_SESUAI)
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
                response.body() ?: RegisterResponse(false, PesanApi.RESPONS_TIDAK_SESUAI)
            } else {
                val pesan = PesanApi.bacaPesanError(response.errorBody()?.string())
                RegisterResponse(false, pesan ?: PESAN_REGISTER_GAGAL)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Register gagal terhubung ke server", e)
            RegisterResponse(false, PesanApi.KONEKSI)
        } catch (e: Exception) {
            Log.e(TAG, "Register gagal membaca respons server", e)
            RegisterResponse(false, PesanApi.RESPONS_TIDAK_SESUAI)
        }
    }

    // Mengambil data pengguna terbaru dari database lewat API, dipakai pada halaman Profil.
    suspend fun ambilProfil(id: Int): UserResponse {
        return try {
            val response = apiService.getUser(id)

            if (response.isSuccessful) {
                response.body() ?: UserResponse(false, PesanApi.RESPONS_TIDAK_SESUAI)
            } else {
                val pesan = PesanApi.bacaPesanError(response.errorBody()?.string())
                UserResponse(false, pesan ?: PesanApi.GAGAL_MEMUAT)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Gagal terhubung ke server saat mengambil profil", e)
            UserResponse(false, PesanApi.KONEKSI)
        } catch (e: Exception) {
            Log.e(TAG, "Gagal membaca respons profil", e)
            UserResponse(false, PesanApi.RESPONS_TIDAK_SESUAI)
        }
    }

    // Memperbarui profil (nickname + foto profil) lewat endpoint POST /api/users/{id}.
    // avatarBytes dan avatarMime boleh null kalau pengguna tidak mengganti fotonya.
    // Bila berhasil, sesi langsung diperbarui supaya seluruh aplikasi memakai data terbaru.
    suspend fun perbaruiProfil(
        id: Int,
        username: String,
        avatarBytes: ByteArray?,
        avatarMime: String?
    ): UserResponse {
        return try {
            val bodyUsername = username.toRequestBody("text/plain".toMediaTypeOrNull())

            val bagianAvatar = if (avatarBytes != null) {
                val mime = avatarMime ?: "image/jpeg"
                val isi = avatarBytes.toRequestBody(mime.toMediaTypeOrNull())
                val namaFile = "avatar." + when (mime) {
                    "image/png" -> "png"
                    "image/webp" -> "webp"
                    else -> "jpg"
                }
                MultipartBody.Part.createFormData("avatar", namaFile, isi)
            } else {
                null
            }

            val response = apiService.updateProfile(id, bodyUsername, bagianAvatar)

            if (response.isSuccessful) {
                val isi = response.body() ?: UserResponse(false, PesanApi.RESPONS_TIDAK_SESUAI)
                // Simpan data terbaru ke sesi agar halaman Profil ikut diperbarui.
                if (isi.status && isi.data != null) {
                    simpanSesi(isi.data)
                }
                isi
            } else {
                val pesan = PesanApi.bacaPesanError(response.errorBody()?.string())
                UserResponse(false, pesan ?: PESAN_UPDATE_GAGAL)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Gagal terhubung ke server saat memperbarui profil", e)
            UserResponse(false, PesanApi.KONEKSI)
        } catch (e: Exception) {
            Log.e(TAG, "Gagal membaca respons pembaruan profil", e)
            UserResponse(false, PesanApi.RESPONS_TIDAK_SESUAI)
        }
    }

    // Mengunduh foto profil dari server lalu mengubahnya menjadi Bitmap.
    // Mengembalikan null kalau path kosong atau gambar gagal diambil,
    // sehingga pemanggil bisa menampilkan avatar cadangan (huruf awal nama).
    suspend fun unduhAvatar(path: String?): Bitmap? {
        if (path.isNullOrBlank()) return null

        val url = if (path.startsWith("http")) path else ApiConfig.SERVER_URL + path.trimStart('/')

        return try {
            val response = apiService.unduhGambar(url)
            val bytes = response.body()?.bytes()
            if (response.isSuccessful && bytes != null) {
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Gagal mengunduh foto profil", e)
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
            .putString(KEY_AVATAR, user.avatar)
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
            noTlp = preferences.getString(KEY_NO_TLP, "").orEmpty(),
            avatar = preferences.getString(KEY_AVATAR, null)
        )
    }

    fun hapusSesi() {
        preferences.edit().clear().apply()
    }

    companion object {
        private const val TAG = "UserRepository"
        private const val PREF_NAME = "sesi_toko_online"
        private const val KEY_IS_LOGIN = "is_login"
        private const val KEY_ID = "id"
        private const val KEY_USERNAME = "username"
        private const val KEY_EMAIL = "email"
        private const val KEY_NO_TLP = "no_tlp"
        private const val KEY_AVATAR = "avatar"

        private const val PESAN_LOGIN_GAGAL = "Login gagal. Periksa kembali username dan password."
        private const val PESAN_REGISTER_GAGAL = "Registrasi gagal. Silakan coba lagi."
        private const val PESAN_UPDATE_GAGAL = "Gagal memperbarui profil. Silakan coba lagi."
    }
}
