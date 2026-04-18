package com.example.campuscircleapp.features.home.models

data class EnrollmentByCourseResponse(
    val enrollmentId: Long,
    val studentName: String,
    val rollNo: String,
    val courseName: String,
    val semesterName: String,
    val spaceName: String,
    val enrollmentDate: String,
    val status: String
)
