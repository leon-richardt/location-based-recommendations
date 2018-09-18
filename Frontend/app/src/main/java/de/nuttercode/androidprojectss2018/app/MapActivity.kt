package de.nuttercode.androidprojectss2018.app

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import de.nuttercode.androidprojectss2018.csi.config.ClientConfiguration
import de.nuttercode.androidprojectss2018.csi.pojo.ScoredEvent
import de.nuttercode.androidprojectss2018.csi.store.EventStore
import de.nuttercode.androidprojectss2018.csi.store.TagStore

class MapActivity : AppCompatActivity(), OnMapReadyCallback, EventListFragment.OnListFragmentInteractionListener {

    private lateinit var mMap: GoogleMap
    private lateinit var mList: EventListFragment
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var clientConfig: ClientConfiguration

    private var firstStart = true
    
    private lateinit var jobScheduler: JobScheduler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createNotificationChannel(this)

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

        firstStart = sharedPrefs.getBoolean(SHARED_PREFS_FIRST_START, true)

        if (firstStart) {
            // If this is indeed the first start, we need to create a new entry for the ClientConfiguration in SharedPreferences
            val freshClientConfiguration = ClientConfiguration().apply {
                radius = 200.0 // TODO: Get radius from settings
            }
            saveToSharedPrefs(sharedPrefs, freshClientConfiguration)

            // Remember that we are not on the first start anymore
            sharedPrefs.edit().putBoolean(SHARED_PREFS_FIRST_START, false).apply()
        }

        // At this point, there must be a ClientConfiguration saved --> retrieve it and save an EventStore
        clientConfig = getFromSharedPrefs(sharedPrefs, SHARED_PREFS_CLIENT_CONFIG) as ClientConfiguration
        updateMostRecentEventStore(EventStore(clientConfig))

        setContentView(R.layout.activity_map)

        // Obtain the Event list
        mList = supportFragmentManager.findFragmentById(R.id.list) as EventListFragment

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Get events/tags for the first time and update the event list and event map views
        val updateEventsTask = object : UpdateEventsTask(this@MapActivity) {
            override fun onPostExecute(result: Boolean) {
                updateEventList()
                updateEventMap()
            }
        }

        val updateTagsTask = object : UpdateTagsTask(this@MapActivity) {
            override fun onPostExecute(result: Boolean) {
                val tagStore = getFromSharedPrefs(sharedPrefs, SHARED_PREFS_TAG_STORE) as TagStore
                for (t in tagStore.all) clientConfig.tagPreferenceConfiguration.addTag(t)   // TODO: Get tags from settings instead of adding them all
                saveToSharedPrefs(sharedPrefs, clientConfig)
                updateMostRecentEventStore(EventStore(clientConfig))
                // Once we finished fetching/adding tags to the ClientConfiguration, we can start fetching events
                updateEventsTask.execute()
            }
        }
        updateTagsTask.execute()

        // Schedule the repeating tasks for event and tag fetching
        jobScheduler.schedule(buildJobInfo(UpdateEventsJobService::class.java))
        Log.i("JobSchedulerInfo", "The periodic UpdateEventsJob has been scheduled")
        jobScheduler.schedule(buildJobInfo(UpdateTagsJobService::class.java))
        Log.i("JobSchedulerInfo", "The periodic UpdateTagsJob has been scheduled")
    }

    override fun onResume() {
        super.onResume()
        // If the user opens the activity, he likely wants to see the most recent data
        updateEventList()
        updateEventMap()
    }

    override fun onListFragmentInteraction(item: ScoredEvent?) {
        // Create an Intent for that specific event and start the overview activity
        val intent = Intent(this, EventOverviewActivity::class.java)
        intent.putExtra(EXTRA_EVENT_CLICKED, item!!.id)
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

    fun updateEventList() {
        val mostRecentEventStore = obtainMostRecentEventStore()
        mList.clearList()
        mList.addAllElements(mostRecentEventStore.all)
        mList.refreshList()
    }

    fun updateEventMap() {
        if (!::mMap.isInitialized) return

        // Make sure to remove all markers (we will add them again if their events are still in the store)
        mMap.clear()

        val mostRecentEventStore = obtainMostRecentEventStore()

        // If there are no events in the EventStore, we can quit already
        if (mostRecentEventStore.all.isEmpty()) return

        val boundsBuilder = LatLngBounds.builder()
        for (scoredEvent in mostRecentEventStore.all) {
            val venuePos = LatLng(scoredEvent.event.venue.latitude, scoredEvent.event.venue.longitude)
            boundsBuilder.include(venuePos)
            mMap.addMarker(MarkerOptions().position(venuePos).title("${scoredEvent.event.name} at ${scoredEvent.event.venue.name}"))
        }
        // Move the camera in such a way that every event marked on the map is visible
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 100))
    }

    private fun buildJobInfo(cls: Class<*>): JobInfo {
        if (cls != UpdateTagsJobService::class.java && cls != UpdateEventsJobService::class.java)
            throw IllegalArgumentException("Can only pass UpdateTagsJobService or UpdateEventsJobService")

        return JobInfo.Builder(if (cls == UpdateTagsJobService::class.java) PERIODIC_TAG_UPDATES_JOB_ID else PERIODIC_EVENT_UPDATES_JOB_ID, ComponentName(this, cls))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(if (cls == UpdateTagsJobService::class.java) PERIODIC_TAG_UPDATES_JOB_INTERVAL else PERIODIC_EVENT_UPDATES_JOB_INTERVAL)
                .build()
    }
}