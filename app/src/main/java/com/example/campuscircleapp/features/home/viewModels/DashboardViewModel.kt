package com.example.campuscircleapp.features.home.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.auth.models.UserDataResponse
import com.example.campuscircleapp.features.home.models.DashboardAnalyticsResponse
import com.example.campuscircleapp.features.home.services.HomeService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {

    private val homeService = HomeService()

    private val _dashboardState = MutableStateFlow<UiState<DashboardAnalyticsResponse>>(UiState.Idle)
    val dashboardState = _dashboardState.asStateFlow()

    private val _userState = MutableStateFlow<UiState<UserDataResponse>>(UiState.Idle)
    val userState = _userState.asStateFlow()

    fun loadDashboardData(token: String) {
        viewModelScope.launch {
            _dashboardState.value = UiState.Loading
            try {
                val response = homeService.getDashboardAnalytics(token)
                _dashboardState.value = UiState.Success(response.data, response.message)
            } catch (e: Exception) {
                _dashboardState.value = UiState.Error(e.message ?: "Unknown Error")
            }
        }
    }

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
