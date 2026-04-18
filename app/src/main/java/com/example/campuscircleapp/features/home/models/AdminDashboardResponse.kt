package com.example.campuscircleapp.features.home.models

data class AdminDashboardResponse(
    val courses: List<AdminDashboardCourse>,
    val analytics: AdminDashboardAnalytics
)

data class AdminDashboardCourse(
    val credits: Int,
    val attendance: Int,
    val courseCode: String,
    val courseName: String,
    val enrollments: Int
)

data class AdminDashboardAnalytics(
    val activeCourses: Int,
    val totalStudents: Int,
    val activeInstructors: Int,
    val averageAttendance: Double
)
