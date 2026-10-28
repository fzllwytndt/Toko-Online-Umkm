package com.faizalwiyatnodito.tokoonlineumkm.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.faizalwiyatnodito.tokoonlineumkm.R
import com.faizalwiyatnodito.tokoonlineumkm.data.network.Product
import com.faizalwiyatnodito.tokoonlineumkm.databinding.ItemProductBinding
import com.faizalwiyatnodito.tokoonlineumkm.util.Format

// Menampilkan daftar produk yang diambil dari REST API.
// Tombol pada setiap baris dipakai untuk menambahkan produk ke keranjang.
class ProductAdapter(
    private val saatTambahDiklik: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private val daftarProduk = mutableListOf<Product>()

    fun perbarui(produkBaru: List<Product>) {
        daftarProduk.clear()
        daftarProduk.addAll(produkBaru)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.pasangData(daftarProduk[position])
    }

    override fun getItemCount(): Int = daftarProduk.size

    inner class ProductViewHolder(
        private val binding: ItemProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun pasangData(produk: Product) {
            binding.tvNama.text = produk.name
            binding.tvKategori.text = produk.categoryName.orEmpty()
            binding.tvHarga.text = Format.rupiah(produk.price)
            binding.tvInisial.text = produk.name.take(1).uppercase()

            // Label hanya muncul kalau produk best seller atau sedang habis.
            val konteks = binding.root.context

            when {
                !produk.isAvailable -> {
                    binding.tvLabel.visibility = View.VISIBLE
                    binding.tvLabel.text = konteks.getString(R.string.label_habis)
                }

                produk.isBestSeller -> {
                    binding.tvLabel.visibility = View.VISIBLE
                    binding.tvLabel.text = konteks.getString(R.string.label_best_seller)
                }

                else -> binding.tvLabel.visibility = View.GONE
            }

            // Produk yang stoknya habis tidak bisa dimasukkan ke keranjang.
            binding.btnTambah.isEnabled = produk.isAvailable
            binding.btnTambah.setOnClickListener { saatTambahDiklik(produk) }
        }
    }
}
