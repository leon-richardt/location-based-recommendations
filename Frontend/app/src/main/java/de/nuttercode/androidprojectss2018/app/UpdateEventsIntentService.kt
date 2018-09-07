package de.nuttercode.androidprojectss2018.app

import android.Manifest
import android.annotation.SuppressLint
import android.app.IntentService
import android.content.Intent
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.PowerManager
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import de.nuttercode.androidprojectss2018.csi.EventStore

const val ACTION_FETCH_EVENTS = "de.nuttercode.androidprojectss2018.app.action.FETCH_EVENTS"
const val ACTION_BROADCAST = "de.nuttercode.androidprojectss2018.app.action.BROADCAST"
const val EVENT_STORE_UPDATED = "de.nuttercode.androidprojectss2018.app.action.EVENT_STORE_UPDATED"

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
class UpdateEventsIntentService : IntentService("UpdateEventsIntentService") {
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var eventStore: EventStore
    private lateinit var wakeLock: PowerManager.WakeLock


    override fun onHandleIntent(intent: Intent) {
        if (intent.action == ACTION_FETCH_EVENTS) {
            // Acquire a wakelock so the service does not get put into sleep while executing.
            // Also provide a one minute timeout for the wakelock.
            wakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "LocationBasedRecommendations::UpdateEventsWakeLock").apply {
                    acquire(60*1000L /*1 minute*/)
                    Log.i(TAG, "Acquired wakelock.")
                }
            }

            sharedPrefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
            // Get the event store from shared prefs in order to make sure we use the up-to-date version
            eventStore = Gson().fromJson(sharedPrefs.getString("EventStore", null), EventStore::class.java)
            handleActionFetchEvents(eventStore)
        }
    }

    /**
     * Handle action in the provided background thread with the provided
     * parameters.
     */
    @SuppressLint("MissingPermission")  // TODO: REMOVE LATER
    private fun handleActionFetchEvents(eventStore: EventStore) {
        Log.i(TAG, "Started handleActionFetchEvents()")
        try {
            if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                val locTask = LocationServices.getFusedLocationProviderClient(applicationContext).lastLocation

                while (!locTask.isComplete) {
                    Log.i(TAG, "locTask is not completed yet, waiting 100ms ...")
                    Thread.sleep(100)
                }
                Log.i(TAG, "locTask completed and the result is ${locTask.result}")

                val location = locTask.result
                if (location == null) {
                    Log.e(TAG, "Location returned by locTask is null! Ending service ...")
                    return
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

                // Get the JSON representation of the updated EventStore and send it as a broadcast
                val answerBroadcast = Intent(ACTION_BROADCAST).apply {
                    action = ACTION_BROADCAST
                    putExtra(EVENT_STORE_UPDATED, Gson().toJson(eventStore))
                }
                LocalBroadcastManager.getInstance(this).sendBroadcast(answerBroadcast)

            } else {
                Log.e(TAG, "App is missing permission to get user location")
            }

        // Release the wakelock in finally block in case anything goes wrong
        } finally {
            wakeLock.release()
            Log.i(TAG, "Released wakelock.")
        }
    }

    companion object {
        const val TAG = "UpdateEventsService"

        /**
         * Starts this service to perform action FetchEvents with the given parameters. If
         * the service is already performing a task this action will be queued.
         *
         * @see IntentService
         */
        @JvmStatic
        fun startActionFetchEvents(context: Context) {
            val intent = Intent(context, UpdateEventsIntentService::class.java).apply {
                action = ACTION_FETCH_EVENTS
            }
            context.startService(intent)
        }
    }
}
