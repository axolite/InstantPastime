package ch.instantpastime.nback

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import ch.instantpastime.*
import ch.instantpastime.nback.ui.BackStackHelper
import ch.instantpastime.nback.ui.NBackResource
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_nback.*

class NBackActivity : AppCompatActivity() {

    private val backStackHelper = BackStackHelper(this)
    private var drawerToolbar: ActionBarDrawerToggle? = null
    private var locationHelper: LocationHelper? = null
    private var googleMapApi: GoogleMapApi? = null
    private var googlePlaceApi: GooglePlaceApi? = null
    private val contextualImages: MutableList<Bitmap> = mutableListOf()

    /**
     * Once the game has started, stock images
     * should be used if there are not enough contextual images.
     * and if a new image received it must be discarded.
     */
    var allowImageReception: Boolean = true

    /**
     * Number of symbols (letters, contextual images) to be
     * available in the game.
     */
    val nbSymbols: Int = 8

    override fun onCreate(savedInstanceState: Bundle?) {
        //android.os.Debug.waitForDebugger()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nback)
        nav_view.setOnNavigationItemSelectedListener { backStackHelper.onNavigationItemSelected(it) }
        backStackHelper.loadFragment(nav_view.selectedItemId)
        initDrawer()

        // Start location activity if needed.
        if (LocationHelper.needLocationActivity(this)) {
            LocationHelper.startLocationActivity(this, nbImages = nbSymbols)
        } else if (LocationHelper.wantUseLocation(this, defValue = false)) {
            // The user wants to use location and it is authorized.
            fetchContextualImages()
        } else {
            // The user doesn't want to use location.
            useStockImages()
        }
    }

    private fun initDrawer() {
        val drawerLayout = findViewById<View>(R.id.local_drawer_layout) as? DrawerLayout
        if (drawerLayout != null) {
            val drawerToolbar = ActionBarDrawerToggle(this, drawerLayout, 0, 0).apply {
                syncState()
            }
            this.drawerToolbar = drawerToolbar
            drawerLayout.addDrawerListener(drawerToolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        val drawerNavView = findViewById<View>(R.id.nav_view_drawer) as? NavigationView
        if (drawerNavView != null) {
            drawerNavView.setNavigationItemSelectedListener {
                when (val id = it.itemId) {
                    ch.instantpastime.R.id.info -> {
                        Toast.makeText(
                            this@NBackActivity,
                            "À propos de ..",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    ch.instantpastime.R.id.install -> {
                        Toast.makeText(
                            this@NBackActivity,
                            "Installation",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> {
                    }
                }
                true
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (drawerToolbar?.onOptionsItemSelected(item) == true) {
            true
        } else if (item != null) {
            super.onOptionsItemSelected(item)
        } else {
            false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            LocationActivity.LOCATION_REQ_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val useLocation =
                        data?.getBooleanExtra(PrefManager.USE_LOCATION_PREF_KEY, false)
                    if (useLocation == true) {
                        fetchContextualImages()
                    } else {
                        useStockImages()
                    }
                }
            }
        }
    }

    private fun fetchContextualImages() {
        allowImageReception = true
        if (locationHelper == null) {
            locationHelper = LocationHelper()
        }
        if (googleMapApi == null) {
            googleMapApi = GoogleMapApi()
            googleMapApi?.init(this)
        }
        if (googlePlaceApi == null) {
            googlePlaceApi = GooglePlaceApi(
                NumImages = nbSymbols,
                imageRequestReady = { imageRequestedReady(it) }
            )
            googlePlaceApi?.init(this)
        }

        locationHelper?.getLocation(this) {
            processLocation(it)
        }
    }

    private fun useStockImages() {
        Toast.makeText(this, "Using stock images", Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSION_FINE_LOCATION ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationHelper?.processPermissionStatus(PermissionStatus.Accepted, this) {
                        processLocation(it)
                    }
                }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    private fun processLocation(location: Location?) {
        if (location != null) {
            val latitude: Double = location.latitude
            val longitude: Double = location.latitude
            Toast.makeText(
                this,
                "Location is ${LocationActivity.formatLatitude(latitude)}, ${LocationActivity.formatLongitude(
                    longitude
                )}",
                Toast.LENGTH_SHORT
            ).show()
            googleMapApi?.requestNearbyPlaces(location) { processPlaces(it) }
        }
    }

    private fun imageRequestedReady(placePhoto: PlacePhoto) {
        if (!allowImageReception) {
            return
        }
        val bitmap = placePhoto.bitmap
        contextualImages.add(bitmap)
        Log.d("[IMG]", "Received image ${contextualImages.size}/${googlePlaceApi?.NumImages}")

        if (contextualImages.size >= nbSymbols) {
            Toast.makeText(this, "Loaded all $nbSymbols contextual images", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun processPlaces(places: ArrayList<PlaceInfo>) {
        Toast.makeText(this, "Fetching contextual images", Toast.LENGTH_SHORT).show()
        googlePlaceApi?.getPhotoAndDetail(places)
    }

    fun getCardImage(index: Int): Bitmap? {
        if (index < contextualImages.size) {
            return contextualImages[index]
        } else {
            return NBackResource.getStockCardImage(this, index)
        }
    }

    fun cancelImageReception() {
        // It seems that the com.google.android.gms.tasks.Task returned by
        // PlacesClient.fetchPhoto(FetchPhotoRequest)) cannot be cancelled,
        // according to https://stackoverflow.com/a/43478082 .
        // So use a flag to dismiss the late responses from within the callback.
        allowImageReception = false
    }
}
