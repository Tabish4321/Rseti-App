package com.rsetiapp.core.geoFancing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent!!)
        if (geofencingEvent != null) {
            if (geofencingEvent.hasError()) return
        }

        val geofenceTransition = geofencingEvent?.geofenceTransition

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            Toast.makeText(context, "You are in the attendance zone!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "You left the attendance zone!", Toast.LENGTH_SHORT).show()
        }
    }
}
