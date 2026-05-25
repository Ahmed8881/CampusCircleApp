package com.example.campuscircleapp.core.workers

import android.content.Context
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.campuscircleapp.features.auth.models.UserDeviceTokenDTO
import com.example.campuscircleapp.features.auth.services.AuthService
import com.example.campuscircleapp.shared.services.SessionManager

class FCMRegistrationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    private val authService = AuthService()

    override suspend fun doWork(): Result {
        val context = applicationContext
        val authToken = SessionManager.getToken(context)
        val fcmToken = SessionManager.getFcmToken(context)

        if (authToken.isNullOrBlank() || fcmToken.isNullOrBlank()) {
            return Result.failure()
        }

        val deviceId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        val deviceName = Build.MODEL
        val platform = "Android"

        return try {
            val deviceTokenDto = UserDeviceTokenDTO(
                deviceToken = deviceId,
                fcmToken = fcmToken,
                deviceName = deviceName,
                platform = platform
            )
            
            Log.d("FCMWorker", "Attempting device registration for: $deviceId")
            authService.registerDevice(authToken, deviceTokenDto)
            
            // Mark as successfully registered
            SessionManager.setLastRegisteredFcmToken(context, fcmToken)
            Log.d("FCMWorker", "Device registration successful")
            
            Result.success()
        } catch (e: Exception) {
            Log.e("FCMWorker", "Device registration failed: ${e.message}")
            // Retry if it's a network issue (handled by WorkManager configuration)
            Result.retry()
        }
    }
}
