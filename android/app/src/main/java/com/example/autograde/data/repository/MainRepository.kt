package com.example.autograde.data.repository

import com.example.autograde.data.api.response.LoginResponse
import com.example.autograde.data.api.response.RegisterRequest
import com.example.autograde.data.api.response.RegisterResponse
import com.example.autograde.data.api.response.User
import com.example.autograde.data.api.retrofit.ApiService
import com.example.autograde.data.pref.UserModel
import com.example.autograde.data.pref.UserPreference
import kotlinx.coroutines.flow.Flow

class MainRepository (private val apiService: ApiService, private val userPreference: UserPreference) {

    suspend fun registerUser(registerRequest: RegisterRequest): RegisterResponse {
        return apiService.register(registerRequest)
    }

    suspend fun loginUser(loginRequest : User): LoginResponse {
        return apiService.login(loginRequest)
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }


    companion object {
        @Volatile
        private var INSTANCE: MainRepository? = null

        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ): MainRepository = INSTANCE ?: synchronized(this) {
            INSTANCE ?: MainRepository(apiService, userPreference)
        }.also { INSTANCE = it }
    }
}