package com.example.campuscircleapp.core.services.api
import com.example.campuscircleapp.core.models.apiResponse
import com.example.campuscircleapp.features.auth.models.LoginResponse
import com.example.campuscircleapp.features.auth.models.LoginRequest
import retrofit2.Response
import retrofit2.http.*
interface ApiService {
    @POST("User/LoginUser")
    suspend fun login(@Body loginRequest: LoginRequest): Response<apiResponse<LoginResponse>>
}
