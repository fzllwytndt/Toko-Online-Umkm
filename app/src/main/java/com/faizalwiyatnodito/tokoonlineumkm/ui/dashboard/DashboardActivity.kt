package com.faizalwiyatnodito.tokoonlineumkm.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.faizalwiyatnodito.tokoonlineumkm.R
import com.faizalwiyatnodito.tokoonlineumkm.data.repository.UserRepository
import com.faizalwiyatnodito.tokoonlineumkm.databinding.ActivityDashboardBinding
import com.faizalwiyatnodito.tokoonlineumkm.ui.login.LoginActivity

/**
 * Halaman tujuan setelah Login berhasil.
 * Data yang ditampilkan diambil dari sesi yang tersimpan di SharedPreferences,
 * sehingga tidak perlu memanggil API lagi.
 */
class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var repository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        aturInsets()

        repository = UserRepository(this)

        // Bila sesi sudah tidak valid, pengguna diminta login kembali
        if (!repository.sudahLogin()) {
            bukaLogin()
            return
        }

        tampilkanDataPengguna()

        binding.btnLogout.setOnClickListener {
            repository.hapusSesi()
            Toast.makeText(this, getString(R.string.pesan_logout), Toast.LENGTH_SHORT).show()
            bukaLogin()
        }
    }

    private fun aturInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun tampilkanDataPengguna() {
        val user = repository.ambilUser()
        binding.tvUsername.text = user.username
        binding.tvEmail.text = user.email
        binding.tvNoTlp.text = user.noTlp
    }

    private fun bukaLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
