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

    fun loadSemesters(token: String) {
        _semestersState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val result = homeService.getSemesters(token)
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
                val result = homeService.createSemester(token, CreateSemesterRequest(name, no, start, end))
                _createState.value = UiState.Success(Unit, result.message)
            } catch (e: Exception) {
                _createState.value = UiState.Error(e.message ?: "Failed to create semester")
            }
        }
    }

    fun resetCreateState() { _createState.value = UiState.Idle }
}
