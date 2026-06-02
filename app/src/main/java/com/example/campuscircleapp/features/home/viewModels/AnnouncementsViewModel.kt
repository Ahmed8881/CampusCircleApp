package com.example.campuscircleapp.features.home.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.core.services.signalr.SignalRManager
import com.example.campuscircleapp.features.notifications.models.NotificationModel
import com.example.campuscircleapp.features.notifications.services.NotificationService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AnnouncementsViewModel : ViewModel() {

    private val TAG = "AnnouncementsViewModel"
    private val notificationService = NotificationService()

    private val _announcementsState = MutableStateFlow<UiState<List<NotificationModel>>>(UiState.Idle)
    val announcementsState = _announcementsState.asStateFlow()

    private val _actionState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val actionState = _actionState.asStateFlow()

    // Temporary storage for notifications that arrive before/during the API load
    private val realTimeBuffer = mutableListOf<NotificationModel>()

    init {
        // Observe real-time notifications from SignalR
        viewModelScope.launch {
            SignalRManager.newNotifications.collect { notification ->
                Log.d(TAG, "New real-time notification collected: ${notification.id}")
                handleRealTimeNotification(notification)
            }
        }
    }

    private fun handleRealTimeNotification(notification: NotificationModel) {
        val currentState = _announcementsState.value
        
        if (currentState is UiState.Success) {
            // If already showing a list, prepend and update UI immediately
            val newList = (listOf(notification) + currentState.data).distinctBy { it.id }
            _announcementsState.value = UiState.Success(newList, "New update received")
            Log.d(TAG, "UI updated with new real-time notification")
        } else {
            // Buffer it so it can be merged when the API call finishes
            realTimeBuffer.add(0, notification)
            Log.d(TAG, "Notification buffered (Current state: ${currentState::class.simpleName})")
            
            // If state was Idle or Error, we can transition to Success immediately with just this notification
            if (currentState is UiState.Idle || currentState is UiState.Error) {
                _announcementsState.value = UiState.Success(realTimeBuffer.toList(), "New message")
            }
        }
    }

    fun loadAnnouncements(token: String) {
        viewModelScope.launch {
            _announcementsState.value = UiState.Loading
            try {
                val result = notificationService.getMyNotifications(token)
                val apiData = result.data ?: emptyList()
                
                // Merge API data with any notifications that arrived in real-time
                val combinedList = (realTimeBuffer + apiData).distinctBy { it.id }
                realTimeBuffer.clear() // Clear buffer after successful merge
                
                _announcementsState.value = UiState.Success(combinedList, result.message)
                Log.d(TAG, "Announcements loaded and merged. Total count: ${combinedList.size}")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load announcements: ${e.message}")
                // Even on error, if we have buffered real-time notifications, show them
                if (realTimeBuffer.isNotEmpty()) {
                    _announcementsState.value = UiState.Success(realTimeBuffer.toList(), "Offline updates")
                } else {
                    _announcementsState.value = UiState.Error(e.message ?: "Unknown Error")
                }
            }
        }
    }

    fun markAsRead(token: String, id: Long) {
        viewModelScope.launch {
            _actionState.value = UiState.Loading
            try {
                notificationService.markAsRead(token, id)
                _actionState.value = UiState.Success(Unit, "Marked as read")
                
                // Locally update the item to avoid full reload
                val currentState = _announcementsState.value
                if (currentState is UiState.Success) {
                    val updatedList = currentState.data.map {
                        if (it.id == id) it.copy(isRead = true) else it
                    }
                    _announcementsState.value = UiState.Success(updatedList, currentState.message)
                }
            } catch (e: Exception) {
                _actionState.value = UiState.Error(e.message ?: "Failed to mark as read")
            }
        }
    }

    fun clearAll(token: String) {
        val currentList = (announcementsState.value as? UiState.Success)?.data ?: return
        if (currentList.isEmpty()) return
        
        val ids = currentList.map { it.id }
        
        viewModelScope.launch {
            _actionState.value = UiState.Loading
            try {
                notificationService.clearNotifications(token, ids)
                _actionState.value = UiState.Success(Unit, "Notifications cleared")
                _announcementsState.value = UiState.Success(emptyList(), "Cleared")
            } catch (e: Exception) {
                _actionState.value = UiState.Error(e.message ?: "Failed to clear notifications")
            }
        }
    }

    fun resetActionState() {
        _actionState.value = UiState.Idle
    }
}
