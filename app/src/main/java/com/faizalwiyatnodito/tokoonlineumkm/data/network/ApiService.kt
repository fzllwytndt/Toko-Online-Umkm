package com.faizalwiyatnodito.tokoonlineumkm.data.network

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Daftar endpoint REST API yang dipakai untuk proses autentikasi.
 * Backend menerima data dalam bentuk form (x-www-form-urlencoded),
 * karena itu dipakai anotasi @FormUrlEncoded dan @Field.
 *
 * Hasilnya dibungkus Response<T> supaya pesan kesalahan dari server
 * (kode 400 dan 401) tetap bisa dibaca oleh Repository.
 */
interface ApiService {

    /** POST auth/login : memeriksa username dan password pengguna. */
    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    /** POST auth/register : mendaftarkan akun pengguna baru. */
    @FormUrlEncoded
    @POST("auth/register")
    suspend fun register(
        @Field("username") username: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("no_tlp") noTlp: String
    ): Response<RegisterResponse>
}
