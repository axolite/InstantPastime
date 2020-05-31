package ch.instantpastime.memory

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import ch.instantpastime.PrefManager
import ch.instantpastime.ValueChange
import ch.instantpastime.memory.core.MemoryScore
import ch.instantpastime.memory.core.MemorySettings
import ch.instantpastime.memory.core.MemorySound
import ch.instantpastime.memory.fragments.MemoryFragment
import ch.instantpastime.memory.ui.FragmentStack
import ch.instantpastime.memory.ui.MyFragmentHelper
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_memory.*




lateinit var drawer_toolbar: ActionBarDrawerToggle



class MemoryActivity : AppCompatActivity() {

    //private val backStackHelper = BackStackHelper(this)
    private val fragmentStack: FragmentStack = FragmentStack(
        activity = this,
        containerId = R.id.memory_fragment_container,
        homeTag = MemoryFragment::class.java.simpleName
    )
    companion object{
        lateinit var prefManager : PrefManager
        lateinit var tuto_slides: IntArray
        lateinit var tuto_images: IntArray
        lateinit var tuto_texts: IntArray
        lateinit var memoryScore : MemoryScore
        lateinit var memorySettings : MemorySettings
        lateinit var memorySound : MemorySound


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        // ************* DEBUG **********************
        //android.os.Debug.waitForDebugger()
        // ******************************************
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memory)
        nav_view.setOnNavigationItemSelectedListener { bottomMenuItemSelected(it) }
        fragmentStack.currentTagChanged = { currentFragmentChanged(it) }
        fragmentStack.pushFragment(fragmentStack.homeTag)

        prefManager = PrefManager(this)

        memorySettings = MemorySettings(this)
        memorySound = MemorySound()
        var drawerlayout = findViewById(R.id.activity_main) as DrawerLayout

        drawer_toolbar = ActionBarDrawerToggle(this, drawerlayout, 0, 0)
        drawer_toolbar.syncState()

        drawerlayout.addDrawerListener(drawer_toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        var nav_view_drawer = findViewById(R.id.nav_view_drawer) as NavigationView
        nav_view_drawer.setNavigationItemSelectedListener(object :
            NavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                val id: Int = item.getItemId()
                when (id) {
                    ch.instantpastime.R.id.info -> Toast.makeText(
                        this@MemoryActivity,
                        "À propos de ..",
                        Toast.LENGTH_SHORT
                    ).show()
                    ch.instantpastime.R.id.install -> Toast.makeText(
                        this@MemoryActivity,
                        "Installation",
                        Toast.LENGTH_SHORT
                    ).show()

                    else -> return true
                }
                return true
            }
        })

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
        return if (drawer_toolbar?.onOptionsItemSelected(item) == true) {
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

}
