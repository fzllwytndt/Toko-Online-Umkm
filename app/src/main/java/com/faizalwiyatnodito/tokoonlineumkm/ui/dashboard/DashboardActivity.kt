package com.faizalwiyatnodito.tokoonlineumkm.ui.dashboard

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.faizalwiyatnodito.tokoonlineumkm.R
import com.faizalwiyatnodito.tokoonlineumkm.data.repository.UserRepository
import com.faizalwiyatnodito.tokoonlineumkm.databinding.ActivityDashboardBinding
import com.faizalwiyatnodito.tokoonlineumkm.ui.cart.CartFragment
import com.faizalwiyatnodito.tokoonlineumkm.ui.history.HistoryFragment
import com.faizalwiyatnodito.tokoonlineumkm.ui.home.HomeFragment
import com.faizalwiyatnodito.tokoonlineumkm.ui.login.LoginActivity
import com.faizalwiyatnodito.tokoonlineumkm.ui.profile.ProfileFragment

// Halaman utama setelah login. Activity ini hanya bertugas menjadi wadah,
// isi layarnya ditentukan oleh Fragment yang dipilih lewat Bottom Navigation.
class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        aturInsets()

        // Halaman ini hanya boleh dibuka kalau sesi login masih ada.
        if (!UserRepository(this).sudahLogin()) {
            bukaLogin()
            return
        }

        aturBottomNavigation()

        // Saat pertama kali dibuka, halaman Home yang ditampilkan.
        if (savedInstanceState == null) {
            binding.bottomNavigation.selectedItemId = R.id.menu_home
        }
    }

    private fun aturInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun aturBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { menu ->
            val fragment: Fragment = when (menu.itemId) {
                R.id.menu_home -> HomeFragment()
                R.id.menu_keranjang -> CartFragment()
                R.id.menu_riwayat -> HistoryFragment()
                else -> ProfileFragment()
            }

            tampilkanFragment(fragment)
            true
        }
    }

    private fun tampilkanFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.wadahFragment.id, fragment)
            .commit()
    }

    private fun bukaLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
