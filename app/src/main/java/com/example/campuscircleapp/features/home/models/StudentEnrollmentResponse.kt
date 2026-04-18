package com.example.campuscircleapp.features.home.models

data class StudentEnrollmentResponse(
    val enrollmentId: Long,
    val courseName: String,
    val semesterName: String,
    val semesterNo: Int,
    val spaceName: String,
    val enrollmentDate: String,
    val status: String,
    val totalClasses: Int,
    val attended: Int
)
