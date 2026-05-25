package com.example.campuscircleapp.features.notifications.services

import com.example.campuscircleapp.core.models.ServiceResult
import com.example.campuscircleapp.core.utils.RetrofitInstance
import com.example.campuscircleapp.features.notifications.models.NotificationModel
import com.google.gson.Gson

class NotificationService {
    suspend fun getMyNotifications(token: String): ServiceResult<List<NotificationModel>> {
        val response = RetrofitInstance.api.getMyNotifications("Bearer $token")
        val body = response.body()

        return if (response.isSuccessful && body != null) {
            ServiceResult(body, "Success")
        } else {
            val errorJson = response.errorBody()?.string()
            val errorMessage = try {
                val errorObj = Gson().fromJson(errorJson, Map::class.java)
                errorObj["errorMessage"]?.toString() ?: "Failed to fetch notifications"
            } catch (e: Exception) {
                "Server Error: ${response.code()}"
            }
            throw Exception(errorMessage)
        }
    }

    suspend fun markAsRead(token: String, id: Long): ServiceResult<Unit> {
        val response = RetrofitInstance.api.markAsRead("Bearer $token", id)
        return if (response.isSuccessful) {
            ServiceResult(Unit, "Marked as read")
        } else {
            throw Exception("Failed to mark as read")
        }
    }

    suspend fun clearNotifications(token: String, ids: List<Long>): ServiceResult<Any> {
        val response = RetrofitInstance.api.clearNotifications("Bearer $token", ids)
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            ServiceResult(body.data ?: Unit, body.responseMessage)
        } else {
            throw Exception("Failed to clear notifications")
        }
    }
}
