package com.example.campuscircleapp.core.models

sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T,val message: String?) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
