package com.faizalwiyatnodito.tokoonlineumkm.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.faizalwiyatnodito.tokoonlineumkm.R
import com.faizalwiyatnodito.tokoonlineumkm.data.repository.UserRepository
import kotlinx.coroutines.launch

/**
 * VIEWMODEL
 * Mengatur validasi input, proses Login melalui Repository,
 * serta status loading, berhasil, dan gagal.
 *
 * View (LoginActivity) hanya mengamati LiveData di bawah ini.
 */
class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UserRepository(application)

    // Status loading ketika permintaan API sedang diproses
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // Pesan kesalahan validasi untuk tiap kolom (null berarti tidak ada kesalahan)
    private val _usernameError = MutableLiveData<String?>()
    val usernameError: LiveData<String?> = _usernameError

    private val _passwordError = MutableLiveData<String?>()
    val passwordError: LiveData<String?> = _passwordError

    // Pesan kesalahan dari proses autentikasi (ditampilkan sebagai Snackbar/Toast)
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    // Penanda Login berhasil, dipakai untuk pindah ke halaman Dashboard
    private val _loginBerhasil = MutableLiveData<Boolean>()
    val loginBerhasil: LiveData<Boolean> = _loginBerhasil

    /**
     * Dipanggil ketika tombol Masuk ditekan.
     * Input divalidasi dulu, baru dikirim ke Repository bila sudah benar.
     */
    fun login(username: String, password: String) {
        val usernameBersih = username.trim()

        if (!validasiInput(usernameBersih, password)) return

        viewModelScope.launch {
            _isLoading.value = true

            val response = repository.login(usernameBersih, password)

            if (response.status && response.data != null) {
                // Login berhasil : simpan sesi lalu beri tahu View
                repository.simpanSesi(response.data)
                _loginBerhasil.value = true
            } else {
                // Login gagal : tampilkan pesan dari API
                _errorMessage.value = response.message
            }

            _isLoading.value = false
        }
    }

    /**
     * Memeriksa kelengkapan input sebelum data dikirim ke API.
     * Mengembalikan true bila seluruh input sudah benar.
     */
    private fun validasiInput(username: String, password: String): Boolean {
        var valid = true

        when {
            username.isEmpty() -> {
                _usernameError.value = pesan(R.string.error_username_kosong)
                valid = false
            }

            else -> _usernameError.value = null
        }

        when {
            password.isEmpty() -> {
                _passwordError.value = pesan(R.string.error_password_kosong)
                valid = false
            }

            else -> _passwordError.value = null
        }

        return valid
    }

    /** Mengambil teks pesan dari strings.xml. */
    private fun pesan(idTeks: Int): String = getApplication<Application>().getString(idTeks)

    /** Menghapus pesan kesalahan agar tidak muncul lagi saat layar dibuat ulang. */
    fun pesanErrorSudahDitampilkan() {
        _errorMessage.value = null
    }

    /** Menandai perpindahan halaman sudah dilakukan, agar tidak terulang. */
    fun navigasiSudahDilakukan() {
        _loginBerhasil.value = false
    }
}
