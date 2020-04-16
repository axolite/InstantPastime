package ch.instantpastime.nback.fragments

import ch.instantpastime.nback.core.NBackGame

class NBackSettings(level: Int, time_per_trial: Int) {

    companion object {
        val NBACK_OPTIONS_CAT_KEY = "nback_options_category"
        val NBACK_LEVEL_KEY = "nback_level"
        val NBACK_MILLISECONDS_KEY = "nback_milliseconds"
    }

    /**
     * N-back level, that is the number of events to remember.
     */
    val level: Int

    /**
     * Time per trial in milliseconds.
     */
    val time_per_trial: Int

    init {
        this.level = if (level < NBackGame.MIN_LEVEL) {
            NBackGame.MIN_LEVEL
        } else if (level > NBackGame.MAX_LEVEL) {
            NBackGame.MAX_LEVEL
        } else {
            level
        }

        this.time_per_trial = if (time_per_trial < NBackGame.MIN_MILLISEC) {
            NBackGame.MIN_MILLISEC
        } else if (time_per_trial > NBackGame.MAX_MILLISEC) {
            NBackGame.MAX_MILLISEC
        } else {
            time_per_trial
        }
    }
}
