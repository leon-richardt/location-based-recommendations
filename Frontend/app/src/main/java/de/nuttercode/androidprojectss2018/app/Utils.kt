package de.nuttercode.androidprojectss2018.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import de.nuttercode.androidprojectss2018.csi.config.ClientConfiguration
import de.nuttercode.androidprojectss2018.csi.store.EventStore
import de.nuttercode.androidprojectss2018.csi.store.TagStore

/**
 * Holds the most recent instance of the [EventStore] that should be used by every Activity in the app.
 */
private lateinit var mostRecentEventStore: EventStore
private lateinit var mostRecentLocation: LatLng

/**
 * Convenience method for getting an object saved in [SharedPreferences].
 * Provide a SharedPreferences instance that contains one of the accepted keys (see below).
 * If the [sharedPrefs] do not contain the [SHARED_PREFS_FIRST_START] key at time of execution, this
 * method will return true.
 *
 * @param sharedPrefs The [SharedPreferences] instance to use for the look-up.
 * @param key One of the following: [SHARED_PREFS_CLIENT_CONFIG], [SHARED_PREFS_TAG_STORE] or [SHARED_PREFS_FIRST_START]
 * @throws IllegalStateException if the [sharedPrefs] do not contain the passed [key]
 * @throws IllegalArgumentException if the passed [key] is not one of the key listed above
 */
fun getFromSharedPrefs(sharedPrefs: SharedPreferences, key: String): Any {
    val gson = Gson()

    when (key) {
        SHARED_PREFS_CLIENT_CONFIG -> {
            return gson.fromJson(sharedPrefs.getString(key, null), ClientConfiguration::class.java)
                    ?: throw IllegalStateException("Could not find a value associated with key $key in SharedPreferences")
        }

        SHARED_PREFS_TAG_STORE -> {
            return gson.fromJson(sharedPrefs.getString(key, null), TagStore::class.java)
                    ?: throw IllegalStateException("Could not find a value associated with key $key in SharedPreferences")
        }

        SHARED_PREFS_FIRST_START -> {
            return sharedPrefs.getBoolean(key, true)
        }

        else -> {
            throw IllegalArgumentException("Key $key is not used in SharedPreferences")
        }
    }
}

/**
 * Convenience method for saving a value to [SharedPreferences]. This method will automatically save
 * [ClientConfiguration] objects to [SHARED_PREFS_CLIENT_CONFIG] and [TagStore] objects to [SHARED_PREFS_TAG_STORE].
 * Please note that [Boolean] objects CANNOT be passed to [entry] as Kotlin does not accept primitive
 * types for [Any].
 *
 * @param sharedPrefs The [SharedPreferences] instance to save the values to.
 * @param entry An instance of one of the following classes: [ClientConfiguration] or [TagStore]
 * @throws IllegalArgumentException if the object passed to [entry] is not of the right type
 */
fun saveToSharedPrefs(sharedPrefs: SharedPreferences, entry: Any) {
    val gson = Gson()

    when (entry.javaClass) {
        ClientConfiguration::class.java -> {
            sharedPrefs.edit().putString(SHARED_PREFS_CLIENT_CONFIG, gson.toJson(entry)).apply()
        }

        TagStore::class.java -> {
            sharedPrefs.edit().putString(SHARED_PREFS_TAG_STORE, gson.toJson(entry)).apply()
        }

        else -> {
            throw IllegalArgumentException("Entry ${entry.javaClass} is not used in SharedPreferences")
        }
    }
}

/**
 * Update the [EventStore] holder with [newEventStore].
 */
fun updateMostRecentEventStore(newEventStore: EventStore) {
    mostRecentEventStore = newEventStore
}

/**
 * Obtain the most recent [EventStore] instance saved.
 *
 * @return The most recent [EventStore] saved, or null if none has been saved yet
 */
fun obtainMostRecentEventStore(): EventStore? {
    if (!::mostRecentEventStore.isInitialized) return null
    return mostRecentEventStore
}

/**
 * Update the [LatLng] holder with [newLocation].
 */
fun updateMostRecentLocation(newLocation: LatLng) {
    mostRecentLocation = newLocation
}

/**
 * Obtain the most recent location saved.
 *
 * @return The most recent location (as a [LatLng]) saved, or null if none has been saved yet
 */
fun obtainMostRecentLocation(): LatLng? {
    if (!::mostRecentLocation.isInitialized) return null
    return mostRecentLocation
}

/**
 * Holds the ID for the last notification that was sent
 */
private var notifId: Int = -1

/**
 * Sends a notification with the given title and text.
 *
 * @param context The context to send the notification from
 * @param title Notification title
 * @param text The (non-expendable) content text of the notification
 * @param bigText The text that is shown once the notification is expanded by the user (leave null
 *          if not needed)
 */
fun sendNotification(context: Context, title: String, text: String, bigText: String? = null): Int {
    val tmpIntent = Intent(context, MapActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT) }
    val pendingIntent = PendingIntent.getActivity(context, ++notifId, tmpIntent, PendingIntent.FLAG_UPDATE_CURRENT)

    val notifBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_icon_background)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

    if (bigText != null) {
        notifBuilder.setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
    }

    val notifManager = NotificationManagerCompat.from(context)
    notifManager.notify(notifId, notifBuilder.build())
    return notifId
}

fun createNotificationChannel(context: Context) {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Default Channel"
        val description = "Channel for all notifications"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance)
        channel.description = description
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager!!.createNotificationChannel(channel)
    }
}


