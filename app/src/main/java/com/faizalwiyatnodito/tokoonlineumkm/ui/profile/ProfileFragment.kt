package com.faizalwiyatnodito.tokoonlineumkm.ui.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.faizalwiyatnodito.tokoonlineumkm.R
import com.faizalwiyatnodito.tokoonlineumkm.databinding.FragmentProfilBinding
import com.faizalwiyatnodito.tokoonlineumkm.ui.editprofile.EditProfileActivity
import com.faizalwiyatnodito.tokoonlineumkm.ui.login.LoginActivity

// Halaman Profil: menampilkan data akun yang sedang login, foto profil,
// serta tombol untuk membuka Edit Profil dan keluar.
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfilBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    // Membuka halaman Edit Profil dan memuat ulang data begitu perubahan berhasil disimpan.
    private val editProfilLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { hasil ->
        if (hasil.resultCode == Activity.RESULT_OK) {
            viewModel.muatProfil()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        amatiViewModel()

        binding.btnEditProfil.setOnClickListener {
            val user = viewModel.user.value ?: return@setOnClickListener
            val intent = Intent(requireContext(), EditProfileActivity::class.java).apply {
                putExtra(EditProfileActivity.EXTRA_ID, user.id)
                putExtra(EditProfileActivity.EXTRA_USERNAME, user.username)
                putExtra(EditProfileActivity.EXTRA_AVATAR, user.avatar)
            }
            editProfilLauncher.launch(intent)
        }

        binding.btnLogout.setOnClickListener {
            viewModel.logout()
            Toast.makeText(requireContext(), getString(R.string.pesan_logout), Toast.LENGTH_SHORT)
                .show()
            bukaLogin()
        }

        if (savedInstanceState == null && viewModel.user.value == null) {
            viewModel.muatProfil()
        }
    }

    private fun amatiViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { sedangLoading ->
            binding.progressBar.visibility = if (sedangLoading) View.VISIBLE else View.GONE
        }

        viewModel.user.observe(viewLifecycleOwner) { user ->
            binding.tvUsername.text = user.username
            binding.tvEmail.text = user.email
            binding.tvNoTlp.text = user.noTlp
            binding.tvInisial.text = inisial(user.username)
        }

        viewModel.avatar.observe(viewLifecycleOwner) { bitmap ->
            if (bitmap != null) {
                binding.imgAvatar.setImageBitmap(bitmap)
                binding.imgAvatar.visibility = View.VISIBLE
            } else {
                binding.imgAvatar.visibility = View.GONE
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { pesan ->
            if (pesan.isNullOrBlank()) {
                binding.tvPesan.visibility = View.GONE
            } else {
                // Data sesi tetap terlihat, pesan ini hanya memberitahu bahwa
                // data terbaru dari server belum berhasil diambil.
                binding.tvPesan.visibility = View.VISIBLE
                binding.tvPesan.text = pesan
            }
        }
    }

    // Huruf awal nama untuk avatar cadangan ketika foto profil belum ada.
    private fun inisial(nama: String): String {
        val bersih = nama.trim()
        return if (bersih.isEmpty()) "?" else bersih.substring(0, 1).uppercase()
    }

    private fun bukaLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
