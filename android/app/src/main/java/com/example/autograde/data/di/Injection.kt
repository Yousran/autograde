package com.example.autograde.data.di

import android.content.Context
import com.example.autograde.data.repository.MainRepository
import com.example.autograde.data.api.retrofit.ApiConfig
import com.example.autograde.data.pref.UserPreference
import com.example.autograde.data.pref.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): MainRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return MainRepository.getInstance(apiService, pref)
    }
}