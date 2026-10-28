package com.faizalwiyatnodito.tokoonlineumkm.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiConfig {
    // Alamat backend REST API yang dijalankan di komputer sendiri pada port 3000.
    //
    // Alamat ini berbeda tergantung tempat aplikasi dijalankan:
    // - Android Emulator  : http://10.0.2.2:3000/api/   (10.0.2.2 = localhost komputer host)
    // - HP Android fisik  : http://<IP-lokal-komputer>:3000/api/  misalnya http://192.168.1.5:3000/api/
    // - Postman / browser di komputer yang sama : http://localhost:3000/api/
    //
    // Catatan: pada sebagian emulator alamat 10.0.2.2 tidak bisa dipakai karena
    // jaringan Wi-Fi dan jaringan seluler bawaan emulator berada pada subnet yang sama.
    // Kalau aplikasi menampilkan pesan koneksi gagal padahal backend sudah jalan,
    // ganti baris di bawah ini dengan IP lokal komputer, lalu daftarkan IP tersebut
    // pada res/xml/network_security_config.xml.
    // Alamat root server (tanpa /api). Dipakai untuk menyusun URL penuh foto profil,
    // karena file avatar dilayani di luar /api, contoh: http://10.0.2.2:3000/uploads/avatars/xxx.png
    const val SERVER_URL = "http://10.0.2.2:3000/"

    private const val BASE_URL = SERVER_URL + "api/"

    private val instance: ApiService by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

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

    fun getApiService(): ApiService = instance
}
