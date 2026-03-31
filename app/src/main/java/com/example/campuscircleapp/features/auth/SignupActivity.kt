package com.example.campuscircleapp.features.auth

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import com.example.campuscircleapp.MainActivity
import com.example.campuscircleapp.R
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.auth.models.SignUpRequest
import com.example.campuscircleapp.features.auth.viewModels.SignUpViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class SignupActivity : AppCompatActivity() {

    private val viewmodel: SignUpViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        observeSignUpState()
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
//                viewmodel.performSignUp(SignUpRequest( name, email, password))

            }
        }

        loginText.setOnClickListener {

            // Go back to Login
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
    fun observeSignUpState()
    {
        lifecycleScope.launch {
            viewmodel.signupState.collect { state ->
                when(state)
                {
                    is UiState.Loading ->
                    {
                        // loader set
                    }
                    is UiState.Success ->
                    {
                        MotionToast.createColorToast(
                            this@SignupActivity,
                            "SignUp Successful!",
                            state.message ?: "SignUp Successful!",
                            MotionToastStyle.SUCCESS,
                            MotionToast.GRAVITY_TOP,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(this@SignupActivity, www.sanju.motiontoast.R.font.helvetica_regular)
                        )
                        startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
                        finish()
                    }
                    is UiState.Error ->
                    {
                        MotionToast.createColorToast(
                            this@SignupActivity,
                            "SignUp Failed",
                            state.message,
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(this@SignupActivity, www.sanju.motiontoast.R.font.helvetica_regular))

                    }
                    is UiState.Idle -> {}
                }
            }
        }
    }
}