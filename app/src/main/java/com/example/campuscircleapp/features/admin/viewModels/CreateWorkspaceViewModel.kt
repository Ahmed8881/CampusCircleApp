package com.example.campuscircleapp.features.admin.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.admin.models.CreateWorkspaceRequest
import com.example.campuscircleapp.features.home.models.SpaceResponse
import com.example.campuscircleapp.features.home.services.HomeService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreateWorkspaceViewModel : ViewModel() {
    private val homeService = HomeService()

    private val _createState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val createState = _createState.asStateFlow()

    private val _spacesState = MutableStateFlow<UiState<List<SpaceResponse>>>(UiState.Idle)
    val spacesState = _spacesState.asStateFlow()

    fun loadSpaces(token: String) {
        _spacesState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val result = homeService.getSpaces(token)
                _spacesState.value = UiState.Success(result.data, result.message)
            } catch (e: Exception) {
                _spacesState.value = UiState.Error(e.message ?: "Failed to load workspaces")
            }
        }
    }

    fun createWorkspace(token: String, name: String, description: String) {
        if (name.isBlank()) { _createState.value = UiState.Error("Workspace name is required"); return }
        _createState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val result = homeService.createWorkspace(token, CreateWorkspaceRequest(name, description))
                _createState.value = UiState.Success(Unit, result.message)
            } catch (e: Exception) {
                _createState.value = UiState.Error(e.message ?: "Failed to create workspace")
            }
        }
    }

    fun resetCreateState() { _createState.value = UiState.Idle }
}
