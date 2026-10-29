package com.faizalwiyatnodito.tokoonlineumkm.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.faizalwiyatnodito.tokoonlineumkm.data.network.Category
import com.faizalwiyatnodito.tokoonlineumkm.databinding.ItemKategoriBinding

// Kartu kategori pada bagian "Pilih Menu". Daftarnya berasal dari GET /api/categories,
// sehingga kategori baru pada backend langsung ikut tampil tanpa mengubah kode.
class CategoryAdapter(
    private val saatDiklik: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private val daftarKategori = mutableListOf<Category>()

    fun perbarui(kategoriBaru: List<Category>) {
        daftarKategori.clear()
        daftarKategori.addAll(kategoriBaru)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemKategoriBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.pasangData(daftarKategori[position])
    }

    override fun getItemCount(): Int = daftarKategori.size

    inner class CategoryViewHolder(
        private val binding: ItemKategoriBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun pasangData(kategori: Category) {
            binding.tvNamaKategori.text = kategori.name
            binding.root.setOnClickListener { saatDiklik(kategori) }
        }
    }
}
