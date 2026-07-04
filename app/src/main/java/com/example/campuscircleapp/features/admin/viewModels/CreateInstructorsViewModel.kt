package com.example.campuscircleapp.features.admin.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.admin.models.CreateInstructorRequest
import com.example.campuscircleapp.features.admin.models.InstructorResponse
import com.example.campuscircleapp.features.home.services.HomeService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreateInstructorsViewModel : ViewModel() {
    private val homeService = HomeService()

    private val _createState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val createState = _createState.asStateFlow()

    private val _instructorsState = MutableStateFlow<UiState<List<InstructorResponse>>>(UiState.Idle)
    val instructorsState = _instructorsState.asStateFlow()

    private val _actionState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val actionState = _actionState.asStateFlow()

    fun loadInstructors(token: String) {
        _instructorsState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val result = homeService.getAllInstructors(token)
                _instructorsState.value = UiState.Success(result.data, result.message)
            } catch (e: Exception) {
                _instructorsState.value = UiState.Error(e.message ?: "Failed to load instructors")
            }
        }
    }

    fun createInstructor(token: String, name: String, email: String, username: String, password: String, dob: String) {
        if (name.isBlank() || email.isBlank() || username.isBlank() || password.isBlank() || dob.isBlank()) {
            _createState.value = UiState.Error("All fields are required"); return
        }
        _createState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val result = homeService.addInstructor(
                    token, CreateInstructorRequest(
                        fullName = name,
                        userName = username,
                        email = email,
                        password = password,
                        dob = dob
                    )
                )
                _createState.value = UiState.Success(Unit, result.message)
            } catch (e: Exception) {
                _createState.value = UiState.Error(e.message ?: "Failed to create instructor")
            }
        }
    }

    fun deleteInstructor(token: String, instructorId: Long) {
        _actionState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val result = homeService.deleteInstructor(token, instructorId)
                _actionState.value = UiState.Success(Unit, result.message)
            } catch (e: Exception) {
                _actionState.value = UiState.Error(e.message ?: "Failed to delete instructor")
            }
        }
    }

    fun resetCreateState() { _createState.value = UiState.Idle }
    fun resetActionState() { _actionState.value = UiState.Idle }
}
