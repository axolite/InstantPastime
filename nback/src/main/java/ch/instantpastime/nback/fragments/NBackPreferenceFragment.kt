package ch.instantpastime.nback.fragments

import android.content.Context
import android.os.Bundle
import android.os.SystemClock.uptimeMillis
import android.widget.Toast
import androidx.preference.*
import ch.instantpastime.nback.R
import ch.instantpastime.nback.core.NBackEnvironmentSettings
import ch.instantpastime.nback.core.NBackRun
import ch.instantpastime.nback.ui.TranslatableValue

class NBackPreferenceFragment : PreferenceFragmentCompat() {

    companion object {
        const val SYMBOL_IMAGE = "image"
        const val SYMBOL_LETTER = "letter"
        val symbolEntries: List<TranslatableValue<String>> = listOf(
            TranslatableValue(value = SYMBOL_IMAGE, langId = R.string.nback_symbols_images),
            TranslatableValue(value = SYMBOL_LETTER, langId = R.string.nback_symbols_letters)
        )
    }

    var mSoundPreference: SwitchPreference? = null

    var lastClick: Long = 0

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

    private fun onNBackLevelPreferenceChanged(preference: Preference, newValue: Any?): Boolean {

        return if (newValue is Int && preference is SeekBarPreference) {
            val oldValue = preference.value
            if (oldValue != newValue) {
                preference.value = newValue
                Toast.makeText(
                    preference.context,
                    getString(R.string.nback_level_changed, newValue.toInt()),
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
        val gameOptionsCategory = PreferenceCategory(context).apply {
            key = NBackSettings.NBACK_OPTIONS_CAT_KEY
            title = getString(R.string.game_levels)
        }
        addPreference(gameOptionsCategory)

        val nbackLevelPref = SeekBarPreference(context).apply {
            key = NBackSettings.NBACK_LEVEL_KEY
            title = getString(R.string.nback_level, NBackRun.MIN_LEVEL, NBackRun.MAX_LEVEL)
            summary = getString(R.string.nback_level_hint)
            min = NBackRun.MIN_LEVEL
            max = NBackRun.MAX_LEVEL
            showSeekBarValue = true
            setDefaultValue(NBackRun.DEFAULT_LEVEL)
            onPreferenceChangeListener = Preference.OnPreferenceChangeListener { p, v ->
                onNBackLevelPreferenceChanged(p, v)
            }
            setOnPreferenceClickListener {
                if (isDoubleClick()) {
                    value = NBackRun.DEFAULT_LEVEL
                }
                true
            }
        }
        gameOptionsCategory.addPreference(nbackLevelPref)

        val timePerTrialPref = SeekBarPreference(context).apply {
            key = NBackSettings.NBACK_MILLISECONDS_KEY
            title = getString(R.string.nback_interval)
            summary = getString(R.string.nback_interval_hint)
            max = NBackRun.MAX_MILLISEC
            min = NBackRun.MIN_MILLISEC
            showSeekBarValue = true
            setDefaultValue(NBackRun.DEFAULT_MILLISEC)
            onPreferenceChangeListener = Preference.OnPreferenceChangeListener { p, v ->
                onTimePerTrialPreferenceChanged(p, v)
            }
            setOnPreferenceClickListener {
                if (isDoubleClick()) {
                    value = NBackRun.DEFAULT_MILLISEC
                }
                true
            }
        }
        gameOptionsCategory.addPreference(timePerTrialPref)

        // Note from https://developer.android.com/guide/topics/ui/settings/programmatic-hierarchy
        // Warning: Make sure to add the PreferenceCategory to the PreferenceScreen before adding children to it.
        // Preferences cannot be added to a PreferenceCategory that isn’t attached to the root screen.
    }

    private fun PreferenceScreen.addGameContextSettings() {
        val gameContextCategory = PreferenceCategory(context).apply {
            key = NBackSettings.NBACK_CONTEXT_CAT_KEY
            title = getString(R.string.game_context)
        }
        addPreference(gameContextCategory)

        // Property ListPreference.value doesn't contain the saved value yet,
        // so get it manually.
        val savedSymbolEntry: String = PreferenceManager.getDefaultSharedPreferences(context)?.run {
            getString(NBackSettings.NBACK_SYMBOL_KEY, SYMBOL_IMAGE)
        } ?: SYMBOL_IMAGE

        val symbolPref = ListPreference(context).apply {
            key = NBackSettings.NBACK_SYMBOL_KEY
            title = getString(R.string.nback_symbols_long)
            entryValues = symbolEntries.map { it.value }.toTypedArray()
            entries = symbolEntries.map { getString(it.langId) }.toTypedArray()
            setDefaultValue(SYMBOL_IMAGE)
            summary = symbolEntries.getTranslation(savedSymbolEntry)
            onPreferenceChangeListener = Preference.OnPreferenceChangeListener { p, v ->
                return@OnPreferenceChangeListener if (p is ListPreference && v is String) {
                    p.value = v
                    p.summary = symbolEntries.getTranslation(v)
                    mSoundPreference?.isVisible = v == SYMBOL_LETTER
                    true
                } else {
                    false
                }
            }
        }
        gameContextCategory.addPreference(symbolPref)

        val soundPref = SwitchPreference(context).apply {
            key = NBackSettings.NBACK_SOUND_KEY
            title = getString(R.string.nback_sound)
            summary = getString(R.string.nback_sound_hint)
            setDefaultValue(NBackEnvironmentSettings.PLAY_SOUND_DEFAULT)
            isVisible = savedSymbolEntry == SYMBOL_LETTER
            mSoundPreference = this
            onPreferenceChangeListener = Preference.OnPreferenceChangeListener { p, v ->
                return@OnPreferenceChangeListener if (p is SwitchPreference && v is Boolean) {
                    p.isChecked = v
                    true
                } else {
                    false
                }
            }
        }
        gameContextCategory.addPreference(soundPref)

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

    fun Iterable<TranslatableValue<String>>.getTranslation(value: String?): String {
        val translatable = firstOrNull { it.value == value }
        return if (translatable != null) {
            getString(translatable.langId)
        } else {
            "Unknown"
        }
    }

    private fun isDoubleClick(): Boolean {
        val current = uptimeMillis()
        val last = lastClick
        lastClick = current
        val diff = current - last
        return diff < 300
    }
}
