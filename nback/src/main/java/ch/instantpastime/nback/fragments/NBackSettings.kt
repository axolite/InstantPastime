package ch.instantpastime.nback.fragments

import ch.instantpastime.nback.core.NBackGame

class NBackSettings(level: Int, seconds_per_trial: Double) {

    companion object {
        val NBACK_OPTIONS_CAT_KEY = "nback_options_category"
        val NBACK_LEVEL_KEY = "nback_level"
        val NBACK_SECONDS_KEY = "nback_seconds"
    }

    val level: Int
    val seconds_per_trial: Double

    init {
        this.level = if (NBackGame.MIN_LEVEL < level) {
            NBackGame.MIN_LEVEL
        } else if (level > NBackGame.MAX_LEVEL) {
            NBackGame.MAX_LEVEL
        } else {
            level
        }

        this.seconds_per_trial = if (NBackGame.MIN_SECONDS < seconds_per_trial) {
            NBackGame.MIN_SECONDS
        } else if (seconds_per_trial > NBackGame.MAX_SECONDS) {
            NBackGame.MAX_SECONDS
        } else {
            seconds_per_trial
        }
    }
}
