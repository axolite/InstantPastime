package ch.instantpastime.nback.core

class NBackBoard(nbLetters: Int, nBackLevel: Int) {

    companion object {
        const val DEFAULT_NB_TRIALS: Int = 20
    }

    private var gameRun: NBackRun = NBackRun(nbLetters = nbLetters, nBackLevel = nBackLevel)
    private val score: NBackScore = NBackScore()
    var lastDraw: NBackTrial? = null
        private set
    /**
     * True when the user says it is the same location, otherwise false.
     */
    var mAnswerSameLocation: Boolean = false

    /**
     * True when the user says it is the same letter, otherwise false.
     */
    var mAnswerSameLetter: Boolean = false

    var nbTrials: Int = DEFAULT_NB_TRIALS
        private set

    val TotalCount: Int
        get() = score.TotalCount

    val CorrectCount: Int
        get() = score.CorrectCount

    val Level: Int
        get() = gameRun._level

    fun toggleLocationAnswer(): Boolean {
        return mAnswerSameLocation.also {
            mAnswerSameLocation = !it
        }
    }

    fun toggleLetterAnswer(): Boolean {
        return mAnswerSameLetter.also {
            mAnswerSameLetter = !it
        }
    }

    fun reset() {
        score.reset()
        lastDraw = null
        mAnswerSameLocation = false
        mAnswerSameLetter = false
    }

    fun getNextTrial(): NBackTrial {
        return gameRun.getNextTrial().also {
            lastDraw = it
        }
    }

    fun checkCurrentAnswer(): Pair<NBackScore.Correctness?, NBackScore.Correctness?> {
        val correctnesses = checkAnswer(
            locationAnswer = mAnswerSameLetter,
            letterAnswer = mAnswerSameLetter
        )
        // Reset user's answers.
        mAnswerSameLocation = false
        mAnswerSameLetter = false
        return correctnesses
    }

    private fun checkAnswer(locationAnswer: Boolean, letterAnswer: Boolean)
            : Pair<NBackScore.Correctness?, NBackScore.Correctness?> {
        val locationCorrectness =
            NBackScore.getCorrectness(
                answer = locationAnswer,
                // True when it is actually the same location, false when different,
                // null when there isn't enough elements to compare.
                actual = lastDraw?.location?.isSame
            )
        val letterCorrectness =
            NBackScore.getCorrectness(
                answer = letterAnswer,
                // True when it is actually the same letter, false when different,
                // null when there isn't enough elements to compare.
                actual = lastDraw?.symbol?.isSame
            )
        score.updateScore(locationCorrectness)
        score.updateScore(letterCorrectness)
        return Pair(locationCorrectness, letterCorrectness)
    }
}
