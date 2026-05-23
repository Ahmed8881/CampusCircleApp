package com.example.campuscircleapp.features.home.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.home.models.PendingEnrollmentResponse
import com.example.campuscircleapp.features.home.services.HomeService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReviewEnrollmentViewModel : ViewModel() {

    private val homeService = HomeService()

    private val _enrollmentsState = MutableStateFlow<UiState<List<PendingEnrollmentResponse>>>(UiState.Idle)
    val enrollmentsState = _enrollmentsState.asStateFlow()

    private val _reviewState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val reviewState = _reviewState.asStateFlow()

    fun loadPendingEnrollments(token: String) {
        viewModelScope.launch {
            _enrollmentsState.value = UiState.Loading
            try {
                val response = homeService.getPendingEnrollments(token)
                _enrollmentsState.value = UiState.Success(response.data, response.message)
            } catch (e: Exception) {
                _enrollmentsState.value = UiState.Error(e.message ?: "Unknown Error")
            }
        }
    }

    fun reviewEnrollment(token: String, enrollmentId: Long, approve: Boolean, adminName: String) {
        viewModelScope.launch {
            _reviewState.value = UiState.Loading
            try {
                val response = homeService.reviewEnrollment(token, enrollmentId, approve, adminName)
                _reviewState.value = UiState.Success(response.data, response.message)
            } catch (e: Exception) {
                _reviewState.value = UiState.Error(e.message ?: "Unknown Error")
            }
        }
    }

    fun resetReviewState() {
        _reviewState.value = UiState.Idle
    }
}
