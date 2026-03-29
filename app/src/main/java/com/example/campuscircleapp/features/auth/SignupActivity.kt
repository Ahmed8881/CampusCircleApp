package com.example.campuscircleapp.features.auth

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.campuscircleapp.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class SignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val nameInput = findViewById<TextInputEditText>(R.id.nameInput)
        val emailInput = findViewById<TextInputEditText>(R.id.emailInput)
        val passwordInput = findViewById<TextInputEditText>(R.id.passwordInput)

        val registerBtn = findViewById<MaterialButton>(R.id.registerBtn)
        val loginText = findViewById<TextView>(R.id.loginText)

        registerBtn.setOnClickListener {

            val name = nameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {

                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()

            } else if (password.length < 6) {

                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()

            } else {

                Toast.makeText(this, "Account Created Successfully!", Toast.LENGTH_SHORT).show()

                // Go to Login Page
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        loginText.setOnClickListener {

            // Go back to Login
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}