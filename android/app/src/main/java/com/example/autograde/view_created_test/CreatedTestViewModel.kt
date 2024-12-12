package com.example.autograde.view_created_test

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autograde.data.api.response.AcceptResponse
import com.example.autograde.data.api.response.AcceptResponseRequest
import com.example.autograde.data.api.response.JoinTestResponse
import com.example.autograde.data.api.response.QuestionsShow
import com.example.autograde.data.api.response.Test
import com.example.autograde.data.api.response.UserTestsItem
import com.example.autograde.data.repository.MainRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreatedTestViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private val _acceptTestResponse = MutableLiveData<String>()
    val acceptTestResponse: LiveData<String> get() = _acceptTestResponse

    private val _getTestResponse = MutableLiveData<JoinTestResponse>()
    val getTestResponse: LiveData<JoinTestResponse> get() = _getTestResponse

    private val _testDetailResponse = MutableLiveData<Test>()
    val testDetailResponse: LiveData<Test> get() = _testDetailResponse

    private val _testQuestionItemResponse = MutableLiveData<List<QuestionsShow>>()
    val testQuestionItemResponse: LiveData<List<QuestionsShow>> get() = _testQuestionItemResponse

    private val _usertestResponse = MutableLiveData<List <UserTestsItem>>()
    val userTestResponse : LiveData<List<UserTestsItem>> get() = _usertestResponse


    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // Fungsi untuk menerima respons
    fun acceptResponse(testId: String, acceptResponse: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Membuat request dan memanggil API
                val response = withContext(Dispatchers.IO) {
                    val request = AcceptResponseRequest(
                        id = testId,
                        acceptResponses = acceptResponse
                    )
                    mainRepository.acceptResponse(request)
                }

                // Menangani response yang berhasil
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        body.message?.let { message ->
                            _acceptTestResponse.postValue(message)
                        }
                        // Update acceptResponses state by copying the test object
                        _testDetailResponse.value?.let { test ->
                            // Create a new instance of test with updated acceptResponses
                            val updatedTest = test.copy(acceptResponses = acceptResponse)
                            _testDetailResponse.postValue(updatedTest)
                        }
                    }
                } else {
                    // Menangani error dari server
                    val errorMessage = response.errorBody()?.string()?.let { errorBody ->
                        try {
                            val errorResponse =
                                Gson().fromJson(errorBody, AcceptResponse::class.java)
                            errorResponse.message
                        } catch (e: Exception) {
                            "Terjadi kesalahan: ${response.message()}"
                        }
                    } ?: "Terjadi kesalahan tak dikenal"
                    _errorMessage.postValue(errorMessage)
                }
            } catch (e: Exception) {
                Log.e("AcceptResponse", "Error: ${e.message}", e)
                _errorMessage.postValue("Terjadi kesalahan: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Fungsi untuk mendapatkan detail test berdasarkan ID
    fun getTestById(testId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Memanggil API untuk mendapatkan detail test
                val response = withContext(Dispatchers.IO) {
                    mainRepository.getTestById(testId)
                }
                if (response.isSuccessful) {
                    showTestById(testId)
                    response.body()?.let { body ->
                        body.test?.let { test ->
                            _testDetailResponse.postValue(test)
                        } ?: run {
                            _errorMessage.postValue(body.message)
                        }
                    }
                } else {
                    // Menangani error dari server
                    val errorMessage = response.errorBody()?.string()?.let { errorBody ->
                        try {
                            val errorResponse =
                                Gson().fromJson(errorBody, JoinTestResponse::class.java)
                            errorResponse.message
                        } catch (e: Exception) {
                            "Terjadi kesalahan: ${response.message()}"
                        }
                    } ?: "Terjadi kesalahan tak dikenal"
                    _getTestResponse.postValue(JoinTestResponse(message = errorMessage))
                }
            } catch (e: Exception) {
                _errorMessage.postValue(e.message ?: "Terjadi kesalahan")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun showTestById(testId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = withContext(Dispatchers.IO) {
                    mainRepository.showTestById(testId)
                }
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        body.test?.questions.let { question ->
                            _testQuestionItemResponse.postValue(question)
                        } ?: run {
                        }
                        body.test?.userTests.let { user ->
                            if (user != null) {
                                val nonNullUsers = user.filterNotNull()
                                _usertestResponse.postValue(nonNullUsers)
                            }
                        }
                    }
                }

            } catch (e: Exception) {
                _errorMessage.postValue(e.message ?: "Terjadi kesalahan")
            } finally {
                _isLoading.value = false
            }
        }
    }

}
