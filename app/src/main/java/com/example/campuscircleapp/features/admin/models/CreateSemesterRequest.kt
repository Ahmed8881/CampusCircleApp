package com.example.campuscircleapp.features.admin.models

data class CreateSemesterRequest(
    val id: Long = 0,
    val number: Int,
    val name: String,
    val startDate: String,
    val endDate: String
)
