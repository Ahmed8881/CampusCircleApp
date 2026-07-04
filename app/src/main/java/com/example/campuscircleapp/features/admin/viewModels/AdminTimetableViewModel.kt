package com.example.campuscircleapp.features.admin.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.admin.models.CreateTimetableEntryRequest
import com.example.campuscircleapp.features.admin.models.UpdateTimetableEntryRequest
import com.example.campuscircleapp.features.home.models.AdminCourseResponse
import com.example.campuscircleapp.features.home.models.TimetableEntry
import com.example.campuscircleapp.features.home.services.HomeService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminTimetableViewModel : ViewModel() {
    private val homeService = HomeService()

    private val _timetableState = MutableStateFlow<UiState<List<TimetableEntry>>>(UiState.Idle)
    val timetableState = _timetableState.asStateFlow()

    private val _createState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val createState = _createState.asStateFlow()

    private val _updateState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val updateState = _updateState.asStateFlow()

    private val _deleteState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val deleteState = _deleteState.asStateFlow()

    private val _coursesState = MutableStateFlow<UiState<List<AdminCourseResponse>>>(UiState.Idle)
    val coursesState = _coursesState.asStateFlow()

    fun load(token: String, spaceId: Long = 1) {
        _timetableState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val result = homeService.getTimetable(token, spaceId)
                _timetableState.value = UiState.Success(result.data, result.message)
            } catch (e: Exception) {
                _timetableState.value = UiState.Error(e.message ?: "Failed to load timetable")
            }
        }
    }

    fun loadCourses(token: String) {
        _coursesState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val result = homeService.getAllCourses(token)
                _coursesState.value = UiState.Success(result.data, result.message)
            } catch (e: Exception) {
                _coursesState.value = UiState.Error(e.message ?: "Failed to load courses")
            }
        }
    }

    fun createEntry(token: String, request: CreateTimetableEntryRequest) {
        _createState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val result = homeService.createTimetableEntry(token, request)
                _createState.value = UiState.Success(Unit, result.message)
            } catch (e: Exception) {
                _createState.value = UiState.Error(e.message ?: "Failed to add entry")
            }
        }
    }

    fun updateEntry(token: String, request: UpdateTimetableEntryRequest) {
        _updateState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val result = homeService.updateTimetableEntry(token, request)
                _updateState.value = UiState.Success(Unit, result.message)
            } catch (e: Exception) {
                _updateState.value = UiState.Error(e.message ?: "Failed to update entry")
            }
        }
    }

    fun deleteEntry(token: String, id: Long) {
        _deleteState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val result = homeService.deleteTimetableEntry(token, id)
                _deleteState.value = UiState.Success(Unit, result.message)
            } catch (e: Exception) {
                _deleteState.value = UiState.Error(e.message ?: "Failed to delete entry")
            }
        }
    }

    fun resetCreateState() { _createState.value = UiState.Idle }
    fun resetUpdateState() { _updateState.value = UiState.Idle }
    fun resetDeleteState() { _deleteState.value = UiState.Idle }
}
