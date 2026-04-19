package com.example.campuscircleapp.features.home.models

data class MarkAttendanceStudentUi(
    val studentId: Long,
    val studentName: String,
    val rollNo: String,
    var status: String = "present"
)
