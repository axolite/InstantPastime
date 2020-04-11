package ch.instantpastime.nback

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import ch.instantpastime.nback.fragments.NBackFragment
import kotlinx.android.synthetic.main.activity_nback.*

class NBackActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        android.os.Debug.waitForDebugger()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nback)
        nav_view.setOnNavigationItemSelectedListener { onNavigationItemSelected(it) }
        loadFragment(nav_view.selectedItemId)
    }

    private fun buildFragment(@IdRes menuItemId: Int): Fragment? {
        return when (menuItemId) {
            R.id.navigation_home -> NBackFragment()
            else -> null
        }
    }

    private fun loadFragment(@IdRes menuItemId: Int): Boolean {
        val oldFragment = supportFragmentManager.findFragmentById(R.id.nback_fragment_container)
        return if (oldFragment != null && nav_view.selectedItemId == menuItemId) {
            true
        } else {
            val newFragment = buildFragment(menuItemId)
            if (newFragment != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.nback_fragment_container, newFragment)
                    .commit()
                true
            } else {
                false
            }
        }
    }

    private fun onNavigationItemSelected(item: MenuItem): Boolean {
        return loadFragment(item.itemId)
    }
}
