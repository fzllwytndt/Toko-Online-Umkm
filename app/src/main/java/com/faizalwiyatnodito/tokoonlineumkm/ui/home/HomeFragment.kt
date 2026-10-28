package com.faizalwiyatnodito.tokoonlineumkm.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.faizalwiyatnodito.tokoonlineumkm.R
import com.faizalwiyatnodito.tokoonlineumkm.data.repository.UserRepository
import com.faizalwiyatnodito.tokoonlineumkm.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar

// Halaman Home: menampilkan daftar produk yang diambil dari database lewat REST API.
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var adapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        aturDaftar()
        amatiViewModel()

        binding.btnCobaLagi.setOnClickListener { viewModel.muatProduk() }

        // Data hanya diambil sekali saat halaman pertama kali dibuka,
        // supaya tidak memanggil API berulang kali ketika layar diputar.
        if (savedInstanceState == null && viewModel.daftarProduk.value == null) {
            viewModel.muatProduk()
        }
    }

    private fun aturDaftar() {
        adapter = ProductAdapter { produk ->
            val user = UserRepository(requireContext()).ambilUser()
            viewModel.tambahKeKeranjang(user.id, produk)
        }

        binding.rvProduk.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProduk.adapter = adapter
    }

    private fun amatiViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { sedangLoading ->
            binding.progressBar.visibility = if (sedangLoading) View.VISIBLE else View.GONE

            if (sedangLoading) {
                binding.tvPesan.visibility = View.GONE
                binding.btnCobaLagi.visibility = View.GONE
            }
        }

        viewModel.daftarProduk.observe(viewLifecycleOwner) { daftar ->
            adapter.perbarui(daftar)

            // Data kosong bukan error, jadi pesannya dibedakan dari pesan gagal.
            val kosong = daftar.isEmpty() && viewModel.errorMessage.value == null

            if (kosong) {
                binding.tvPesan.visibility = View.VISIBLE
                binding.tvPesan.text = getString(R.string.home_kosong)
            } else if (daftar.isNotEmpty()) {
                binding.tvPesan.visibility = View.GONE
                binding.btnCobaLagi.visibility = View.GONE
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { pesan ->
            if (!pesan.isNullOrBlank()) {
                binding.tvPesan.visibility = View.VISIBLE
                binding.tvPesan.text = pesan
                binding.btnCobaLagi.visibility = View.VISIBLE
            }
        }

        viewModel.pesanKeranjang.observe(viewLifecycleOwner) { pesan ->
            if (!pesan.isNullOrBlank()) {
                Snackbar.make(binding.root, pesan, Snackbar.LENGTH_SHORT).show()
                viewModel.pesanKeranjangSudahDitampilkan()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
