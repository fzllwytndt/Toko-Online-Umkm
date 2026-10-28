package com.faizalwiyatnodito.tokoonlineumkm.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.faizalwiyatnodito.tokoonlineumkm.R
import com.faizalwiyatnodito.tokoonlineumkm.databinding.FragmentProfilBinding
import com.faizalwiyatnodito.tokoonlineumkm.ui.login.LoginActivity

// Halaman Profil: menampilkan data akun yang sedang login dan tombol keluar.
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfilBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

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
