package com.example.campuscircleapp.core.models

data class apiResponse<T>(
    val responseCode : Int,
    val responseMessage : String,
    val errorMessage : String?,
    val data : T?

)
