package com.faizalwiyatnodito.tokoonlineumkm.ui.editprofile

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.faizalwiyatnodito.tokoonlineumkm.R
import com.faizalwiyatnodito.tokoonlineumkm.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = UserRepository(application)

    private var idPengguna: Int = 0

    // Foto baru yang dipilih pengguna. Null berarti foto profil tidak diganti.
    private var fotoBaruUri: Uri? = null

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _usernameError = MutableLiveData<String?>()
    val usernameError: LiveData<String?> = _usernameError

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _berhasil = MutableLiveData<String?>()
    val berhasil: LiveData<String?> = _berhasil

    // Foto profil yang sedang dipakai (diunduh dari server) untuk ditampilkan di form.
    private val _avatarAwal = MutableLiveData<Bitmap?>()
    val avatarAwal: LiveData<Bitmap?> = _avatarAwal

    private var sudahInit = false

    fun init(id: Int, avatarPath: String?) {
        if (sudahInit) return
        sudahInit = true
        idPengguna = id

        viewModelScope.launch {
            _avatarAwal.value = repository.unduhAvatar(avatarPath)
        }
    }

    fun pilihFoto(uri: Uri) {
        fotoBaruUri = uri
    }

    fun simpan(username: String) {
        val usernameBersih = username.trim()

        if (!validasi(usernameBersih)) {
            return
        }

        viewModelScope.launch {
            _isLoading.value = true

            // Membaca isi file foto di thread IO supaya tidak membebani tampilan.
            val isiFoto = bacaFoto()
            val bytes = isiFoto?.first
            val mime = isiFoto?.second

            val response = repository.perbaruiProfil(idPengguna, usernameBersih, bytes, mime)

            if (response.status) {
                _berhasil.value = response.message
            } else {
                _errorMessage.value = response.message
            }

            _isLoading.value = false
        }
    }

    // Mengubah Uri foto menjadi pasangan (byte, tipe MIME). Null kalau tidak ada foto baru.
    private suspend fun bacaFoto(): Pair<ByteArray, String?>? {
        val uri = fotoBaruUri ?: return null

        return withContext(Dispatchers.IO) {
            try {
                val resolver = getApplication<Application>().contentResolver
                val bytes = resolver.openInputStream(uri)?.use { it.readBytes() } ?: return@withContext null
                val mime = resolver.getType(uri)
                Pair(bytes, mime)
            } catch (e: Exception) {
                null
            }
        }
    }

    private fun validasi(username: String): Boolean {
        return when {
            username.isEmpty() -> {
                _usernameError.value = pesan(R.string.error_username_kosong)
                false
            }

            username.length < MIN_USERNAME -> {
                _usernameError.value = pesan(R.string.error_username_pendek, MIN_USERNAME)
                false
            }

            else -> {
                _usernameError.value = null
                true
            }
        }
    }

    private fun pesan(idTeks: Int, vararg nilai: Any): String =
        getApplication<Application>().getString(idTeks, *nilai)

    fun pesanErrorSudahDitampilkan() {
        _errorMessage.value = null
    }

    fun pesanBerhasilSudahDitampilkan() {
        _berhasil.value = null
    }

    companion object {
        private const val MIN_USERNAME = 3
    }
}
