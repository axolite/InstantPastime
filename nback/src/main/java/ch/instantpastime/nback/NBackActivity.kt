package ch.instantpastime.nback

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
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
import ch.instantpastime.fragments.GeneralPreferenceFragment
import ch.instantpastime.nback.fragments.NBackFragment
import ch.instantpastime.nback.ui.FragmentStack
import ch.instantpastime.nback.ui.MyFragmentHelper
import ch.instantpastime.nback.ui.NBackResource
import ch.instantpastime.nback.ui.NBackTutoHelper
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_nback.*

class NBackActivity : AppCompatActivity(), ContextualImageUser {

    private var drawerToolbar: ActionBarDrawerToggle? = null
    private var drawerLayout: DrawerLayout? = null
    private var locationHelper: LocationHelper? = null
    private var googleMapApi: GoogleMapApi? = null
    private var googlePlaceApi: GooglePlaceApi? = null
    private val contextualImages: MutableList<Bitmap> = mutableListOf()
    private var frozenContextualImages: List<Bitmap> = listOf()
    private var keepContextualImages = true
    private val fragmentStack: FragmentStack = FragmentStack(
        activity = this,
        containerId = R.id.nback_fragment_container,
        homeTag = NBackFragment::class.java.simpleName
    )

    /**
     * Number of symbols (letters, contextual images) to be
     * available in the game.
     */
    val nbSymbols: Int = 8

    // TODO agr: implement horizontal orientation support.
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        //android.os.Debug.waitForDebugger()

        //Block the screen orientation (caution: bad practise).
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nback)

        nav_view.setOnNavigationItemSelectedListener { bottomMenuItemSelected(it) }
        fragmentStack.currentTagChanged = { currentFragmentChanged(it) }
        fragmentStack.pushFragment(fragmentStack.homeTag)

        initDrawer()

        // Start location activity if needed.
        if (LocationHelper.needLocationActivity(this)) {
            LocationHelper.startLocationActivity(this, nbImages = nbSymbols)
        } else if (LocationHelper.wantUseLocation(this, defValue = false)) {
            // The user wants to use location and it is authorized.
            fetchContextualImages()
            // Start tutorial while images are being downloaded.
            startTutorialIfNeeded()
        } else {
            // The user doesn't want to use location.
            useStockImages()
            // Start tutorial.
            startTutorialIfNeeded()
        }
    }

    private fun currentFragmentChanged(tag: ValueChange<String>) {
        // Update the active icon in the bottom menu according to the displayed fragment.
        val menuId = MyFragmentHelper.getMenuIdFromTag(tag.newValue)
        if (menuId != null && menuId != nav_view.selectedItemId) {
            //nav_view.selectedItemId = menuId
            nav_view.menu.findItem(menuId).isChecked = true
        } else {
            // The fragment doesn't correspond to any menu item, so deselect the current menu.
            // Doesn't work but why ?
            // nav_view.menu.findItem(nav_view.selectedItemId).isChecked = false
        }
    }

    private fun bottomMenuItemSelected(menuItem: MenuItem): Boolean {

        val tag = MyFragmentHelper.getTagFromMenuId(menuItem.itemId)

        if (tag != null && fragmentStack.currentTag != tag) {
            val fragment = fragmentStack.pushFragment(tag)
            return fragment != null
        }
        return false
    }

    @ExperimentalStdlibApi
    override fun onBackPressed() {
        fragmentStack.popFragment()
    }

    private fun initDrawer() {
        val drawerLayout = findViewById<View>(R.id.local_drawer_layout) as? DrawerLayout
        this.drawerLayout = drawerLayout
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
                when (it.itemId) {
                    ch.instantpastime.R.id.info -> {
                        AboutDialogHelper.showCredits(this)

                    }
                    ch.instantpastime.R.id.install -> {
                        InstallDialogHelper.showDialog(this, "N-Back")
//                        Toast.makeText(
//                            this@NBackActivity,
//                            "Installation",
//                            Toast.LENGTH_SHORT
//                        ).show()
                    }
                    ch.instantpastime.R.id.menu_general_preference -> {
                        showGeneralPreferencesDialog()
                    }
                    ch.instantpastime.R.id.menu_tutorial -> {
                        NBackTutoHelper.startTutoActivity(this)
                    }
                    ch.instantpastime.R.id.menu_credits -> {
                        CreditDialogHelper.showCredits(this)
                    }
                    else -> {
                    }
                }
                drawerLayout?.closeDrawers()
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
                    // Start tutorial if needed.
                    startTutorialIfNeeded()
                }
            }
        }
    }

    override fun enableContextualImages(value: Boolean) {
        if (value) {
            fetchContextualImages()
        } else {
            useStockImages()
        }
    }

    fun fetchContextualImages() {
        keepContextualImages = true

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

    fun useStockImages() {
        keepContextualImages = false
        // Clear the list of contextual images.
        contextualImages.clear()
        frozenContextualImages = listOf()
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
                } else {
                    // Disable contextual images if the user refused geolocation.
                    PrefManager.saveLocationPref(this, value = false)
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
//            Toast.makeText(
//                this,
//                "Location is ${LocationActivity.formatLatitude(latitude)}, ${LocationActivity.formatLongitude(
//                    longitude
//                )}",
//                Toast.LENGTH_SHORT
//            ).show()
            googleMapApi?.requestNearbyPlaces(location) { processPlaces(it) }
        }
    }

    private fun imageRequestedReady(placePhoto: PlacePhoto) {

        if (!keepContextualImages) {
            // The user unchecked the option in the meantime, don't keep the images.
            return
        }

        val bitmap = placePhoto.bitmap
        contextualImages.add(bitmap)
        Log.d("[IMG]", "Received image ${contextualImages.size}/${googlePlaceApi?.NumImages}")

        if (contextualImages.size >= nbSymbols) {
            Toast.makeText(this, getString(ch.instantpastime.R.string.loc_loaded_images, contextualImages.size), Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun processPlaces(places: ArrayList<PlaceInfo>) {
        Toast.makeText(this, getString(ch.instantpastime.R.string.loc_loading_images), Toast.LENGTH_SHORT).show()
        googlePlaceApi?.getPhotoAndDetail(places, nbImagesRequested = nbSymbols)
    }

    fun getCardImage(index: Int): Bitmap? {
        val images = frozenContextualImages
        if (images != null && index < images.size) {
            // Get a contextual image.
            return images[index]
        } else {
            // Get a stock image.
            return NBackResource.getStockCardImage(this, index)
        }
    }

    fun freezeImageSet() {
        frozenContextualImages = contextualImages.toList()
    }

    private fun showGeneralPreferencesDialog() {
        fragmentStack.pushFragment(
            GeneralPreferenceFragment::class.java.simpleName
        )
    }

    /**
     * Starts the tutorial activity if it is the first launch.
     */
    private fun startTutorialIfNeeded() {
        if (PrefManager.getFirstTimeLaunch(this, defValue = true)) {
            PrefManager.setFirstTime(this, value = false)
            NBackTutoHelper.startTutoActivity(this)
        }
    }
}
