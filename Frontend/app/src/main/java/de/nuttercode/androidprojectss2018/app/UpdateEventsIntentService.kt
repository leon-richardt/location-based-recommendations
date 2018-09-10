package de.nuttercode.androidprojectss2018.app

import android.Manifest
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
import de.nuttercode.androidprojectss2018.csi.ClientConfiguration
import de.nuttercode.androidprojectss2018.csi.EventStore
import de.nuttercode.androidprojectss2018.csi.TagStore

const val ACTION_FETCH_EVENTS = "de.nuttercode.androidprojectss2018.app.action.FETCH_EVENTS"
const val BROADCAST_UPDATED_EVENT_STORE = "de.nuttercode.androidprojectss2018.app.broadcast.UPDATED_EVENT_STORE"

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
class UpdateEventsIntentService : IntentService("UpdateEventsIntentService") {
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var eventStore: EventStore
    private lateinit var wakeLock: PowerManager.WakeLock
    private lateinit var tagStore: TagStore


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
            val clientConfiguration = Gson().fromJson(sharedPrefs.getString(MapActivity.SHARED_PREFS_CLIENT_CONFIG, null), ClientConfiguration::class.java)
            val tagStoreString = sharedPrefs.getString(MapActivity.SHARED_PREFS_TAG_STORE, null) ?: Gson().toJson(TagStore(clientConfiguration))

            tagStore = Gson().fromJson(tagStoreString, TagStore::class.java)
            // If the TagStore in SharedPreferences is empty, no events will be found --> release the wakelock and return
            if (tagStore.all.isEmpty()) {
                Log.i(TAG, "TagStore is empty, we would not find any Events. Ending the service now. (Wakelock will also be released)")
                wakeLock.release()
                return
            }
            // Create a new EventStore with the up-to-date ClientConfiguration (from SharedPreferences)
            eventStore = EventStore(clientConfiguration)
            handleActionFetchEvents(eventStore)
        }
    }

    /**
     * Handle action in the provided background thread with the provided
     * parameters.
     */
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


                sharedPrefs.edit().putString(MapActivity.SHARED_PREFS_EVENT_STORE, Gson().toJson(eventStore)).apply()
                // Set the JSON representation of the updated EventStore and send it a broadcast to let MapActivity know we finished updating
                val answerBroadcast = Intent(BROADCAST_UPDATED_EVENT_STORE).apply {
                    action = BROADCAST_UPDATED_EVENT_STORE
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
