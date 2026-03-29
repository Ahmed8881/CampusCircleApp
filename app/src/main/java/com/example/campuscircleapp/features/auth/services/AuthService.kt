package com.example.campuscircleapp.features.auth.services
import com.example.campuscircleapp.core.utils.RetrofitInstance
import com.example.campuscircleapp.features.auth.models.LoginRequest
import com.example.campuscircleapp.features.auth.models.LoginResponse
import com.google.gson.Gson

class AuthService {
    suspend fun login(request : LoginRequest) : LoginResponse {
        val response = RetrofitInstance.api.login(request)
        val body = response.body()

        if (response.isSuccessful && body != null) {
            if (body.responseCode == 200 && body.data != null) {
                return body.data
            } else {
                throw Exception(body.errorMessage ?: body.responseMessage)
            }
        } else {
            // READ THE ACTUAL ERROR FROM SERVER
            val errorJson = response.errorBody()?.string()
            val errorMessage = try {
                // Try to parse the server's error response structure
                val errorObj = Gson().fromJson(errorJson, Map::class.java)
                errorObj["errorMessage"]?.toString() ?: errorObj["responseMessage"]?.toString()
            } catch (e: Exception) {
                null
            }

            throw Exception(errorMessage ?: "Server Error: ${response.code()}")
        }
    }
}
