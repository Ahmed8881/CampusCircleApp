package com.example.campuscircleapp.features.auth.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.auth.models.GoogleAuthRequest
import com.example.campuscircleapp.features.auth.models.LoginResponse
import com.example.campuscircleapp.features.auth.models.SignUpRequest
import com.example.campuscircleapp.features.auth.models.SignUpResponse
import com.example.campuscircleapp.features.auth.models.UpdateUserRequest
import com.example.campuscircleapp.features.auth.services.AuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignUpViewModel : ViewModel() {
    private val authService = AuthService()
    private val _signupState = MutableStateFlow<UiState<SignUpResponse>>(UiState.Idle)
    val signupState = _signupState.asStateFlow()
    private val _googleSignupState = MutableStateFlow<UiState<LoginResponse>>(UiState.Idle)
    val googleSignupState = _googleSignupState.asStateFlow()
    fun performSignUp(request: SignUpRequest) {
        viewModelScope.launch {
            _signupState.value = UiState.Loading
            try {
                val response = authService.signUp(request)
                _signupState.value = UiState.Success(response.data, response.message)
            } catch (e: Exception) {
                _signupState.value = UiState.Error(e.message ?: "Unknown Error")
            }
        }
    }
    fun performGoogleSigninCheck(request: GoogleAuthRequest) {
        viewModelScope.launch {
            _googleSignupState.value = UiState.Loading
            try {
                // First check if they already exist
                val response = authService.googleSignin(request)

                // If the above succeeds, they already have an account!
                _googleSignupState.value =
                        UiState.Error("Account already exists. Please login instead.")
            } catch (e: Exception) {
                if (e.message == "User not found.") {
                    // Perfect, they don't exist! Let's call your GoogleSignup to initialize the
                    // account
                    try {
                        authService.googleSignup(request)
                        // The partial account is created. Tell UI to proceed to Step 2!
                        _googleSignupState.value = UiState.GoogleUserNotFound(request)
                    } catch (signupError: Exception) {
                        _googleSignupState.value =
                                UiState.Error(
                                        signupError.message ?: "Failed to initialize Google account"
                                )
                    }
                } else {
                    _googleSignupState.value = UiState.Error(e.message ?: "Google Auth Error")
                }
            }
        }
    }
    fun completeGoogleSignup(updateRequest: UpdateUserRequest) {
        viewModelScope.launch {
            _googleSignupState.value = UiState.Loading
            try {
                val response = authService.updateGoogleUser(updateRequest)
                _googleSignupState.value = UiState.Success(response.data, response.message)
            } catch (e: Exception) {
                _googleSignupState.value =
                        UiState.Error(e.message ?: "Failed to complete Google Signup")
            }
        }
    }
}
