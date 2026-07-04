package com.example.campuscircleapp.features.onboarding

import androidx.annotation.DrawableRes
import java.io.Serializable

data class OnboardingPage(
    @DrawableRes val imageRes: Int,
    val title: String,
    val description: String
) : Serializable
