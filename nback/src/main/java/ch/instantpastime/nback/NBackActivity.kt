package ch.instantpastime.nback

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import ch.instantpastime.LocationHelper
import ch.instantpastime.nback.ui.BackStackHelper
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_nback.*

class NBackActivity : AppCompatActivity() {

    private val backStackHelper = BackStackHelper(this)
    private var drawerToolbar: ActionBarDrawerToggle? = null

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
        } else {
            // The user doesn't want to use location.
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

    override fun onBackPressed() {
        if (!backStackHelper.onBackPressed()) {
            super.onBackPressed()
        }
    }
}
