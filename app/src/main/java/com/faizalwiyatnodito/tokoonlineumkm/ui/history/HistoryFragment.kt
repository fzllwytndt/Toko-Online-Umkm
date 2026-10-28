package com.faizalwiyatnodito.tokoonlineumkm.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.faizalwiyatnodito.tokoonlineumkm.R
import com.faizalwiyatnodito.tokoonlineumkm.data.repository.UserRepository
import com.faizalwiyatnodito.tokoonlineumkm.databinding.FragmentRiwayatBinding

// Halaman Riwayat: menampilkan pesanan milik pengguna yang sedang login.
class HistoryFragment : Fragment() {
    private var _binding: FragmentRiwayatBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HistoryViewModel by viewModels()
    private lateinit var adapter: OrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRiwayatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = OrderAdapter()
        binding.rvRiwayat.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRiwayat.adapter = adapter

        amatiViewModel()

        if (savedInstanceState == null && viewModel.daftarPesanan.value == null) {
            val idPengguna = UserRepository(requireContext()).ambilUser().id
            viewModel.muatRiwayat(idPengguna)
        }
    }

    private fun amatiViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { sedangLoading ->
            binding.progressBar.visibility = if (sedangLoading) View.VISIBLE else View.GONE

            if (sedangLoading) {
                binding.tvPesan.visibility = View.GONE
            }
        }

        viewModel.daftarPesanan.observe(viewLifecycleOwner) { daftar ->
            adapter.perbarui(daftar)

            if (daftar.isEmpty() && viewModel.errorMessage.value == null) {
                binding.tvPesan.visibility = View.VISIBLE
                binding.tvPesan.text = getString(R.string.riwayat_kosong)
            } else if (daftar.isNotEmpty()) {
                binding.tvPesan.visibility = View.GONE
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { pesan ->
            if (!pesan.isNullOrBlank()) {
                binding.tvPesan.visibility = View.VISIBLE
                binding.tvPesan.text = pesan
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
