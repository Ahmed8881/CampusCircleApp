package com.example.campuscircleapp.features.admin.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.admin.models.CreateTimetableEntryRequest
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

    fun resetCreateState() { _createState.value = UiState.Idle }
}
