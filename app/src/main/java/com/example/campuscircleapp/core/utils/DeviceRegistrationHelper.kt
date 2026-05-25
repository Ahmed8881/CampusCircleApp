package com.example.campuscircleapp.core.utils

import android.content.Context
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.campuscircleapp.core.workers.FCMRegistrationWorker
import com.example.campuscircleapp.shared.services.SessionManager
import com.google.firebase.messaging.FirebaseMessaging
import java.util.concurrent.TimeUnit

object DeviceRegistrationHelper {
    private const val TAG = "DeviceRegistrationHelper"
    private const val WORK_NAME = "fcm_registration_work"

    /**
     * Entry point to ensure the device is registered with the backend.
     * It checks if registration is needed and schedules a background worker.
     */
    fun enqueueRegistration(context: Context, force: Boolean = false) {
        val authToken = SessionManager.getToken(context)
        if (authToken.isNullOrBlank()) {
            Log.d(TAG, "User not logged in, skipping registration")
            return
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val currentToken = task.result
                val lastRegistered = SessionManager.getLastRegisteredFcmToken(context)

                // 1. Save locally for reference
                SessionManager.saveDeviceInfo(
                    context,
                    fcmToken = currentToken,
                    deviceToken = android.provider.Settings.Secure.getString(
                        context.contentResolver,
                        android.provider.Settings.Secure.ANDROID_ID
                    ),
                    deviceName = android.os.Build.MODEL,
                    platform = "Android"
                )

                // 2. Decide if we need to call the API
                if (force || currentToken != lastRegistered) {
                    Log.d(TAG, "Scheduling registration work. Token changed or forced.")
                    scheduleWorker(context)
                } else {
                    Log.d(TAG, "Registration skipped. Token matches last registered.")
                }
            } else {
                Log.e(TAG, "Failed to get FCM token: ${task.exception?.message}")
            }
        }
    }

    private fun scheduleWorker(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val registrationRequest = OneTimeWorkRequestBuilder<FCMRegistrationWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            WORK_NAME,
            ExistingWorkPolicy.REPLACE, // Start a fresh attempt
            registrationRequest
        )
    }
}
