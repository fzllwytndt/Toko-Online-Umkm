package com.faizalwiyatnodito.tokoonlineumkm.ui.product

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.faizalwiyatnodito.tokoonlineumkm.data.network.Product
import com.faizalwiyatnodito.tokoonlineumkm.databinding.ItemProdukBinding
import com.faizalwiyatnodito.tokoonlineumkm.util.Format
import com.faizalwiyatnodito.tokoonlineumkm.util.PemuatGambar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

// Kartu menu yang dipakai bersama oleh halaman Beranda, Rekomendasi, dan Kategori.
// Menekan kartu akan membuka halaman Detail Menu.
//
// lebarKartuDp mengatur bentuk daftarnya:
// - null (bawaan) : lebar kartu mengikuti induknya, dipakai daftar 2 kolom
//                   pada Beranda dan Kategori.
// - diisi angka   : lebar kartu dipatok, dipakai daftar yang digulir ke samping
//                   pada halaman Rekomendasi. Tanpa ini satu kartu akan selebar
//                   layar karena item_produk.xml memakai match_parent.
class ProductAdapter(
    private val cakupan: CoroutineScope,
    private val lebarKartuDp: Int? = null,
    private val saatDiklik: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private val daftarProduk = mutableListOf<Product>()

    fun perbarui(produkBaru: List<Product>) {
        daftarProduk.clear()
        daftarProduk.addAll(produkBaru)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProdukBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        // Dipatok hanya untuk daftar yang digulir ke samping.
        lebarKartuDp?.let { lebar ->
            val kerapatan = parent.resources.displayMetrics.density
            binding.root.layoutParams.width = (lebar * kerapatan).toInt()
        }

        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.pasangData(daftarProduk[position])
    }

    override fun getItemCount(): Int = daftarProduk.size

    inner class ProductViewHolder(
        private val binding: ItemProdukBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private var tugasGambar: Job? = null
        private var idProdukTampil = 0

        fun pasangData(produk: Product) {
            idProdukTampil = produk.id

            binding.tvNama.text = produk.name
            binding.tvHarga.text = Format.rupiah(produk.price)

            // Penanda stok habis mengikuti nilai is_available dari API.
            binding.tvStokHabis.visibility = if (produk.isAvailable) View.GONE else View.VISIBLE

            binding.root.setOnClickListener { saatDiklik(produk) }

            muatGambar(produk)
        }

        // Foto menu diunduh dari URL yang dikirim API. Selama belum selesai,
        // atau ketika produk belum punya foto, kotak gambar pengganti yang terlihat.
        private fun muatGambar(produk: Product) {
            tugasGambar?.cancel()
            binding.imgProduk.visibility = View.GONE

            if (produk.image.isNullOrBlank()) return

            tugasGambar = cakupan.launch {
                val gambar = PemuatGambar.muat(produk.image)

                // Baris bisa sudah dipakai ulang untuk produk lain sebelum
                // unduhan selesai, jadi hasilnya dicocokkan dulu.
                if (gambar != null && idProdukTampil == produk.id) {
                    binding.imgProduk.setImageBitmap(gambar)
                    binding.imgProduk.visibility = View.VISIBLE
                }
            }
        }
    }
}
