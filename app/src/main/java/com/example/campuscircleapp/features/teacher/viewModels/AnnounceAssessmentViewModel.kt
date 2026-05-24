package com.example.campuscircleapp.features.teacher.viewModels

import androidx.lifecycle.ViewModel
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.teacher.models.AnnounceAssessmentRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AnnounceAssessmentViewModel : ViewModel() {

    private val _submitState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val submitState = _submitState.asStateFlow()

    fun submitAssessment(request: AnnounceAssessmentRequest) {
        _submitState.value = UiState.Success(Unit, "${request.type} published successfully!")
    }

    fun resetState() {
        _submitState.value = UiState.Idle
    }
}
