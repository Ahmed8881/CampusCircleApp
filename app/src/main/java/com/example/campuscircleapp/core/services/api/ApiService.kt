package com.example.campuscircleapp.core.services.api
import com.example.campuscircleapp.core.models.apiResponse
import com.example.campuscircleapp.features.auth.models.LoginResponse
import com.example.campuscircleapp.features.auth.models.LoginRequest
import com.example.campuscircleapp.features.auth.models.UserDataResponse
import com.example.campuscircleapp.features.auth.models.SignUpRequest
import com.example.campuscircleapp.features.auth.models.SignUpResponse
import com.example.campuscircleapp.features.home.models.DashboardAnalyticsResponse
import retrofit2.Response
import retrofit2.http.*
interface ApiService {
    @POST("User/LoginUser")
    suspend fun login(@Body loginRequest: LoginRequest): Response<apiResponse<LoginResponse>>

    @POST("User/CreateUser")
    suspend fun signup(@Body signupRequest: SignUpRequest): Response<apiResponse<SignUpResponse>>

    @GET("Student/GetStudentDashboardAnalytics")
    suspend fun getStudentDashboardAnalytics(@Header("Authorization") token: String): Response<apiResponse<DashboardAnalyticsResponse>>

    @GET("User/GetUserData")
    suspend fun getUserData(@Header("Authorization") token: String): Response<apiResponse<UserDataResponse>>
}
