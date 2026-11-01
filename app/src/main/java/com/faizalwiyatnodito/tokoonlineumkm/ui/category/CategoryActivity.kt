package com.faizalwiyatnodito.tokoonlineumkm.ui.category

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.faizalwiyatnodito.tokoonlineumkm.R
import com.faizalwiyatnodito.tokoonlineumkm.databinding.ActivityKategoriBinding
import com.faizalwiyatnodito.tokoonlineumkm.ui.product.ProductAdapter
import com.faizalwiyatnodito.tokoonlineumkm.ui.product.ProductDetailActivity

// Halaman daftar menu per kategori, misalnya "Beranda / Promo Spesial".
// ID dan nama kategori dikirim dari halaman Beranda.
class CategoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityKategoriBinding

    private val viewModel: CategoryViewModel by viewModels()
    private lateinit var produkAdapter: ProductAdapter

    private var idKategori = 0
    private var namaKategori = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityKategoriBinding.inflate(layoutInflater)
        setContentView(binding.root)
        aturInsets()

        idKategori = intent.getIntExtra(EXTRA_ID, 0)
        namaKategori = intent.getStringExtra(EXTRA_NAMA).orEmpty()

        binding.barKembali.tvJudulBar.text =
            getString(R.string.format_breadcrumb_kategori, namaKategori)
        binding.barKembali.btnKembali.setOnClickListener { finish() }
        binding.tvBannerKategori.text = namaKategori
        binding.btnCobaLagi.setOnClickListener { viewModel.muatProduk(idKategori) }

        aturDaftar()
        amatiViewModel()

        if (savedInstanceState == null && viewModel.daftarProduk.value == null) {
            viewModel.muatProduk(idKategori)
        }
    }

    private fun aturInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun aturDaftar() {
        produkAdapter = ProductAdapter(lifecycleScope) { produk ->
            startActivity(ProductDetailActivity.buatIntent(this, produk.id))
        }
        binding.rvProduk.layoutManager = GridLayoutManager(this, 2)
        binding.rvProduk.isNestedScrollingEnabled = false
        binding.rvProduk.adapter = produkAdapter
    }

    private fun amatiViewModel() {
        viewModel.isLoading.observe(this) { sedangLoading ->
            binding.progressBar.visibility = if (sedangLoading) View.VISIBLE else View.GONE

            if (sedangLoading) {
                binding.tvPesan.visibility = View.GONE
                binding.btnCobaLagi.visibility = View.GONE
            }
        }

        viewModel.daftarProduk.observe(this) { daftar ->
            produkAdapter.perbarui(daftar)

            if (daftar.isEmpty() && viewModel.errorMessage.value.isNullOrBlank()) {
                tampilkanPesan(getString(R.string.kategori_kosong), bisaCobaLagi = false)
            } else if (daftar.isNotEmpty()) {
                binding.tvPesan.visibility = View.GONE
                binding.btnCobaLagi.visibility = View.GONE
            }
        }

        viewModel.errorMessage.observe(this) { pesan ->
            if (!pesan.isNullOrBlank()) {
                tampilkanPesan(pesan, bisaCobaLagi = true)
            }
        }
    }

    private fun tampilkanPesan(pesan: String, bisaCobaLagi: Boolean) {
        binding.tvPesan.visibility = View.VISIBLE
        binding.tvPesan.text = pesan
        binding.btnCobaLagi.visibility = if (bisaCobaLagi) View.VISIBLE else View.GONE
    }

    companion object {
        private const val EXTRA_ID = "extra_id_kategori"
        private const val EXTRA_NAMA = "extra_nama_kategori"

        fun buatIntent(context: Context, idKategori: Int, namaKategori: String): Intent =
            Intent(context, CategoryActivity::class.java)
                .putExtra(EXTRA_ID, idKategori)
                .putExtra(EXTRA_NAMA, namaKategori)
    }
}
