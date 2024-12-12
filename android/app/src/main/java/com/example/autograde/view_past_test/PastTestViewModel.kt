package com.example.autograde.view_past_test

import androidx.compose.ui.util.fastFilterNotNull
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autograde.data.api.response.JoinTestResponse
import com.example.autograde.data.api.response.PastTestDetailResponse
import com.example.autograde.data.api.response.QuestionsShow
import com.example.autograde.data.api.response.ResultsItemPast
import com.example.autograde.data.repository.MainRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PastTestViewModel(private val mainRepository: MainRepository) : ViewModel() {


    private val _pastTestDetailResponse = MutableLiveData<PastTestDetailResponse>()
    val pastTestDetailResponse: LiveData<PastTestDetailResponse> get() = _pastTestDetailResponse


    private val _resultItemPastResponse = MutableLiveData<List<ResultsItemPast>>()
    val resultItemPastResponse: LiveData<List<ResultsItemPast>> get() = _resultItemPastResponse


    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun showPastTestDetail(testId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = withContext(Dispatchers.IO) {
                    mainRepository.showPastTestDetail(testId)
                }
                if (response.isSuccessful)
                    response.body()?.let { body ->
                        _pastTestDetailResponse.postValue(body)
                        body.results?.let { test ->
                            val nonNullItem = test.filterNotNull()
                            _resultItemPastResponse.postValue(nonNullItem)
                        } ?: run {
                            _errorMessage.postValue("Something wrong happened")
                        }

                    } else {
                    _errorMessage.postValue("Something wrong happened")
                }

            } catch (e: Exception) {
                _errorMessage.postValue(e.message ?: "Something wrong happened")
            } finally {
                _isLoading.value = false
            }
        }
    }

}
