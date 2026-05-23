package com.example.campuscircleapp.features.auth

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.campuscircleapp.R
import com.google.android.material.button.MaterialButton

class EmailVerificationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_email_verification)

        val resendBtn = findViewById<MaterialButton>(R.id.resendEmailBtn)
        val backBtn = findViewById<MaterialButton>(R.id.backToLoginBtn)
        val statusText = findViewById<TextView>(R.id.emailVerificationStatus)

        resendBtn.setOnClickListener {
            statusText.isVisible = true
            statusText.setTextColor(getColor(android.R.color.holo_green_dark))
            statusText.text = "Verification email sent! Check your inbox."
        }

        backBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
