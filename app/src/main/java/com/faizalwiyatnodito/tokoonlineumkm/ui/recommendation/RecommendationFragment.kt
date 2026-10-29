package com.faizalwiyatnodito.tokoonlineumkm.ui.recommendation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.faizalwiyatnodito.tokoonlineumkm.R
import com.faizalwiyatnodito.tokoonlineumkm.data.network.Product
import com.faizalwiyatnodito.tokoonlineumkm.data.repository.UserRepository
import com.faizalwiyatnodito.tokoonlineumkm.databinding.FragmentRekomendasiBinding
import com.faizalwiyatnodito.tokoonlineumkm.ui.common.BarAtas
import com.faizalwiyatnodito.tokoonlineumkm.ui.dashboard.DashboardActivity
import com.faizalwiyatnodito.tokoonlineumkm.ui.product.ProductAdapter
import com.faizalwiyatnodito.tokoonlineumkm.ui.product.ProductDetailActivity

// Halaman Rekomendasi: menampilkan menu best seller dan menu dengan harga
// paling terjangkau. Keduanya berasal dari REST API dan bisa dibuka detailnya.
class RecommendationFragment : Fragment() {
    private var _binding: FragmentRekomendasiBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RecommendationViewModel by viewModels()

    private lateinit var bestSellerAdapter: ProductAdapter
    private lateinit var terjangkauAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRekomendasiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        aturDaftar()
        aturBarAtas()
        amatiViewModel()

        binding.btnCobaLagi.setOnClickListener { viewModel.muatRekomendasi() }

        if (savedInstanceState == null && viewModel.bestSeller.value == null) {
            viewModel.muatRekomendasi()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.muatJumlahKeranjang(idPengguna())
    }

    private fun aturDaftar() {
        bestSellerAdapter = ProductAdapter(viewLifecycleOwner.lifecycleScope, ::bukaDetail)
        binding.rvBestSeller.adapter = bestSellerAdapter

        terjangkauAdapter = ProductAdapter(viewLifecycleOwner.lifecycleScope, ::bukaDetail)
        binding.rvTerjangkau.adapter = terjangkauAdapter
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

        viewModel.bestSeller.observe(viewLifecycleOwner) { daftar ->
            bestSellerAdapter.perbarui(daftar)
            binding.tvBestSellerKosong.visibility =
                if (daftar.isEmpty() && viewModel.errorMessage.value.isNullOrBlank()) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
        }

        viewModel.hargaTerjangkau.observe(viewLifecycleOwner) { daftar ->
            terjangkauAdapter.perbarui(daftar)
            binding.tvTerjangkauKosong.visibility =
                if (daftar.isEmpty() && viewModel.errorMessage.value.isNullOrBlank()) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { pesan ->
            if (pesan.isNullOrBlank()) {
                binding.tvPesan.visibility = View.GONE
                binding.btnCobaLagi.visibility = View.GONE
            } else {
                binding.tvPesan.visibility = View.VISIBLE
                binding.tvPesan.text = pesan
                binding.btnCobaLagi.visibility = View.VISIBLE
                binding.tvBestSellerKosong.visibility = View.GONE
                binding.tvTerjangkauKosong.visibility = View.GONE
            }
        }

        viewModel.jumlahKeranjang.observe(viewLifecycleOwner) { jumlah ->
            BarAtas.pasangJumlahKeranjang(binding.barAtas, jumlah)
        }
    }

    private fun bukaDetail(produk: Product) {
        startActivity(ProductDetailActivity.buatIntent(requireContext(), produk.id))
    }

    private fun idPengguna(): Int = UserRepository(requireContext()).ambilUser().id

    private fun bukaMenuBawah(idMenu: Int) {
        (activity as? DashboardActivity)?.pilihMenu(idMenu)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
