package ch.instantpastime.nback.fragments

import ch.instantpastime.nback.core.NBackRun

class NBackSettings(level: Int, time_per_trial: Int) {

    companion object {
        val NBACK_OPTIONS_CAT_KEY = "nback_options_category"
        val NBACK_CONTEXT_CAT_KEY = "nback_context_category"

        val NBACK_LEVEL_KEY = "nback_level"
        val NBACK_MILLISECONDS_KEY = "nback_milliseconds"
        val NBACK_SYMBOL_KEY = "nback_symbol"
        val NBACK_SOUND_KEY = "nback_sound"
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
        this.level = if (level < NBackRun.MIN_LEVEL) {
            NBackRun.MIN_LEVEL
        } else if (level > NBackRun.MAX_LEVEL) {
            NBackRun.MAX_LEVEL
        } else {
            level
        }

        this.time_per_trial = if (time_per_trial < NBackRun.MIN_MILLISEC) {
            NBackRun.MIN_MILLISEC
        } else if (time_per_trial > NBackRun.MAX_MILLISEC) {
            NBackRun.MAX_MILLISEC
        } else {
            time_per_trial
        }
    }
}
