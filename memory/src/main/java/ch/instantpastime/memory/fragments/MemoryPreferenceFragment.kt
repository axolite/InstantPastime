package ch.instantpastime.memory.fragments

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.preference.*
import ch.instantpastime.memory.MemoryActivity.Companion.memorySettings
import ch.instantpastime.memory.R
import ch.instantpastime.memory.core.MemorySettings
import ch.instantpastime.memory.core.MemorySettings.Companion.DEFAULT_LEVEL
import ch.instantpastime.memory.core.MemorySettings.Companion.MAX_LEVEL
import ch.instantpastime.memory.core.MemorySettings.Companion.MIN_LEVEL


class MemoryPreferenceFragment : PreferenceFragmentCompat() {


    var mSoundPreference: SwitchPreference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager?.context?.let { context ->
            preferenceScreen = buildPreferenceScreen(context)
        }
    }

    private fun buildPreferenceScreen(context: Context): PreferenceScreen? {
        val screen = preferenceManager?.createPreferenceScreen(context)?.apply {
            addGameOptionsSettings()
            addGameContextSettings()
        }
        return screen
    }

    private fun onMemoryLevelPreferenceChanged(preference: Preference, newValue: Any?): Boolean {
        val levels = arrayOf("8", "16", "36" , "64")

        return if (newValue is Int && preference is SeekBarPreference) {
            val oldValue = preference.value
            if (oldValue != newValue) {
                preference.value = newValue
                Toast.makeText(
                    preference.context,
                    getString(R.string.memory_level_changed, newValue.toInt()),
                    Toast.LENGTH_SHORT
                ).show()

                true
            } else {
                Toast.makeText(preference.context, "No change: same value", Toast.LENGTH_SHORT)
                    .show()
                false
            }
        } else {
            Toast.makeText(preference.context, "No change: wrong type", Toast.LENGTH_SHORT).show()
            false
        }
    }

    private fun PreferenceScreen.addGameOptionsSettings() {
        apply {
            val gameOptionsCategory = PreferenceCategory(context).apply {
                key = MemorySettings.MEMORY_OPTIONS_CAT_KEY
                title = getString(R.string.game_options_full)
            }
            addPreference(gameOptionsCategory)

            val memoryLevelPref = SeekBarPreference(context).apply {
                key = MemorySettings.MEMORY_LEVEL_KEY
                title = getString(R.string.memory_level, MIN_LEVEL, MAX_LEVEL)
                summary = getString(R.string.memory_level_hint) + " = " + memorySettings.num_cards
                min = MIN_LEVEL
                max = MAX_LEVEL
                showSeekBarValue = true
                //seekBarIncrement = 4
                setDefaultValue(DEFAULT_LEVEL)
                onPreferenceChangeListener = Preference.OnPreferenceChangeListener { p, v ->
                    val newvalue: Int = v as Int
                    //memorySettings.setGameLevel(newvalue)
                    summary = getString(R.string.memory_level_hint) + " =" + memorySettings.conversionLeveltoCards(newvalue)
                    onMemoryLevelPreferenceChanged(p, v)
                }
            }
            gameOptionsCategory.addPreference(memoryLevelPref)


            // Note from https://developer.android.com/guide/topics/ui/settings/programmatic-hierarchy
            // Warning: Make sure to add the PreferenceCategory to the PreferenceScreen before adding children to it.
            // Preferences cannot be added to a PreferenceCategory that isn’t attached to the root screen.
        }
    }

    private fun PreferenceScreen.addGameContextSettings() {
        val gameContextCategory = PreferenceCategory(context).apply {
            key = MemorySettings.MEMORY_CONTEXT_CAT_KEY
            title = getString(R.string.game_context)
        }
        addPreference(gameContextCategory)


        val soundPref = SwitchPreference(context).apply {
            key = MemorySettings.MEMORY_SOUND_KEY
            title = getString(R.string.memory_sound)
            summary = getString(R.string.memory_sound_hint)
            isChecked = true
            mSoundPreference = this
            onPreferenceChangeListener = Preference.OnPreferenceChangeListener { p, v ->
                return@OnPreferenceChangeListener if (p is SwitchPreference && v is Boolean) {
                    p.isChecked = v
                    memorySettings.soundOn = v
                    true
                } else {
                    false
                }
            }
        }
        gameContextCategory.addPreference(soundPref)


        val contextCardsPref = SwitchPreference(context).apply {
            key = MemorySettings.MEMORY_CONTEXT_CARDS_KEY
            title = getString(R.string.memory_context_cards)
            summary = getString(R.string.memory_context_cards_hint)
            isChecked = true

            onPreferenceChangeListener = Preference.OnPreferenceChangeListener { p, v ->
                return@OnPreferenceChangeListener if (p is SwitchPreference && v is Boolean) {
                    p.isChecked = v
                    //memorySettings.contextCards = v
                    true
                } else {
                    false
                }
            }
        }
        gameContextCategory.addPreference(contextCardsPref)

        // Note from https://developer.android.com/guide/topics/ui/settings/programmatic-hierarchy
        // Warning: Make sure to add the PreferenceCategory to the PreferenceScreen before adding children to it.
        // Preferences cannot be added to a PreferenceCategory that isn’t attached to the root screen.
    }

    private fun onTimePerTrialPreferenceChanged(preference: Preference, newValue: Any?): Boolean {
        return if (newValue is Int && preference is SeekBarPreference) {
            val oldValue = preference.value
            if (oldValue != newValue) {
                preference.value = newValue
                Toast.makeText(
                    preference.context,
                    resources.getQuantityString(
                        R.plurals.nback_interval_changed,
                        newValue,
                        newValue
                    ),
                    Toast.LENGTH_SHORT
                ).show()
                true
            } else false
        } else false
    }

}
