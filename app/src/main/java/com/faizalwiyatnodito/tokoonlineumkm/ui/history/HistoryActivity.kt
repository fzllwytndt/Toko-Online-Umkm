package com.faizalwiyatnodito.tokoonlineumkm.ui.history

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.faizalwiyatnodito.tokoonlineumkm.R
import com.faizalwiyatnodito.tokoonlineumkm.databinding.ActivityRiwayatBinding

// Wadah halaman Riwayat Pesanan yang dibuka dari halaman Profil.
// Isinya tetap HistoryFragment yang sudah ada, jadi tidak ada logika yang berubah.
class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRiwayatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRiwayatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        aturInsets()

        binding.barKembali.tvJudulBar.text = getString(R.string.riwayat_judul)
        binding.barKembali.btnKembali.setOnClickListener { finish() }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.wadahFragment.id, HistoryFragment())
                .commit()
        }
    }

    private fun aturInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    companion object {
        fun buatIntent(context: Context): Intent = Intent(context, HistoryActivity::class.java)
    }
}
