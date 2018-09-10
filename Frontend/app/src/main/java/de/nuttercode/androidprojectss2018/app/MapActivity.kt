package de.nuttercode.androidprojectss2018.app

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.*
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.preference.PreferenceManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import de.nuttercode.androidprojectss2018.csi.ClientConfiguration
import de.nuttercode.androidprojectss2018.csi.EventStore
import de.nuttercode.androidprojectss2018.csi.ScoredEvent
import de.nuttercode.androidprojectss2018.csi.TagStore

class MapActivity : AppCompatActivity(), OnMapReadyCallback, EventListFragment.OnListFragmentInteractionListener {

    private lateinit var mMap: GoogleMap
    private lateinit var mList: EventListFragment
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var clientConfig: ClientConfiguration
    private lateinit var eventStore: EventStore

    private var firstStart = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkAndRequestPermissions()

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        firstStart = sharedPrefs.getBoolean(SHARED_PREFS_FIRST_START, true)

        // Register a BroadcastReceiver that updates the event list with the new EventStore
        val mUpdateEventsReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                // Set the event store in this class to the most recent one (which is the one the service saved in SharedPreferences)
                eventStore = Gson().fromJson(getFromSharedPrefs(SHARED_PREFS_EVENT_STORE), EventStore::class.java)
                if (this@MapActivity::mList.isInitialized) updateEventList()
                if (this@MapActivity::mMap.isInitialized) updateEventMap()
            }
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(mUpdateEventsReceiver, IntentFilter(BROADCAST_UPDATED_EVENT_STORE))

        val mFetchTagsIntentServiceReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent?) {
                if (firstStart) {
                    // We know that an updated TagStore is now in SharedPrefs --> add all to ClientConfig
                    val tagStore = Gson().fromJson(getFromSharedPrefs(SHARED_PREFS_TAG_STORE), TagStore::class.java)
                    for (tag in tagStore.all) {
                        clientConfig.tagPreferenceConfiguration.addTag(tag)
                    }
                    saveToSharedPrefs(SHARED_PREFS_CLIENT_CONFIG, clientConfig)
                    UpdateEventsIntentService.startActionFetchEvents(this@MapActivity)
                }
            }
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(mFetchTagsIntentServiceReceiver, IntentFilter(BROADCAST_UPDATED_TAG_STORE))

        if (firstStart) {
            // If this is indeed the first start, we need to create new entries in SharedPreferences
            val freshClientConfiguration = ClientConfiguration().apply {
                radius = 200.0 // TODO: Get radius from settings
            }
            saveToSharedPrefs(SHARED_PREFS_CLIENT_CONFIG, freshClientConfiguration)

            // Remember that we are not on the first start anymore
            sharedPrefs.edit().putBoolean(SHARED_PREFS_FIRST_START, false).apply()
        }

        // At this point, there must be a ClientConfiguration saved --> retrieve it
        clientConfig = Gson().fromJson(sharedPrefs.getString(SHARED_PREFS_CLIENT_CONFIG, null), ClientConfiguration::class.java)
        eventStore = EventStore(clientConfig)
        saveToSharedPrefs(SHARED_PREFS_EVENT_STORE, eventStore)

        // On first start, this will also trigger the UpdateEventsIntentService
        FetchTagsIntentService.startActionFetchTags(this)

        val updateEventsIntent = Intent(this, UpdateEventsIntentService::class.java).apply {
            action = ACTION_FETCH_EVENTS
        }

        // Run the service once at start (on the first start, this will be done after fetching tags) ...
        if (!firstStart) startService(updateEventsIntent)

        // ... and schedule it to repeat every ~15 minutes
        val pendingUpdateIntent = PendingIntent.getService(this, UPDATE_EVENTS_INTENT_ID, updateEventsIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingUpdateIntent)

        // Must be set after clientConfig has been loaded
        setContentView(R.layout.activity_map)

        // Obtain the Event list
        mList = supportFragmentManager.findFragmentById(R.id.list) as EventListFragment
        updateEventList()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onResume() {
        super.onResume()
        UpdateEventsIntentService.startActionFetchEvents(this)
    }

    override fun onListFragmentInteraction(item: ScoredEvent?) {
        // Create an Intent for that specific event and start the overview activity
        val intent = Intent(this, EventOverviewActivity::class.java)
        intent.putExtra(EXTRA_EVENT_CLICKED, item)
        startActivity(intent)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty
                if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission granted
                    UpdateEventsIntentService.startActionFetchEvents(this)
                } else {
                    // Permission denied
                    Toast.makeText(this, "This app requires location access.", Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }

    fun saveToSharedPrefs(key: String, value: Any) {
        val internalPrefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        internalPrefs.edit().putString(key, Gson().toJson(value)).apply()
    }

    fun getFromSharedPrefs(key: String): String {
        val internalSharedPrefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        return internalSharedPrefs.getString(key, null)
                ?: throw IllegalStateException("SharedPreferences do not contain key '$key'")
    }

    private fun updateEventList() {
        val mostRecentEventStore = Gson().fromJson(getFromSharedPrefs(SHARED_PREFS_EVENT_STORE), EventStore::class.java)
        mList.clearList()
        mList.addAllElements(mostRecentEventStore.all)
        mList.refreshList()
    }

    private fun updateEventMap() {
        val mostRecentEventStore = Gson().fromJson(getFromSharedPrefs(SHARED_PREFS_EVENT_STORE), EventStore::class.java)
        val boundsBuilder = LatLngBounds.builder()
        for (scoredEvent in mostRecentEventStore.all) {
            val venuePos = LatLng(scoredEvent.event.venue.latitude, scoredEvent.event.venue.longitude)
            boundsBuilder.include(venuePos)
            mMap.addMarker(MarkerOptions().position(venuePos).title("${scoredEvent.event.name} at ${scoredEvent.event.venue.name}"))
        }
        // Move the camera in such a way that every event marked on the map is visible
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 100))
    }

    /**
     * Returns true if the permissions were already granted. Returns false if the permission dialog is prompted.
     */
    fun checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // TODO: Show explanation
            } else {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION),
                        PERMISSIONS_REQUEST_LOCATION)
            }
        }
    }

    companion object {
        const val TAG = "MapActivity"

        const val EXTRA_EVENT_CLICKED = "de.nuttercode.androidprojectss2018.app.extra.EVENT_CLICKED"

        const val SHARED_PREFS_CLIENT_CONFIG = "de.nuttercode.androidprojectss2018.app.sharedpreferences.CLIENT_CONFIGURATION"
        const val SHARED_PREFS_EVENT_STORE = "de.nuttercode.androidprojectss2018.app.sharedpreferences.EVENT_STORE"
        const val SHARED_PREFS_TAG_STORE = "de.nuttercode.androidprojectss2018.app.sharedpreferences.TAG_STORE"
        const val SHARED_PREFS_FIRST_START = "de.nuttercode.androidprojectss2018.app.sharedpreferences.FIRST_START"

        const val PERMISSIONS_REQUEST_LOCATION = 0
        const val UPDATE_EVENTS_INTENT_ID = 42
    }
}
