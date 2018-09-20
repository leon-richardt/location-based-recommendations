package de.nuttercode.androidprojectss2018.app

import android.app.IntentService
import android.app.PendingIntent
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceTransitionsIntentService : IntentService("GeofenceTransitionsIntentService") {

    override fun onHandleIntent(intent: Intent?) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        Log.i(TAG, "Started onHandleIntent() in GeofenceTransitionsIS")
        if (geofencingEvent.hasError()) {
            Log.e(TAG, "Error in GeofencingEvent from Intent: Error Code ${geofencingEvent.errorCode}")
            return
        }

        val geofenceTransition = geofencingEvent.geofenceTransition

        // ENTER because of the initial trigger
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL || geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            val triggeringGeofences = geofencingEvent.triggeringGeofences

            sendNotification(triggeringGeofences)
        }
    }

    /**
     * Holds the ID for the next notification that will be posted. It is incremented when [sendNotification]
     * is called.
     */
    private var notifId: Int = 0

    /**
     * Sends a notification for the first [Geofence] in [triggeringGeofences]. The notification informs
     * the user which [Geofence] has been triggered. Clicking it will direct the user to the according
     * [EventOverviewActivity].
     *
     * @param triggeringGeofences a [List] with one or more [Geofence] instances, as returned by
     * [GeofencingEvent.getTriggeringGeofences]
     */
    private fun sendNotification(triggeringGeofences: List<Geofence>): Int {
        if (triggeringGeofences.isEmpty()) throw IllegalStateException("No geofences in passed list")

        // We are just sending a notification for the first geofence in the list
        val eventStore = obtainMostRecentEventStore()
        val scoredEventId = triggeringGeofences[0].requestId.toInt()
        val scoredEvent = eventStore!!.getById(scoredEventId)
        val eventOverviewIntent = Intent(this, EventOverviewActivity::class.java).apply { putExtra(EXTRA_EVENT_CLICKED, scoredEventId) }
        val pendingIntent = PendingIntent.getActivity(this, PENDING_INTENT_ID, eventOverviewIntent, PendingIntent.FLAG_UPDATE_CURRENT)


        val notifBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.notification_icon_background)
                .setContentTitle("Nearby Event")
                .setContentText("${scoredEvent.event.name} is close to you!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)

        val notifManager = NotificationManagerCompat.from(this)
        notifManager.notify(notifId, notifBuilder.build())
        return notifId++
    }

    companion object {
        const val TAG = "GeofenceTransitionsIS"

        const val PENDING_INTENT_ID = 0
    }

}