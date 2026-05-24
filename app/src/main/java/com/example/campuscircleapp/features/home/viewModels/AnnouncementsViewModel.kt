package com.example.campuscircleapp.features.home.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.home.models.AnnouncementItem
import com.example.campuscircleapp.features.home.services.HomeService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AnnouncementsViewModel : ViewModel() {

    private val homeService = HomeService()

    private val _announcementsState = MutableStateFlow<UiState<List<AnnouncementItem>>>(UiState.Idle)
    val announcementsState = _announcementsState.asStateFlow()

    fun loadAnnouncements(token: String) {
        viewModelScope.launch {
            _announcementsState.value = UiState.Loading
            try {
                val response = homeService.getAnnouncements(token)
                _announcementsState.value = UiState.Success(response.data, response.message)
            } catch (e: Exception) {
                _announcementsState.value = UiState.Error(e.message ?: "Unknown Error")
            }
        }
    }
}
