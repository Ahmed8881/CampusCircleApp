package com.example.campuscircleapp.features.home.models

data class DashboardAnalyticsResponse(
    val totalPresent: Int,
    val totalAbsent: Int,
    val totalLeaves: Int,
    val totalClasses: Int,
    val averageAttendance: Int,
    val attendanceTrendData: List<AttendanceTrendItem>,
    val attendancePerCourseData: List<AttendancePerCourseItem>
)

data class AttendanceTrendItem(
    val month: String,
    val attendance: Float
)

data class AttendancePerCourseItem(
    val course: String,
    val attendance: Float
)
