package com.faizalwiyatnodito.tokoonlineumkm.ui.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.faizalwiyatnodito.tokoonlineumkm.R
import com.faizalwiyatnodito.tokoonlineumkm.data.network.CartItem
import com.faizalwiyatnodito.tokoonlineumkm.databinding.ItemCartBinding
import com.faizalwiyatnodito.tokoonlineumkm.util.Format

// Menampilkan isi keranjang beserta tombol ubah jumlah dan hapus.
class CartAdapter(
    private val saatJumlahBerubah: (CartItem, Int) -> Unit,
    private val saatHapusDiklik: (CartItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private val daftarItem = mutableListOf<CartItem>()

    fun perbarui(itemBaru: List<CartItem>) {
        daftarItem.clear()
        daftarItem.addAll(itemBaru)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.pasangData(daftarItem[position])
    }

    override fun getItemCount(): Int = daftarItem.size

    inner class CartViewHolder(
        private val binding: ItemCartBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun pasangData(item: CartItem) {
            val konteks = binding.root.context

            binding.tvNama.text = item.productName
            binding.tvHarga.text = konteks.getString(R.string.format_harga_per_item, Format.rupiah(item.price))
            binding.tvJumlah.text = item.quantity.toString()
            binding.tvSubtotal.text = Format.rupiah(item.subtotal)

            if (item.notes.isNullOrBlank()) {
                binding.tvCatatan.visibility = View.GONE
            } else {
                binding.tvCatatan.visibility = View.VISIBLE
                binding.tvCatatan.text = konteks.getString(R.string.format_catatan, item.notes)
            }

            binding.btnKurang.setOnClickListener { saatJumlahBerubah(item, item.quantity - 1) }
            binding.btnTambah.setOnClickListener { saatJumlahBerubah(item, item.quantity + 1) }
            binding.tvHapus.setOnClickListener { saatHapusDiklik(item) }
        }
    }
}
