package com.faizalwiyatnodito.tokoonlineumkm.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.faizalwiyatnodito.tokoonlineumkm.data.network.Category
import com.faizalwiyatnodito.tokoonlineumkm.databinding.ItemChipKategoriBinding

// Chip filter cepat di bawah bagian "Pilih Menu".
// Chip pertama ("Semua") memakai ID null yang berarti daftar menu tidak disaring.
class CategoryFilterAdapter(
    private val saatDipilih: (Int?) -> Unit
) : RecyclerView.Adapter<CategoryFilterAdapter.FilterViewHolder>() {

    // Pasangan ID kategori (null = semua) dengan tulisan yang tampil pada chip.
    private val daftarFilter = mutableListOf<Pair<Int?, String>>()
    private var idTerpilih: Int? = null

    fun perbarui(kategori: List<Category>, labelSemua: String) {
        daftarFilter.clear()
        daftarFilter.add(null to labelSemua)
        kategori.forEach { daftarFilter.add(it.id to it.name) }
        notifyDataSetChanged()
    }

    fun pilih(idKategori: Int?) {
        idTerpilih = idKategori
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val binding =
            ItemChipKategoriBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FilterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        holder.pasangData(daftarFilter[position])
    }

    override fun getItemCount(): Int = daftarFilter.size

    inner class FilterViewHolder(
        private val binding: ItemChipKategoriBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun pasangData(filter: Pair<Int?, String>) {
            binding.tvChip.text = filter.second
            binding.tvChip.isSelected = filter.first == idTerpilih
            binding.tvChip.setOnClickListener { saatDipilih(filter.first) }
        }
    }
}
