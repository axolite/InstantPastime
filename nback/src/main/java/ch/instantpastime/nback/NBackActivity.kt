package ch.instantpastime.nback

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import ch.instantpastime.nback.fragments.NBackFragment
import ch.instantpastime.nback.fragments.NBackPreferenceFragment
import ch.instantpastime.nback.ui.BackStackHelper
import kotlinx.android.synthetic.main.activity_nback.*

class NBackActivity : AppCompatActivity() {

    private val backStackHelper = BackStackHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        //android.os.Debug.waitForDebugger()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nback)
        nav_view.setOnNavigationItemSelectedListener { backStackHelper.onNavigationItemSelected(it) }
        backStackHelper.loadFragment(nav_view.selectedItemId)
    }

    override fun onBackPressed() {
        if (!backStackHelper.onBackPressed()) {
            super.onBackPressed()
        }
    }
}
