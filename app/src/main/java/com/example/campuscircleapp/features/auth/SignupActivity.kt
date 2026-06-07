package com.example.campuscircleapp.features.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.example.campuscircleapp.AdminActivity
import com.example.campuscircleapp.AttendenceActivity
import com.example.campuscircleapp.R
import com.example.campuscircleapp.TeacherActivity
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.auth.models.GoogleAuthRequest
import com.example.campuscircleapp.features.auth.models.SignUpRequest
import com.example.campuscircleapp.features.auth.models.UpdateUserRequest
import com.example.campuscircleapp.features.auth.viewModels.SignUpViewModel
import com.example.campuscircleapp.features.home.services.HomeService
import com.example.campuscircleapp.shared.enums.MessageSeverity
import com.example.campuscircleapp.shared.services.MessageService
import com.example.campuscircleapp.shared.services.SessionManager
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class SignupActivity : AppCompatActivity() {

    private companion object {
        const val TAG = "SignupActivity"
    }

    private val viewmodel: SignUpViewModel by viewModels()
    private val homeService = HomeService()
    private lateinit var credentialManager: CredentialManager
    private val webClientId by lazy { getString(R.string.google_web_client_id) }

    private var isGoogleSignup = false
    private var googleEmail = ""
    private var googleFullName = ""
    private var googleId = ""
    private var googleToken = ""
    private var currentStep = 0

    private lateinit var nameInput: TextInputEditText
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var confirmPasswordInput: TextInputEditText
    private lateinit var usernameInput: TextInputEditText
    private lateinit var dobInput: TextInputEditText
    private lateinit var stepViews: List<LinearLayout>
    private lateinit var nextBtn: MaterialButton
    private lateinit var prevBtn: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        credentialManager = CredentialManager.create(this)

        isGoogleSignup = intent.getBooleanExtra("IS_GOOGLE_SIGNUP", false)

        observeSignUpState()

        // Stepper views (2 steps)
        stepViews =
                listOf(
                        findViewById<LinearLayout>(R.id.step1),
                        findViewById<LinearLayout>(R.id.step2)
                )
        nextBtn = findViewById<MaterialButton>(R.id.nextBtn)
        prevBtn = findViewById<MaterialButton>(R.id.prevBtn)

        // Inputs
        nameInput = findViewById<TextInputEditText>(R.id.nameInput)
        emailInput = findViewById<TextInputEditText>(R.id.emailInput)
        passwordInput = findViewById<TextInputEditText>(R.id.passwordInput)
        confirmPasswordInput = findViewById<TextInputEditText>(R.id.confirmPasswordInput)
        usernameInput = findViewById<TextInputEditText>(R.id.usernameInput)
        dobInput = findViewById<TextInputEditText>(R.id.dobInput)

        val loginText = findViewById<TextView>(R.id.loginText)
        val googleSignUpBtn = findViewById<MaterialButton>(R.id.googleSignInBtn)

        googleSignUpBtn.setOnClickListener { lifecycleScope.launch { launchGoogleSignIn() } }

        if (isGoogleSignup) {
            googleEmail = intent.getStringExtra("GOOGLE_EMAIL") ?: ""
            googleFullName = intent.getStringExtra("GOOGLE_FULL_NAME") ?: ""
            googleId = intent.getStringExtra("GOOGLE_ID") ?: ""
            googleToken = intent.getStringExtra("GOOGLE_TOKEN") ?: ""

            // Pre-fill Step 1
            nameInput.setText(googleFullName)
            emailInput.setText(googleEmail)

            // Hide password requirements since Google handles auth!
            passwordInput.visibility =
                    LinearLayout.GONE // if inside a layout, maybe hide the parent layout
            confirmPasswordInput.visibility = LinearLayout.GONE

            // Optionally, skip straight to Step 2!
            currentStep = 1
        }

        updateStepper()
        observeGoogleSignUpState()

        nextBtn.setOnClickListener {
            if (currentStep == 0) {
                val name = nameInput.text.toString().trim()
                val email = emailInput.text.toString().trim()
                val password = passwordInput.text.toString()
                val confirmPassword = confirmPasswordInput.text.toString()

                if (name.isEmpty() ||
                                email.isEmpty() ||
                                password.isEmpty() ||
                                confirmPassword.isEmpty()
                ) {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (!isGoogleSignup) {
                    if (password.length < 6) {
                        Toast.makeText(
                                        this,
                                        "Password must be at least 6 characters",
                                        Toast.LENGTH_SHORT
                                )
                                .show()
                        return@setOnClickListener
                    }
                    if (password != confirmPassword) {
                        Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
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
                if (isGoogleSignup) {
                    // CALL GOOGLE SIGNUP ENDPOINT
                    val request =
                            UpdateUserRequest(
                                    email = googleEmail,
                                    fullName = googleFullName,
                                    googleId = googleId,
                                    googleToken = googleToken,
                                    userName = username,
                                    dob = dob,
                                    isDirectSignin = true
                            )
                    viewmodel.completeGoogleSignup(request)
                } else {
                    val req =
                            SignUpRequest(
                                    FullName = nameInput.text.toString().trim(),
                                    Email = emailInput.text.toString().trim(),
                                    Username = usernameInput.text.toString().trim(),
                                    Password = passwordInput.text.toString().trim(),
                                    dob = dobInput.text.toString().trim()
                            )
                    viewmodel.performSignUp(req)
                }
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

    private fun updateStepper() {
        stepViews.forEachIndexed { idx, view ->
            view.visibility =
                    if (idx == currentStep) LinearLayout.VISIBLE else LinearLayout.GONE
        }
        prevBtn.visibility = if (currentStep == 0) LinearLayout.GONE else LinearLayout.VISIBLE
        nextBtn.text = if (currentStep == stepViews.lastIndex) "Register" else "Next"
    }

    fun observeSignUpState() {
        lifecycleScope.launch {
            viewmodel.signupState.collect { state ->
                when (state) {
                    is UiState.Loading -> {
                        // loader set
                    }
                    is UiState.Success -> {
                        MotionToast.createColorToast(
                                this@SignupActivity,
                                "SignUp Successful!",
                                state.message ?: "SignUp Successful!",
                                MotionToastStyle.SUCCESS,
                                MotionToast.GRAVITY_TOP,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(
                                        this@SignupActivity,
                                        www.sanju.motiontoast.R.font.helvetica_regular
                                )
                        )
                        startActivity(Intent(this@SignupActivity, EmailVerificationActivity::class.java))
                        finish()
                    }
                    is UiState.Error -> {
                        MotionToast.createColorToast(
                                this@SignupActivity,
                                "SignUp Failed",
                                state.message,
                                MotionToastStyle.ERROR,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(
                                        this@SignupActivity,
                                        www.sanju.motiontoast.R.font.helvetica_regular
                                )
                        )
                    }
                    is UiState.Idle -> {}
                    else -> {}
                }
            }
        }
    }

    private fun fetchProfileAndNavigate(token: String, successMessage: String?) {
        lifecycleScope.launch {
            try {
                val userResponse = homeService.getUserData(token)
                val role = userResponse.data.role.trim().lowercase()

                SessionManager.saveRole(this@SignupActivity, role)
                SessionManager.saveName(this@SignupActivity, userResponse.data.name)

                MotionToast.createColorToast(
                        this@SignupActivity,
                        "Signup Successful!",
                        successMessage ?: "Welcome to Campus Circle!",
                        MotionToastStyle.SUCCESS,
                        MotionToast.GRAVITY_TOP,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(
                                this@SignupActivity,
                                www.sanju.motiontoast.R.font.helvetica_regular
                        )
                )

                val intent =
                        when (role) {
                            "admin", "superadmin" ->
                                    Intent(this@SignupActivity, AdminActivity::class.java)
                            "teacher", "instructor" ->
                                    Intent(this@SignupActivity, TeacherActivity::class.java)
                            else -> Intent(this@SignupActivity, AttendenceActivity::class.java)
                        }
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                MessageService.show(
                        this@SignupActivity,
                        e.message ?: "Unable to load profile after signup",
                        MessageSeverity.ERROR
                )
            }
        }
    }

    fun observeGoogleSignUpState() {
        lifecycleScope.launch {
            viewmodel.googleSignupState.collect { state ->
                when (state) {
                    is UiState.Loading -> {
                        // loader set
                    }
                    is UiState.Success -> {
                        // Successful Google Signups return a token right away! We can save it and
                        // dive in.
                        SessionManager.saveToken(this@SignupActivity, state.data.token)
                        Toast.makeText(
                                        this@SignupActivity,
                                        "Registration & Login Successful!",
                                        Toast.LENGTH_LONG
                                )
                                .show()

                        fetchProfileAndNavigate(state.data.token, state.message)
                    }
                    is UiState.Error -> {
                        MotionToast.createColorToast(
                                this@SignupActivity,
                                "Google Registration Failed",
                                state.message,
                                MotionToastStyle.ERROR,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(
                                        this@SignupActivity,
                                        www.sanju.motiontoast.R.font.helvetica_regular
                                )
                        )
                        // If the account already exists, redirect them to Login
                        if (state.message.contains("Account already exists")) {
                            startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
                            finish()
                        }
                    }
                    is UiState.GoogleUserNotFound -> {
                        // Google account successfully partially created!
                        // Let's populate the details and jump right to Step 2.
                        val pendingRequest = state.pendingRequest

                        googleEmail = pendingRequest.email
                        googleFullName = pendingRequest.fullName
                        googleId = pendingRequest.googleId
                        googleToken = pendingRequest.googleToken

                        nameInput.setText(pendingRequest.fullName)
                        emailInput.setText(pendingRequest.email)

                        passwordInput.visibility = LinearLayout.GONE
                        confirmPasswordInput.visibility = LinearLayout.GONE

                        isGoogleSignup = true
                        currentStep = 1 // Jump directly to the Stepper page needing Roll No/DOB
                        updateStepper()
                    }
                    is UiState.Idle -> {}
                    else -> {}
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
                    credentialManager.getCredential(
                            context = this@SignupActivity,
                            request = request
                    )

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
                        val payloadStr = String(android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE))
                        val json = org.json.JSONObject(payloadStr)
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

                // We need to call performGoogleSignin first to see if they already exist,
                // or if we get a 404 (which triggers the UI step 2 for extra details).
                // NOTE: You will need to make sure your SignUpViewModel has a method for this,
                // or use the LoginViewModel just for the initial check.
                viewmodel.performGoogleSigninCheck(authRequest)
            }
        } catch (e: GetCredentialException) {
            Log.e(TAG, "Google Sign-In failed", e)
            Toast.makeText(
                    this,
                    e.message ?: e.javaClass.simpleName,
                    Toast.LENGTH_LONG
            ).show()
        }
    }
}
