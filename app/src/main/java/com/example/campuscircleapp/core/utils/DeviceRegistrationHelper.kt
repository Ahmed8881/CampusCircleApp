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
     * Enqueues a worker to register the current FCM token with the backend.
     */
    fun enqueueRegistration(context: Context) {
        val appContext = context.applicationContext
        val authToken = SessionManager.getToken(appContext)
        
        if (authToken.isNullOrBlank()) {
            Log.d(TAG, "Registration skipped: User not logged in.")
            return
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val currentToken = task.result

                Log.d(TAG, "Current FCM Token: ${currentToken?.take(10)}...")

                if (currentToken.isNullOrBlank()) {
                    Log.w(TAG, "FCM Token is null or empty. Skipping registration.")
                    return@addOnCompleteListener
                }

                // Update local device info cache for the worker to use
                SessionManager.saveDeviceInfo(
                    appContext,
                    fcmToken = currentToken,
                    deviceToken = android.provider.Settings.Secure.getString(
                        appContext.contentResolver,
                        android.provider.Settings.Secure.ANDROID_ID
                    ),
                    deviceName = android.os.Build.MODEL,
                    platform = "Android"
                )

                Log.i(TAG, "Scheduling FCM registration worker")
                scheduleWorker(appContext)
            } else {
                Log.e(TAG, "FCM Token fetch failed: ${task.exception?.message}")
            }
        }
    }

    /**
     * Clears local FCM state and requests a fresh token from Firebase.
     * Use this if the backend indicates the current token is "Unregistered" or invalid.
     */
    fun resetAndRegistration(context: Context) {
        val appContext = context.applicationContext
        Log.w(TAG, "Resetting FCM token and re-registering...")

        FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.i(TAG, "FCM token deleted successfully. Requesting new token...")
                // Clear the local record of the last successful registration
                SessionManager.setLastRegisteredFcmToken(appContext, null)
                // Trigger a fresh registration
                enqueueRegistration(appContext)
            } else {
                Log.e(TAG, "Failed to delete FCM token: ${task.exception?.message}. Attempting re-registration anyway.")
                enqueueRegistration(appContext)
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
            ExistingWorkPolicy.REPLACE,
            registrationRequest
        )
    }
}
