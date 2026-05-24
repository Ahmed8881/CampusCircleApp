package com.example.campuscircleapp.features.admin.models

data class InstructorResponse(
    val userId: Long,
    val name: String,
    val email: String,
    val username: String,
    val image: String?
)
