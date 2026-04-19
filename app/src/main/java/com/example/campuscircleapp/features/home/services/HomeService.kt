package com.example.campuscircleapp.features.home.services

import com.example.campuscircleapp.core.models.ServiceResult
import com.example.campuscircleapp.core.utils.RetrofitInstance
import com.example.campuscircleapp.features.auth.models.UserDataResponse
import com.example.campuscircleapp.features.home.models.AdminCourseResponse
import com.example.campuscircleapp.features.home.models.AdminDashboardResponse
import com.example.campuscircleapp.features.home.models.AttendanceHistoryItem
import com.example.campuscircleapp.features.home.models.DashboardAnalyticsResponse
import com.example.campuscircleapp.features.home.models.EnrollmentByCourseResponse
import com.example.campuscircleapp.features.home.models.MarkAttendanceRequest
import com.example.campuscircleapp.features.home.models.StudentEnrollmentResponse
import com.google.gson.Gson

class HomeService {

    suspend fun getDashboardAnalytics(token: String): ServiceResult<DashboardAnalyticsResponse> {
        val response = RetrofitInstance.api.getStudentDashboardAnalytics("Bearer $token")
        val body = response.body()

        if (response.isSuccessful && body != null) {
            if (body.responseCode == 200 && body.data != null) {
                return ServiceResult(body.data, body.responseMessage)
            }
            throw Exception(body.errorMessage ?: body.responseMessage)
        }

        throw Exception(parseErrorMessage(response.errorBody()?.string(), response.code()))
    }

    suspend fun getUserData(token: String): ServiceResult<UserDataResponse> {
        val response = RetrofitInstance.api.getUserData("Bearer $token")
        val body = response.body()

        if (response.isSuccessful && body != null) {
            if (body.responseCode == 200 && body.data != null) {
                return ServiceResult(body.data, body.responseMessage)
            }
            throw Exception(body.errorMessage ?: body.responseMessage)
        }

        throw Exception(parseErrorMessage(response.errorBody()?.string(), response.code()))
    }

    suspend fun getStudentCourses(token: String): ServiceResult<List<StudentEnrollmentResponse>> {
        val response = RetrofitInstance.api.getStudentCourses("Bearer $token")
        val body = response.body()

        if (response.isSuccessful && body != null) {
            if (body.responseCode == 200 && body.data != null) {
                return ServiceResult(body.data, body.responseMessage)
            }
            throw Exception(body.errorMessage ?: body.responseMessage)
        }

        throw Exception(parseErrorMessage(response.errorBody()?.string(), response.code()))
    }

    suspend fun getAttendanceHistory(token: String, courseId: Long): ServiceResult<List<AttendanceHistoryItem>> {
        val response = RetrofitInstance.api.getAttendanceHistory("Bearer $token", courseId)
        val body = response.body()

        if (response.isSuccessful && body != null) {
            if (body.responseCode == 200 && body.data != null) {
                return ServiceResult(body.data, body.responseMessage)
            }
            throw Exception(body.errorMessage ?: body.responseMessage)
        }

        throw Exception(parseErrorMessage(response.errorBody()?.string(), response.code()))
    }

    suspend fun getAdminDashboardAnalytics(token: String, spaceId: Long): ServiceResult<AdminDashboardResponse> {
        val response = RetrofitInstance.api.getAdminDashboardAnalytics("Bearer $token", spaceId)
        val body = response.body()

        if (response.isSuccessful && body != null) {
            if (body.responseCode == 200 && body.data != null) {
                return ServiceResult(body.data, body.responseMessage)
            }
            throw Exception(body.errorMessage ?: body.responseMessage)
        }

        throw Exception(parseErrorMessage(response.errorBody()?.string(), response.code()))
    }

    suspend fun getAllCourses(token: String): ServiceResult<List<AdminCourseResponse>> {
        val response = RetrofitInstance.api.getAllCourses("Bearer $token")
        val body = response.body()

        if (response.isSuccessful && body != null) {
            if (body.responseCode == 200 && body.data != null) {
                return ServiceResult(body.data, body.responseMessage)
            }
            throw Exception(body.errorMessage ?: body.responseMessage)
        }

        throw Exception(parseErrorMessage(response.errorBody()?.string(), response.code()))
    }

    suspend fun getEnrollmentsByCourseId(token: String, courseId: Long): ServiceResult<List<EnrollmentByCourseResponse>> {
        val response = RetrofitInstance.api.getEnrollmentsByCourseId("Bearer $token", courseId)
        val body = response.body()

        if (response.isSuccessful && body != null) {
            if (body.responseCode == 200 && body.data != null) {
                return ServiceResult(body.data, body.responseMessage)
            }
            throw Exception(body.errorMessage ?: body.responseMessage)
        }

        throw Exception(parseErrorMessage(response.errorBody()?.string(), response.code()))
    }

    suspend fun markAttendance(token: String, request: MarkAttendanceRequest): ServiceResult<Unit> {
        val response = RetrofitInstance.api.markAttendance("Bearer $token", request)
        val body = response.body()

        if (response.isSuccessful && body != null) {
            if (body.responseCode == 200) {
                return ServiceResult(Unit, body.responseMessage)
            }
            throw Exception(body.errorMessage ?: body.responseMessage)
        }

        throw Exception(parseErrorMessage(response.errorBody()?.string(), response.code()))
    }

    private fun parseErrorMessage(errorJson: String?, code: Int): String {
        val errorMessage = try {
            val errorObj = Gson().fromJson(errorJson, Map::class.java)
            errorObj["errorMessage"]?.toString()
                ?: errorObj["responseMessage"]?.toString()
        } catch (_: Exception) {
            null
        }

        return errorMessage ?: "Server Error: $code"
    }
}
