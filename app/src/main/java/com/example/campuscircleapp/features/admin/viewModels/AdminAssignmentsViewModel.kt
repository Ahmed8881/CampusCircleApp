package com.example.campuscircleapp.features.admin.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.admin.models.AssignmentResponse
import com.example.campuscircleapp.features.home.services.HomeService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminAssignmentsViewModel : ViewModel() {
    private val homeService = HomeService()

    private val _assignmentsState = MutableStateFlow<UiState<List<AssignmentResponse>>>(UiState.Idle)
    val assignmentsState = _assignmentsState.asStateFlow()

    fun load(token: String) {
        _assignmentsState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val result = homeService.getAssessments(token)
                _assignmentsState.value = UiState.Success(result.data, result.message)
            } catch (e: Exception) {
                _assignmentsState.value = UiState.Error(e.message ?: "Failed to load assignments")
            }
        }
    }
}
