package ch.instantpastime.nback

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ch.instantpastime.nback.fragments.NBackFragment

class NBackActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nback)

        configureAndShowNBackFragment()
    }

    private fun configureAndShowNBackFragment() {
        if (supportFragmentManager.findFragmentById(R.id.nback_fragment_container) == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.nback_fragment_container, NBackFragment())
                .commit()
        }
    }
}
