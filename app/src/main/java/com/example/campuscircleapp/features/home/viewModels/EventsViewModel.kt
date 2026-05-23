package com.example.campuscircleapp.features.home.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.home.models.BirthdayResponse
import com.example.campuscircleapp.features.home.services.HomeService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EventsViewModel : ViewModel() {
    private val homeService = HomeService()

    private val _birthdaysState = MutableStateFlow<UiState<List<BirthdayResponse>>>(UiState.Idle)
    val birthdaysState = _birthdaysState.asStateFlow()

    fun load(token: String, spaceId: Long = 1) {
        _birthdaysState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val result = homeService.getBirthdays(token, spaceId)
                _birthdaysState.value = UiState.Success(result.data, result.message)
            } catch (e: Exception) {
                _birthdaysState.value = UiState.Error(e.message ?: "Failed to load events")
            }
        }
    }
}
