package ch.instantpastime

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private var locationHelper: LocationHelper? = null
    private var drawerLayout: DrawerLayout? = null
    private var drawerToolbar: ActionBarDrawerToggle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        //android.os.Debug.waitForDebugger()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        locationHelper = LocationHelper()
        locationHelper?.getLocation(this, { processLocation(it) })

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
}
