package com.example.campuscircleapp.features.admin.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuscircleapp.core.models.UiState
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

    private val _actionState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val actionState = _actionState.asStateFlow()

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

    fun createWorkspace(token: String, name: String) {
        if (name.isBlank()) { _createState.value = UiState.Error("Workspace name is required"); return }
        _createState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val result = homeService.createWorkspace(token, name)
                _createState.value = UiState.Success(Unit, result.message)
            } catch (e: Exception) {
                _createState.value = UiState.Error(e.message ?: "Failed to create workspace")
            }
        }
    }

    fun updateWorkspace(token: String, id: Long, name: String) {
        if (name.isBlank()) { _actionState.value = UiState.Error("Workspace name is required"); return }
        _actionState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val result = homeService.updateWorkspace(token, id, name)
                _actionState.value = UiState.Success(Unit, result.message)
            } catch (e: Exception) {
                _actionState.value = UiState.Error(e.message ?: "Failed to update workspace")
            }
        }
    }

    fun deleteWorkspace(token: String, id: Long) {
        _actionState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val result = homeService.deleteWorkspace(token, id)
                _actionState.value = UiState.Success(Unit, result.message)
            } catch (e: Exception) {
                _actionState.value = UiState.Error(e.message ?: "Failed to delete workspace")
            }
        }
    }

    fun toggleWorkspaceStatus(token: String, id: Long, isActive: Boolean) {
        _actionState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val result = homeService.toggleWorkspaceStatus(token, id, isActive)
                _actionState.value = UiState.Success(Unit, result.message)
            } catch (e: Exception) {
                _actionState.value = UiState.Error(e.message ?: "Failed to toggle status")
            }
        }
    }

    fun resetCreateState() { _createState.value = UiState.Idle }
    fun resetActionState() { _actionState.value = UiState.Idle }
}
