package com.faizalwiyatnodito.tokoonlineumkm.ui.register

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.faizalwiyatnodito.tokoonlineumkm.R
import com.faizalwiyatnodito.tokoonlineumkm.data.repository.UserRepository
import kotlinx.coroutines.launch

class RegisterViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = UserRepository(application)

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _usernameError = MutableLiveData<String?>()
    val usernameError: LiveData<String?> = _usernameError

    private val _emailError = MutableLiveData<String?>()
    val emailError: LiveData<String?> = _emailError

    private val _passwordError = MutableLiveData<String?>()
    val passwordError: LiveData<String?> = _passwordError

    private val _konfirmasiPasswordError = MutableLiveData<String?>()
    val konfirmasiPasswordError: LiveData<String?> = _konfirmasiPasswordError

    private val _noTlpError = MutableLiveData<String?>()
    val noTlpError: LiveData<String?> = _noTlpError

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _registerBerhasil = MutableLiveData<String?>()
    val registerBerhasil: LiveData<String?> = _registerBerhasil

    fun register(
        username: String,
        email: String,
        password: String,
        konfirmasiPassword: String,
        noTlp: String
    ) {
        val usernameBersih = username.trim()
        val emailBersih = email.trim()
        val noTlpBersih = noTlp.trim()

        if (!validasiInput(usernameBersih, emailBersih, password, konfirmasiPassword, noTlpBersih)) {
            return
        }

        viewModelScope.launch {
            _isLoading.value = true

            val response = repository.register(usernameBersih, emailBersih, password, noTlpBersih)

            if (response.status) {
                _registerBerhasil.value = response.message
            } else {
                _errorMessage.value = response.message
            }

            _isLoading.value = false
        }
    }

    private fun validasiInput(
        username: String,
        email: String,
        password: String,
        konfirmasiPassword: String,
        noTlp: String
    ): Boolean {
        var valid = true

        when {
            username.isEmpty() -> {
                _usernameError.value = pesan(R.string.error_username_kosong)
                valid = false
            }

            username.length < MIN_USERNAME -> {
                _usernameError.value = pesan(R.string.error_username_pendek, MIN_USERNAME)
                valid = false
            }

            else -> _usernameError.value = null
        }

        when {
            email.isEmpty() -> {
                _emailError.value = pesan(R.string.error_email_kosong)
                valid = false
            }

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                _emailError.value = pesan(R.string.error_email_tidak_valid)
                valid = false
            }

            else -> _emailError.value = null
        }

        when {
            password.isEmpty() -> {
                _passwordError.value = pesan(R.string.error_password_kosong)
                valid = false
            }

            password.length < MIN_PASSWORD -> {
                _passwordError.value = pesan(R.string.error_password_pendek, MIN_PASSWORD)
                valid = false
            }

            else -> _passwordError.value = null
        }

        when {
            konfirmasiPassword.isEmpty() -> {
                _konfirmasiPasswordError.value = pesan(R.string.error_konfirmasi_kosong)
                valid = false
            }

            konfirmasiPassword != password -> {
                _konfirmasiPasswordError.value = pesan(R.string.error_konfirmasi_tidak_sama)
                valid = false
            }

            else -> _konfirmasiPasswordError.value = null
        }

        when {
            noTlp.isEmpty() -> {
                _noTlpError.value = pesan(R.string.error_no_tlp_kosong)
                valid = false
            }

            noTlp.length < MIN_NO_TLP || !noTlp.all { it.isDigit() } -> {
                _noTlpError.value = pesan(R.string.error_no_tlp_tidak_valid, MIN_NO_TLP)
                valid = false
            }

            else -> _noTlpError.value = null
        }

        return valid
    }

    private fun pesan(idTeks: Int, vararg nilai: Any): String =
        getApplication<Application>().getString(idTeks, *nilai)

    fun pesanErrorSudahDitampilkan() {
        _errorMessage.value = null
    }

    fun pesanBerhasilSudahDitampilkan() {
        _registerBerhasil.value = null
    }

    companion object {
        private const val MIN_USERNAME = 3
        private const val MIN_PASSWORD = 6
        private const val MIN_NO_TLP = 9
    }
}
