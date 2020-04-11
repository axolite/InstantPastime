package ch.instantpastime.nback

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import ch.instantpastime.nback.fragments.NBackFragment
import ch.instantpastime.nback.fragments.NBackPreferenceFragment
import kotlinx.android.synthetic.main.activity_nback.*

class NBackActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        //android.os.Debug.waitForDebugger()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nback)
        nav_view.setOnNavigationItemSelectedListener { onNavigationItemSelected(it) }
        loadFragment(nav_view.selectedItemId)
    }

    private fun buildFragment(@IdRes menuItemId: Int): Fragment? {
        return when (menuItemId) {
            R.id.navigation_home -> NBackFragment()
            R.id.navigation_option -> NBackPreferenceFragment()
            else -> null
        }
    }

    private fun loadFragment(@IdRes menuItemId: Int): Boolean {

        // Check whether the given menu item is already selected and has a valid fragment.
        return if (nav_view.selectedItemId == menuItemId &&
            supportFragmentManager.findFragmentById(R.id.nback_fragment_container) != null) {
            // The requested menu item is already selected and has a valid fragment.
            true
        } else {
            // Build the fragment for the requested menu item.
            val newFragment = buildFragment(menuItemId)
            if (newFragment != null) {
                // Pass the built fragment to the fragment manager.
                supportFragmentManager.beginTransaction()
                    .replace(R.id.nback_fragment_container, newFragment)
                    .commit()
                true
            } else {
                // No fragment could be built for the requested menu item.
                false
            }
        }
    }

    private fun onNavigationItemSelected(item: MenuItem): Boolean {
        return loadFragment(item.itemId)
    }
}
