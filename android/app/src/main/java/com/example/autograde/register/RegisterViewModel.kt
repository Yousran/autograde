package com.example.autograde.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autograde.data.api.response.JoinTestResponse
import com.example.autograde.data.api.response.RegisterRequest
import com.example.autograde.data.api.response.RegisterResponse
import com.example.autograde.data.repository.MainRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterViewModel(private val mainRepository: MainRepository) : ViewModel() {


    private val _registerResponse = MutableLiveData<RegisterResponse>()
    val registerResponse: LiveData<RegisterResponse> get() = _registerResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage


    fun registerUser(registerRequest: RegisterRequest) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                val response = withContext(Dispatchers.IO) {
                    mainRepository.registerUser(registerRequest) // Pastikan return-nya Response<RegisterResponse>
                }

                if (response.isSuccessful) {
                    // Respons sukses (status 200-299)
                    response.body()?.let { body ->
                        _registerResponse.postValue(body) // Parsing data sukses
                    } ?: run {
                        _errorMessage.postValue("Respons dari server kosong")
                    }
                } else {
                    val errorMessage = response.errorBody()?.string()?.let { errorBody ->
                        try {
                            val errorResponse = Gson().fromJson(errorBody, RegisterResponse::class.java)
                            errorResponse.message
                        } catch (e: Exception) {
                            "Terjadi kesalahan: ${response.message()}"
                        }
                    } ?: "Terjadi kesalahan tak dikenal"
                    _registerResponse.postValue(RegisterResponse(message = errorMessage))
                    _errorMessage.postValue(errorMessage)
                }
            } catch (e: Exception) {
                // Tangani kesalahan seperti jaringan
                _errorMessage.postValue(e.message ?: "Terjadi kesalahan")
            } finally {
                _isLoading.value = false
            }
        }
    }

}
