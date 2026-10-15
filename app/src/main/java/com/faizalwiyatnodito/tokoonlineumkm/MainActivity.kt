package com.faizalwiyatnodito.tokoonlineumkm

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.faizalwiyatnodito.tokoonlineumkm.data.repository.UserRepository
import com.faizalwiyatnodito.tokoonlineumkm.databinding.ActivityMainBinding
import com.faizalwiyatnodito.tokoonlineumkm.ui.dashboard.DashboardActivity
import com.faizalwiyatnodito.tokoonlineumkm.ui.login.LoginActivity

/**
 * Halaman pembuka (Splash) sekaligus pemeriksa sesi login.
 *
 * Alur :
 * 1. Aplikasi dibuka, tampil halaman Splash.
 * 2. Sesi login dibaca dari SharedPreferences melalui Repository.
 * 3. Sesi masih valid  -> langsung menuju Dashboard.
 *    Belum login / sesi tidak valid -> menuju halaman Login.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Halaman Splash ditampilkan sebentar, lalu sesi diperiksa.
        handler.postDelayed({ periksaSesiLogin() }, DURASI_SPLASH)
    }

    private fun periksaSesiLogin() {
        val repository = UserRepository(this)

        val tujuan = if (repository.sudahLogin()) {
            DashboardActivity::class.java
        } else {
            LoginActivity::class.java
        }

        startActivity(Intent(this, tujuan))
        finish()
    }

    override fun onDestroy() {
        // Menghentikan penundaan agar tidak berjalan saat halaman sudah ditutup.
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    companion object {
        private const val DURASI_SPLASH = 2500L
    }
}
