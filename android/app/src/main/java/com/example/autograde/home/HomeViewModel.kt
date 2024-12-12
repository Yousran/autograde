package com.example.autograde.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autograde.data.api.response.JoinTestResponse
import com.example.autograde.data.api.response.Test
import com.example.autograde.data.repository.MainRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel (private val mainRepository: MainRepository ) : ViewModel() {


    private val _joinTestResponse = MutableLiveData<JoinTestResponse>()
    val joinTestResponse : LiveData<JoinTestResponse> get() = _joinTestResponse

    private val _testResponse = MutableLiveData<List<Test>>()
    val testResponse : LiveData<List<Test>> get() = _testResponse

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    fun getTestById(testId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = withContext(Dispatchers.IO) {
                    mainRepository.getTestById(testId)
                }
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        body.test?.let { test ->
                            _testResponse.postValue(listOf(test))
                            _joinTestResponse.postValue(body)
                        } ?: run {
                            _joinTestResponse.postValue(body)
                        }
                    }
                } else {
                    val errorMessage = response.errorBody()?.string()?.let { errorBody ->
                        // Parsing JSON jika perlu, misalnya menggunakan Gson
                        try {
                            val errorResponse = Gson().fromJson(errorBody, JoinTestResponse::class.java)
                            errorResponse.message
                        } catch (e: Exception) {
                            "Terjadi kesalahan: ${response.message()}"
                        }
                    } ?: "Terjadi kesalahan tak dikenal"
                    _joinTestResponse.postValue(JoinTestResponse(message = errorMessage)) // Mengirimkan data default
                }


            } catch (e: Exception) {
                _errorMessage.postValue(e.message ?: "Terjadi kesalahan")
            } finally {
                _isLoading.value = false
            }
        }
    }




}