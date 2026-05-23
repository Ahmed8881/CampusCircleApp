package com.example.campuscircleapp.features.home.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.home.models.ResetPasswordRequest
import com.example.campuscircleapp.features.home.services.HomeService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {
    private val homeService = HomeService()

    private val _resetState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val resetState = _resetState.asStateFlow()

    fun resetPassword(token: String, oldPassword: String, newPassword: String) {
        _resetState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val result = homeService.resetPassword(token, ResetPasswordRequest(oldPassword, newPassword, token))
                _resetState.value = UiState.Success(Unit, result.message)
            } catch (e: Exception) {
                _resetState.value = UiState.Error(e.message ?: "Failed to reset password")
            }
        }
    }

    fun resetIdle() { _resetState.value = UiState.Idle }
}
