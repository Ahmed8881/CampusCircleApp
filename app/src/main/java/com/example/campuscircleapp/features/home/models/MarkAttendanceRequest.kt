package com.example.campuscircleapp.features.home.models

data class MarkAttendanceRequest(
    val courseId: Long,
    val attendanceDate: String,
    val attendance: List<MarkAttendanceStudent>
)

data class MarkAttendanceStudent(
    val studentId: Long,
    val status: String
)
