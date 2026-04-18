package com.example.campuscircleapp.features.auth.models

data class UserDataResponse(
    val userName: String,
    val email: String,
    val name: String,
    val role: String,
    val dob: String,
    val space: String?,
    val spaceId: Long,
    val image: String?
)
