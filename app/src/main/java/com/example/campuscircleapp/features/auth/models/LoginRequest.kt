package com.example.campuscircleapp.features.auth.models

data class LoginRequest(
    val emailOrUsername : String,
    val password : String
)
