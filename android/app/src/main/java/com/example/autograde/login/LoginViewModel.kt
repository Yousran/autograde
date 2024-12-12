package com.example.autograde.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.asLiveData
import com.example.autograde.data.api.response.LoginResponse
import com.example.autograde.data.api.response.RegisterResponse
import com.example.autograde.data.api.response.User
import com.example.autograde.data.repository.MainRepository
import com.example.autograde.data.pref.UserModel
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(private val mainRepository: MainRepository) : ViewModel() {


    private val _loginResponse = MutableLiveData<LoginResponse>()
    val loginResponse: LiveData<LoginResponse> get() = _loginResponse

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = withContext(Dispatchers.IO) {
                    val loginRequest = User(
                        email = email,
                        password = password
                    )
                    mainRepository.loginUser(loginRequest)
                }

                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        response.body()?.let { response ->
                            mainRepository.getProfile().body().let { profile ->
                                mainRepository.saveSession(
                                        UserModel(
                                        username = response.username ?: "",
                                        email = email,
                                        token = response.token ?: "",
                                        isLogin = true,
                                        profilePictureUrl = profile?.profilePictureUrl?:""
                                    )
                                )
                            }
                            _loginResponse.postValue(body)

                        }
                        _loginResponse.postValue(body)
                    } ?: run {
                        _errorMessage.postValue("Respons dari server kosong")
                    }
                } else {
                    val errorMessage = response.errorBody()?.string()?.let { errorBody ->
                        try {
                            val errorResponse =
                                Gson().fromJson(errorBody, LoginResponse::class.java)
                            errorResponse.message
                        } catch (e: Exception) {
                            "Terjadi kesalahan: ${response.message()}"
                        }
                    } ?: "Terjadi kesalahan tak dikenal"
                    _errorMessage.postValue(errorMessage)
                }
            } catch (e: Exception) {
                _errorMessage.postValue(e.message ?: "Terjadi kesalahan")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getSession(): LiveData<UserModel> {
        return mainRepository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            mainRepository.logout()
        }
    }

}