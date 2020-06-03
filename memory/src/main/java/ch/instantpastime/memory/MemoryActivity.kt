package ch.instantpastime.memory

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import ch.instantpastime.*
import ch.instantpastime.PrefManager.Companion.setFirstTime
import ch.instantpastime.fragments.GeneralPreferenceFragment
import ch.instantpastime.memory.core.MemoryScore
import ch.instantpastime.memory.core.MemorySettings
import ch.instantpastime.memory.core.MemorySound
import ch.instantpastime.memory.fragments.MemoryFragment
import ch.instantpastime.memory.fragments.MemoryPreferenceFragment
import ch.instantpastime.memory.ui.MemoryTutoHelper
import ch.instantpastime.memory.ui.MyFragmentHelper
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_memory.*







class MemoryActivity :  AppCompatActivity(), MemoryFragment.reStartGame {


    private var mFragmentManager = supportFragmentManager
    var MemoryFragment = MemoryFragment()
    private var MemoryPreferenceFragment = MemoryPreferenceFragment()
    private var GeneralPreferenceFragment = GeneralPreferenceFragment()

    private var drawerToolbar: ActionBarDrawerToggle? = null
    private var drawerLayout: DrawerLayout? = null


    companion object{
        lateinit var prefManager : PrefManager
        lateinit var memoryScore : MemoryScore
        lateinit var memorySettings : MemorySettings
        lateinit var memorySound : MemorySound
        var gameOngoing : Boolean =false

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        // ************* DEBUG **********************
        //android.os.Debug.waitForDebugger()
        // ******************************************
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memory)
        nav_view.setOnNavigationItemSelectedListener { bottomMenuItemSelected(it) }


        mFragmentManager.beginTransaction()
            .add(R.id.memory_fragment_container, MemoryFragment,"MemoryFragment")
            .commit()

        prefManager = PrefManager(this)

        memorySettings = MemorySettings(this)
        memorySound = MemorySound()

        initDrawer()





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

    private fun bottomMenuItemSelected(menuItem: MenuItem): Boolean {

        val tag = MyFragmentHelper.getTagFromMenuId(menuItem.itemId)

        if (tag == "MemoryPreferenceFragment") {
            showSettings()
        }
        if (tag == "MemoryFragment") {
            showHome()
        }
        return true
    }

    private fun showHome(){

       if ((memorySettings.isLevelChanged() or (memorySettings.isContextImagesChanged()))){
           reStartGame()
        }
        else{
           mFragmentManager.popBackStack()

       }

       /* mFragmentManager.beginTransaction()
            .replace(
                R.id.memory_fragment_container,
                MemoryFragment,
                "MemoryFragment"
            )
            .addToBackStack("MemoryFragment")
            .commit()*/
    }

    private fun showSettings(){

        mFragmentManager.beginTransaction()
            .replace(
                R.id.memory_fragment_container,
                MemoryPreferenceFragment,
                "MemoryPreferenceFragment"
            )
            .addToBackStack("MemoryPreferenceFragment")
            .commit()
    }


    override fun reStartGame(){
        gameOngoing=false
        MemoryFragment = MemoryFragment()
        mFragmentManager.beginTransaction()
            .add(
                R.id.memory_fragment_container,
                MemoryFragment,
                "MemoryFragment"
            )
            .addToBackStack("MemoryFragment")
            .commit()

    }
    override fun onBackPressed(){
        super.onBackPressed()
        nav_view.getMenu().findItem(R.id.navigation_home).setChecked(true);
        showHome()
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
        //drawerNavView.menu.findItem(R.id.menu_general_preference)?.isVisible = false

        val drawerNavView = findViewById<View>(R.id.nav_view_drawer) as? NavigationView
        if (drawerNavView != null) {
            drawerNavView.setNavigationItemSelectedListener {
                when (it.itemId) {
                    ch.instantpastime.R.id.info -> {
                        AboutDialogHelper.showCredits(this)

                    }
                    ch.instantpastime.R.id.install -> {
                        InstallDialogHelper.showDialog(this, "Memory")
//                        Toast.makeText(
//                            this@MemoryActivity,
//                            "Installation",
//                            Toast.LENGTH_SHORT
//                        ).show()
                    }
                    ch.instantpastime.R.id.menu_general_preference -> {
                        showGeneralPreferencesDialog()
                    }
                    ch.instantpastime.R.id.menu_tutorial -> {
                        setFirstTime(MemoryFragment.context!! , false)
                        MemoryTutoHelper.startTutoActivity(this, true)
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
    private fun showGeneralPreferencesDialog() {
        mFragmentManager.beginTransaction()
            .replace(
                R.id.memory_fragment_container,
                GeneralPreferenceFragment,
                "GeneralPreferenceFragment"
            )
            .addToBackStack("GeneralPreferenceFragment")
            .commit()

    }
}
