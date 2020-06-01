package ch.instantpastime

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    //private var locationHelper: LocationHelper? = null
    private var drawerLayout: DrawerLayout? = null
    private var drawerToolbar: ActionBarDrawerToggle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        //android.os.Debug.waitForDebugger()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.try_memory_button).setOnClickListener {
            val urlIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(MEMORY_INSTANT_URL))
            startActivity(urlIntent)
        }
        findViewById<View>(R.id.try_nback_button).setOnClickListener {
            val urlIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(NBACK_INSTANT_URL))
            startActivity(urlIntent)
        }
        //locationHelper = LocationHelper()
        //locationHelper?.getLocation(this, { processLocation(it) })

        initDrawer()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSION_FINE_LOCATION ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    locationHelper?.processPermissionStatus(PermissionStatus.Accepted,
//                        this, { loc -> processLocation(loc) })
                } else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
//                    locationHelper?.processPermissionStatus(PermissionStatus.RefusedOnce,
//                        this, { loc -> processLocation(loc) })
                } else {
//                    locationHelper?.processPermissionStatus(PermissionStatus.AlwaysRefused,
//                        this, { loc -> processLocation(loc) })
                }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    private fun initDrawer() {
        val drawerLayout = findViewById<View>(R.id.activity_main) as? DrawerLayout
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

            // Hide menu items that are not available in the common (main) app.
            drawerNavView.menu.findItem(R.id.menu_tutorial)?.isVisible = false
            drawerNavView.menu.findItem(R.id.menu_general_preference)?.isVisible = false

            drawerNavView.setNavigationItemSelectedListener {
                when (it.itemId) {
                    ch.instantpastime.R.id.info -> {
                        Toast.makeText(
                            this@MainActivity,
                            "À propos de ..",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    ch.instantpastime.R.id.install -> {
                        Toast.makeText(
                            this@MainActivity,
                            "Installation",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    ch.instantpastime.R.id.menu_general_preference -> {
                        //showGeneralPreferencesDialog()
                    }
                    ch.instantpastime.R.id.menu_tutorial -> {
                        //NBackTutoHelper.startTutoActivity(this)
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

    private fun processLocation(location: Location?) {
        if (location != null) {
            Toast.makeText(
                this,
                "Your location is (${location.latitude}, ${location.longitude})",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        /**
         * URL associated to the InstantPastime Memory game instant app.
         */
        const val MEMORY_INSTANT_URL = "http://mountainmind.ch/memory"

        /**
         * URL associated to the InstantPastime N-Back game instant app.
         */
        const val NBACK_INSTANT_URL = "http://mountainmind.ch/nback"
    }

}
