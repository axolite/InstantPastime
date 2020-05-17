package ch.instantpastime.nback.core

class NBackGame {

    companion object {
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
    }

    enum class Correctness {
        CORRECT_SAME,
        CORRECT_DIFFERENT,
        WRONG_ACTUALLY_SAME,
        WRONG_ACTUALLY_DIFFERENT,
    }

    var CorrectCount: Int = 0
    var WrongCount: Int = 0
    var TotalCount: Int = 0

    fun updateScore(answer: Boolean, actual: Boolean?): Correctness? {
        return if (actual != null) {
            updateScore(answer, actual)
        } else {
            TotalCount++
            null
        }
    }

    private fun updateScore(answer: Boolean, actual: Boolean): Correctness {
        val correctness = getCorrectness(answer = answer, actual = actual)
        when (correctness) {
            Correctness.CORRECT_DIFFERENT -> {
                CorrectCount++
            }
            Correctness.CORRECT_SAME -> {
                CorrectCount++
            }
            Correctness.WRONG_ACTUALLY_DIFFERENT -> {
                WrongCount++
            }
            Correctness.WRONG_ACTUALLY_SAME -> {
                WrongCount++
            }
        }
        TotalCount++
        return correctness
    }

    fun reset() {
        CorrectCount = 0
        WrongCount = 0
        TotalCount = 0
    }
}
