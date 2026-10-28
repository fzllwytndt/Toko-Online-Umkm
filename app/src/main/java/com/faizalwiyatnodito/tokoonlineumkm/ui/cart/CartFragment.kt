package com.faizalwiyatnodito.tokoonlineumkm.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.faizalwiyatnodito.tokoonlineumkm.R
import com.faizalwiyatnodito.tokoonlineumkm.data.repository.UserRepository
import com.faizalwiyatnodito.tokoonlineumkm.databinding.FragmentKeranjangBinding
import com.faizalwiyatnodito.tokoonlineumkm.util.Format
import com.google.android.material.snackbar.Snackbar

// Halaman Keranjang: menampilkan isi keranjang pengguna dari REST API,
// lengkap dengan ubah jumlah (PUT) dan hapus item (DELETE).
class CartFragment : Fragment() {
    private var _binding: FragmentKeranjangBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CartViewModel by viewModels()
    private lateinit var adapter: CartAdapter
    private var idPengguna = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKeranjangBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        idPengguna = UserRepository(requireContext()).ambilUser().id

        aturDaftar()
        amatiViewModel()
    }

    // Keranjang dimuat ulang setiap halaman ditampilkan, supaya produk yang baru
    // ditambahkan dari halaman Home langsung ikut terlihat.
    override fun onResume() {
        super.onResume()
        viewModel.muatKeranjang(idPengguna)
    }

    private fun aturDaftar() {
        adapter = CartAdapter(
            saatJumlahBerubah = { item, jumlahBaru ->
                if (jumlahBaru < 1) {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.pesan_jumlah_minimal),
                        Snackbar.LENGTH_SHORT
                    ).show()
                } else {
                    viewModel.ubahJumlah(idPengguna, item.id, jumlahBaru)
                }
            },
            saatHapusDiklik = { item -> viewModel.hapusItem(idPengguna, item.id) }
        )

        binding.rvKeranjang.layoutManager = LinearLayoutManager(requireContext())
        binding.rvKeranjang.adapter = adapter
    }

    private fun amatiViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { sedangLoading ->
            binding.progressBar.visibility = if (sedangLoading) View.VISIBLE else View.GONE

            if (sedangLoading) {
                binding.tvPesan.visibility = View.GONE
            }
        }

        viewModel.keranjang.observe(viewLifecycleOwner) { keranjang ->
            adapter.perbarui(keranjang.items)

            binding.tvTotalHarga.text = Format.rupiah(keranjang.totalHarga)
            binding.kotakTotal.visibility =
                if (keranjang.items.isEmpty()) View.GONE else View.VISIBLE

            if (keranjang.items.isEmpty() && viewModel.errorMessage.value == null) {
                binding.tvPesan.visibility = View.VISIBLE
                binding.tvPesan.text = getString(R.string.keranjang_kosong)
            } else if (keranjang.items.isNotEmpty()) {
                binding.tvPesan.visibility = View.GONE
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { pesan ->
            if (!pesan.isNullOrBlank()) {
                binding.tvPesan.visibility = View.VISIBLE
                binding.tvPesan.text = pesan
            }
        }

        viewModel.pesanAksi.observe(viewLifecycleOwner) { pesan ->
            if (!pesan.isNullOrBlank()) {
                Snackbar.make(binding.root, pesan, Snackbar.LENGTH_SHORT).show()
                viewModel.pesanAksiSudahDitampilkan()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
