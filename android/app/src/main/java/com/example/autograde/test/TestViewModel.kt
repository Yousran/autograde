package com.example.autograde.test

import android.app.Application
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autograde.data.api.response.JoinTestResponse
import com.example.autograde.data.api.response.StartTestResponse
import com.example.autograde.data.api.response.TestRequest
import com.example.autograde.data.api.response.TestRequestForGuest
import com.example.autograde.data.api.response.TestStart
import com.example.autograde.data.local.entity.UserAnswer
import com.example.autograde.data.local.room.UserAnswerDao
import com.example.autograde.data.local.room.UserAnswerDatabase
import com.example.autograde.data.repository.MainRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TestViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private val _startTestResponse = MutableLiveData<StartTestResponse>()
    val startTestResponse : LiveData<StartTestResponse> get() = _startTestResponse

    private val _testResponse = MutableLiveData<List<TestStart>>()
    val testResponse : LiveData<List<TestStart>> get() = _testResponse

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _savedAnswer = MutableLiveData<UserAnswer>()
    val savedAnswer : LiveData<UserAnswer> get() = _savedAnswer


    fun startTestById(testId : String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = withContext(Dispatchers.IO) {
                    val testRequest = TestRequest(
                        testId = testId
                    )
                    mainRepository.startTestByid(testRequest)
                }
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        body.test?.let { test ->
                            _testResponse.postValue(listOf(test))
                            _startTestResponse.postValue(body)
                        } ?: run {
                            _startTestResponse.postValue(body)
                            _errorMessage.postValue("Test tidak ditemukan")
                        }
                    }
                } else {
                    val errorMessage = response.errorBody()?.string()?.let { errorBody ->
                        try {
                            val errorResponse = Gson().fromJson(errorBody, JoinTestResponse::class.java)
                            errorResponse.message
                        } catch (e: Exception) {
                            "Terjadi kesalahan: ${response.message()}"
                        }
                    } ?: "Terjadi kesalahan tak dikenal"
                    _startTestResponse.postValue(StartTestResponse(message = errorMessage)) // Mengirimkan data default
                }


            } catch (e: Exception) {

            } finally {
                _isLoading.value = false
            }
        }
    }

    fun startTestForGuest(testId : String, username : String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = withContext(Dispatchers.IO) {
                    val testRequestForGuest = TestRequestForGuest(
                        testId = testId,
                        username = username
                    )
                    mainRepository.startTestForGuest(testRequestForGuest)
                }
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        body.test?.let { test ->
                            _testResponse.postValue(listOf(test))
                            _startTestResponse.postValue(body)
                        } ?: run {
                            _startTestResponse.postValue(body)
                            _errorMessage.postValue("Test tidak ditemukan")
                        }
                    }
                } else {
                    val errorMessage = response.errorBody()?.string()?.let { errorBody ->
                        try {
                            val errorResponse =
                                Gson().fromJson(errorBody, JoinTestResponse::class.java)
                            errorResponse.message
                        } catch (e: Exception) {
                            "Terjadi kesalahan: ${response.message()}"
                        }
                    } ?: "Terjadi kesalahan tak dikenal"
                    _startTestResponse.postValue(StartTestResponse(message = errorMessage)) // Mengirimkan data default
                }

            } catch (e: Exception) {

            } finally {
                _isLoading.value = false
            }
        }
    }



}