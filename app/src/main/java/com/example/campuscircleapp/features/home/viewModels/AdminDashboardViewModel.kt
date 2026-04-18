package com.example.campuscircleapp.features.home.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.home.models.AdminDashboardResponse
import com.example.campuscircleapp.features.home.services.HomeService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminDashboardViewModel : ViewModel() {

    private val homeService = HomeService()

    private val _dashboardState = MutableStateFlow<UiState<AdminDashboardResponse>>(UiState.Idle)
    val dashboardState = _dashboardState.asStateFlow()

    fun loadDashboard(token: String, spaceId: Long) {
        viewModelScope.launch {
            _dashboardState.value = UiState.Loading
            try {
                val response = homeService.getAdminDashboardAnalytics(token, spaceId)
                _dashboardState.value = UiState.Success(response.data, response.message)
            } catch (e: Exception) {
                _dashboardState.value = UiState.Error(e.message ?: "Unable to load admin analytics")
            }
        }
    }
}
