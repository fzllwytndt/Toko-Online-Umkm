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

    private fun aturInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun aturTombol() {
        binding.btnSubmit.setOnClickListener {
            viewModel.register(
                username = binding.etUsername.text.toString(),
                email = binding.etEmail.text.toString(),
                password = binding.etPassword.text.toString(),
                konfirmasiPassword = binding.etKonfirmasiPassword.text.toString(),
                noTlp = binding.etNoTlp.text.toString()
            )
        }

        binding.tvMasuk.setOnClickListener {
            finish()
        }
    }

    private fun amatiViewModel() {
        viewModel.isLoading.observe(this) { sedangLoading ->
            binding.progressBar.visibility = if (sedangLoading) View.VISIBLE else View.GONE
            binding.btnSubmit.isEnabled = !sedangLoading
            binding.tvMasuk.isEnabled = !sedangLoading
        }

        viewModel.usernameError.observe(this) { binding.tilUsername.error = it }
        viewModel.emailError.observe(this) { binding.tilEmail.error = it }
        viewModel.passwordError.observe(this) { binding.tilPassword.error = it }
        viewModel.konfirmasiPasswordError.observe(this) {
            binding.tilKonfirmasiPassword.error = it
        }
        viewModel.noTlpError.observe(this) { binding.tilNoTlp.error = it }

        viewModel.errorMessage.observe(this) { pesan ->
            if (!pesan.isNullOrBlank()) {
                tampilkanPesan(pesan)
                viewModel.pesanErrorSudahDitampilkan()
            }
        }

        viewModel.registerBerhasil.observe(this) { pesan ->
            if (!pesan.isNullOrBlank()) {
                Toast.makeText(this, pesan, Toast.LENGTH_LONG).show()
                viewModel.pesanBerhasilSudahDitampilkan()
                finish()
            }
        }
    }

    private fun tampilkanPesan(pesan: String) {
        Snackbar.make(binding.root, pesan, Snackbar.LENGTH_LONG).show()
    }
}
