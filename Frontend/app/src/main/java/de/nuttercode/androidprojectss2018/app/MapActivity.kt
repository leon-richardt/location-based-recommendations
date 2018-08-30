package de.nuttercode.androidprojectss2018.app

import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
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
import de.nuttercode.androidprojectss2018.csi.ScoredEvent
import java.lang.ref.WeakReference

class MapActivity : AppCompatActivity(), OnMapReadyCallback, EventListFragment.OnListFragmentInteractionListener, AddAllTagsTaskCallback {
    private lateinit var mMap: GoogleMap

    private lateinit var mList: Fragment
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var clientConfig: ClientConfiguration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        // Check whether ClientConfiguration entry already exists
        if (sharedPrefs.getString("ClientConfiguration", null) == null) {
            // Custom ClientConfiguration does not exist yet, save a default ClientConfiguration
            val clientConfigJson = Gson().toJson(ClientConfiguration().apply { radius = 200.0 })
            sharedPrefs.edit().putString("ClientConfiguration", clientConfigJson).apply()
        }

        // At this point, there must be a ClientConfiguration saved --> retrieve it
        clientConfig = Gson().fromJson(sharedPrefs.getString("ClientConfiguration", null), ClientConfiguration::class.java)


        // Currently, we run this on every startup
        // TODO: Implement settings menu and check whether new tags should automatically be added to ClientConfiguration
        AddAllTagsTask(WeakReference(this), this).execute(clientConfig)
        sharedPrefs.edit().putBoolean("FirstStart", false).apply()


        // Must be set after clientConfig has been loaded
        setContentView(R.layout.activity_map)

        // Obtain the Event list
        mList = supportFragmentManager.findFragmentById(R.id.list) as Fragment

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onListFragmentInteraction(item: ScoredEvent?) {
        val msg = "List interaction registered on Event ${item?.event?.id}: Name = ${item?.event?.name}, Description = ${item?.event?.description}"
        // TODO: Make this method open an overview activity for the selected item
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        Log.i("MapActivity", msg)
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

    fun getSharedPrefs() = sharedPrefs

    fun getClientConfig() = clientConfig

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
