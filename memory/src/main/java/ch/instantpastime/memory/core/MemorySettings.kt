package ch.instantpastime.memory.core

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class MemorySettings(context:Context) {
    val numCards_array  = intArrayOf(8, 16, 36, 64)

    companion object {
        const val MIN_LEVEL = 1
        const val MAX_LEVEL = 4
        const val DEFAULT_LEVEL = 1

        val MEMORY_OPTIONS_CAT_KEY = "memory_options_category"
        val MEMORY_CONTEXT_CAT_KEY = "memory_context_category"
        val MEMORY_LEVEL_KEY = "memory_level"
        val MEMORY_SOUND_KEY = "memory_sound"
        val MEMORY_CONTEXT_CARDS_KEY = "memory_context_cards"
    }
    /**
     * Memory level, that is the number of events to remember.
     */
    var level: Int
    var num_cards: Int
    var num_images: Int
    var soundOn: Boolean
    var contextCards: Boolean
            var sharedPreferences: SharedPreferences

    init {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        level = sharedPreferences.getInt(
            MEMORY_LEVEL_KEY,
            DEFAULT_LEVEL
        )
        soundOn = sharedPreferences.getBoolean(
            MEMORY_SOUND_KEY,
            true
        )
        num_cards =numCards_array[level-1]
        num_images = num_cards/2
        contextCards = sharedPreferences.getBoolean(
            MEMORY_CONTEXT_CARDS_KEY,
            true
        )
    }
    fun setGameLevel(level: Int){
        this.level = if (level < MIN_LEVEL) {
            MIN_LEVEL
        } else if (level > MAX_LEVEL) {
            MAX_LEVEL
        } else {
            level
        }
        num_cards =numCards_array[level-1]
        num_images = num_cards/2
    }

    fun isLevelChanged():Boolean {
        val new_level = sharedPreferences.getInt(
            MEMORY_LEVEL_KEY,
            DEFAULT_LEVEL
        )
        if (new_level != level) {
            setGameLevel(new_level)
            return true
        }
        return false
    }

    fun isContextImagesChanged():Boolean {
        val new_context = sharedPreferences.getBoolean(
            MEMORY_CONTEXT_CARDS_KEY,
            true
        )
        if (new_context != contextCards) {
            contextCards = new_context
            return true
        }
        return false
    }




    fun conversionLeveltoCards(level: Int): Int{
        return numCards_array[level-1]


     }


}
