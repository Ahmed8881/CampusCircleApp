package com.example.campuscircleapp.features.teacher.models

import com.google.gson.annotations.SerializedName

data class TeacherStudentResponse(
    @SerializedName("enrollmentId") val enrollmentId: Long,
    @SerializedName("studentName") val studentName: String,
    @SerializedName("studentEmail") val studentEmail: String?,
    @SerializedName("courseName") val courseName: String,
    @SerializedName("attended") val attended: Int,
    @SerializedName("totalClasses") val totalClasses: Int
)
