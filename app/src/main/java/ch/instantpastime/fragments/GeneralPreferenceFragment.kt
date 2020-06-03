package ch.instantpastime.fragments

import android.content.Context
import android.os.Bundle
import androidx.preference.*
import ch.instantpastime.ContextualImageUser
import ch.instantpastime.LocationHelper
import ch.instantpastime.PrefManager
import ch.instantpastime.R

class GeneralPreferenceFragment : PreferenceFragmentCompat()  {

    val locationHelper = LocationHelper()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager?.context?.let { context ->
            preferenceScreen = buildPreferenceScreen(context)
        }
    }

    private fun buildPreferenceScreen(context: Context): PreferenceScreen? {
        val screen = preferenceManager?.createPreferenceScreen(context)?.also {
            addGeolocationSettings(it)
        }
        return screen
    }

    private fun addGeolocationSettings(screen: PreferenceScreen) {

        val context = this.context ?: return

        val geolocationCategory = PreferenceCategory(context).apply {
            key = PrefManager.LOCATION_CAT_KEY
            title = getString(R.string.pref_geolocation_category)
        }
        screen.addPreference(geolocationCategory)

        // Fetch the value manually, otherwise it won't be up to date unless the app is restarted.
        val useContextualImage = PrefManager.getLocationPref(context, defValue = false)

        val contextualImagePref = SwitchPreference(context).apply {
            key = PrefManager.USE_LOCATION_PREF_KEY
            title = getString(R.string.pref_contextual_image)
            summary = getString(R.string.pref_contextual_image_hint)
            setDefaultValue(useContextualImage)
            onPreferenceChangeListener = Preference.OnPreferenceChangeListener { p, v ->
                if (p is SwitchPreference && v is Boolean) {
                    p.isChecked = v
                    // Set the value manually in the shared preference otherwise it won't be saved.
                    // Must be due to a wrong key but cannot find out.
                    PrefManager.saveLocationPref(context, v)

                    // Transmit the information the activity in order to
                    // start or stop it can start or stop loading images.
                    val specialActivity = activity as? ContextualImageUser
                    specialActivity?.enableContextualImages(v)
                    true
                } else {
                    false
                }
            }
        }
        geolocationCategory.addPreference(contextualImagePref)
        // Set the value after having added the preference to the category otherwise it won't work.
        contextualImagePref.isChecked = useContextualImage

        // Note from https://developer.android.com/guide/topics/ui/settings/programmatic-hierarchy
        // Warning: Make sure to add the PreferenceCategory to the PreferenceScreen before adding children to it.
        // Preferences cannot be added to a PreferenceCategory that isn’t attached to the root screen.
    }

}
