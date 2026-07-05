package com.example.campuscircleapp.shared

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.campuscircleapp.AdminActivity
import com.example.campuscircleapp.AttendenceActivity
import com.example.campuscircleapp.R
import com.example.campuscircleapp.TeacherActivity
import com.example.campuscircleapp.core.theme.ThemeManager
import com.example.campuscircleapp.core.utils.DeviceRegistrationHelper
import com.example.campuscircleapp.features.auth.LoginActivity
import com.example.campuscircleapp.features.onboarding.OnboardingActivity
import com.example.campuscircleapp.shared.services.SessionManager

class SplashScreen : AppCompatActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        SessionManager.setNotificationPermissionGranted(this, isGranted)
        if (isGranted) {
            // Force registration if permission was just granted
            DeviceRegistrationHelper.enqueueRegistration(this, force = true)
        }
        proceedToNextScreen()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applyTheme(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)

        checkNotificationPermission()
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    SessionManager.setNotificationPermissionGranted(this, true)
                    // Force sync on every cold start to handle cases where backend cleared the token
                    DeviceRegistrationHelper.enqueueRegistration(this, force = true)
                    startTimer()
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            SessionManager.setNotificationPermissionGranted(this, true)
            DeviceRegistrationHelper.enqueueRegistration(this, force = true)
            startTimer()
        }
    }

    private fun startTimer() {
        Handler(Looper.getMainLooper()).postDelayed({
            proceedToNextScreen()
        }, 800)
    }

    private fun proceedToNextScreen() {
        val token = SessionManager.getToken(this)
        val role = SessionManager.getRole(this)?.lowercase()

        val destination = if (token.isNullOrBlank()) {
            if (!ThemeManager.isOnboardingCompleted(this)) {
                Intent(this, OnboardingActivity::class.java)
            } else {
                Intent(this, LoginActivity::class.java)
            }
        } else {
            when (role) {
                "admin", "superadmin" -> Intent(this, AdminActivity::class.java)
                "teacher", "instructor" -> Intent(this, TeacherActivity::class.java)
                else -> Intent(this, AttendenceActivity::class.java)
            }
        }

        startActivity(destination)
        finish()
    }
}
