package com.faizalwiyatnodito.tokoonlineumkm.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.faizalwiyatnodito.tokoonlineumkm.R
import com.faizalwiyatnodito.tokoonlineumkm.databinding.ActivityLoginBinding
import com.faizalwiyatnodito.tokoonlineumkm.ui.dashboard.DashboardActivity
import com.faizalwiyatnodito.tokoonlineumkm.ui.register.RegisterActivity
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        aturInsets()

        aturTombol()
        amatiViewModel()
    }

    private fun aturInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun aturTombol() {
        binding.btnMasuk.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()
            viewModel.login(username, password)
        }

        binding.tvDaftar.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun amatiViewModel() {
        viewModel.isLoading.observe(this) { sedangLoading ->
            binding.progressBar.visibility = if (sedangLoading) View.VISIBLE else View.GONE
            binding.btnMasuk.isEnabled = !sedangLoading
            binding.tvDaftar.isEnabled = !sedangLoading
        }

        viewModel.usernameError.observe(this) { pesan ->
            binding.tilUsername.error = pesan
        }

        viewModel.passwordError.observe(this) { pesan ->
            binding.tilPassword.error = pesan
        }

        viewModel.errorMessage.observe(this) { pesan ->
            if (!pesan.isNullOrBlank()) {
                tampilkanPesan(pesan)
                viewModel.pesanErrorSudahDitampilkan()
            }
        }

        viewModel.loginBerhasil.observe(this) { berhasil ->
            if (berhasil == true) {
                Toast.makeText(this, getString(R.string.pesan_login_berhasil), Toast.LENGTH_SHORT)
                    .show()
                viewModel.navigasiSudahDilakukan()
                bukaDashboard()
            }
        }
    }

    private fun tampilkanPesan(pesan: String) {
        Snackbar.make(binding.root, pesan, Snackbar.LENGTH_LONG).show()
    }

    private fun bukaDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
