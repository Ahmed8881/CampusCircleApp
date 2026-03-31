package com.example.campuscircleapp.features.auth.models

data class SignUpRequest(
    val FullName : String,
    val Email : String,
    val Username : String,
    val Password : String,
    val dob : String
)
