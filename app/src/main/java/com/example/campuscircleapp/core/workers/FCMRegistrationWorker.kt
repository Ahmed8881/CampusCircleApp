package com.example.campuscircleapp.core.workers

import android.content.Context
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.campuscircleapp.core.utils.DeviceRegistrationHelper
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
            Log.w("FCMWorker", "Registration aborted: Missing Auth Token or FCM Token.")
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
            
            Log.d("FCMWorker", "Syncing device info with backend. Token: ${fcmToken.take(10)}...")
            authService.registerDevice(authToken, deviceTokenDto)
            
            // Success: Update the last registered token locally to prevent redundant calls
            SessionManager.setLastRegisteredFcmToken(context, fcmToken)
            Log.i("FCMWorker", "Device registration successful.")
            
            Result.success()
        } catch (e: Exception) {
            val errorMsg = e.message ?: ""
            Log.e("FCMWorker", "Registration failed: $errorMsg")

            // If the backend specifically says the token is invalid or unregistered
            if (errorMsg.contains("unregistered", ignoreCase = true) || 
                errorMsg.contains("invalid", ignoreCase = true)) {
                Log.w("FCMWorker", "Token rejected by backend. Triggering fresh token request.")
                DeviceRegistrationHelper.resetAndRegistration(context)
                return Result.failure()
            }

            // For network errors, let WorkManager handle the exponential backoff retry
            Result.retry()
        }
    }
}
