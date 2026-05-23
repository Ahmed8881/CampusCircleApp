package com.example.campuscircleapp.features.home.models

import com.google.gson.annotations.SerializedName

data class SpaceResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String
)
