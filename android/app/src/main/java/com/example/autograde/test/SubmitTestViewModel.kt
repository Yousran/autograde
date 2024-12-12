package com.example.autograde.test

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autograde.data.api.response.Answers
import com.example.autograde.data.api.response.ResultsItem
import com.example.autograde.data.api.response.SubmitTestRequest
import com.example.autograde.data.api.response.SubmitTestResponse
import com.example.autograde.data.repository.MainRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SubmitTestViewModel (private val mainRepository: MainRepository) : ViewModel() {

    private val _submitTestResponse = MutableLiveData<SubmitTestResponse>()
    val submitTestResponse : LiveData<SubmitTestResponse> get() = _submitTestResponse

    private val _resultItemResponse = MutableLiveData<List<ResultsItem>>()
    val resultItemResponse : LiveData<List<ResultsItem>> get() = _resultItemResponse

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    fun submitTest(userTestId: String, answers: List<Answers>, timeLeft : Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = withContext(Dispatchers.IO) {
                    val request = SubmitTestRequest (
                        userTestId = userTestId,
                        questions = answers,
                        timeLeft = timeLeft
                    )
                    mainRepository.submitTest(request)
                }
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        body.results?.let { test ->
                            _resultItemResponse.postValue(listOf(ResultsItem()))
                            _submitTestResponse.postValue(body)
                            _errorMessage.postValue(body.message ?: "Something wrong")
                        } ?: run {
                            _submitTestResponse.postValue(body)
                            _errorMessage.postValue("Gagal")
                        }
                    }
                } else {
                    val errorMessage = response.errorBody()?.string()?.let { errorBody ->
                        try {
                            val errorResponse = Gson().fromJson(errorBody, SubmitTestResponse::class.java)
                            errorResponse.message
                        } catch (e: Exception) {
                            "Terjadi kesalahan: ${response.message()}"
                        }
                    } ?: "Terjadi kesalahan tak dikenal"
                    _submitTestResponse.postValue(SubmitTestResponse(message = errorMessage)) // Kirimkan data default
                }
            } catch (e: Exception) {
                Log.e("SubmitTest", "Error: ${e.message}", e)
                _errorMessage.postValue("Terjadi kesalahan: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

}