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
import com.example.campuscircleapp.features.admin.models.UpdateTimetableEntryRequest
import com.example.campuscircleapp.features.admin.models.StudentResponse
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

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

        // 404 means no pending enrollments exist — treat as empty list
        if (response.code() == 404) {
            return ServiceResult(emptyList(), "No pending enrollments")
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

    suspend fun createWorkspace(token: String, name: String): ServiceResult<Unit> {
        val namePart = name.toRequestBody("text/plain".toMediaTypeOrNull())
        val emptyPart = MultipartBody.Part.createFormData("image", "")
        val response = RetrofitInstance.api.createWorkspace("Bearer $token", namePart, emptyPart)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            if (body.responseCode == 200) return ServiceResult(Unit, body.responseMessage)
            throw Exception(body.errorMessage ?: body.responseMessage)
        }
        throw Exception(parseErrorMessage(response.errorBody()?.string(), response.code()))
    }

    suspend fun updateWorkspace(token: String, id: Long, name: String): ServiceResult<Unit> {
        val idPart = id.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val namePart = name.toRequestBody("text/plain".toMediaTypeOrNull())
        val emptyPart = MultipartBody.Part.createFormData("image", "")
        val response = RetrofitInstance.api.updateWorkspace("Bearer $token", idPart, namePart, emptyPart)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            if (body.responseCode == 200) return ServiceResult(Unit, body.responseMessage)
            throw Exception(body.errorMessage ?: body.responseMessage)
        }
        throw Exception(parseErrorMessage(response.errorBody()?.string(), response.code()))
    }

    suspend fun deleteWorkspace(token: String, id: Long): ServiceResult<Unit> {
        val response = RetrofitInstance.api.deleteWorkspace("Bearer $token", id)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            if (body.responseCode == 200) return ServiceResult(Unit, body.responseMessage)
            throw Exception(body.errorMessage ?: body.responseMessage)
        }
        throw Exception(parseErrorMessage(response.errorBody()?.string(), response.code()))
    }

    suspend fun toggleWorkspaceStatus(token: String, id: Long, isActive: Boolean): ServiceResult<Unit> {
        val response = RetrofitInstance.api.toggleWorkspaceStatus("Bearer $token", id, isActive)
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

    suspend fun updateSemester(token: String, request: CreateSemesterRequest): ServiceResult<Unit> {
        val response = RetrofitInstance.api.updateSemester("Bearer $token", request)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            if (body.responseCode == 200) return ServiceResult(Unit, body.responseMessage)
            throw Exception(body.errorMessage ?: body.responseMessage)
        }
        throw Exception(parseErrorMessage(response.errorBody()?.string(), response.code()))
    }

    suspend fun deleteSemester(token: String, semesterId: Long): ServiceResult<Unit> {
        val response = RetrofitInstance.api.deleteSemester("Bearer $token", semesterId)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            if (body.responseCode == 200) return ServiceResult(Unit, body.responseMessage)
            throw Exception(body.errorMessage ?: body.responseMessage)
        }
        throw Exception(parseErrorMessage(response.errorBody()?.string(), response.code()))
    }

    suspend fun getAllSemesters(token: String): ServiceResult<List<SemesterResponse>> {
        val response = RetrofitInstance.api.getAllSemesters("Bearer $token")
        val body = response.body()
        if (response.isSuccessful && body != null) {
            if (body.responseCode == 200 && body.data != null) return ServiceResult(body.data, body.responseMessage)
            throw Exception(body.errorMessage ?: body.responseMessage)
        }
        if (response.code() == 404) {
            return ServiceResult(emptyList(), "No semesters found")
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
        if (response.code() == 404) {
            return ServiceResult(emptyList(), "No semesters found")
        }
        throw Exception(parseErrorMessage(response.errorBody()?.string(), response.code()))
    }

    suspend fun addInstructor(token: String, request: CreateInstructorRequest): ServiceResult<Unit> {
        val response = RetrofitInstance.api.addInstructor("Bearer $token", request)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            if (body.responseCode == 200) return ServiceResult(Unit, body.responseMessage)
            throw Exception(body.errorMessage ?: body.responseMessage)
        }
        throw Exception(parseErrorMessage(response.errorBody()?.string(), response.code()))
    }

    suspend fun deleteInstructor(token: String, instructorId: Long): ServiceResult<Unit> {
        val response = RetrofitInstance.api.deleteInstructor("Bearer $token", instructorId)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            if (body.responseCode == 200) return ServiceResult(Unit, body.responseMessage)
            throw Exception(body.errorMessage ?: body.responseMessage)
        }
        throw Exception(parseErrorMessage(response.errorBody()?.string(), response.code()))
    }

    suspend fun getAllInstructors(token: String): ServiceResult<List<InstructorResponse>> {
        val response = RetrofitInstance.api.getAllInstructors("Bearer $token")
        val body = response.body()
        if (response.isSuccessful && body != null) {
            if (body.responseCode == 200 && body.data != null) return ServiceResult(body.data, body.responseMessage)
            throw Exception(body.errorMessage ?: body.responseMessage)
        }
        if (response.code() == 404) {
            return ServiceResult(emptyList(), "No instructors found")
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
        if (response.code() == 404) {
            return ServiceResult(emptyList(), "No instructors found")
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

    suspend fun getAllStudents(token: String): ServiceResult<List<StudentResponse>> {
        val response = RetrofitInstance.api.getAllStudents("Bearer $token")
        val body = response.body()
        if (response.isSuccessful && body != null) {
            if (body.responseCode == 200 && body.data != null) return ServiceResult(body.data, body.responseMessage)
            throw Exception(body.errorMessage ?: body.responseMessage)
        }
        if (response.code() == 404) {
            return ServiceResult(emptyList(), "No students found")
        }
        throw Exception(parseErrorMessage(response.errorBody()?.string(), response.code()))
    }

    suspend fun assignSemester(token: String, spaceId: Long, semesterId: Long): ServiceResult<Unit> {
        val response = RetrofitInstance.api.assignSemester("Bearer $token", spaceId, semesterId)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            if (body.responseCode == 200) return ServiceResult(Unit, body.responseMessage)
            throw Exception(body.errorMessage ?: body.responseMessage)
        }
        throw Exception(parseErrorMessage(response.errorBody()?.string(), response.code()))
    }

    suspend fun assignSpaceToInstructor(token: String, instructorId: Long, spaceId: Long): ServiceResult<Unit> {
        val response = RetrofitInstance.api.assignSpaceToInstructor("Bearer $token", instructorId, spaceId)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            if (body.responseCode == 200) return ServiceResult(Unit, body.responseMessage)
            throw Exception(body.errorMessage ?: body.responseMessage)
        }
        throw Exception(parseErrorMessage(response.errorBody()?.string(), response.code()))
    }

    suspend fun assignCR(token: String, userName: String): ServiceResult<Unit> {
        val response = RetrofitInstance.api.assignCR("Bearer $token", userName)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            if (body.responseCode == 200) return ServiceResult(Unit, body.responseMessage)
            throw Exception(body.errorMessage ?: body.responseMessage)
        }
        throw Exception(parseErrorMessage(response.errorBody()?.string(), response.code()))
    }

    suspend fun updateTimetableEntry(token: String, request: UpdateTimetableEntryRequest): ServiceResult<Unit> {
        val response = RetrofitInstance.api.updateTimetableEntry("Bearer $token", request)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            if (body.responseCode == 200) return ServiceResult(Unit, body.responseMessage)
            throw Exception(body.errorMessage ?: body.responseMessage)
        }
        throw Exception(parseErrorMessage(response.errorBody()?.string(), response.code()))
    }

    suspend fun deleteTimetableEntry(token: String, id: Long): ServiceResult<Unit> {
        val response = RetrofitInstance.api.deleteTimetableEntry("Bearer $token", id)
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
