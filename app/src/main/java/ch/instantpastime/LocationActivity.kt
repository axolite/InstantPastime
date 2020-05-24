package ch.instantpastime

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_location.*

class LocationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        findViewById<Button>(R.id.loc_accept_button).apply {
            setOnClickListener { acceptButtonClicked() }
        }
        findViewById<Button>(R.id.loc_refuse_button).apply {
            setOnClickListener { refuseButtonClicked() }
        }
    }

    private fun acceptButtonClicked() {
        Toast.makeText(this, "Locating and downloading...", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun refuseButtonClicked() {
        Toast.makeText(this, "Ok, no location", Toast.LENGTH_SHORT).show()
        finish()
    }
}
