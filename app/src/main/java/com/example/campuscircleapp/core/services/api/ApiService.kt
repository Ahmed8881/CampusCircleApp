package com.example.campuscircleapp.core.services.api

import com.example.campuscircleapp.core.models.apiResponse
import com.example.campuscircleapp.features.auth.models.GoogleAuthRequest
import com.example.campuscircleapp.features.auth.models.LoginRequest
import com.example.campuscircleapp.features.auth.models.LoginResponse
import com.example.campuscircleapp.features.auth.models.SignUpRequest
import com.example.campuscircleapp.features.auth.models.SignUpResponse
import com.example.campuscircleapp.features.auth.models.UserDataResponse
import com.example.campuscircleapp.features.auth.models.UpdateUserRequest
import com.example.campuscircleapp.features.home.models.AttendanceHistoryItem
import com.example.campuscircleapp.features.home.models.AdminCourseResponse
import com.example.campuscircleapp.features.home.models.AdminDashboardResponse
import com.example.campuscircleapp.features.home.models.AnnouncementItem
import com.example.campuscircleapp.features.home.models.EnrollmentByCourseResponse
import com.example.campuscircleapp.features.home.models.MarkAttendanceRequest
import com.example.campuscircleapp.features.home.models.DashboardAnalyticsResponse
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
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("User/LoginUser")
    suspend fun login(@Body loginRequest: LoginRequest): Response<apiResponse<LoginResponse>>

    @Headers("No-Loader: true")
    @POST("User/GoogleSignup")
    suspend fun googleSignup(@Body request: GoogleAuthRequest): Response<apiResponse<Any>> // We use Any for now until we define GoogleSignupResponseDTO

    @POST("User/GoogleSignin")
    suspend fun googleSignin(@Body request: GoogleAuthRequest): Response<apiResponse<LoginResponse>>

    
    @POST("User/UpdateGoogleUser")
    suspend fun updateGoogleUser(@Body request: UpdateUserRequest): Response<apiResponse<LoginResponse>>
    
    @POST("User/CreateUser")
    suspend fun signup(@Body signupRequest: SignUpRequest): Response<apiResponse<SignUpResponse>>

    @GET("Attendance/GetStudentDashboardAnalytics")
    suspend fun getStudentDashboardAnalytics(
            @Header("Authorization") token: String
    ): Response<apiResponse<DashboardAnalyticsResponse>>

    @GET("User/GetUserData")
    suspend fun getUserData(
            @Header("Authorization") token: String
    ): Response<apiResponse<UserDataResponse>>

    @GET("Enrollment/GetStudentCourses")
    suspend fun getStudentCourses(
            @Header("Authorization") token: String
    ): Response<apiResponse<List<StudentEnrollmentResponse>>>

    @GET("Attendance/GetAttendanceHistory")
    suspend fun getAttendanceHistory(
            @Header("Authorization") token: String,
            @Query("courseId") courseId: Long
    ): Response<apiResponse<List<AttendanceHistoryItem>>>

    @GET("User/DashboardAnalytics")
    suspend fun getAdminDashboardAnalytics(
        @Header("Authorization") token: String,
        @Query("spaceId") spaceId: Long
    ): Response<apiResponse<AdminDashboardResponse>>

    @GET("Course/GetAllCourses")
    suspend fun getAllCourses(@Header("Authorization") token: String): Response<apiResponse<List<AdminCourseResponse>>>

    @GET("Enrollment/GetEnrollmentsByCourseId")
    suspend fun getEnrollmentsByCourseId(
        @Header("Authorization") token: String,
        @Query("courseId") courseId: Long
    ): Response<apiResponse<List<EnrollmentByCourseResponse>>>

    @POST("Attendance/MarkAttendance")
    suspend fun markAttendance(
        @Header("Authorization") token: String,
        @Body request: MarkAttendanceRequest
    ): Response<apiResponse<Any>>

    @GET("Timetable/GetTimetable")
    suspend fun getTimetable(
        @Header("Authorization") token: String,
        @Query("spaceId") spaceId: Long = 1
    ): Response<apiResponse<List<TimetableEntry>>>

    @GET("Space/GetAnnouncements")
    suspend fun getAnnouncements(
        @Header("Authorization") token: String
    ): Response<apiResponse<List<AnnouncementItem>>>

    @GET("Space/GetSpaces")
    suspend fun getSpaces(
        @Header("Authorization") token: String
    ): Response<apiResponse<List<SpaceResponse>>>

    @POST("Space/AnnounceToAllSpaces")
    suspend fun announceToAllSpaces(
        @Header("Authorization") token: String,
        @Query("message") message: String
    ): Response<apiResponse<Any>>

    @POST("Space/AnnounceToSpace")
    suspend fun announceToSpace(
        @Header("Authorization") token: String,
        @Query("spaceId") spaceId: Long,
        @Query("message") message: String
    ): Response<apiResponse<Any>>

    @GET("Enrollment/PendingEnrollments")
    suspend fun getPendingEnrollments(
        @Header("Authorization") token: String
    ): Response<apiResponse<List<PendingEnrollmentResponse>>>

    @POST("Enrollment/ReviewEnrollment")
    suspend fun reviewEnrollment(
        @Header("Authorization") token: String,
        @Query("enrollmentId") enrollmentId: Long,
        @Query("approve") approve: Boolean,
        @Query("adminUserName") adminUserName: String
    ): Response<apiResponse<Any>>

    @GET("Course/GetTeacherCourses")
    suspend fun getTeacherCourses(
        @Header("Authorization") token: String
    ): Response<apiResponse<List<AdminCourseResponse>>>

    @GET("Enrollment/GetStudentsByCourse")
    suspend fun getStudentsByCourse(
        @Header("Authorization") token: String,
        @Query("courseId") courseId: Long
    ): Response<apiResponse<List<EnrollmentByCourseResponse>>>

    @GET("User/GetStudentBirthdaysBySpace")
    suspend fun getBirthdays(
        @Header("Authorization") token: String,
        @Query("spaceId") spaceId: Long = 1
    ): Response<apiResponse<List<BirthdayResponse>>>

    @POST("User/ResetPassword")
    suspend fun resetPassword(
        @Header("Authorization") token: String,
        @Body request: ResetPasswordRequest
    ): Response<apiResponse<Any>>

    @POST("Space/CreateSpace")
    suspend fun createWorkspace(
        @Header("Authorization") token: String,
        @Body request: CreateWorkspaceRequest
    ): Response<apiResponse<Any>>

    @POST("Semester/CreateSemester")
    suspend fun createSemester(
        @Header("Authorization") token: String,
        @Body request: CreateSemesterRequest
    ): Response<apiResponse<Any>>

    @GET("Semester/GetSemesters")
    suspend fun getSemesters(
        @Header("Authorization") token: String
    ): Response<apiResponse<List<SemesterResponse>>>

    @POST("User/CreateInstructor")
    suspend fun createInstructor(
        @Header("Authorization") token: String,
        @Body request: CreateInstructorRequest
    ): Response<apiResponse<Any>>

    @GET("User/GetInstructors")
    suspend fun getInstructors(
        @Header("Authorization") token: String
    ): Response<apiResponse<List<InstructorResponse>>>

    @GET("Assessment/GetAssessments")
    suspend fun getAssessments(
        @Header("Authorization") token: String
    ): Response<apiResponse<List<AssignmentResponse>>>

    @POST("Timetable/CreateTimetable")
    suspend fun createTimetableEntry(
        @Header("Authorization") token: String,
        @Body request: CreateTimetableEntryRequest
    ): Response<apiResponse<Any>>
}
