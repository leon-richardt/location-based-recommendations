package de.nuttercode.androidprojectss2018.app

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.support.v4.content.ContextCompat
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import de.nuttercode.androidprojectss2018.csi.ScoredEvent
import java.lang.ref.WeakReference
import java.util.*

open class UpdateEventsTask(context: Context) : AsyncTask<Void, Void, Boolean>() {

    private var contextRef = WeakReference(context)
    // Included for future use
//    private var geofencingClient = LocationServices.getGeofencingClient(contextRef.get()!!)


    /**
     * Returns true if fetching events should be rescheduled and false if not.
     */
    override fun doInBackground(vararg parameters: Void?): Boolean {
        if (ContextCompat.checkSelfPermission(contextRef.get()!!, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(contextRef.get()!!, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // We initialize them here in order to make sure they are up-to-date when the task is executed, not when it is declared
            val eventStore = obtainMostRecentEventStore()

            val locTask = LocationServices.getFusedLocationProviderClient(contextRef.get()!!).lastLocation

            while (!locTask.isComplete) {
                Log.i(TAG, "locTask is not completed yet, waiting 100ms ...")
                Thread.sleep(100)
            }
            Log.i(TAG, "locTask completed and the result is ${locTask.result}")

            val location = locTask.result
            if (location == null) {
                Log.e(TAG, "Location returned by locTask is null! Ending service ...")
                // Indicate that this job should be rescheduled
                return true
            }

            eventStore.setUserLocation(location.latitude, location.longitude)
            val qri = eventStore.refresh()
            val cqrs = qri.clientQueryResultState
            val sqrs = qri.serverQueryResultState

            // TODO: Remove logging
            Log.i(TAG, "CQRS = $cqrs, SQRS = $sqrs")
            if (!qri.isOK) Log.e(TAG, "Query was not successful. Message: ${qri.message}")
            Log.i(TAG, "Now listing all Events in EventStore. There are ${eventStore.all.size} events matching the given tags in the store")
            for (e in eventStore.all) {
                Log.i(TAG, "Event in eventStore: ${e.event.name}")
            }
            Log.i(TAG, "Finished listing all Events in EventStore")


            if (!eventStore.all.isEmpty()) {
                GeofencingRequest.Builder()
                        .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                        .addGeofences(
                                LinkedList<Geofence>().apply {
                                    for (e in eventStore.all) add(buildGeofence(e))
                                })
                        .build()

                // TODO: Register geofencing events
            }

            // Update the EventStore holder (this does not need to be done as EventStores are mutable)
            updateMostRecentEventStore(eventStore)
            // Indicate that this job does not need to be rescheduled immediately
            return false
        } else {
            Log.e(TAG, "App is missing permission to get user location")
            // Indicate that this job does not need to be rescheduled
            return false
        }
    }

    private fun buildGeofence(scoredEvent: ScoredEvent): Geofence {
        val event = scoredEvent.event

        return Geofence.Builder()
                .setRequestId(event.id.toString())
                .setCircularRegion(event.venue.latitude, event.venue.longitude, GEOFENCE_RADIUS)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL)
                .setLoiteringDelay(GEOFENCE_LOITERING_DELAY)   // Only trigger an GeofencingEvent when the user stays inside the circular region for the given time
                .build()
    }

    companion object {
        const val TAG = "UpdateEventsTask"

        /**
         * Radius (in meters) inside which a geofencing event should trigger
         */
        private const val GEOFENCE_RADIUS: Float = 100.0f

        /**
         * Time (in milliseconds) that a user has to spend inside the circular region before a geofencing event should trigger
         */
        private const val GEOFENCE_LOITERING_DELAY: Int = 1000 * 20
    }

}