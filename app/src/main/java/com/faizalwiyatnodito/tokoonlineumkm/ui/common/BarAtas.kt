package com.faizalwiyatnodito.tokoonlineumkm.ui.common

import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doAfterTextChanged
import com.faizalwiyatnodito.tokoonlineumkm.data.network.User
import com.faizalwiyatnodito.tokoonlineumkm.databinding.LayoutBarAtasBinding
import com.faizalwiyatnodito.tokoonlineumkm.util.PemuatGambar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// Pengatur isi bar atas (layout_bar_atas.xml).
// Dibuat terpisah supaya halaman Beranda dan Rekomendasi tidak menulis
// kode yang sama dua kali untuk bar atas yang bentuknya persis sama.
object BarAtas {

    // Menampilkan huruf awal nama sebagai avatar cadangan, lalu menimpanya
    // dengan foto profil begitu selesai diunduh.
    fun pasangProfil(binding: LayoutBarAtasBinding, cakupan: CoroutineScope, user: User) {
        binding.tvInisialBar.text = inisial(user.username)
        binding.imgAvatarBar.visibility = View.GONE

        cakupan.launch {
            val foto = PemuatGambar.muat(user.avatar)

            if (foto != null) {
                binding.imgAvatarBar.setImageBitmap(foto)
                binding.imgAvatarBar.visibility = View.VISIBLE
            }
        }
    }

    // Lingkaran merah hanya muncul ketika keranjang benar-benar berisi.
    fun pasangJumlahKeranjang(binding: LayoutBarAtasBinding, jumlah: Int) {
        if (jumlah > 0) {
            binding.tvBadge.visibility = View.VISIBLE
            binding.tvBadge.text = if (jumlah > 99) BATAS_BADGE else jumlah.toString()
        } else {
            binding.tvBadge.visibility = View.GONE
        }
    }

    // Pencarian dijalankan ketika pengguna menekan tombol cari pada papan ketik,
    // dan ketika kolom pencarian dikosongkan kembali.
    fun pasangPencarian(binding: LayoutBarAtasBinding, saatCari: (String) -> Unit) {
        binding.etCari.setOnEditorActionListener { _, aksi, _ ->
            if (aksi == EditorInfo.IME_ACTION_SEARCH) {
                saatCari(binding.etCari.text.toString().trim())
                true
            } else {
                false
            }
        }

        binding.etCari.doAfterTextChanged { teks ->
            if (teks.isNullOrBlank()) {
                saatCari("")
            }
        }
    }

    fun pasangKlikKeranjang(binding: LayoutBarAtasBinding, saatDiklik: () -> Unit) {
        binding.wadahKeranjang.setOnClickListener { saatDiklik() }
    }

    fun pasangKlikProfil(binding: LayoutBarAtasBinding, saatDiklik: () -> Unit) {
        binding.wadahAvatarBar.setOnClickListener { saatDiklik() }
    }

    private fun inisial(nama: String): String {
        val bersih = nama.trim()
        return if (bersih.isEmpty()) "?" else bersih.substring(0, 1).uppercase()
    }

    private const val BATAS_BADGE = "99"
}
