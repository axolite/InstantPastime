package ch.instantpastime.nback.core

class NBackBoard(nbLetters: Int, nBackLevel: Int) {

    companion object {
        const val DEFAULT_NB_TRIALS: Int = 20
    }

    private var gameRun: NBackRun = NBackRun(nbLetters = nbLetters, nBackLevel = nBackLevel)
    private val score: NBackScore = NBackScore()
    private var timer: NBackCountDown? = null
    private var lastDraw: NBackTrial? = null
    /**
     * True when the user says it is the same location, otherwise false.
     */
    var mAnswerSameLocation: Boolean = false

    /**
     * True when the user says it is the same letter, otherwise false.
     */
    var mAnswerSameLetter: Boolean = false

    /**
     * True when it is actually the same location, false when different,
     * null when there isn't enough elements to compare.
     */
    var mSameLocation: Boolean? = null

    /**
     * True when it is actually the same letter, false when different,
     * null when there isn't enough elements to compare.
     */
    var mSameLetter: Boolean? = null
    var nbTrials: Int = DEFAULT_NB_TRIALS
        private set
}
