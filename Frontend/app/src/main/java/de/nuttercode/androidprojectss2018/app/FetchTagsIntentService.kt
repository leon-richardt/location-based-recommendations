package de.nuttercode.androidprojectss2018.app

import android.app.IntentService
import android.content.Intent
import android.content.Context
import android.content.SharedPreferences
import android.os.PowerManager
import android.preference.PreferenceManager
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.google.gson.Gson
import de.nuttercode.androidprojectss2018.csi.ClientConfiguration
import de.nuttercode.androidprojectss2018.csi.TagStore

const val ACTION_UPDATE_TAGS = "de.nuttercode.androidprojectss2018.app.action.UPDATE_TAGS"

const val BROADCAST_UPDATED_TAG_STORE = "de.nuttercode.androidprojectss2018.app.broadcast.UPDATED_TAG_STORE"

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
class FetchTagsIntentService : IntentService("FetchTagsIntentService") {
    private lateinit var wakeLock: PowerManager.WakeLock
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var tagStore: TagStore

    override fun onHandleIntent(intent: Intent) {
        if (intent.action == ACTION_UPDATE_TAGS) {
            // Acquire a wakelock so the service does not get put into sleep while executing.
            // Also provide a one minute timeout for the wakelock.
            wakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "LocationBasedRecommendations::UpdateEventsWakeLock").apply {
                    acquire(60*1000L /*1 minute*/)
                    Log.i(TAG, "Acquired wakelock.")
                }
            }

            sharedPrefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
            // Get the TagStore from shared preferences or provide a default empty TagStore if none exists yet
            tagStore = Gson().fromJson(sharedPrefs.getString(MapActivity.SHARED_PREFS_TAG_STORE, null), TagStore::class.java)
                    ?: TagStore(Gson().fromJson(sharedPrefs.getString(MapActivity.SHARED_PREFS_CLIENT_CONFIG, null), ClientConfiguration::class.java))
            handleActionFetchTags()
        }
    }

    /**
     * Updates the TagStore saved in SharedPreferences with live data from server.
     */
    private fun handleActionFetchTags() {
        try {
            val qri = tagStore.refresh()
            val cqrs = qri.clientQueryResultState
            val sqrs = qri.serverQueryResultState

            Log.i(TAG, "CQRS = $cqrs, SQRS = $sqrs")
            if (!qri.isOK) Log.e(TAG, "TagQuery was not successful. Message: ${qri.message}")
            Log.i(TAG, "Now listing all tags in TagStore:")
            for (tag in tagStore.all) Log.i(TAG, "Tag in TagStore: ${tag.name}")
            Log.i(TAG, "Finished listing all tags")

            // Updated the TagStore in SharedPreferences and send a Broadcast indicating the service finished
            sharedPrefs.edit().putString(MapActivity.SHARED_PREFS_TAG_STORE, Gson().toJson(tagStore)).apply()
            val answerBroadcast = Intent(BROADCAST_UPDATED_TAG_STORE).apply { action = BROADCAST_UPDATED_TAG_STORE }
            LocalBroadcastManager.getInstance(this).sendBroadcast(answerBroadcast)

        } finally {
            wakeLock.release()
        }
    }

    companion object {
        const val TAG = "FetchTagsIntentService"

        /**
         * Starts this service to perform action Foo with the given parameters. If
         * the service is already performing a task this action will be queued.
         *
         * @see IntentService
         */
        @JvmStatic
        fun startActionFetchTags(context: Context) {
            val intent = Intent(context, FetchTagsIntentService::class.java).apply {
                action = ACTION_UPDATE_TAGS
            }
            context.startService(intent)
        }

    }
}
