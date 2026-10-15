package com.faizalwiyatnodito.tokoonlineumkm.data.network

import com.google.gson.annotations.SerializedName

/**
 * MODEL
 * Berisi struktur data pengguna dan bentuk respons yang dikirim oleh REST API.
 *
 * Contoh respons Login berhasil :
 * {
 *   "status": true,
 *   "message": "Login berhasil.",
 *   "data": { "id": 4, "username": "probe", "email": "probe@mail.com", "no_tlp": "08123" }
 * }
 *
 * Contoh respons gagal :
 * { "status": false, "message": "Username atau Password salah." }
 */

/** Data pengguna yang dikirim API setelah Login berhasil. */
data class User(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("username")
    val username: String = "",

    @SerializedName("email")
    val email: String = "",

    @SerializedName("no_tlp")
    val noTlp: String = ""
)

/** Respons endpoint auth/login. */
data class LoginResponse(
    @SerializedName("status")
    val status: Boolean = false,

    @SerializedName("message")
    val message: String = "",

    @SerializedName("data")
    val data: User? = null
)

/** Respons endpoint auth/register. */
data class RegisterResponse(
    @SerializedName("status")
    val status: Boolean = false,

    @SerializedName("message")
    val message: String = ""
)
