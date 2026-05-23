package com.example.campuscircleapp.features.teacher.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.auth.models.UserDataResponse
import com.example.campuscircleapp.features.home.services.HomeService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TeacherDashboardViewModel : ViewModel() {

    private val homeService = HomeService()

    private val _userState = MutableStateFlow<UiState<UserDataResponse>>(UiState.Idle)
    val userState = _userState.asStateFlow()

    fun loadUserData(token: String) {
        viewModelScope.launch {
            _userState.value = UiState.Loading
            try {
                val response = homeService.getUserData(token)
                _userState.value = UiState.Success(response.data, response.message)
            } catch (e: Exception) {
                _userState.value = UiState.Error(e.message ?: "Unknown Error")
            }
        }
    }
}
