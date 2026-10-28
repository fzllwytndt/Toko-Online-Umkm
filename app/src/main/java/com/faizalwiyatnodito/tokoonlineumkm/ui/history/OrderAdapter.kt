package com.faizalwiyatnodito.tokoonlineumkm.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.faizalwiyatnodito.tokoonlineumkm.R
import com.faizalwiyatnodito.tokoonlineumkm.data.network.Order
import com.faizalwiyatnodito.tokoonlineumkm.databinding.ItemOrderBinding
import com.faizalwiyatnodito.tokoonlineumkm.util.Format

// Menampilkan riwayat pesanan beserta daftar barang pada setiap pesanan.
class OrderAdapter : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    private val daftarPesanan = mutableListOf<Order>()

    fun perbarui(pesananBaru: List<Order>) {
        daftarPesanan.clear()
        daftarPesanan.addAll(pesananBaru)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.pasangData(daftarPesanan[position])
    }

    override fun getItemCount(): Int = daftarPesanan.size

    inner class OrderViewHolder(
        private val binding: ItemOrderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun pasangData(pesanan: Order) {
            val konteks = binding.root.context

            binding.tvNomorPesanan.text =
                konteks.getString(R.string.format_nomor_pesanan, pesanan.id)
            binding.tvStatus.text = pesanan.status
            binding.tvKeterangan.text = konteks.getString(
                R.string.format_keterangan_pesanan,
                pesanan.orderType,
                pesanan.paymentMethod,
                Format.tanggalSingkat(pesanan.createdAt)
            )
            binding.tvTotal.text = Format.rupiah(pesanan.totalAmount)

            // Daftar barang ditulis sebagai beberapa baris teks dalam satu TextView.
            binding.tvDaftarBarang.text = pesanan.items.joinToString("\n") { barang ->
                konteks.getString(R.string.format_baris_barang, barang.quantity, barang.productName)
            }
        }
    }
}
