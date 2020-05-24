package ch.instantpastime

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_location.*

class LocationActivity : AppCompatActivity() {

    companion object {
        const val NB_IMAGES_ARG = "nbImages"
    }

    private var locationHelper: LocationHelper? = null
    private var googleMapApi: GoogleMapApi? = null
    private var googlePlaceApi: GooglePlaceApi? = null
    private var nbImagesNeeded: Int = 0
    private val contextualImages: MutableList<Bitmap> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        nbImagesNeeded = intent.getIntExtra(NB_IMAGES_ARG, 0)

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

        if (locationHelper == null) {
            locationHelper = LocationHelper()
        }
        if (googleMapApi == null) {
            googleMapApi = GoogleMapApi()
            googleMapApi?.init(this)
        }
        if (googlePlaceApi == null) {
            googlePlaceApi = GooglePlaceApi(
                NumImages = nbImagesNeeded,
                imageRequestReady = { imageRequestedReady(it) }
            )
            googlePlaceApi?.init(this)
        }
        locationHelper?.getLocation(this) {
            processLocation(it)
        }
    }

    private fun refuseButtonClicked() {
        onLocationRefused()
    }

    private fun onLocationRefused() {
        loc_question_panel.visibility = View.GONE
        loc_accepted_panel.visibility = View.GONE
        loc_refused_panel.visibility = View.VISIBLE
        AsyncRun {
            Thread.sleep(3000)
            runOnUiThread {
                finish()
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
            googleMapApi?.requestNearbyPlaces(location) { processPlaces(it) }
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
                    locationHelper?.processPermissionStatus(PermissionStatus.Accepted,
                        this, { loc -> processLocation(loc) })
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

    private fun processPlaces(places: ArrayList<PlaceInfo>) {
        googlePlaceApi?.getPhotoAndDetail(places)
    }

    fun imageRequestedReady(placePhoto: PlacePhoto) {
        val bitmap = placePhoto.response.bitmap
        val placeInfo = placePhoto.info

        contextualImages.add(bitmap)

        loc_progress_text.text = getString(R.string.loc_image_progress, contextualImages.size, nbImagesNeeded)

        Log.d("[IMG]", "Received image ${contextualImages.size}/${googlePlaceApi?.NumImages}")

        if (contextualImages.size >= nbImagesNeeded) {
            finish()
        }
    }
}
