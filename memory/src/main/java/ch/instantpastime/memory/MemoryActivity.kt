package ch.instantpastime.memory

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import ch.instantpastime.memory.ui.BackStackHelper
import kotlinx.android.synthetic.main.activity_memory.*
import kotlinx.android.synthetic.main.fragment_memory.*
import org.json.JSONObject

import android.view.MenuItem
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import ch.instantpastime.memory.fragments.MemoryFragment
import kotlinx.android.synthetic.main.activity_memory.*

const val num_images = 32


class MemoryActivity : AppCompatActivity() {

    private val backStackHelper = BackStackHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        //android.os.Debug.waitForDebugger()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memory)
        nav_view.setOnNavigationItemSelectedListener { backStackHelper.onNavigationItemSelected(it) }
        backStackHelper.loadFragment(nav_view.selectedItemId)
    }

    override fun onBackPressed() {
        if (!backStackHelper.onBackPressed()) {
            super.onBackPressed()
        }
    }

}
