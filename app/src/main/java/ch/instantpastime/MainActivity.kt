package ch.instantpastime

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var locationHelper: LocationHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        //android.os.Debug.waitForDebugger()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        locationHelper = LocationHelper()
        locationHelper?.getLocation(this, { processLocation(it) })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSION_FINE_LOCATION ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationHelper?.processPermissionStatus(PermissionStatus.Accepted,
                        this, { loc -> processLocation(loc) })
                } else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    locationHelper?.processPermissionStatus(PermissionStatus.RefusedOnce,
                        this, { loc -> processLocation(loc) })
                } else {
                    locationHelper?.processPermissionStatus(PermissionStatus.AlwaysRefused,
                        this, { loc -> processLocation(loc) })
                }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    private fun processLocation(location: Location?) {
        if (location != null) {
            Toast.makeText(
                this,
                "Your location is (${location.latitude}, ${location.longitude})",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
