package com.example.campuscircleapp.features.home.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.home.models.SpaceResponse
import com.example.campuscircleapp.features.home.services.HomeService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminAnnouncementsViewModel : ViewModel() {

    private val homeService = HomeService()

    private val _spacesState = MutableStateFlow<UiState<List<SpaceResponse>>>(UiState.Idle)
    val spacesState = _spacesState.asStateFlow()

    private val _announceState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val announceState = _announceState.asStateFlow()

    fun loadSpaces(token: String) {
        viewModelScope.launch {
            _spacesState.value = UiState.Loading
            try {
                val response = homeService.getSpaces(token)
                _spacesState.value = UiState.Success(response.data, response.message)
            } catch (e: Exception) {
                _spacesState.value = UiState.Error(e.message ?: "Unknown Error")
            }
        }
    }

    fun announceToAll(token: String, message: String) {
        viewModelScope.launch {
            _announceState.value = UiState.Loading
            try {
                val response = homeService.announceToAllSpaces(token, message)
                _announceState.value = UiState.Success(response.data, response.message)
            } catch (e: Exception) {
                _announceState.value = UiState.Error(e.message ?: "Unknown Error")
            }
        }
    }

    fun announceToSpace(token: String, spaceId: Long, message: String) {
        viewModelScope.launch {
            _announceState.value = UiState.Loading
            try {
                val response = homeService.announceToSpace(token, spaceId, message)
                _announceState.value = UiState.Success(response.data, response.message)
            } catch (e: Exception) {
                _announceState.value = UiState.Error(e.message ?: "Unknown Error")
            }
        }
    }

    fun resetAnnounceState() {
        _announceState.value = UiState.Idle
    }
}
