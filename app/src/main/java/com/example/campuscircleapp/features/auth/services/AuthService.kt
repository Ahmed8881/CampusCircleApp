package com.example.campuscircleapp.features.auth.services

import com.example.campuscircleapp.core.models.ServiceResult
import com.example.campuscircleapp.core.utils.RetrofitInstance
import com.example.campuscircleapp.features.auth.models.GoogleAuthRequest
import com.example.campuscircleapp.features.auth.models.LoginRequest
import com.example.campuscircleapp.features.auth.models.LoginResponse
import com.example.campuscircleapp.features.auth.models.SignUpRequest
import com.example.campuscircleapp.features.auth.models.SignUpResponse
import com.example.campuscircleapp.features.auth.models.UpdateUserRequest
import com.example.campuscircleapp.features.auth.models.UserDeviceTokenDTO
import com.google.gson.Gson

class AuthService {
    suspend fun login(request: LoginRequest): ServiceResult<LoginResponse> {
        val response = RetrofitInstance.api.login(request)
        val body = response.body()

        if (response.isSuccessful && body != null) {
            if (body.responseCode == 200 && body.data != null) {
                return ServiceResult(body.data, body.responseMessage)
            } else {
                throw Exception(body.errorMessage ?: body.responseMessage)
            }
        } else {
            // READ THE ACTUAL ERROR FROM SERVER
            val errorJson = response.errorBody()?.string()
            val errorMessage =
                    try {
                        // Try to parse the server's error response structure
                        val errorObj = Gson().fromJson(errorJson, Map::class.java)
                        errorObj["errorMessage"]?.toString()
                                ?: errorObj["responseMessage"]?.toString()
                    } catch (e: Exception) {
                        null
                    }

            throw Exception(errorMessage ?: "Server Error: ${response.code()}")
        }
    }
    suspend fun signUp(request: SignUpRequest): ServiceResult<SignUpResponse> {
        val response = RetrofitInstance.api.signup(request)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            if (body.responseCode == 200 && body.data != null) {
                return ServiceResult(body.data, body.responseMessage)
            } else {
                throw Exception(body.errorMessage ?: body.responseMessage)
            }
        } else {

            val errorJson = response.errorBody()?.string()
            val errorMessage =
                    try {
                        // Try to parse the server's error response structure
                        val errorObj = Gson().fromJson(errorJson, Map::class.java)
                        errorObj["errorMessage"]?.toString()
                                ?: errorObj["responseMessage"]?.toString()
                    } catch (e: Exception) {
                        null
                    }

            throw Exception(errorMessage ?: "Server Error: ${response.code()}")
        }
    }
    suspend fun googleSignup(request: GoogleAuthRequest): ServiceResult<Any> {
        val response = RetrofitInstance.api.googleSignup(request)
        val body = response.body()

        if (response.isSuccessful && body != null) {
            if (body.responseCode == 200 && body.data != null) {
                return ServiceResult(body.data, body.responseMessage)
            } else {
                throw Exception(body.errorMessage ?: body.responseMessage)
            }
        } else {
            val errorJson = response.errorBody()?.string()
            val errorMessage =
                    try {
                        val errorObj = Gson().fromJson(errorJson, Map::class.java)
                        errorObj["errorMessage"]?.toString()
                                ?: errorObj["responseMessage"]?.toString()
                    } catch (e: Exception) {
                        null
                    }
            throw Exception(errorMessage ?: "Server Error: ${response.code()}")
        }
    }

    suspend fun googleSignin(request: GoogleAuthRequest): ServiceResult<LoginResponse> {
        val response = RetrofitInstance.api.googleSignin(request)
        val body = response.body()

        if (response.isSuccessful && body != null) {
            if (body.responseCode == 200 && body.data != null) {
                return ServiceResult(body.data, body.responseMessage)
            } else {
                throw Exception(body.errorMessage ?: body.responseMessage)
            }
        } else {
            val errorJson = response.errorBody()?.string()
            val errorMessage =
                    try {
                        val errorObj = Gson().fromJson(errorJson, Map::class.java)
                        errorObj["errorMessage"]?.toString()
                                ?: errorObj["responseMessage"]?.toString()
                    } catch (e: Exception) {
                        null
                    }
            throw Exception(errorMessage ?: "Server Error: ${response.code()}")
        }
    }

    suspend fun updateGoogleUser(request: UpdateUserRequest): ServiceResult<LoginResponse> {
        val response = RetrofitInstance.api.updateGoogleUser(request)
        val body = response.body()

        if (response.isSuccessful && body != null) {
            if (body.responseCode == 200 && body.data != null) {
                return ServiceResult(body.data, body.responseMessage)
            } else {
                throw Exception(body.errorMessage ?: body.responseMessage)
            }
        } else {
            val errorJson = response.errorBody()?.string()
            val errorMessage =
                    try {
                        val errorObj = Gson().fromJson(errorJson, Map::class.java)
                        errorObj["errorMessage"]?.toString()
                                ?: errorObj["responseMessage"]?.toString()
                    } catch (e: Exception) {
                        null
                    }
            throw Exception(errorMessage ?: "Server Error: ${response.code()}")
        }
    }

    suspend fun registerDevice(token: String, deviceTokenDto: UserDeviceTokenDTO): ServiceResult<Any> {
        val response = RetrofitInstance.api.registerDevice("Bearer $token", deviceTokenDto)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            if (body.responseCode == 200 && body.data != null) {
                return ServiceResult(body.data, body.responseMessage)
            } else {
                throw Exception(body.errorMessage ?: body.responseMessage)
            }
        } else {
            val errorJson = response.errorBody()?.string()
            val errorMessage =
                    try {
                        val errorObj = Gson().fromJson(errorJson, Map::class.java)
                        errorObj["errorMessage"]?.toString()
                                ?: errorObj["responseMessage"]?.toString()
                    } catch (e: Exception) {
                        null
                    }
            throw Exception(errorMessage ?: "Server Error: ${response.code()}")
        }
    }
}
