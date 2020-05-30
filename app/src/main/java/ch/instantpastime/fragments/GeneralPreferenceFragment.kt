package ch.instantpastime.fragments

import android.os.Bundle
import android.widget.Toast
import androidx.preference.PreferenceFragmentCompat

class GeneralPreferenceFragment : PreferenceFragmentCompat()  {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        Toast.makeText(activity, "It works!", Toast.LENGTH_SHORT).show()

    }

}
