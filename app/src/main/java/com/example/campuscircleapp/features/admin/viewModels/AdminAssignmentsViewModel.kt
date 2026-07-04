package com.example.campuscircleapp.features.admin.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.admin.models.InstructorResponse
import com.example.campuscircleapp.features.admin.models.SemesterResponse
import com.example.campuscircleapp.features.admin.models.StudentResponse
import com.example.campuscircleapp.features.home.models.SpaceResponse
import com.example.campuscircleapp.features.home.services.HomeService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminAssignmentsViewModel : ViewModel() {
    private val homeService = HomeService()

    private val _spacesState = MutableStateFlow<UiState<List<SpaceResponse>>>(UiState.Idle)
    val spacesState = _spacesState.asStateFlow()

    private val _semestersState = MutableStateFlow<UiState<List<SemesterResponse>>>(UiState.Idle)
    val semestersState = _semestersState.asStateFlow()

    private val _instructorsState = MutableStateFlow<UiState<List<InstructorResponse>>>(UiState.Idle)
    val instructorsState = _instructorsState.asStateFlow()

    private val _studentsState = MutableStateFlow<UiState<List<StudentResponse>>>(UiState.Idle)
    val studentsState = _studentsState.asStateFlow()

    private val _assignSemesterState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val assignSemesterState = _assignSemesterState.asStateFlow()

    private val _assignInstructorState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val assignInstructorState = _assignInstructorState.asStateFlow()

    private val _assignCRState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val assignCRState = _assignCRState.asStateFlow()

    fun loadAll(token: String) {
        loadSpaces(token)
        loadSemesters(token)
        loadInstructors(token)
        loadStudents(token)
    }

    private fun loadSpaces(token: String) {
        _spacesState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val result = homeService.getSpaces(token)
                _spacesState.value = UiState.Success(result.data, result.message)
            } catch (e: Exception) {
                _spacesState.value = UiState.Error(e.message ?: "Failed to load spaces")
            }
        }
    }

    private fun loadSemesters(token: String) {
        _semestersState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val result = homeService.getAllSemesters(token)
                _semestersState.value = UiState.Success(result.data, result.message)
            } catch (e: Exception) {
                _semestersState.value = UiState.Error(e.message ?: "Failed to load semesters")
            }
        }
    }

    private fun loadInstructors(token: String) {
        _instructorsState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val result = homeService.getAllInstructors(token)
                _instructorsState.value = UiState.Success(result.data, result.message)
            } catch (e: Exception) {
                _instructorsState.value = UiState.Error(e.message ?: "Failed to load instructors")
            }
        }
    }

    private fun loadStudents(token: String) {
        _studentsState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val result = homeService.getAllStudents(token)
                _studentsState.value = UiState.Success(result.data, result.message)
            } catch (e: Exception) {
                _studentsState.value = UiState.Error(e.message ?: "Failed to load students")
            }
        }
    }

    fun performAssignSemester(token: String, spaceId: Long, semesterId: Long) {
        _assignSemesterState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val result = homeService.assignSemester(token, spaceId, semesterId)
                _assignSemesterState.value = UiState.Success(Unit, result.message)
            } catch (e: Exception) {
                _assignSemesterState.value = UiState.Error(e.message ?: "Failed to assign semester")
            }
        }
    }

    fun performAssignInstructor(token: String, instructorId: Long, spaceId: Long) {
        _assignInstructorState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val result = homeService.assignSpaceToInstructor(token, instructorId, spaceId)
                _assignInstructorState.value = UiState.Success(Unit, result.message)
            } catch (e: Exception) {
                _assignInstructorState.value = UiState.Error(e.message ?: "Failed to assign instructor")
            }
        }
    }

    fun performAssignCR(token: String, userName: String) {
        _assignCRState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val result = homeService.assignCR(token, userName)
                _assignCRState.value = UiState.Success(Unit, result.message)
            } catch (e: Exception) {
                _assignCRState.value = UiState.Error(e.message ?: "Failed to assign CR")
            }
        }
    }

    fun resetAssignSemesterState() { _assignSemesterState.value = UiState.Idle }
    fun resetAssignInstructorState() { _assignInstructorState.value = UiState.Idle }
    fun resetAssignCRState() { _assignCRState.value = UiState.Idle }
}
