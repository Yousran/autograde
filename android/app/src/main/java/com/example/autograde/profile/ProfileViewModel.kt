package com.example.autograde.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autograde.data.api.response.ProfileResponse
import com.example.autograde.data.api.response.TestsItem
import com.example.autograde.data.repository.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private val _profileResponse = MutableLiveData<ProfileResponse>()
    val profileResponse: LiveData<ProfileResponse> get() = _profileResponse

    private val _allTestResponse = MutableLiveData<List<TestsItem>>()
    val allTestResponse: LiveData<List<TestsItem>> get() = _allTestResponse

    private val _pastTestResponse = MutableLiveData<List<TestsItem>>()
    val pastTestResponse: LiveData<List<TestsItem>> get() = _pastTestResponse

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        getProfile()
        getAllTest()
        getPastTest()
    }

    fun getProfile() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = withContext(Dispatchers.IO) {
                    mainRepository.getProfile()
                }

                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        _profileResponse.postValue(body)
                    } ?: run {
                        _errorMessage.postValue("Respons dari server kosong")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    _errorMessage.postValue(
                        errorBody ?: "Terjadi kesalahan tak dikenal"
                    )
                }
            } catch (e: Exception) {
                _errorMessage.postValue(e.message ?: "Terjadi kesalahan")
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun getAllTest() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = withContext(Dispatchers.IO) {
                    mainRepository.getAllTest()
                }

                if (response.isSuccessful) {
                    response.body()?.tests?.let { body ->
                        val nonNullTests = body.filterNotNull()
                        _allTestResponse.postValue(nonNullTests)
                    } ?: run {
                        _errorMessage.postValue("Your created test is empty")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    _errorMessage.postValue(
                        errorBody ?: "Something wrong happened"
                    )
                }
            } catch (e: Exception) {
                _errorMessage.postValue(e.message ?: "Something wrong happened")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getPastTest() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = withContext(Dispatchers.IO) {
                    mainRepository.getPastTest()
                }

                if (response.isSuccessful) {
                    response.body()?.tests?.let { body ->
                        val nonNullTests = body.filterNotNull()
                        _pastTestResponse.postValue(nonNullTests)
                    } ?: run {
                        _errorMessage.postValue("Your created test is empty")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    _errorMessage.postValue(
                        errorBody ?: "Something wrong happened"
                    )
                }
            } catch (e: Exception) {
                _errorMessage.postValue(e.message ?: "Something wrong happened")
            } finally {
                _isLoading.value = false
            }
        }
    }



}
