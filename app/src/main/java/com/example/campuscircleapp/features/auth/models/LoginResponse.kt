package com.example.campuscircleapp.features.auth.models

data class LoginResponse(
    val userName : String,
    val fullName : String,
    val email : String,
    val token : String,
    val spaceId : Long
)
