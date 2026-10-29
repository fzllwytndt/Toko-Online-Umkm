package com.faizalwiyatnodito.tokoonlineumkm.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.faizalwiyatnodito.tokoonlineumkm.R
import com.faizalwiyatnodito.tokoonlineumkm.data.repository.UserRepository
import com.faizalwiyatnodito.tokoonlineumkm.databinding.FragmentBerandaBinding
import com.faizalwiyatnodito.tokoonlineumkm.ui.category.CategoryActivity
import com.faizalwiyatnodito.tokoonlineumkm.ui.common.BarAtas
import com.faizalwiyatnodito.tokoonlineumkm.ui.dashboard.DashboardActivity
import com.faizalwiyatnodito.tokoonlineumkm.ui.product.ProductAdapter
import com.faizalwiyatnodito.tokoonlineumkm.ui.product.ProductDetailActivity

// Halaman Beranda: menampilkan kategori dan daftar menu yang diambil dari REST API.
// Menekan salah satu menu akan membuka halaman Detail Menu.
class HomeFragment : Fragment() {
    private var _binding: FragmentBerandaBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var produkAdapter: ProductAdapter
    private lateinit var kategoriAdapter: CategoryAdapter
    private lateinit var filterAdapter: CategoryFilterAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBerandaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        aturDaftar()
        aturBarAtas()
        amatiViewModel()

        binding.btnCobaLagi.setOnClickListener { viewModel.muatProduk() }
        binding.btnFavorit.setOnClickListener { viewModel.ubahModeFavorit() }

        // Data hanya diambil sekali saat halaman pertama kali dibuka,
        // supaya tidak memanggil API berulang kali ketika layar diputar.
        if (savedInstanceState == null && viewModel.daftarProduk.value == null) {
            viewModel.mulai(idPengguna())
        }
    }

    override fun onResume() {
        super.onResume()
        // Jumlah item keranjang bisa berubah dari halaman lain,
        // jadi angka pada bar atas disegarkan setiap halaman ditampilkan.
        viewModel.muatJumlahKeranjang(idPengguna())
    }

    private fun aturDaftar() {
        produkAdapter = ProductAdapter(viewLifecycleOwner.lifecycleScope) { produk ->
            startActivity(ProductDetailActivity.buatIntent(requireContext(), produk.id))
        }
        binding.rvProduk.adapter = produkAdapter

        kategoriAdapter = CategoryAdapter { kategori ->
            startActivity(CategoryActivity.buatIntent(requireContext(), kategori.id, kategori.name))
        }
        binding.rvKategori.adapter = kategoriAdapter

        filterAdapter = CategoryFilterAdapter { idKategori -> viewModel.pilihKategori(idKategori) }
        binding.rvFilter.adapter = filterAdapter
    }

    private fun aturBarAtas() {
        val user = UserRepository(requireContext()).ambilUser()

        BarAtas.pasangProfil(binding.barAtas, viewLifecycleOwner.lifecycleScope, user)
        BarAtas.pasangPencarian(binding.barAtas) { kata -> viewModel.cari(kata) }
        BarAtas.pasangKlikKeranjang(binding.barAtas) { bukaMenuBawah(R.id.menu_keranjang) }
        BarAtas.pasangKlikProfil(binding.barAtas) { bukaMenuBawah(R.id.menu_profil) }
    }

    private fun amatiViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { sedangLoading ->
            binding.progressBar.visibility = if (sedangLoading) View.VISIBLE else View.GONE

            if (sedangLoading) {
                binding.tvPesan.visibility = View.GONE
                binding.btnCobaLagi.visibility = View.GONE
            }
        }

        viewModel.daftarKategori.observe(viewLifecycleOwner) { daftar ->
            kategoriAdapter.perbarui(daftar)
            filterAdapter.perbarui(daftar, getString(R.string.filter_semua))
            filterAdapter.pilih(viewModel.idKategoriTerpilih.value)

            binding.rvKategori.visibility = if (daftar.isEmpty()) View.GONE else View.VISIBLE
            binding.rvFilter.visibility = if (daftar.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.daftarProduk.observe(viewLifecycleOwner) { daftar ->
            produkAdapter.perbarui(daftar)

            // Pesan "belum ada data" hanya muncul kalau memang tidak ada error.
            if (daftar.isEmpty() && viewModel.errorMessage.value.isNullOrBlank()) {
                tampilkanPesan(pesanKosong(), bisaCobaLagi = false)
            } else if (daftar.isNotEmpty()) {
                sembunyikanPesan()
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { pesan ->
            if (!pesan.isNullOrBlank()) {
                tampilkanPesan(pesan, bisaCobaLagi = true)
            }
        }

        viewModel.idKategoriTerpilih.observe(viewLifecycleOwner) { idKategori ->
            filterAdapter.pilih(idKategori)
        }

        viewModel.modeFavorit.observe(viewLifecycleOwner) { aktif ->
            binding.btnFavorit.isSelected = aktif
            perbaruiJudulMenu()

            // Daftar favorit tidak ikut disaring kata kunci, jadi kolom
            // pencarian dikosongkan supaya tidak membingungkan.
            if (aktif && binding.barAtas.etCari.text.isNotBlank()) {
                binding.barAtas.etCari.text = null
            }
        }

        viewModel.kataKunci.observe(viewLifecycleOwner) { perbaruiJudulMenu() }

        viewModel.jumlahKeranjang.observe(viewLifecycleOwner) { jumlah ->
            BarAtas.pasangJumlahKeranjang(binding.barAtas, jumlah)
        }
    }

    // Judul bagian menyesuaikan tampilan yang sedang aktif: menu biasa,
    // hasil pencarian, atau daftar favorit.
    private fun perbaruiJudulMenu() {
        val kata = viewModel.kataKunci.value.orEmpty()

        binding.tvJudulMenu.text = when {
            viewModel.modeFavorit.value == true -> getString(R.string.beranda_favorit)
            kata.isNotBlank() -> getString(R.string.format_hasil_cari, kata)
            else -> getString(R.string.beranda_menu)
        }
    }

    private fun pesanKosong(): String {
        return if (viewModel.modeFavorit.value == true) {
            getString(R.string.beranda_favorit_kosong)
        } else {
            getString(R.string.home_kosong)
        }
    }

    private fun tampilkanPesan(pesan: String, bisaCobaLagi: Boolean) {
        binding.tvPesan.visibility = View.VISIBLE
        binding.tvPesan.text = pesan
        binding.btnCobaLagi.visibility = if (bisaCobaLagi) View.VISIBLE else View.GONE
    }

    private fun sembunyikanPesan() {
        binding.tvPesan.visibility = View.GONE
        binding.btnCobaLagi.visibility = View.GONE
    }

    private fun idPengguna(): Int = UserRepository(requireContext()).ambilUser().id

    // Ikon keranjang dan foto profil pada bar atas memindahkan Bottom Navigation
    // ke menu yang sesuai, bukan membuka Activity baru.
    private fun bukaMenuBawah(idMenu: Int) {
        (activity as? DashboardActivity)?.pilihMenu(idMenu)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
