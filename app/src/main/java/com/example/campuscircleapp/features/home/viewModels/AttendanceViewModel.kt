package com.example.campuscircleapp.features.home.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.home.models.AttendanceHistoryItem
import com.example.campuscircleapp.features.home.models.StudentEnrollmentResponse
import com.example.campuscircleapp.features.home.services.HomeService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AttendanceViewModel : ViewModel() {

    private val homeService = HomeService()

    private val _coursesState = MutableStateFlow<UiState<List<StudentEnrollmentResponse>>>(UiState.Idle)
    val coursesState = _coursesState.asStateFlow()

    private val _historyState = MutableStateFlow<UiState<List<AttendanceHistoryItem>>>(UiState.Idle)
    val historyState = _historyState.asStateFlow()

    fun loadStudentCourses(token: String) {
        viewModelScope.launch {
            _coursesState.value = UiState.Loading
            try {
                val response = homeService.getStudentCourses(token)
                _coursesState.value = UiState.Success(response.data, response.message)
            } catch (e: Exception) {
                _coursesState.value = UiState.Error(e.message ?: "Unable to load courses")
            }
        }
    }

    fun loadAttendanceHistory(token: String, courseId: Long) {
        viewModelScope.launch {
            _historyState.value = UiState.Loading
            try {
                val response = homeService.getAttendanceHistory(token, courseId)
                _historyState.value = UiState.Success(response.data, response.message)
            } catch (e: Exception) {
                _historyState.value = UiState.Error(e.message ?: "Unable to load attendance history")
            }
        }
    }
}
