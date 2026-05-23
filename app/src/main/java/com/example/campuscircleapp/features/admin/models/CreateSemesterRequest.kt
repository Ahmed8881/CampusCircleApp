package com.example.campuscircleapp.features.admin.models

data class CreateSemesterRequest(
    val semesterName: String,
    val semesterNo: Int,
    val startDate: String,
    val endDate: String
)
