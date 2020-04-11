package ch.instantpastime.nback.fragments

import ch.instantpastime.nback.core.NBackGame

class NBackSettings(level: Int) {

    companion object {
        val NBACK_OPTIONS_CAT_KEY = "nback_options_category"
        val NBACK_LEVEL_KEY ="nback_level"
    }

    val level: Int

    init {
        this.level = if (NBackGame.MIN_LEVEL < level) {
            NBackGame.MIN_LEVEL
        } else if (level > NBackGame.MAX_LEVEL) {
            NBackGame.MAX_LEVEL
        } else {
            level
        }
    }
}
