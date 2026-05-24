package com.example.campuscircleapp.features.admin.models

data class CreateInstructorRequest(
    val fullName: String,
    val email: String,
    val username: String,
    val password: String,
    val dob: String
)
