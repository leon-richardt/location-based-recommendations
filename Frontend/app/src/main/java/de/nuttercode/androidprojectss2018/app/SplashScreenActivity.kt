package de.nuttercode.androidprojectss2018.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.TextView
import android.widget.Toast

class SplashScreenActivity : AppCompatActivity() {

    /**
     * A [TextView] for stating information about the permission request.
     */
    private lateinit var permissionRequestTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        permissionRequestTextView = findViewById(R.id.permissionRequestTextView)
    }

    override fun onResume() {
        super.onResume()
        checkAndRequestPermissions()
    }

    /**
     * This method is called as a callback from [ActivityCompat.requestPermissions] which is called
     * in [checkAndRequestPermissions]. We act depending on the result of the permission request
     * (either granted or denied).
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted --> open the MapActivity
                    openMapActivity()
                } else {
                    // Permission denied
                    handlePermissionDenied()
                }
                return
            }
        }
    }

    /**
     * Returns true if the permissions were already granted. Returns false if the permission dialog is prompted.
     */
    private fun checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                handlePermissionDenied()
            } else {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        PERMISSIONS_REQUEST_LOCATION)
            }
        } else {
            // We already had permission --> start the Activity directly
            openMapActivity()
        }
    }

    /**
     * Opens a [MapActivity] and finishes this Activity (removing it from the stack).
     */
    private fun openMapActivity() {
        val mapIntent = Intent(this, MapActivity::class.java)
        startActivity(mapIntent)
        finish()
    }

    /**
     * Shows a toast stating that this app needs location access and updates [permissionRequestTextView].
     */
    private fun handlePermissionDenied() {
        Toast.makeText(this, "This app requires location access.", Toast.LENGTH_LONG).show()
        permissionRequestTextView.text = "This app needs location access. Please grant the necessary permission. "
    }
}
