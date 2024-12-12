package com.example.autograde.data.repository

import com.example.autograde.data.api.response.AcceptResponse
import com.example.autograde.data.api.response.AcceptResponseRequest
import com.example.autograde.data.api.response.AllTestResponse
import com.example.autograde.data.api.response.CreateTestRequest
import com.example.autograde.data.api.response.CreateTestResponse
import com.example.autograde.data.api.response.JoinTestResponse
import com.example.autograde.data.api.response.LoginResponse
import com.example.autograde.data.api.response.PastTestDetailResponse
import com.example.autograde.data.api.response.PastTestResponse
import com.example.autograde.data.api.response.ProfileResponse
import com.example.autograde.data.api.response.RegisterRequest
import com.example.autograde.data.api.response.RegisterResponse
import com.example.autograde.data.api.response.Request
import com.example.autograde.data.api.response.ShowTestResponse
import com.example.autograde.data.api.response.StartTestResponse
import com.example.autograde.data.api.response.SubmitTestRequest
import com.example.autograde.data.api.response.SubmitTestResponse
import com.example.autograde.data.api.response.TestRequest
import com.example.autograde.data.api.response.TestRequestForGuest
import com.example.autograde.data.api.response.UpdateAnswer
import com.example.autograde.data.api.response.UpdateUserGradeResponse
import com.example.autograde.data.api.response.User
import com.example.autograde.data.api.response.UserTestResponse
import com.example.autograde.data.api.retrofit.ApiService
import com.example.autograde.data.pref.UserModel
import com.example.autograde.data.pref.UserPreference
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class MainRepository (private val apiService: ApiService, private val userPreference: UserPreference) {


    suspend fun registerUser(registerRequest: RegisterRequest): Response<RegisterResponse> {
        return apiService.register(registerRequest)
    }

    suspend fun loginUser(loginRequest : User): Response<LoginResponse> {
        return apiService.login(loginRequest)
    }
    suspend fun getTestById(testId : String) : Response<JoinTestResponse> {
        return apiService.getTestById(testId)
    }

    suspend fun startTestByid(requestBody : TestRequest) : Response<StartTestResponse> {
        return apiService.startTestById(requestBody)
    }

    suspend fun startTestForGuest(requestBody : TestRequestForGuest) : Response<StartTestResponse> {
        return apiService.startTestForGuset(requestBody)
    }

    suspend fun storeTest(requestBody : CreateTestRequest) : Response<CreateTestResponse> {
        return apiService.storeTest(requestBody)
    }

    suspend fun submitTest (request : SubmitTestRequest) : Response<SubmitTestResponse> {
        return apiService.submitTestAnswers(request)
    }

    suspend fun acceptResponse ( request : AcceptResponseRequest) : Response<AcceptResponse> {
        return apiService.acceptResponse(request)
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

    suspend fun getProfile () : Response<ProfileResponse> {
        return apiService.getProfile()
    }
    suspend fun getAllTest () : Response<AllTestResponse> {
        return apiService.getAllTest()
    }
    suspend fun getPastTest () : Response<PastTestResponse> {
        return apiService.getPastTest()
    }

    suspend fun showTestById(testId : String) : Response<ShowTestResponse> {
        return apiService.showTestById(testId)
    }

    suspend fun showPastTestDetail(testId : String) : Response<PastTestDetailResponse> {
        return apiService.showPastTestDetail(testId)
    }

    suspend fun getUserTestDetail(userTestId : Request) : Response<UserTestResponse> {
        return apiService.getUserTestDetail(userTestId)
    }
    suspend fun updateAnswer (updateAnswer : UpdateAnswer) : Response<UpdateUserGradeResponse> {
        return apiService.updateAnswerGrade(updateAnswer)
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
