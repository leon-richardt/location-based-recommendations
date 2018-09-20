package de.nuttercode.androidprojectss2018.app

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import de.nuttercode.androidprojectss2018.csi.pojo.ScoredEvent
import java.lang.Exception
import java.lang.ref.WeakReference
import java.util.*

open class UpdateEventsTask(context: Context) : AsyncTask<Void, Void, Boolean>() {

    private var contextRef = WeakReference(context)
    // Included for future use
    private var geofencingClient = LocationServices.getGeofencingClient(contextRef.get()!!)


    /**
     * Returns true if fetching events should be rescheduled and false if not.
     */
    override fun doInBackground(vararg parameters: Void?): Boolean {
        if (ContextCompat.checkSelfPermission(contextRef.get()!!, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // We initialize them here in order to make sure they are up-to-date when the task is executed, not when it is declared
            val eventStore = obtainMostRecentEventStore()!!

            val locTask = LocationServices.getFusedLocationProviderClient(contextRef.get()!!).lastLocation

            while (!locTask.isComplete) {
                Log.i(TAG, "locTask is not completed yet, waiting 100ms ...")
                Thread.sleep(100)
            }
            Log.i(TAG, "locTask completed and the result is ${locTask.result}")

            val location = locTask.result
            if (location == null) {
                Log.e(TAG, "Location returned by locTask is null! Ending service ...")
                Toast.makeText(contextRef.get()!!, "Could not fetch location at this time, please try again later.", Toast.LENGTH_LONG).show()
                // Indicate that this job should be rescheduled
                return true
            }
            updateMostRecentLocation(LatLng(location.latitude, location.longitude))

            eventStore.setUserLocation(location.latitude, location.longitude)
            val qri = eventStore.refresh()

            if (!qri.isOK) Log.e(TAG, "Query was not successful. Message: ${qri.message}")

            if (!eventStore.all.isEmpty()) {
                // Add a geofence for every event in the EventStore (can be expanded to only include event above a certain score threshold)
                val geofenceList = LinkedList<Geofence>().apply {
                    for (e in eventStore.all) {
                        add(buildGeofence(e))
                    }
                }

                val request = GeofencingRequest.Builder()
                        .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                        .addGeofences(geofenceList)
                        .build()

                // Register geofencing events
                val geofencingServiceIntent = Intent(contextRef.get(), GeofenceTransitionsIntentService::class.java)
                val pendingIntent = PendingIntent.getService(contextRef.get(), PENDING_INTENT_ID, geofencingServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                if (ContextCompat.checkSelfPermission((contextRef.get() as Context), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    geofencingClient.addGeofences(request, pendingIntent)!!.run {
                        addOnSuccessListener { Log.i(TAG, "Geofences added successfully.") }
                        addOnFailureListener { Log.i(TAG, "Geofences could not be added. Exception Code: ${(exception as Exception).message}") }
                    }
            }

            // Update the EventStore holder (this does not need to be done as EventStores are mutable,
            // but we might make them immutable in the future)
            updateMostRecentEventStore(eventStore)
            // Indicate that this job does not need to be rescheduled immediately
            return false
        } else {
            // If the location access has been revoked, we show a toast and redirect the user back to
            // the splash screen
            Log.e(TAG, "App is missing permission to get user location")
            if (contextRef.get() != null) {
                val splashScreenIntent = Intent(contextRef.get()!!, SplashScreenActivity::class.java)
                        .apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK) }
                contextRef.get()!!.startActivity(splashScreenIntent)
            }

            // Indicate that this job does not need to be rescheduled
            return false
        }
    }

    private fun buildGeofence(scoredEvent: ScoredEvent): Geofence {
        val event = scoredEvent.event

        return Geofence.Builder()
                .setRequestId(scoredEvent.id.toString())
                .setCircularRegion(event.venue.latitude, event.venue.longitude, GEOFENCE_RADIUS)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)       // TODO: Maybe change? Could also remove geofences in StoreListener?
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL)
                .setLoiteringDelay(GEOFENCE_LOITERING_DELAY)   // Only trigger an GeofencingEvent when the user stays inside the circular region for the given time
                .build()
    }

    companion object {
        const val TAG = "UpdateEventsTask"

        const val PENDING_INTENT_ID = 0

        /**
         * Radius (in meters) inside which a geofencing event should trigger
         */
        private const val GEOFENCE_RADIUS: Float = 300.0f

        /**
         * Time (in milliseconds) that a user has to spend inside the circular region before a geofencing event should trigger
         */
        private const val GEOFENCE_LOITERING_DELAY: Int = 1000 * 20     // 20 seconds
    }

}