package com.example.autograde.create_test

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autograde.data.api.response.CreateTestRequest
import com.example.autograde.data.api.response.CreateTestResponse
import com.example.autograde.data.api.response.CreatedQuestionsItem
import com.example.autograde.data.repository.MainRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateTestViewModel (private val mainRepository: MainRepository) : ViewModel() {

    private val _storeTestResponse = MutableLiveData<CreateTestResponse>()
    val storeTestResponse : LiveData<CreateTestResponse> get() = _storeTestResponse

    private val _createdTestResponse = MutableLiveData<List<CreatedQuestionsItem>>()
    val createdTestResponse : LiveData<List<CreatedQuestionsItem>> get() = _createdTestResponse

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun storeTest(createTestRequest : CreateTestRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = withContext(Dispatchers.IO) {
                    mainRepository.storeTest(createTestRequest)
                }
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        _storeTestResponse.postValue(body)
                        body.test?.questions?.let { test ->
                            _createdTestResponse.postValue(listOf(CreatedQuestionsItem()))
                        } ?: run {
                            _storeTestResponse.postValue(body)
                            _errorMessage.postValue(body.message?:"Gagal")
                        }
                    }
                } else {
                    val errorMessage = response.errorBody()?.string()?.let { errorBody ->
                        try {
                            val errorResponse = Gson().fromJson(errorBody, CreateTestResponse::class.java)
                            errorResponse.message
                        } catch (e: Exception) {
                            "Terjadi kesalahan: ${response.message()}"
                        }
                    } ?: "Terjadi kesalahan tak dikenal"
                    _storeTestResponse.postValue(CreateTestResponse(message = errorMessage)) // Kirimkan data default
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