package com.example.campuscircleapp.features.auth

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import android.widget.LinearLayout
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

        // Stepper views (2 steps)
        val stepViews = listOf(
            findViewById<LinearLayout>(R.id.step1),
            findViewById<LinearLayout>(R.id.step2)
        )
        val nextBtn = findViewById<MaterialButton>(R.id.nextBtn)
        val prevBtn = findViewById<MaterialButton>(R.id.prevBtn)

        // Inputs
        val nameInput = findViewById<TextInputEditText>(R.id.nameInput)
        val emailInput = findViewById<TextInputEditText>(R.id.emailInput)
        val passwordInput = findViewById<TextInputEditText>(R.id.passwordInput)
        val confirmPasswordInput = findViewById<TextInputEditText>(R.id.confirmPasswordInput)
        val usernameInput = findViewById<TextInputEditText>(R.id.usernameInput)
        val dobInput = findViewById<TextInputEditText>(R.id.dobInput)

        val loginText = findViewById<TextView>(R.id.loginText)

        var currentStep = 0
        fun updateStepper() {
            stepViews.forEachIndexed { idx, view ->
                view.visibility = if (idx == currentStep) LinearLayout.VISIBLE else LinearLayout.GONE
            }
            prevBtn.visibility = if (currentStep == 0) LinearLayout.GONE else LinearLayout.VISIBLE
            nextBtn.text = if (currentStep == stepViews.lastIndex) "Register" else "Next"
        }

        updateStepper()

        nextBtn.setOnClickListener {
            if (currentStep == 0) {
                val name = nameInput.text.toString().trim()
                val email = emailInput.text.toString().trim()
                val password = passwordInput.text.toString()
                val confirmPassword = confirmPasswordInput.text.toString()
                if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (password.length < 6) {
                    Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (password != confirmPassword) {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                // Go to next step
                currentStep = 1
                updateStepper()
                return@setOnClickListener
            }
            if (currentStep == 1) {
                val username = usernameInput.text.toString().trim()
                val dob = dobInput.text.toString().trim()
                if (username.isEmpty() || dob.isEmpty()) {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                // All fields collected, send payload
                val req = SignUpRequest(
                    FullName = nameInput.text.toString().trim(),
                    Email = emailInput.text.toString().trim(),
                    Username = usernameInput.text.toString().trim(),
                    Password = passwordInput.text.toString().trim(),
                    dob = dobInput.text.toString().trim()
                )
                viewmodel.performSignUp(req)
            }
        }

        prevBtn.setOnClickListener {
            if (currentStep > 0) {
                currentStep--
                updateStepper()
            }
        }

        // Optional: Date picker for dob
        dobInput.setOnClickListener {
            val datePicker = android.app.DatePickerDialog(this)
            datePicker.setOnDateSetListener { _, year, month, dayOfMonth ->
                val mm = (month + 1).toString().padStart(2, '0')
                val dd = dayOfMonth.toString().padStart(2, '0')
                dobInput.setText("$dd-$mm-$year")
            }
            datePicker.show()
        }

        loginText.setOnClickListener {
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