package com.rsetiapp.core.geoFancing

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import androidx.core.app.NotificationCompat

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("GeofenceReceiver", "onReceive called") // Add this
        if (context == null || intent == null) {
            Log.e("GeofenceReceiver", "Context or Intent is null")
            return
        }

        val geofencingEvent = com.google.android.gms.location.GeofencingEvent.fromIntent(intent)
        if (geofencingEvent != null) {
            if (geofencingEvent.hasError()) {
                if (geofencingEvent != null) {
                    Log.e("GeofenceReceiver", "Error code: ${geofencingEvent.errorCode}")
                }
                return
            }
        }

        when (val transition = geofencingEvent?.geofenceTransition) {
            com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER -> {
                sendNotification(context, "You entered the institute zone!")
                Log.d("GeofenceReceiver", "ENTERED geofence")
            }
            com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_EXIT -> {
                sendNotification(context, "You exited the institute zone!")
                Log.d("GeofenceReceiver", "EXITED geofence")
            }
            else -> {
                Log.d("GeofenceReceiver", "Unknown geofence transition: $transition")
            }
        }
    }

    private fun sendNotification(context: Context, message: String) {
        val channelId = "geofence_channel"
        val notificationId = 101

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for Android 8+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Geofence Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for geofence transitions"
                enableLights(true)
                lightColor = Color.GREEN
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_map)
            .setContentTitle("Geofence Alert")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }
}
