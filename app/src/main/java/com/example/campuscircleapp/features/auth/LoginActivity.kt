package com.example.campuscircleapp.features.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.GetPasswordOption
import androidx.credentials.PasswordCredential
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.example.campuscircleapp.AdminActivity
import com.example.campuscircleapp.AttendenceActivity
import com.example.campuscircleapp.BaseActivity
import com.example.campuscircleapp.R
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.auth.models.GoogleAuthRequest
import com.example.campuscircleapp.features.auth.models.LoginRequest
import com.example.campuscircleapp.features.auth.viewModels.LoginViewModel
import com.example.campuscircleapp.features.home.services.HomeService
import com.example.campuscircleapp.shared.enums.MessageSeverity
import com.example.campuscircleapp.shared.services.MessageService
import com.example.campuscircleapp.shared.services.SessionManager
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class LoginActivity : BaseActivity() {

    private val viewModel: LoginViewModel by viewModels()
    private val homeService = HomeService()
    private lateinit var credentialManager: CredentialManager
    private val webClientId by lazy { getString(R.string.google_web_client_id) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        bindGlobalLoader(R.id.globalLoader)
        credentialManager = CredentialManager.create(this)
        requestSavedCredentials()

        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val loginBtn = findViewById<Button>(R.id.loginBtn)
        val signupText = findViewById<TextView>(R.id.signupText)
        val forgotPassword = findViewById<TextView>(R.id.forgotPassword)
        val googleLoginBtn = findViewById<Button>(R.id.googleSignInBtn)

        observeLoginState()

        loginBtn.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                MessageService.show(
                        this,
                        "Please enter email and password",
                        MessageSeverity.WARNING
                )
            } else {
                viewModel.performLogin(LoginRequest(email, password))
            }
        }
        googleLoginBtn.setOnClickListener { lifecycleScope.launch { launchGoogleSignIn() } }

        signupText.setOnClickListener { startActivity(Intent(this, SignupActivity::class.java)) }

        forgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgetPassword::class.java))
        }
    }

    private fun observeLoginState() {
        lifecycleScope.launch {
            viewModel.loginState.collect { state ->
                when (state) {
                    is UiState.Loading -> {
                        // Handle loading state
                    }
                    is UiState.Success -> {
                        SessionManager.saveToken(this@LoginActivity, state.data.token)
                        val emailInput = findViewById<EditText>(R.id.emailInput)
                        val passwordInput = findViewById<EditText>(R.id.passwordInput)
                        val email = emailInput.text.toString()
                        val password = passwordInput.text.toString()
                        
                        if (email.isNotEmpty() && password.isNotEmpty()) {
                            saveCredentials(email, password)
                        }
                        
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
                                ResourcesCompat.getFont(
                                        this@LoginActivity,
                                        www.sanju.motiontoast.R.font.helvetica_regular
                                )
                        )
                    }
                    is UiState.GoogleUserNotFound -> {
                        val intent =
                                Intent(this@LoginActivity, SignupActivity::class.java).apply {
                                    putExtra("IS_GOOGLE_SIGNUP", true)
                                    putExtra("GOOGLE_EMAIL", state.pendingRequest.email)
                                    putExtra("GOOGLE_FULL_NAME", state.pendingRequest.fullName)
                                    putExtra("GOOGLE_ID", state.pendingRequest.googleId)
                                    putExtra("GOOGLE_TOKEN", state.pendingRequest.googleToken)
                                }
                        startActivity(intent)
                        finish()
                    }
                    is UiState.Idle -> {}
                }
            }
        }
    }

    private suspend fun launchGoogleSignIn() {
        val googleIdOption =
                GetGoogleIdOption.Builder()
                        .setFilterByAuthorizedAccounts(false)
                        .setServerClientId(webClientId)
                        .setAutoSelectEnabled(true)
                        .build()

        val request = GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()

        try {
            val result =
                    credentialManager.getCredential(context = this@LoginActivity, request = request)

            val credential = result.credential
            if (credential is CustomCredential &&
                            credential.type ==
                                    GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val userEmail = googleIdTokenCredential.id
                val tokenStr = googleIdTokenCredential.idToken
                var subjectId = userEmail
                try {
                    val parts = tokenStr.split(".")
                    if (parts.size == 3) {
                        val payload =
                                String(
                                        android.util.Base64.decode(
                                                parts[1],
                                                android.util.Base64.URL_SAFE
                                        )
                                )
                        val json = org.json.JSONObject(payload)
                        if (json.has("sub")) {
                            subjectId = json.getString("sub")
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                val authRequest =
                        GoogleAuthRequest(
                                provider = "Google",
                                googleToken = googleIdTokenCredential.idToken,
                                googleId = subjectId,
                                email = userEmail,
                                fullName = googleIdTokenCredential.displayName ?: "Unknown User"
                        )

                viewModel.performGoogleSignin(authRequest)
            }
        } catch (e: GetCredentialException) {
            e.printStackTrace()
            Toast.makeText(this, "Google Sign-In Cancelled", Toast.LENGTH_SHORT).show()
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
                        ResourcesCompat.getFont(
                                this@LoginActivity,
                                www.sanju.motiontoast.R.font.helvetica_regular
                        )
                )

                val intent =
                        when (role) {
                            "admin", "superadmin" ->
                                    Intent(this@LoginActivity, AdminActivity::class.java)
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

    private fun requestSavedCredentials() {
        val request =
                GetCredentialRequest.Builder()
                        .addCredentialOption(GetPasswordOption())
                        .build()

        lifecycleScope.launch {
            try {
                val response: GetCredentialResponse =
                        credentialManager.getCredential(
                                request = request,
                                context = this@LoginActivity
                        )
                val credential = response.credential
                if (credential is PasswordCredential) {
                    val emailInput = findViewById<EditText>(R.id.emailInput)
                    val passwordInput = findViewById<EditText>(R.id.passwordInput)
                    emailInput.setText(credential.id)
                    passwordInput.setText(credential.password)
                }
            } catch (e: Exception) {
                // No saved credentials or user dismissed
            }
        }
    }

    private fun saveCredentials(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) return

        val request = CreatePasswordRequest(email, password)

        lifecycleScope.launch {
            try {
                credentialManager.createCredential(this@LoginActivity, request)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
