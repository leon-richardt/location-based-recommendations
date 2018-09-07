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
import android.util.Log
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import de.nuttercode.androidprojectss2018.csi.ClientConfiguration
import de.nuttercode.androidprojectss2018.csi.EventStore
import de.nuttercode.androidprojectss2018.csi.ScoredEvent
import java.lang.ref.WeakReference

private const val UPDATE_EVENTS_INTENT_ID = 42

class MapActivity : AppCompatActivity(), OnMapReadyCallback, EventListFragment.OnListFragmentInteractionListener, AddAllTagsTaskCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mList: EventListFragment
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var clientConfig: ClientConfiguration
    private lateinit var eventStore: EventStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkAndRequestPermissions()

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        // Check whether ClientConfiguration entry already exists
        if (sharedPrefs.getString("ClientConfiguration", null) == null) {
            // Custom ClientConfiguration does not exist yet, save a default ClientConfiguration
            val clientConfigJson = Gson().toJson(ClientConfiguration().apply { radius = 200.0 })
            sharedPrefs.edit().putString("ClientConfiguration", clientConfigJson).apply()
        }

        // At this point, there must be a ClientConfiguration saved --> retrieve it
        clientConfig = Gson().fromJson(sharedPrefs.getString("ClientConfiguration", null), ClientConfiguration::class.java)
        eventStore = EventStore(clientConfig)
        updateEventStorePrefs()


        // Add all available tags to the passed clientConfig. Currently, we run this on every startup.
        // TODO: Implement settings menu and check whether new tags should automatically be added to ClientConfiguration
        AddAllTagsTask(WeakReference(this), this).execute(clientConfig)


        // Register a BroadcastReceiver that updates the event list with the new EventStore
        val mUpdateEventsReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val newEventStoreGson = intent!!.getStringExtra(EVENT_STORE_UPDATED)
                eventStore = Gson().fromJson(newEventStoreGson, EventStore::class.java)
                updateEventStorePrefs()
                updateEventList()
            }
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(mUpdateEventsReceiver, IntentFilter(ACTION_BROADCAST))


        val updateIntent = Intent(this, UpdateEventsIntentService::class.java).apply {
            action = ACTION_FETCH_EVENTS
        }

        // Run the service once now ...
        startService(updateIntent)

        // ... and schedule it to repeat every ~15 minutes
        val pendingUpdateIntent = PendingIntent.getService(this, UPDATE_EVENTS_INTENT_ID, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT)
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
        // Run the service when we re-open the activity
        UpdateEventsIntentService.startActionFetchEvents(this)
        updateEventList()
    }

    override fun onListFragmentInteraction(item: ScoredEvent?) {
        // TODO: Remove logging/Toast
        val msg = "List interaction registered on Event ${item?.event?.id}: Name = ${item?.event?.name}, Description = ${item?.event?.description}"
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        Log.i("MapActivity", msg)

        // Create an Intent for that specific event and start the overview activity
        val intent = Intent(this, EventOverviewActivity::class.java)
        intent.putExtra("EXTRA_EVENT", item)
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

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    fun updateEventList() {
        mList.clearList()
        mList.addAllElements(eventStore.all)
        mList.refreshList()
    }

    fun updateEventStorePrefs() {
        sharedPrefs.edit().putString("EventStore", Gson().toJson(eventStore)).apply()
    }

    /**
     * Returns true if the permissions were already granted. Returns false if the permission dialog is prompted.
     */
    fun checkAndRequestPermissions(): Boolean {
        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 0)
            return false
        }
        return true
    }

    override fun processAddAllTagsResult(result: ClientConfiguration) {
        // Update JSON representation
        Log.i(TAG, "Listing tags in callback:")
        for (tag in result.tagPreferenceConfiguration) {
            Log.i(TAG, tag.name)
        }
        sharedPrefs.edit().putString("ClientConfiguration", Gson().toJson(result)).apply()
    }

    companion object {
        const val TAG = "MapActivity"
    }
}
