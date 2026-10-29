package com.faizalwiyatnodito.tokoonlineumkm.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

// Pengubah tampilan angka dan tanggal, dipakai bersama oleh beberapa halaman.
object Format {
    private val lokalIndonesia: Locale = Locale.forLanguageTag("id-ID")

    // 25000.0 -> "Rp. 25.000" (mengikuti penulisan harga pada desain aplikasi)
    fun rupiah(nilai: Double): String {
        val format = NumberFormat.getNumberInstance(lokalIndonesia)
        format.maximumFractionDigits = 0
        return "Rp. " + format.format(nilai)
    }

    // "2026-10-12 05:29:07" -> "12 Okt 2026"
    fun tanggalSingkat(teks: String): String {
        return try {
            val asal = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", lokalIndonesia)
            val tujuan = SimpleDateFormat("d MMM yyyy", lokalIndonesia)
            val tanggal = asal.parse(teks)

            if (tanggal == null) teks else tujuan.format(tanggal)
        } catch (e: Exception) {
            // Kalau format tanggal dari server berubah, tampilkan apa adanya.
            teks
        }
    }
}
