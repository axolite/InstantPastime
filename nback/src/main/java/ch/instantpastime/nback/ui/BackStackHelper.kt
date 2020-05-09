package ch.instantpastime.nback.ui

import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import ch.instantpastime.nback.NBackActivity
import ch.instantpastime.nback.R
import ch.instantpastime.nback.fragments.NBackFragment
import ch.instantpastime.nback.fragments.NBackPreferenceFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_nback.*

/**
 * Back stack helper that handles corner cases.
 * TODO: simplify using hide and show methods of FragmentManager: https://stackoverflow.com/a/22714222
 */
class BackStackHelper(private val activity: NBackActivity) {

    data class FragmentTableEntry(
        val menuItemId: Int,
        val generator: (x: Bundle?) -> Fragment,
        val stackTag: String
    )

    companion object {
        val GAME_STACK_TAG = "GAME_STACK_TAG"
        val OPTIONS_STACK_TAG = "OPTIONS_STACK_TAG"
        val fragmentTable = listOf(
            FragmentTableEntry(
                menuItemId = R.id.navigation_home,
                generator = { _ -> NBackFragment() },
                stackTag = GAME_STACK_TAG
            ),
            FragmentTableEntry(
                menuItemId = R.id.navigation_option,
                generator = { _ -> NBackPreferenceFragment() },
                stackTag = OPTIONS_STACK_TAG
            )
        )
    }

    private var _navigatingFromCode = false

    private val supportFragmentManager: FragmentManager
        get() {
            return activity.supportFragmentManager
        }

    private val nav_view: BottomNavigationView
        get() {
            return activity.nav_view
        }

    private val nback_fragment_container: FrameLayout
        get() {
            return activity.nback_fragment_container
        }

    fun onBackPressed(): Boolean {
        return if (supportFragmentManager.backStackEntryCount > 0) {
            if (supportFragmentManager.popBackStackImmediate(null, 0)) {
                getCurrentMenuItemId()?.let {
                    setNavigationViewFromCode(it)
                }
            }
            true
        } else {
            false
        }
    }

    @IdRes
    private fun getCurrentMenuItemId(): Int? {
        return when (val fragment =
            supportFragmentManager.findFragmentById(R.id.nback_fragment_container)) {
            null -> null
            else -> getMenuItemIdFrom(fragment)
        }
    }

    /**
     * Gets the menu item ID on top of the back stack, if any.
     * Based on https://stackoverflow.com/a/15028843
     */
    @IdRes
    private fun getTopMenuItemId(): Int? {
        val lastIndex = supportFragmentManager.backStackEntryCount - 1
        return if (lastIndex >= 0) {
            supportFragmentManager.getBackStackEntryAt(lastIndex).name?.let { entryName ->
                fragmentTable.firstOrNull { it.stackTag == entryName }?.menuItemId
            }
        } else {
            null
        }
    }

    private fun buildFragment(@IdRes menuItemId: Int): Fragment? {
        return when (menuItemId) {
            R.id.navigation_home -> NBackFragment()
            R.id.navigation_option -> NBackPreferenceFragment()
            else -> null
        }
    }

    @IdRes
    private fun getMenuItemIdFrom(fragment: Fragment): Int? {
        return when (fragment) {
            is NBackFragment -> R.id.navigation_home
            is NBackPreferenceFragment -> R.id.navigation_option
            else -> null
        }
    }

    private fun popBackStackToFragment(@IdRes menuItemId: Int): Boolean {
        val fragmentEntry = fragmentTable.firstOrNull { it -> it.menuItemId == menuItemId }
        return if (fragmentEntry != null) {
            val ret = supportFragmentManager.popBackStackImmediate(fragmentEntry.stackTag, 0)
            ret
        } else {
            false
        }
    }

    fun loadFragment(@IdRes menuItemId: Int): Boolean {

        // Check whether the given menu item is already selected and has a valid fragment.
        return if (nav_view.selectedItemId == menuItemId &&
            supportFragmentManager.findFragmentById(R.id.nback_fragment_container) != null
        ) {
            // The requested menu item is already selected and has a valid fragment.
            true
        } else if (popBackStackToFragment(menuItemId)) {
            setNavigationViewFromCode(menuItemId)
            true
        } else {
            // Build the fragment for the requested menu item.
            val newFragmentEntry = fragmentTable.firstOrNull { it -> it.menuItemId == menuItemId }
            if (newFragmentEntry != null) {
                // Pass the built fragment to the fragment manager.
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.nback_fragment_container, newFragmentEntry.generator(null))
                    // Don't add the first fragment to the back stack, otherwise when pressing
                    // the back button the container will be blank.
                    // Source: https://stackoverflow.com/a/34025775
                    if (nback_fragment_container.childCount > 0) {
                        addToBackStack(newFragmentEntry.stackTag)
                    }
                    commit()
                }
                true
            } else {
                // No fragment could be built for the requested menu item.
                false
            }
        }
    }

    fun onNavigationItemSelected(item: MenuItem): Boolean {
        return if (_navigatingFromCode) {
            true
        } else {
            loadFragment(item.itemId)
        }
    }

    private fun setNavigationViewFromCode(@IdRes menuItemId: Int) {
        if (nav_view.selectedItemId != menuItemId) {
            _navigatingFromCode = true
            nav_view.selectedItemId = menuItemId
            _navigatingFromCode = false
        }
    }
}
