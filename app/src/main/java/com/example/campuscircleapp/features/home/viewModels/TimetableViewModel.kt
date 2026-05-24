package com.example.campuscircleapp.features.home.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.home.models.TimetableEntry
import com.example.campuscircleapp.features.home.services.HomeService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TimetableViewModel : ViewModel() {

    private val homeService = HomeService()

    private val _timetableState = MutableStateFlow<UiState<List<TimetableEntry>>>(UiState.Idle)
    val timetableState = _timetableState.asStateFlow()

    fun loadTimetable(token: String, spaceId: Long = 1) {
        viewModelScope.launch {
            _timetableState.value = UiState.Loading
            try {
                val response = homeService.getTimetable(token, spaceId)
                _timetableState.value = UiState.Success(response.data, response.message)
            } catch (e: Exception) {
                _timetableState.value = UiState.Error(e.message ?: "Unknown Error")
            }
        }
    }
}
