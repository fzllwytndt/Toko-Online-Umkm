package com.faizalwiyatnodito.tokoonlineumkm.ui.register

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.faizalwiyatnodito.tokoonlineumkm.databinding.ActivityRegisterBinding
import com.google.android.material.snackbar.Snackbar

/**
 * VIEW
 * Menampilkan form Register dan menerima input pengguna.
 * Validasi dan pengiriman data dikerjakan oleh RegisterViewModel.
 */
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        aturInsets()

        aturTombol()
        amatiViewModel()
    }

    /** Memberi jarak aman agar tampilan tidak tertutup status bar dan navigation bar. */
    private fun aturInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun aturTombol() {
        // Tombol Submit : mengirim seluruh input ke ViewModel
        binding.btnSubmit.setOnClickListener {
            viewModel.register(
                username = binding.etUsername.text.toString(),
                email = binding.etEmail.text.toString(),
                password = binding.etPassword.text.toString(),
                konfirmasiPassword = binding.etKonfirmasiPassword.text.toString(),
                noTlp = binding.etNoTlp.text.toString()
            )
        }

        // Navigasi kembali ke halaman Login
        binding.tvMasuk.setOnClickListener {
            finish()
        }
    }

    private fun amatiViewModel() {
        // Status loading : tombol dinonaktifkan selama permintaan diproses
        viewModel.isLoading.observe(this) { sedangLoading ->
            binding.progressBar.visibility = if (sedangLoading) View.VISIBLE else View.GONE
            binding.btnSubmit.isEnabled = !sedangLoading
            binding.tvMasuk.isEnabled = !sedangLoading
        }

        // Pesan validasi ditampilkan di bawah kolom yang bermasalah
        viewModel.usernameError.observe(this) { binding.tilUsername.error = it }
        viewModel.emailError.observe(this) { binding.tilEmail.error = it }
        viewModel.passwordError.observe(this) { binding.tilPassword.error = it }
        viewModel.konfirmasiPasswordError.observe(this) {
            binding.tilKonfirmasiPassword.error = it
        }
        viewModel.noTlpError.observe(this) { binding.tilNoTlp.error = it }

        // Pesan gagal dari API, misalnya "Username atau Email sudah terdaftar."
        viewModel.errorMessage.observe(this) { pesan ->
            if (!pesan.isNullOrBlank()) {
                tampilkanPesan(pesan)
                viewModel.pesanErrorSudahDitampilkan()
            }
        }

        // Register berhasil : tampilkan pesan lalu kembali ke halaman Login
        viewModel.registerBerhasil.observe(this) { pesan ->
            if (!pesan.isNullOrBlank()) {
                // Toast dipakai di sini karena halaman langsung kembali ke Login
                Toast.makeText(this, pesan, Toast.LENGTH_LONG).show()
                viewModel.pesanBerhasilSudahDitampilkan()
                finish()
            }
        }
    }

    /** Menampilkan pesan gagal di bagian bawah layar selama pengguna masih di halaman ini. */
    private fun tampilkanPesan(pesan: String) {
        Snackbar.make(binding.root, pesan, Snackbar.LENGTH_LONG).show()
    }
}
