package com.example.campuscircleapp.shared.services

import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object LoaderManager {
    private val activeRequests = AtomicInteger(0)
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun show() {
        val count = activeRequests.incrementAndGet()
        if (count > 0) _isLoading.value = true
    }

    fun hide() {
        val count = activeRequests.decrementAndGet()
        if (count <= 0) {
            activeRequests.set(0)
            _isLoading.value = false
        }
    }
}