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
            isChecked = useContextualImage
            onPreferenceChangeListener = Preference.OnPreferenceChangeListener { p, v ->
                if (p is SwitchPreference && v is Boolean) {
                    p.isChecked = v
                    val activity = activity
                    val specialActivity = activity as ContextualImageUser
                    if (v) {
                        if (locationHelper.askLocationAccess(activity)) {
                            specialActivity.fetchContextualImages()
                        }
                    } else {
                        specialActivity.useStockImages()
                    }
                    true
                } else {
                    false
                }
            }
        }
        geolocationCategory.addPreference(contextualImagePref)

        // Note from https://developer.android.com/guide/topics/ui/settings/programmatic-hierarchy
        // Warning: Make sure to add the PreferenceCategory to the PreferenceScreen before adding children to it.
        // Preferences cannot be added to a PreferenceCategory that isn’t attached to the root screen.
    }

}
