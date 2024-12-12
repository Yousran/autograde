package com.example.autograde.user_test

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autograde.data.api.response.AnswersItem
import com.example.autograde.data.api.response.JoinTestResponse
import com.example.autograde.data.api.response.Request
import com.example.autograde.data.api.response.StartTestResponse
import com.example.autograde.data.api.response.TestRequest
import com.example.autograde.data.api.response.TestStart
import com.example.autograde.data.api.response.UpdateAnswer
import com.example.autograde.data.api.response.UserTest
import com.example.autograde.data.api.response.UserTestResponse
import com.example.autograde.data.local.entity.UserAnswer
import com.example.autograde.data.repository.MainRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserTestViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private val _userTestResponse = MutableLiveData<UserTest>()
    val userTestResponse: LiveData<UserTest> get() = _userTestResponse

    private val _answerItemResponse = MutableLiveData<List<AnswersItem>>()
    val answerItemResponse: LiveData<List<AnswersItem>> get() = _answerItemResponse

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _updateResponse= MutableLiveData<String>()
    val updateResponse: LiveData<String> get() = _updateResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    fun getUserTestDetail(userTestId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = withContext(Dispatchers.IO) {
                    val userTestIdRequest = Request (
                        userTestId = userTestId
                    )
                    mainRepository.getUserTestDetail(userTestIdRequest)
                }
                if (response.isSuccessful) {
                    response.body()?.userTest?.let { user ->
                        _userTestResponse.postValue(user)
                        val nonNullAnswer = user.answers?.filterNotNull()
                        _answerItemResponse.postValue(nonNullAnswer)
                    } ?: run {
                        _errorMessage.postValue("Test tidak ditemukan")
                    }
                } else {
                    val errorMessage = response.errorBody()?.string()?.let { errorBody ->
                        try {
                            val errorResponse =
                                Gson().fromJson(errorBody, UserTestResponse::class.java)
                            errorResponse.message
                        } catch (e: Exception) {
                            "Terjadi kesalahan: ${response.message()}"
                        }
                    } ?: "Terjadi kesalahan tak dikenal"
                }

            } catch (e: Exception) {

            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateGrade(updateGrade : UpdateAnswer) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = withContext(Dispatchers.IO) {
                    mainRepository.updateAnswer(updateGrade)
                }
                if (response.isSuccessful) {
                    response.body()?.let { user ->
                        _updateResponse.postValue(user.message)
                    } ?: run {
                        _errorMessage.postValue("Test tidak ditemukan")
                    }
                } else {
                    val errorMessage = response.errorBody()?.string()?.let { errorBody ->
                        try {
                            val errorResponse =
                                Gson().fromJson(errorBody, UserTestResponse::class.java)
                            errorResponse.message
                        } catch (e: Exception) {
                            "Terjadi kesalahan: ${response.message()}"
                        }
                    } ?: "Terjadi kesalahan tak dikenal"
                }

            } catch (e: Exception) {

            } finally {
                _isLoading.value = false
            }
        }
    }
}