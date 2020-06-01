package ch.instantpastime.nback.core

class NBackScore {

    companion object {

        const val SCORE_MAX = 1000

        fun getCorrectness(answer: Boolean, actual: Boolean?): Correctness? {
            return if (actual == null) {
                null
            } else {
                getCorrectness(answer = answer, actual = actual)
            }
        }

        fun getCorrectness(answer: Boolean, actual: Boolean): Correctness {
            return if (actual) {
                if (answer) {
                    // The user said "same" and it is: answer is correct.
                    Correctness.CORRECT_SAME
                } else {
                    // The user didn't say "same" but it is: answer is wrong.
                    Correctness.WRONG_ACTUALLY_SAME
                }
            } else {
                if (answer) {
                    // The user said "same" but it's not: answer is wrong.
                    Correctness.WRONG_ACTUALLY_DIFFERENT
                } else {
                    // The user didn't say "same" and it's not: answer is correct.
                    Correctness.CORRECT_DIFFERENT
                }
            }
        }

        fun isCorrect(correctness: Correctness?): Boolean? {
            return if (correctness == null) {
                null
            } else {
                isCorrect(correctness)
            }
        }

        fun isCorrect(correctness: Correctness): Boolean {
            return when (correctness) {
                Correctness.CORRECT_DIFFERENT -> true
                Correctness.CORRECT_SAME -> true
                Correctness.WRONG_ACTUALLY_DIFFERENT -> false
                Correctness.WRONG_ACTUALLY_SAME -> false
            }
        }

        fun allCorrect(correctnessList: Iterable<Correctness?>): Boolean? {
            for (correctness in correctnessList) {
                if (correctness == null) {
                    return null
                } else if (!NBackScore.isCorrect(correctness)) {
                    return false
                }
            }
            return true
        }
    }

    enum class Correctness {
        CORRECT_SAME,
        CORRECT_DIFFERENT,
        WRONG_ACTUALLY_SAME,
        WRONG_ACTUALLY_DIFFERENT,;

        fun isCorrect(): Boolean {
            return when (this) {
                CORRECT_SAME -> true
                CORRECT_DIFFERENT -> true
                else -> false
            }
        }
    }

    var scoreRecord: NBackScoreRecord = NBackScoreRecord(Total = 0, Correct = 0, Wrong = 0)
        private set

    val CorrectCount: Int
        get() = ((scoreRecord.Correct / 2.0) + 0.5).toInt()

    val WrongCount: Int
        get() = ((scoreRecord.Wrong / 2.0) + 0.5).toInt()

    val TotalCount: Int
        get() = ((scoreRecord.Total / 2.0) + 0.5).toInt()

    val percentCorrect: Int
        get() = scoreRecord.run {
            (SCORE_MAX * Correct) / Total
        }

    fun updateScore(correctness: Correctness?): NBackScoreRecord {
        return if (correctness != null) {
            calculateScore(scoreRecord, correctness).also {
                scoreRecord = it
            }
        } else {
            // Increment the total of the current score and return it.
            scoreRecord.run {
                copy(Total = Total + 1)
            }.also {
                scoreRecord = it
            }
        }
    }

    private fun calculateScore(
        oldScore: NBackScoreRecord,
        correctness: Correctness
    ): NBackScoreRecord {
        return when (correctness) {
            Correctness.CORRECT_DIFFERENT -> {
                oldScore.run {
                    copy(Total = Total + 1, Correct = Correct + 1)
                }
            }
            Correctness.CORRECT_SAME -> {
                oldScore.run {
                    copy(Total = Total + 1, Correct = Correct + 1)
                }
            }
            Correctness.WRONG_ACTUALLY_DIFFERENT -> {
                oldScore.run {
                    copy(Total = Total + 1, Wrong = Wrong + 1)
                }
            }
            Correctness.WRONG_ACTUALLY_SAME -> {
                oldScore.run {
                    copy(Total = Total + 1, Wrong = Wrong + 1)
                }
            }
        }
    }

    fun reset() {
        scoreRecord = NBackScoreRecord(Total = 0, Correct = 0, Wrong = 0)
    }
}
