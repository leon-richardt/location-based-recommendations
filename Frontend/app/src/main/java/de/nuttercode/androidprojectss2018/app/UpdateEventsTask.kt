package de.nuttercode.androidprojectss2018.app

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import de.nuttercode.androidprojectss2018.csi.ClientConfiguration
import de.nuttercode.androidprojectss2018.csi.EventStore
import de.nuttercode.androidprojectss2018.csi.ScoredEvent
import java.lang.ref.WeakReference
import java.util.*

open class UpdateEventsTask(context: Context) : AsyncTask<Void, Void, Boolean>() {

    private var contextRef = WeakReference(context)
    private var sharedPrefs = PreferenceManager.getDefaultSharedPreferences(contextRef.get())
    private var geofencingClient = LocationServices.getGeofencingClient(contextRef.get()!!)
    private lateinit var clientConfiguration: ClientConfiguration
    private lateinit var eventStore: EventStore


    /**
     * Returns true if fetching events should be rescheduled and false if not.
     */
    override fun doInBackground(vararg parameters: Void?): Boolean {
        if (ContextCompat.checkSelfPermission(contextRef.get()!!, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(contextRef.get()!!, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // We initialize them here in order to make sure they are up-to-date when the task is executed, not when it is declared
            clientConfiguration = Gson().fromJson(sharedPrefs.getString(SHARED_PREFS_CLIENT_CONFIG, null), ClientConfiguration::class.java)
            eventStore = EventStore(clientConfiguration)

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

            Log.i(TAG, "ClientConfiguration = $clientConfiguration")
            Log.i(TAG, "Listing Tags in TPC:")
            for (t in clientConfiguration.tagPreferenceConfiguration) Log.i(TAG, "Tag in TPC: ${t.name}")

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
            // Update EventStore representation in SharedPreferences
            sharedPrefs.edit().putString(SHARED_PREFS_EVENT_STORE, Gson().toJson(eventStore)).apply()
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
                .setCircularRegion(event.venue.latitude, event.venue.longitude, 100.0f)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL)
                .setLoiteringDelay(1000 * 20)   // Only trigger an GeofencingEvent when the user stays inside the circular region for >= 20 seconds
                .build()
    }

    companion object {
        const val TAG = "UpdateEventsTask"
    }

}