package com.example.campuscircleapp.shared

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.campuscircleapp.AdminActivity
import com.example.campuscircleapp.AttendenceActivity
import com.example.campuscircleapp.R
import com.example.campuscircleapp.features.auth.LoginActivity
import com.example.campuscircleapp.shared.services.SessionManager

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)

        Handler(Looper.getMainLooper()).postDelayed({
            val token = SessionManager.getToken(this)
            val role = SessionManager.getRole(this)?.lowercase()

            val destination = if (token.isNullOrBlank()) {
                Intent(this, LoginActivity::class.java)
            } else {
                when (role) {
                    "admin", "superadmin" -> Intent(this, AdminActivity::class.java)
                    else -> Intent(this, AttendenceActivity::class.java)
                }
            }

            startActivity(destination)
            finish()
        }, 2500)
    }
}