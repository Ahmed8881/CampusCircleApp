package com.example.campuscircleapp.features.teacher.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.home.models.AdminCourseResponse
import com.example.campuscircleapp.features.home.models.EnrollmentByCourseResponse
import com.example.campuscircleapp.features.home.services.HomeService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TeacherStudentsViewModel : ViewModel() {

    private val homeService = HomeService()

    private val _coursesState = MutableStateFlow<UiState<List<AdminCourseResponse>>>(UiState.Idle)
    val coursesState = _coursesState.asStateFlow()

    private val _studentsState = MutableStateFlow<UiState<List<EnrollmentByCourseResponse>>>(UiState.Idle)
    val studentsState = _studentsState.asStateFlow()

    fun loadCourses(token: String) {
        viewModelScope.launch {
            _coursesState.value = UiState.Loading
            try {
                val response = homeService.getTeacherCourses(token)
                _coursesState.value = UiState.Success(response.data, response.message)
            } catch (e: Exception) {
                _coursesState.value = UiState.Error(e.message ?: "Unknown Error")
            }
        }
    }

    fun loadStudentsByCourse(token: String, courseId: Long) {
        viewModelScope.launch {
            _studentsState.value = UiState.Loading
            try {
                val response = homeService.getStudentsByCourse(token, courseId)
                _studentsState.value = UiState.Success(response.data, response.message)
            } catch (e: Exception) {
                _studentsState.value = UiState.Error(e.message ?: "Unknown Error")
            }
        }
    }
}
