package com.faizalwiyatnodito.tokoonlineumkm.data.network

import com.faizalwiyatnodito.tokoonlineumkm.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiConfig {
    // Alamat root server (tanpa /api), contoh "http://10.0.2.2:3000/".
    // Dipakai juga untuk menyusun URL penuh foto menu dan foto profil,
    // karena berkas gambar dilayani di luar /api,
    // contoh: http://10.0.2.2:3000/uploads/produk/miso-ramen.jpg
    //
    // Nilainya TIDAK ditulis di sini, melainkan dibaca dari local.properties
    // lewat buildConfigField pada app/build.gradle.kts. Alasannya alamat ini
    // berbeda pada tiap komputer dan berubah setiap ganti jaringan:
    // - Android Emulator  : http://10.0.2.2:3000/  (dipakai kalau tidak diatur)
    // - HP Android fisik  : http://<IP-lokal-komputer>:3000/
    //
    // Cara menggantinya, tambahkan satu baris pada local.properties:
    //   server.url=http://192.168.1.5:3000/
    // lalu Sync/Rebuild. Tidak ada berkas kode yang perlu disentuh.
    val SERVER_URL: String = BuildConfig.SERVER_URL

    private val BASE_URL = SERVER_URL + "api/"

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
