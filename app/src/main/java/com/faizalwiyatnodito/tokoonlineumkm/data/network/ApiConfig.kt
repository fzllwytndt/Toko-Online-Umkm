package com.faizalwiyatnodito.tokoonlineumkm.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Pengaturan koneksi ke REST API.
 * Di sini Retrofit dibuat satu kali lalu dipakai bersama oleh Repository.
 */
object ApiConfig {

    private const val BASE_URL = "https://pasarrakyatbangetayukulon.my.id/toko_online/"

    private val instance: ApiService by lazy {
        // Logging dipakai agar permintaan dan respons API terlihat di Logcat.
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // Batas waktu dipasang supaya aplikasi tidak menunggu terlalu lama
        // ketika koneksi internet bermasalah.
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    /** Mengambil ApiService yang siap dipakai. */
    fun getApiService(): ApiService = instance
}
