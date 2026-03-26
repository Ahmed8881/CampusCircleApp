package com.example.campuscircleapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class ForgetPassword : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forget_password)

        val emailInput = findViewById<TextInputEditText>(R.id.emailInput)
        val resetBtn = findViewById<MaterialButton>(R.id.resetBtn)

        resetBtn.setOnClickListener {
            val email = emailInput.text.toString()
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            } else {
                // Show success message
                Toast.makeText(this, "Password reset link sent to $email", Toast.LENGTH_SHORT).show()

                // Navigate back to Login screen
                val intent = Intent(this, LoginActivity::class.java)
                // Optional: clear stack so user cannot go back to ForgetPassword with back button
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }
}
