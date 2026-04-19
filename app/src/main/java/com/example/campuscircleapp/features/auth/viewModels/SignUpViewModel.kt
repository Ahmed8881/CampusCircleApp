package com.example.campuscircleapp.features.auth.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuscircleapp.features.auth.services.AuthService
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.auth.models.SignUpRequest
import com.example.campuscircleapp.features.auth.models.SignUpResponse
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignUpViewModel : ViewModel() {
    private val authService = AuthService()
    private  val _signupState = MutableStateFlow<UiState<SignUpResponse>>(UiState.Idle)
    val signupState = _signupState.asStateFlow()
    fun performSignUp(request : SignUpRequest){
        viewModelScope.launch {
            _signupState.value = UiState.Loading
            try {
                val response = authService.signUp(request)
                _signupState.value = UiState.Success(response.data,response.message)
            }
            catch (e: Exception){
                _signupState.value = UiState.Error(e.message ?: "Unknown Error")
            }
            }
        }

    }