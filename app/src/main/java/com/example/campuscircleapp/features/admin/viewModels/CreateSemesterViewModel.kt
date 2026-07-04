package com.example.campuscircleapp.features.admin.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.admin.models.CreateSemesterRequest
import com.example.campuscircleapp.features.admin.models.SemesterResponse
import com.example.campuscircleapp.features.home.services.HomeService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreateSemesterViewModel : ViewModel() {
    private val homeService = HomeService()

    private val _createState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val createState = _createState.asStateFlow()

    private val _semestersState = MutableStateFlow<UiState<List<SemesterResponse>>>(UiState.Idle)
    val semestersState = _semestersState.asStateFlow()

    private val _actionState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val actionState = _actionState.asStateFlow()

    fun loadSemesters(token: String) {
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

    fun createSemester(token: String, name: String, no: Int, start: String, end: String) {
        if (name.isBlank() || start.isBlank() || end.isBlank()) {
            _createState.value = UiState.Error("All fields are required"); return
        }
        _createState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val result = homeService.createSemester(token, CreateSemesterRequest(id = 0, number = no, name = name, startDate = start, endDate = end))
                _createState.value = UiState.Success(Unit, result.message)
            } catch (e: Exception) {
                _createState.value = UiState.Error(e.message ?: "Failed to create semester")
            }
        }
    }

    fun updateSemester(token: String, id: Long, name: String, no: Int, start: String, end: String) {
        if (name.isBlank() || start.isBlank() || end.isBlank()) {
            _actionState.value = UiState.Error("All fields are required"); return
        }
        _actionState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val result = homeService.updateSemester(token, CreateSemesterRequest(id = id, number = no, name = name, startDate = start, endDate = end))
                _actionState.value = UiState.Success(Unit, result.message)
            } catch (e: Exception) {
                _actionState.value = UiState.Error(e.message ?: "Failed to update semester")
            }
        }
    }

    fun deleteSemester(token: String, semesterId: Long) {
        _actionState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val result = homeService.deleteSemester(token, semesterId)
                _actionState.value = UiState.Success(Unit, result.message)
            } catch (e: Exception) {
                _actionState.value = UiState.Error(e.message ?: "Failed to delete semester")
            }
        }
    }

    fun resetCreateState() { _createState.value = UiState.Idle }
    fun resetActionState() { _actionState.value = UiState.Idle }
}
