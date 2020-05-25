package ch.instantpastime

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_location.*

class LocationActivity() : AppCompatActivity() {

    companion object {
        const val LOCATION_REQ_CODE = 0x4488
        const val NB_IMAGES_ARG = "nbImages"

        fun formatLatitude(latitude: Double): String {
            return if (latitude >= 0) {
                String.format("%.1f", latitude) + "°E"
            } else {
                String.format("%.1f", -latitude) + "°W"
            }
        }

        fun formatLongitude(longitude: Double): String {
            return if (longitude >= 0) {
                String.format("%.1f", longitude) + " °N"
            } else {
                String.format("%.1f", -longitude) + " °S"
            }
        }
    }

    private var locationHelper: LocationHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        findViewById<Button>(R.id.loc_accept_button).apply {
            setOnClickListener { acceptButtonClicked() }
        }
        findViewById<Button>(R.id.loc_refuse_button).apply {
            setOnClickListener { refuseButtonClicked() }
        }
    }

    private fun acceptButtonClicked() {
        loc_question_panel.visibility = View.GONE
        loc_refused_panel.visibility = View.GONE
        loc_accepted_panel.visibility = View.VISIBLE

        // Save in preferences.
        PrefManager.saveLocationPref(this, value = true)

        if (locationHelper == null) {
            locationHelper = LocationHelper()
        }

        // If false, onRequestPermissionsResult() will be called.
        if (locationHelper?.askLocationAccess(this) == true) {
            // Close this activity as location access is already granted.
            finishWithResult(useLocation = true)
        }
    }

    private fun refuseButtonClicked() {
        onLocationRefused()
    }

    private fun onLocationRefused() {
        loc_question_panel.visibility = View.GONE
        loc_accepted_panel.visibility = View.GONE
        loc_refused_panel.visibility = View.VISIBLE

        // Save in preferences.
        PrefManager.saveLocationPref(this, value = false)

        AsyncRun {
            Thread.sleep(3000)
            runOnUiThread {
                // Close this activity returning user's answer.
                finishWithResult(useLocation = false)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSION_FINE_LOCATION ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Close this activity returning user's answer.
                    finishWithResult(useLocation = true)
                } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    onLocationRefused()
                } else {
                    onLocationRefused()
                }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    /**
     * Sets the result (user answer) in activity result intent.
     */
    private fun setResultIntent(useLocation: Boolean) {
        val resIntent = Intent().apply {
            putExtra(PrefManager.USE_LOCATION_PREF_KEY, useLocation)
        }
        setResult(Activity.RESULT_OK, resIntent)
    }

    /**
     * Closes this activity returning its result using an intent.
     */
    private fun finishWithResult(useLocation: Boolean) {
        // Set activity result intent.
        setResultIntent(useLocation)
        // Close this activity.
        finish()
    }
}
