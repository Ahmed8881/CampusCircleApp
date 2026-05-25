package com.example.campuscircleapp.features.notifications.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuscircleapp.features.notifications.models.NotificationModel
import com.example.campuscircleapp.features.notifications.services.NotificationService
import kotlinx.coroutines.launch

class NotificationViewModel : ViewModel() {
    private val notificationService = NotificationService()

    private val _notifications = MutableLiveData<List<NotificationModel>>()
    val notifications: LiveData<List<NotificationModel>> get() = _notifications

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun fetchNotifications(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = notificationService.getMyNotifications(token)
                _notifications.value = result.data ?: emptyList()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun markAsRead(token: String, notification: NotificationModel) {
        viewModelScope.launch {
            try {
                notificationService.markAsRead(token, notification.id)
                // Update local list
                val currentList = _notifications.value?.toMutableList()
                currentList?.find { it.id == notification.id }?.isRead = true
                _notifications.value = currentList
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}
