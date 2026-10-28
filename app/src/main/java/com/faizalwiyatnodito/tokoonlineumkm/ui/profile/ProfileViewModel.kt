package com.faizalwiyatnodito.tokoonlineumkm.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.faizalwiyatnodito.tokoonlineumkm.data.network.User
import com.faizalwiyatnodito.tokoonlineumkm.data.repository.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = UserRepository(application)

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    // Data sesi ditampilkan lebih dulu supaya halaman tidak kosong,
    // lalu diperbarui dengan data terbaru dari database lewat API.
    fun muatProfil() {
        val dariSesi = repository.ambilUser()
        _user.value = dariSesi

        viewModelScope.launch {
            _isLoading.value = true

            val response = repository.ambilProfil(dariSesi.id)

            if (response.status && response.data != null) {
                _user.value = response.data
                _errorMessage.value = null
            } else {
                _errorMessage.value = response.message
            }

            _isLoading.value = false
        }
    }

    fun logout() {
        repository.hapusSesi()
    }
}
