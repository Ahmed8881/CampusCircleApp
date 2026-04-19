package com.example.campuscircleapp.features.auth.models

import com.google.gson.annotations.SerializedName

data class UpdateUserRequest(
    @SerializedName("Email") val email: String,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("googleId") val googleId: String,
    @SerializedName("googleToken") val googleToken: String,
    @SerializedName("UserName") val userName: String,
    @SerializedName("dob") val dob: String, // Expected format from backend: "dd-MM-yyyy"
    @SerializedName("is_direct_signin") val isDirectSignin: Boolean = true
)