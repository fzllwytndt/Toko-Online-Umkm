package com.faizalwiyatnodito.tokoonlineumkm.ui.editprofile

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.faizalwiyatnodito.tokoonlineumkm.databinding.ActivityEditProfileBinding
import com.google.android.material.snackbar.Snackbar

// Halaman Edit Profil: mengubah nickname (username) dan foto profil,
// lalu mengirimnya ke server lewat ViewModel dan Repository.
class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private val viewModel: EditProfileViewModel by viewModels()

    // Menandai pengguna sudah memilih foto baru, supaya foto lama tidak menimpa pratinjau.
    private var fotoDipilih = false

    // Pemilih gambar bawaan sistem (tidak butuh izin penyimpanan).
    private val pilihGambar = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            fotoDipilih = true
            viewModel.pilihFoto(uri)
            binding.imgAvatar.setImageURI(uri)
            binding.imgAvatar.visibility = View.VISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        aturInsets()

        val id = intent.getIntExtra(EXTRA_ID, 0)
        val username = intent.getStringExtra(EXTRA_USERNAME).orEmpty()
        val avatar = intent.getStringExtra(EXTRA_AVATAR)

        viewModel.init(id, avatar)

        binding.tvInisial.text = inisial(username)
        if (savedInstanceState == null) {
            binding.etNickname.setText(username)
        }

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
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.btnBatal.setOnClickListener { finish() }

        binding.btnGantiFoto.setOnClickListener {
            pilihGambar.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

        binding.btnKonfirmasi.setOnClickListener {
            viewModel.simpan(binding.etNickname.text.toString())
        }
    }

    private fun amatiViewModel() {
        viewModel.isLoading.observe(this) { sedangLoading ->
            binding.progressBar.visibility = if (sedangLoading) View.VISIBLE else View.GONE
            binding.btnKonfirmasi.isEnabled = !sedangLoading
            binding.btnGantiFoto.isEnabled = !sedangLoading
            binding.btnBatal.isEnabled = !sedangLoading
        }

        viewModel.usernameError.observe(this) { binding.tilNickname.error = it }

        // Foto profil yang sedang dipakai, ditampilkan selama pengguna belum memilih foto baru.
        viewModel.avatarAwal.observe(this) { bitmap ->
            if (!fotoDipilih && bitmap != null) {
                binding.imgAvatar.setImageBitmap(bitmap)
                binding.imgAvatar.visibility = View.VISIBLE
            }
        }

        viewModel.errorMessage.observe(this) { pesan ->
            if (!pesan.isNullOrBlank()) {
                Snackbar.make(binding.root, pesan, Snackbar.LENGTH_LONG).show()
                viewModel.pesanErrorSudahDitampilkan()
            }
        }

        viewModel.berhasil.observe(this) { pesan ->
            if (!pesan.isNullOrBlank()) {
                Toast.makeText(this, pesan, Toast.LENGTH_LONG).show()
                viewModel.pesanBerhasilSudahDitampilkan()
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    private fun inisial(nama: String): String {
        val bersih = nama.trim()
        return if (bersih.isEmpty()) "?" else bersih.substring(0, 1).uppercase()
    }

    companion object {
        const val EXTRA_ID = "extra_id"
        const val EXTRA_USERNAME = "extra_username"
        const val EXTRA_AVATAR = "extra_avatar"
    }
}
