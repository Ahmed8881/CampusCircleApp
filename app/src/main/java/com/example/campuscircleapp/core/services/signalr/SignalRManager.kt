package com.example.campuscircleapp.core.services.signalr

import android.content.Context
import android.util.Log
import com.example.campuscircleapp.features.notifications.models.NotificationModel
import com.example.campuscircleapp.shared.services.SessionManager
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import com.google.gson.Gson
import com.google.gson.JsonObject

object SignalRManager {
    private const val TAG = "SignalRManager"
    private const val HUB_URL = "https://campus-circle.hserver321.dpdns.org/notificationHub"

    private var hubConnection: HubConnection? = null
    private val gson = Gson()

    private val _newNotifications = MutableSharedFlow<NotificationModel>(extraBufferCapacity = 64)
    val newNotifications = _newNotifications.asSharedFlow()

    private val scope = CoroutineScope(Dispatchers.IO)
    private var isManuallyStopped = false

    fun init(context: Context) {
        try {
            val token = SessionManager.getToken(context)
            if (token == null) return

            if (hubConnection != null && hubConnection?.connectionState != HubConnectionState.DISCONNECTED) {
                return
            }

            Log.i(TAG, "Initializing SignalR HubConnection...")
            isManuallyStopped = false

            hubConnection = HubConnectionBuilder.create(HUB_URL)
                .withAccessTokenProvider(Single.just(token))
                .build()

            setupHandlers()

            hubConnection?.onClosed { exception ->
                if (exception != null) Log.e(TAG, "Connection Closed: ${exception.message}")
                if (!isManuallyStopped) scheduleReconnect()
            }

            start()
        } catch (e: Exception) {
            Log.e(TAG, "SignalR init failed", e)
        }
    }

    private fun setupHandlers() {
        Log.d(TAG, "Registering SignalR event handlers...")

        // Generic notification handler (very common in SignalR hubs)
        hubConnection?.on("TimetableChanged", { payload ->

            Log.i(TAG, "SIGNALR RECEIVE: TimetableChanged")

            handleIncomingPayload(payload) { json ->

                val details = getJsonString(json, "details") ?: ""
                val changedBy = getJsonString(json, "changedBy") ?: ""

                NotificationModel(
                    id = getJsonLong(json, "notificationId", "id")
                        ?: System.currentTimeMillis(),

                    userId = getJsonLong(json, "userId"),

                    title = "Timetable Changed",

                    body = if (changedBy.isNotEmpty())
                        "$details\nUpdated by: $changedBy"
                    else
                        details,

                    type = getJsonString(json, "type")
                        ?: "TimeTableUpdate",

                    isRead = false,

                    createdAt = getJsonString(json, "timestamp", "createdAt"),

                    spaceId = getJsonLong(json, "spaceId")
                )
            }

        }, Any::class.java)

        hubConnection?.on("ReceiveAnnouncement", { payload ->
            Log.i(TAG, "SIGNALR RECEIVE: ReceiveAnnouncement")
            handleIncomingPayload(payload) { parseGenericNotification(it) }
        }, Any::class.java)

        hubConnection?.on("ReceiveAttendanceNotification", { payload ->
            Log.i(TAG, "SIGNALR RECEIVE: ReceiveAttendanceNotification")
            handleIncomingPayload(payload) { json ->
                val status = getJsonString(json, "status", "attendanceStatus") ?: ""
                val course = getJsonString(json, "courseName", "course") ?: ""
                NotificationModel(
                    id = getJsonLong(json, "notificationId", "id") ?: System.currentTimeMillis(),
                    userId = getJsonLong(json, "userId"),
                    title = getJsonString(json, "title") ?: "Attendance: $status",
                    body = getJsonString(json, "message", "body") ?: "Attendance for $course is $status",
                    type = "Attendance",
                    isRead = false,
                    createdAt = getJsonString(json, "timestamp", "createdAt"),
                    spaceId = getJsonLong(json, "spaceId")
                )
            }
        }, Any::class.java)
    }

    private fun parseGenericNotification(json: JsonObject): NotificationModel {
        return NotificationModel(
            id = getJsonLong(json, "notificationId", "id") ?: System.currentTimeMillis(),
            userId = getJsonLong(json, "userId"),
            title = getJsonString(json, "title", "announcedBy") ?: "Notification",
            body = getJsonString(json, "message", "body", "details") ?: "",
            type = getJsonString(json, "type") ?: "General",
            isRead = false,
            createdAt = getJsonString(json, "timestamp", "createdAt"),
            spaceId = getJsonLong(json, "spaceId")
        )
    }

    private fun getJsonString(json: JsonObject, vararg keys: String): String? {
        for (key in keys) {
            if (json.has(key) && !json.get(key).isJsonNull) return json.get(key).asString
        }
        return null
    }

    private fun getJsonLong(json: JsonObject, vararg keys: String): Long? {
        for (key in keys) {
            if (json.has(key) && !json.get(key).isJsonNull) {
                return try { json.get(key).asLong } catch (e: Exception) { null }
            }
        }
        return null
    }

    private fun handleIncomingPayload(payload: Any?, mapper: (JsonObject) -> NotificationModel) {
        if (payload == null) return
        try {
            val json = gson.toJsonTree(payload).asJsonObject
            val notification = mapper(json)
            emitNotification(notification)
        } catch (e: Exception) {
            Log.e(TAG, "Error mapping SignalR payload: ${e.message}")
        }
    }

    /**
     * Public method to allow other services (like FCM) to inject notifications into the UI flow
     */
    fun emitNotification(notification: NotificationModel) {
        scope.launch {
            Log.d(TAG, "Emitting notification to Flow: ${notification.id}")
            _newNotifications.emit(notification)
        }
    }

    private fun start() {
        scope.launch {
            try {
                if (hubConnection?.connectionState == HubConnectionState.DISCONNECTED) {
                    hubConnection?.start()?.blockingAwait()
                    Log.i(TAG, "SignalR Connected! State: ${hubConnection?.connectionState}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "SignalR Start Failed: ${e.message}")
                if (!isManuallyStopped) scheduleReconnect()
            }
        }
    }

    private fun scheduleReconnect() {
        if (isManuallyStopped) return
        scope.launch {
            delay(5000)
            if (!isManuallyStopped && hubConnection?.connectionState == HubConnectionState.DISCONNECTED) {
                start()
            }
        }
    }

    fun stop() {
        isManuallyStopped = true
        hubConnection?.stop()
    }
}
