package com.example.autograde.data.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.autograde.create_test.CreateTestViewModel
import com.example.autograde.data.local.room.UserAnswerDao
import com.example.autograde.data.local.room.UserAnswerDatabase
import com.example.autograde.data.repository.MainRepository
import com.example.autograde.data.pref.UserPreference
import com.example.autograde.data.pref.dataStore
import com.example.autograde.home.HomeViewModel
import com.example.autograde.login.LoginViewModel
import com.example.autograde.profile.ProfileViewModel
import com.example.autograde.register.RegisterViewModel
import com.example.autograde.test.SubmitTestViewModel
import com.example.autograde.test.TestViewModel
import com.example.autograde.user_test.UserTestViewModel
import com.example.autograde.view_created_test.CreatedTestViewModel
import com.example.autograde.view_past_test.PastTestViewModel


class ViewModelFactory(
    private val mainRespository: MainRepository,
    userPreference: UserPreference,
    private val userAnswerDao: UserAnswerDao
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(mainRespository) as T
        }
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(mainRespository) as T
        }
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(mainRespository) as T
        }
        if (modelClass.isAssignableFrom(TestViewModel::class.java)) {
            return TestViewModel(mainRespository, userAnswerDao) as T
        }
        if (modelClass.isAssignableFrom(SubmitTestViewModel::class.java)) {
            return SubmitTestViewModel(mainRespository) as T
        }
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(mainRespository) as T
        }
        if (modelClass.isAssignableFrom(CreateTestViewModel::class.java)) {
            return CreateTestViewModel(mainRespository) as T
        }
        if (modelClass.isAssignableFrom(CreatedTestViewModel::class.java)) {
            return CreatedTestViewModel(mainRespository) as T
        }

        if (modelClass.isAssignableFrom(UserTestViewModel::class.java)) {
            return UserTestViewModel(mainRespository) as T
        }
        if (modelClass.isAssignableFrom(PastTestViewModel::class.java)) {
            return PastTestViewModel(mainRespository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                val mainRepository = Injection.provideRepository(context)
                val userPreference = UserPreference.getInstance(context.dataStore)
                val db = UserAnswerDatabase.getInstance(context)
                val userAnswerDao = db.userAnswerDao()

                instance ?: ViewModelFactory(mainRepository, userPreference, userAnswerDao)
            }.also { instance = it }
    }
}