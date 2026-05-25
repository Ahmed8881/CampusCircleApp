package com.example.campuscircleapp.features.auth.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.auth.models.GoogleAuthRequest
import com.example.campuscircleapp.features.auth.models.LoginRequest
import com.example.campuscircleapp.features.auth.models.LoginResponse
import com.example.campuscircleapp.features.auth.models.UpdateUserRequest
import com.example.campuscircleapp.features.auth.models.UserDeviceTokenDTO
import com.example.campuscircleapp.features.auth.services.AuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel() : ViewModel() {

    private val authService = AuthService()
    private val _loginState = MutableStateFlow<UiState<LoginResponse>>(UiState.Idle)
    val loginState = _loginState.asStateFlow()

    fun performLogin(request: LoginRequest) {
        viewModelScope.launch {
            _loginState.value = UiState.Loading
            try {
                val response = authService.login(request)
                _loginState.value = UiState.Success(response.data, response.message)
            } catch (e: Exception) {
                _loginState.value = UiState.Error(e.message ?: "Unknown Error")
            }
        }
    }
    fun performGoogleSignin(request: GoogleAuthRequest) {
        viewModelScope.launch {
            _loginState.value = UiState.Loading
            try {
                val response = authService.googleSignin(request)
                _loginState.value = UiState.Success(response.data, response.message)
            } catch (e: Exception) {
                // Check if the error is exactly the 404 error your backend sends
                if (e.message == "User not found.") {
                    // Tell the UI to ask for more details!
                    _loginState.value = UiState.GoogleUserNotFound(request)
                } else {
                    _loginState.value = UiState.Error(e.message ?: "Google Sign-In Error")
                }
            }
        }
    }
    fun completeGoogleSignup(updateRequest: UpdateUserRequest) {
        viewModelScope.launch {
            _loginState.value = UiState.Loading
            try {
                // This calls the UpdateGoogleUser endpoint
                val response = authService.updateGoogleUser(updateRequest)
                // If successful, the backend returns the same UserLoginResponseDTO
                _loginState.value = UiState.Success(response.data, response.message)
            } catch (e: Exception) {
                _loginState.value = UiState.Error(e.message ?: "Failed to complete Google Signup")
            }
        }
    }

    fun registerDevice(token: String, deviceTokenDto: UserDeviceTokenDTO) {
        viewModelScope.launch {
            try {
                authService.registerDevice(token, deviceTokenDto)
            } catch (e: Exception) {
                // Silently fail or log for device registration
                e.printStackTrace()
            }
        }
    }
}
