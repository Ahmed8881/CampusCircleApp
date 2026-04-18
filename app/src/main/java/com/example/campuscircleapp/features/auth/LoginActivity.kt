package com.example.campuscircleapp.features.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import com.example.campuscircleapp.AdminActivity
import com.example.campuscircleapp.AttendenceActivity
import com.example.campuscircleapp.R
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.auth.models.LoginRequest
import com.example.campuscircleapp.features.home.services.HomeService
import com.example.campuscircleapp.features.auth.viewModels.LoginViewModel
import com.example.campuscircleapp.shared.enums.MessageSeverity
import com.example.campuscircleapp.shared.services.MessageService
import com.example.campuscircleapp.shared.services.SessionManager
import kotlinx.coroutines.launch
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels()
    private val homeService = HomeService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val loginBtn = findViewById<Button>(R.id.loginBtn)
        val signupText = findViewById<TextView>(R.id.signupText)
        val forgotPassword = findViewById<TextView>(R.id.forgotPassword)

        observeLoginState()

        loginBtn.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                MessageService.show(this, "Please enter email and password", MessageSeverity.WARNING)
            } else {
                viewModel.performLogin(LoginRequest(email, password))
            }
        }

        signupText.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        forgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgetPassword::class.java))
        }
    }

    private fun observeLoginState() {
        lifecycleScope.launch {
            viewModel.loginState.collect { state ->
                when (state) {
                    is UiState.Loading -> {
                        // Handle loading state (e.g., show a spinner)
                    }
                    is UiState.Success -> {
                        SessionManager.saveToken(this@LoginActivity, state.data.token)
                        fetchProfileAndNavigate(state.data.token, state.message)
                    }
                    is UiState.Error -> {
                        MotionToast.createColorToast(
                            this@LoginActivity,
                            "Login Failed",
                            state.message,
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(this@LoginActivity, www.sanju.motiontoast.R.font.helvetica_regular)
                        )
                    }
                    is UiState.Idle -> {}
                }
            }
        }
    }

    private fun fetchProfileAndNavigate(token: String, successMessage: String?) {
        lifecycleScope.launch {
            try {
                val userResponse = homeService.getUserData(token)
                val role = userResponse.data.role.trim().lowercase()

                SessionManager.saveRole(this@LoginActivity, role)
                SessionManager.saveName(this@LoginActivity, userResponse.data.name)

                MotionToast.createColorToast(
                    this@LoginActivity,
                    "Login Successful!",
                    successMessage ?: "Welcome to Campus Circle!",
                    MotionToastStyle.SUCCESS,
                    MotionToast.GRAVITY_TOP,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(this@LoginActivity, www.sanju.motiontoast.R.font.helvetica_regular)
                )

                val intent = when (role) {
                    "admin", "superadmin" -> Intent(this@LoginActivity, AdminActivity::class.java)
                    else -> Intent(this@LoginActivity, AttendenceActivity::class.java)
                }
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                MessageService.show(
                    this@LoginActivity,
                    e.message ?: "Unable to load profile after login",
                    MessageSeverity.ERROR
                )
            }
        }
    }
}
