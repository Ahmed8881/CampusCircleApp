package com.example.campuscircleapp.features.home.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.home.models.StudentEnrollmentResponse
import com.example.campuscircleapp.features.home.services.HomeService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EnrolledCoursesViewModel : ViewModel() {
    private val homeService = HomeService()

    private val _coursesState = MutableStateFlow<UiState<List<StudentEnrollmentResponse>>>(UiState.Idle)
    val coursesState = _coursesState.asStateFlow()

    fun load(token: String) {
        _coursesState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val result = homeService.getStudentCourses(token)
                _coursesState.value = UiState.Success(result.data, result.message)
            } catch (e: Exception) {
                _coursesState.value = UiState.Error(e.message ?: "Failed to load courses")
            }
        }
    }
}
