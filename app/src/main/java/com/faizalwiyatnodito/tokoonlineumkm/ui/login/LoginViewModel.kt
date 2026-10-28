package com.faizalwiyatnodito.tokoonlineumkm.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.faizalwiyatnodito.tokoonlineumkm.R
import com.faizalwiyatnodito.tokoonlineumkm.data.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = UserRepository(application)

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _usernameError = MutableLiveData<String?>()
    val usernameError: LiveData<String?> = _usernameError

    private val _passwordError = MutableLiveData<String?>()
    val passwordError: LiveData<String?> = _passwordError

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _loginBerhasil = MutableLiveData<Boolean>()
    val loginBerhasil: LiveData<Boolean> = _loginBerhasil

    fun login(username: String, password: String) {
        val usernameBersih = username.trim()

        if (!validasiInput(usernameBersih, password)) return

        viewModelScope.launch {
            _isLoading.value = true

            val response = repository.login(usernameBersih, password)

            if (response.status && response.data != null) {
                repository.simpanSesi(response.data)
                _loginBerhasil.value = true
            } else {
                _errorMessage.value = response.message
            }

            _isLoading.value = false
        }
    }

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

    private fun pesan(idTeks: Int): String = getApplication<Application>().getString(idTeks)

    fun pesanErrorSudahDitampilkan() {
        _errorMessage.value = null
    }

    fun navigasiSudahDilakukan() {
        _loginBerhasil.value = false
    }
}
