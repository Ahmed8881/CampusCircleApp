package com.example.campuscircleapp.features.home.services

import com.example.campuscircleapp.core.models.ServiceResult
import com.example.campuscircleapp.core.utils.RetrofitInstance
import com.example.campuscircleapp.features.auth.models.UserDataResponse
import com.example.campuscircleapp.features.home.models.AdminCourseResponse
import com.example.campuscircleapp.features.home.models.AdminDashboardResponse
import com.example.campuscircleapp.features.home.models.AnnouncementItem
import com.example.campuscircleapp.features.home.models.AttendanceHistoryItem
import com.example.campuscircleapp.features.home.models.DashboardAnalyticsResponse
import com.example.campuscircleapp.features.home.models.EnrollmentByCourseResponse
import com.example.campuscircleapp.features.home.models.MarkAttendanceRequest
import com.example.campuscircleapp.features.home.models.PendingEnrollmentResponse
import com.example.campuscircleapp.features.home.models.SpaceResponse
import com.example.campuscircleapp.features.home.models.StudentEnrollmentResponse
import com.example.campuscircleapp.features.home.models.TimetableEntry
import com.example.campuscircleapp.features.home.models.BirthdayResponse
import com.example.campuscircleapp.features.home.models.ResetPasswordRequest
import com.example.campuscircleapp.features.admin.models.CreateWorkspaceRequest
import com.example.campuscircleapp.features.admin.models.CreateSemesterRequest
import com.example.campuscircleapp.features.admin.models.SemesterResponse
import com.example.campuscircleapp.features.admin.models.CreateInstructorRequest
import com.example.campuscircleapp.features.admin.models.InstructorResponse
import com.example.campuscircleapp.features.admin.models.AssignmentResponse
import com.example.campuscircleapp.features.admin.models.CreateTimetableEntryRequest
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

    suspend fun getTimetable(token: String, spaceId: Long = 1): ServiceResult<List<TimetableEntry>> {
        val response = RetrofitInstance.api.getTimetable("Bearer $token", spaceId)
        val body = response.body()

        if (response.isSuccessful && body != null) {
            if (body.responseCode == 200 && body.data != null) {
                return ServiceResult(body.data, body.responseMessage)
            }
            throw Exception(body.errorMessage ?: body.responseMessage)
        }

        throw Exception(parseErrorMessage(response.errorBody()?.string(), response.code()))
    }

    suspend fun getAnnouncements(token: String): ServiceResult<List<AnnouncementItem>> {
        val response = RetrofitInstance.api.getAnnouncements("Bearer $token")
        val body = response.body()

        if (response.isSuccessful && body != null) {
            if (body.responseCode == 200 && body.data != null) {
                return ServiceResult(body.data, body.responseMessage)
            }
            throw Exception(body.errorMessage ?: body.responseMessage)
        }

        throw Exception(parseErrorMessage(response.errorBody()?.string(), response.code()))
    }

    suspend fun getSpaces(token: String): ServiceResult<List<SpaceResponse>> {
        val response = RetrofitInstance.api.getSpaces("Bearer $token")
        val body = response.body()

        if (response.isSuccessful && body != null) {
            if (body.responseCode == 200 && body.data != null) {
                return ServiceResult(body.data, body.responseMessage)
            }
            throw Exception(body.errorMessage ?: body.responseMessage)
        }

        throw Exception(parseErrorMessage(response.errorBody()?.string(), response.code()))
    }

    suspend fun announceToAllSpaces(token: String, message: String): ServiceResult<Unit> {
        val response = RetrofitInstance.api.announceToAllSpaces("Bearer $token", message)
        val body = response.body()

        if (response.isSuccessful && body != null) {
            if (body.responseCode == 200) {
                return ServiceResult(Unit, body.responseMessage)
            }
            throw Exception(body.errorMessage ?: body.responseMessage)
        }

        throw Exception(parseErrorMessage(response.errorBody()?.string(), response.code()))
    }

    suspend fun announceToSpace(token: String, spaceId: Long, message: String): ServiceResult<Unit> {
        val response = RetrofitInstance.api.announceToSpace("Bearer $token", spaceId, message)
        val body = response.body()

        if (response.isSuccessful && body != null) {
            if (body.responseCode == 200) {
                return ServiceResult(Unit, body.responseMessage)
            }
            throw Exception(body.errorMessage ?: body.responseMessage)
        }

        throw Exception(parseErrorMessage(response.errorBody()?.string(), response.code()))
    }

    suspend fun getPendingEnrollments(token: String): ServiceResult<List<PendingEnrollmentResponse>> {
        val response = RetrofitInstance.api.getPendingEnrollments("Bearer $token")
        val body = response.body()

        if (response.isSuccessful && body != null) {
            if (body.responseCode == 200 && body.data != null) {
                return ServiceResult(body.data, body.responseMessage)
            }
            throw Exception(body.errorMessage ?: body.responseMessage)
        }

        throw Exception(parseErrorMessage(response.errorBody()?.string(), response.code()))
    }

    suspend fun reviewEnrollment(token: String, enrollmentId: Long, approve: Boolean, adminName: String): ServiceResult<Unit> {
        val response = RetrofitInstance.api.reviewEnrollment("Bearer $token", enrollmentId, approve, adminName)
        val body = response.body()

        if (response.isSuccessful && body != null) {
            if (body.responseCode == 200) {
                return ServiceResult(Unit, body.responseMessage)
            }
            throw Exception(body.errorMessage ?: body.responseMessage)
        }

        throw Exception(parseErrorMessage(response.errorBody()?.string(), response.code()))
    }

    suspend fun getTeacherCourses(token: String): ServiceResult<List<AdminCourseResponse>> {
        val response = RetrofitInstance.api.getTeacherCourses("Bearer $token")
        val body = response.body()

        if (response.isSuccessful && body != null) {
            if (body.responseCode == 200 && body.data != null) {
                return ServiceResult(body.data, body.responseMessage)
            }
            throw Exception(body.errorMessage ?: body.responseMessage)
        }

        throw Exception(parseErrorMessage(response.errorBody()?.string(), response.code()))
    }

    suspend fun getStudentsByCourse(token: String, courseId: Long): ServiceResult<List<EnrollmentByCourseResponse>> {
        val response = RetrofitInstance.api.getStudentsByCourse("Bearer $token", courseId)
        val body = response.body()

        if (response.isSuccessful && body != null) {
            if (body.responseCode == 200 && body.data != null) {
                return ServiceResult(body.data, body.responseMessage)
            }
            throw Exception(body.errorMessage ?: body.responseMessage)
        }

        throw Exception(parseErrorMessage(response.errorBody()?.string(), response.code()))
    }

    suspend fun getBirthdays(token: String, spaceId: Long = 1): ServiceResult<List<BirthdayResponse>> {
        val response = RetrofitInstance.api.getBirthdays("Bearer $token", spaceId)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            if (body.responseCode == 200 && body.data != null) return ServiceResult(body.data, body.responseMessage)
            throw Exception(body.errorMessage ?: body.responseMessage)
        }
        throw Exception(parseErrorMessage(response.errorBody()?.string(), response.code()))
    }

    suspend fun resetPassword(token: String, request: ResetPasswordRequest): ServiceResult<Unit> {
        val response = RetrofitInstance.api.resetPassword("Bearer $token", request)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            if (body.responseCode == 200) return ServiceResult(Unit, body.responseMessage)
            throw Exception(body.errorMessage ?: body.responseMessage)
        }
        throw Exception(parseErrorMessage(response.errorBody()?.string(), response.code()))
    }

    suspend fun createWorkspace(token: String, request: CreateWorkspaceRequest): ServiceResult<Unit> {
        val response = RetrofitInstance.api.createWorkspace("Bearer $token", request)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            if (body.responseCode == 200) return ServiceResult(Unit, body.responseMessage)
            throw Exception(body.errorMessage ?: body.responseMessage)
        }
        throw Exception(parseErrorMessage(response.errorBody()?.string(), response.code()))
    }

    suspend fun createSemester(token: String, request: CreateSemesterRequest): ServiceResult<Unit> {
        val response = RetrofitInstance.api.createSemester("Bearer $token", request)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            if (body.responseCode == 200) return ServiceResult(Unit, body.responseMessage)
            throw Exception(body.errorMessage ?: body.responseMessage)
        }
        throw Exception(parseErrorMessage(response.errorBody()?.string(), response.code()))
    }

    suspend fun getSemesters(token: String): ServiceResult<List<SemesterResponse>> {
        val response = RetrofitInstance.api.getSemesters("Bearer $token")
        val body = response.body()
        if (response.isSuccessful && body != null) {
            if (body.responseCode == 200 && body.data != null) return ServiceResult(body.data, body.responseMessage)
            throw Exception(body.errorMessage ?: body.responseMessage)
        }
        throw Exception(parseErrorMessage(response.errorBody()?.string(), response.code()))
    }

    suspend fun createInstructor(token: String, request: CreateInstructorRequest): ServiceResult<Unit> {
        val response = RetrofitInstance.api.createInstructor("Bearer $token", request)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            if (body.responseCode == 200) return ServiceResult(Unit, body.responseMessage)
            throw Exception(body.errorMessage ?: body.responseMessage)
        }
        throw Exception(parseErrorMessage(response.errorBody()?.string(), response.code()))
    }

    suspend fun getInstructors(token: String): ServiceResult<List<InstructorResponse>> {
        val response = RetrofitInstance.api.getInstructors("Bearer $token")
        val body = response.body()
        if (response.isSuccessful && body != null) {
            if (body.responseCode == 200 && body.data != null) return ServiceResult(body.data, body.responseMessage)
            throw Exception(body.errorMessage ?: body.responseMessage)
        }
        throw Exception(parseErrorMessage(response.errorBody()?.string(), response.code()))
    }

    suspend fun getAssessments(token: String): ServiceResult<List<AssignmentResponse>> {
        val response = RetrofitInstance.api.getAssessments("Bearer $token")
        val body = response.body()
        if (response.isSuccessful && body != null) {
            if (body.responseCode == 200 && body.data != null) return ServiceResult(body.data, body.responseMessage)
            throw Exception(body.errorMessage ?: body.responseMessage)
        }
        throw Exception(parseErrorMessage(response.errorBody()?.string(), response.code()))
    }

    suspend fun createTimetableEntry(token: String, request: CreateTimetableEntryRequest): ServiceResult<Unit> {
        val response = RetrofitInstance.api.createTimetableEntry("Bearer $token", request)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            if (body.responseCode == 200) return ServiceResult(Unit, body.responseMessage)
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
