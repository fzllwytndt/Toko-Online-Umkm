package com.faizalwiyatnodito.tokoonlineumkm.ui.product

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
import com.faizalwiyatnodito.tokoonlineumkm.R
import com.faizalwiyatnodito.tokoonlineumkm.data.network.Product
import com.faizalwiyatnodito.tokoonlineumkm.data.repository.UserRepository
import com.faizalwiyatnodito.tokoonlineumkm.databinding.ActivityDetailProdukBinding
import com.faizalwiyatnodito.tokoonlineumkm.util.Format
import com.faizalwiyatnodito.tokoonlineumkm.util.PemuatGambar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

// Halaman Detail Menu. ID produk diterima dari halaman sebelumnya,
// lalu dipakai untuk memanggil GET /api/products/{id} lewat ViewModel.
class ProductDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailProdukBinding

    private val viewModel: ProductDetailViewModel by viewModels()

    private var idProduk = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailProdukBinding.inflate(layoutInflater)
        setContentView(binding.root)
        aturInsets()

        idProduk = intent.getIntExtra(EXTRA_ID, 0)

        binding.barKembali.tvJudulBar.text = getString(R.string.detail_judul)
        binding.barKembali.btnKembali.setOnClickListener { finish() }
        binding.btnCobaLagi.setOnClickListener { viewModel.muatDetail(idProduk) }
        binding.btnTambahKeranjang.setOnClickListener {
            val idPengguna = UserRepository(this).ambilUser().id
            viewModel.tambahKeKeranjang(idPengguna)
        }

        amatiViewModel()

        // ID yang tidak masuk akal ditangani di sini supaya tidak ada request sia-sia.
        if (idProduk <= 0) {
            tampilkanPesan(getString(R.string.detail_tidak_ditemukan), bisaCobaLagi = false)
        } else if (savedInstanceState == null && viewModel.produk.value == null) {
            viewModel.muatDetail(idProduk)
        }
    }

    private fun aturInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun amatiViewModel() {
        viewModel.isLoading.observe(this) { sedangLoading ->
            binding.progressBar.visibility = if (sedangLoading) View.VISIBLE else View.GONE

            if (sedangLoading) {
                binding.scrollDetail.visibility = View.GONE
                binding.tvPesan.visibility = View.GONE
                binding.btnCobaLagi.visibility = View.GONE
            }
        }

        viewModel.produk.observe(this) { produk ->
            if (produk != null) {
                tampilkanProduk(produk)
            }
        }

        viewModel.errorMessage.observe(this) { pesan ->
            if (!pesan.isNullOrBlank()) {
                tampilkanPesan(pesan, bisaCobaLagi = true)
            }
        }

        viewModel.pesanKeranjang.observe(this) { pesan ->
            if (!pesan.isNullOrBlank()) {
                Snackbar.make(binding.root, pesan, Snackbar.LENGTH_SHORT).show()
                viewModel.pesanKeranjangSudahDitampilkan()
            }
        }
    }

    private fun tampilkanProduk(produk: Product) {
        binding.scrollDetail.visibility = View.VISIBLE
        binding.tvPesan.visibility = View.GONE
        binding.btnCobaLagi.visibility = View.GONE

        binding.barKembali.tvJudulBar.text =
            getString(R.string.format_breadcrumb_detail, produk.name)

        binding.tvNama.text = produk.name
        binding.tvHarga.text = Format.rupiah(produk.price)

        // Nama kategori dan deskripsi tidak selalu dikirim API,
        // jadi ditampilkan keterangan apa adanya tanpa mengarang isi.
        binding.tvKategori.text = produk.categoryName.takeUnless { it.isNullOrBlank() }
            ?: getString(R.string.detail_kategori_kosong)

        binding.tvDeskripsi.text = produk.description.takeUnless { it.isNullOrBlank() }
            ?: getString(R.string.detail_deskripsi_kosong)

        binding.tvLabelBestSeller.visibility =
            if (produk.isBestSeller) View.VISIBLE else View.GONE

        binding.tvStokHabis.visibility = if (produk.isAvailable) View.GONE else View.VISIBLE

        // Menu yang stoknya habis tidak bisa dimasukkan ke keranjang.
        binding.btnTambahKeranjang.isEnabled = produk.isAvailable

        muatGambar(produk)
    }

    private fun muatGambar(produk: Product) {
        binding.imgProduk.visibility = View.GONE

        if (produk.image.isNullOrBlank()) return

        lifecycleScope.launch {
            val gambar = PemuatGambar.muat(produk.image)

            if (gambar != null) {
                binding.imgProduk.setImageBitmap(gambar)
                binding.imgProduk.visibility = View.VISIBLE
            }
        }
    }

    private fun tampilkanPesan(pesan: String, bisaCobaLagi: Boolean) {
        binding.scrollDetail.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.tvPesan.visibility = View.VISIBLE
        binding.tvPesan.text = pesan
        binding.btnCobaLagi.visibility = if (bisaCobaLagi) View.VISIBLE else View.GONE
    }

    companion object {
        private const val EXTRA_ID = "extra_id_produk"

        // Satu-satunya cara membuka halaman ini, supaya ID produk
        // selalu ikut terkirim dan tidak salah nama parameter.
        fun buatIntent(context: Context, idProduk: Int): Intent =
            Intent(context, ProductDetailActivity::class.java)
                .putExtra(EXTRA_ID, idProduk)
    }
}
