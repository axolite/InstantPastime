package ch.instantpastime.memory

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.preference.PreferenceManager
import ch.instantpastime.PrefManager
import ch.instantpastime.ValueChange
import ch.instantpastime.memory.core.MemoryScore
import ch.instantpastime.memory.core.MemorySettings
import ch.instantpastime.memory.core.MemorySound
import ch.instantpastime.memory.fragments.MemoryFragment
import ch.instantpastime.memory.fragments.MemoryPreferenceFragment
import ch.instantpastime.memory.ui.FragmentStack
import ch.instantpastime.memory.ui.MyFragmentHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_memory.*







class MemoryActivity :  AppCompatActivity() {

    //private val backStackHelper = BackStackHelper(this)
   /* private val fragmentStack: FragmentStack = FragmentStack(
        activity = this,
        containerId = R.id.memory_fragment_container,
        homeTag = MemoryFragment::class.java.simpleName
    )*/

    private var mFragmentManager = supportFragmentManager
    private var MemoryFragment = MemoryFragment()
    private var MemoryPreferenceFragment = MemoryPreferenceFragment()

    private var drawerToolbar: ActionBarDrawerToggle? = null
    private var drawerLayout: DrawerLayout? = null


    companion object{
        lateinit var prefManager : PrefManager
        lateinit var tuto_slides: IntArray
        lateinit var tuto_images: IntArray
        lateinit var tuto_texts: IntArray
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
        /*fragmentStack.currentTagChanged = { currentFragmentChanged(it) }
        fragmentStack.pushFragment(fragmentStack.homeTag)*/


        //MemoryFragment.stockImagesLoad(this)


        mFragmentManager.beginTransaction()
            .add(R.id.memory_fragment_container, MemoryFragment,"MemoryFragment")
            .commit()

        prefManager = PrefManager(this)

        memorySettings = MemorySettings(this)
        memorySound = MemorySound()

        initDrawer()



        /* *************************************************************** */
        /* Define Slides for Tuto **************************************** */
        /* ******* Layouts *********************************************** */
        tuto_slides = intArrayOf(
            ch.instantpastime.R.layout.activity_start_content01,
            ch.instantpastime.R.layout.activity_start_content01,
            ch.instantpastime.R.layout.activity_start_content01
        )
        /* ******* Images ************************************************ */
        tuto_images = intArrayOf(
            R.drawable.tutoslide01,
            R.drawable.tutoslide02,
            R.drawable.tutoslide03

        )
        /* ******* Texts ************************************************* */
        tuto_texts = intArrayOf(
            R.string.start01,
            R.string.start02,
            R.string.start03
        )
        /* *************************************************************** */


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
    private fun currentFragmentChanged(tag: ValueChange<String>) {
        // Update the active icon in the bottom menu according to the displayed fragment.
        val menuId = MyFragmentHelper.getMenuIdFromTag(tag.newValue)
        if (menuId != null && nav_view.selectedItemId != menuId) {
            nav_view.selectedItemId = menuId
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

    override fun onBackPressed(){
        super.onBackPressed()
        nav_view.getMenu().findItem(R.id.navigation_home).setChecked(true);
        showHome()
        }

    private fun initDrawer() {
        val drawerLayout = findViewById<View>(R.id.nav_view_drawer) as? DrawerLayout
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
                            this@MemoryActivity,
                            "À propos de ..",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    ch.instantpastime.R.id.install -> {
                        Toast.makeText(
                            this@MemoryActivity,
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
                    else -> {
                    }
                }
                drawerLayout?.closeDrawers()
                true
            }
        }
    }

}
