package com.example.campuscircleapp.features.home.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.home.models.AdminCourseResponse
import com.example.campuscircleapp.features.home.models.EnrollmentByCourseResponse
import com.example.campuscircleapp.features.home.models.MarkAttendanceRequest
import com.example.campuscircleapp.features.home.services.HomeService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MarkAttendanceViewModel : ViewModel() {

    private val homeService = HomeService()

    private val _coursesState = MutableStateFlow<UiState<List<AdminCourseResponse>>>(UiState.Idle)
    val coursesState = _coursesState.asStateFlow()

    private val _enrollmentsState = MutableStateFlow<UiState<List<EnrollmentByCourseResponse>>>(UiState.Idle)
    val enrollmentsState = _enrollmentsState.asStateFlow()

    private val _markState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val markState = _markState.asStateFlow()

    fun loadCourses(token: String) {
        viewModelScope.launch {
            _coursesState.value = UiState.Loading
            try {
                val response = homeService.getAllCourses(token)
                _coursesState.value = UiState.Success(response.data, response.message)
            } catch (e: Exception) {
                _coursesState.value = UiState.Error(e.message ?: "Unable to load courses")
            }
        }
    }

    fun loadEnrollments(token: String, courseId: Long) {
        viewModelScope.launch {
            _enrollmentsState.value = UiState.Loading
            try {
                val response = homeService.getEnrollmentsByCourseId(token, courseId)
                _enrollmentsState.value = UiState.Success(response.data, response.message)
            } catch (e: Exception) {
                _enrollmentsState.value = UiState.Error(e.message ?: "Unable to load enrollments")
            }
        }
    }

    fun submitAttendance(token: String, request: MarkAttendanceRequest) {
        viewModelScope.launch {
            _markState.value = UiState.Loading
            try {
                val response = homeService.markAttendance(token, request)
                _markState.value = UiState.Success(response.message, response.message)
            } catch (e: Exception) {
                _markState.value = UiState.Error(e.message ?: "Unable to mark attendance")
            }
        }
    }
}
