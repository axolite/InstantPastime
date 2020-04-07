package ch.instantpastime.nback.core

import androidx.collection.CircularArray
import kotlin.random.Random

class NBackGame {

    companion object {
        const val INDEX_MIN: Int = 0
        const val INDEX_MAX: Int = 8
    }

    val mLetterCount: Int
    val mLevel: Int

    constructor(nbLetters: Int, nBackLevel: Int) {
        mLetterCount = if (nbLetters >= 0) nbLetters else 0
        mLevel = if (nBackLevel > 0) nBackLevel else 1
        mLastLocations = CircularArray(mLevel)
        mLastLetters = CircularArray(mLevel)
    }

    private val mLastLocations: CircularArray<Int>
    private val mLastLetters: CircularArray<Int>

    fun getNextIndex(): Pair<Int, Boolean> {
        val newIndex = Random.nextInt(from = INDEX_MIN, until = INDEX_MAX + 1)

        // Check if there are enough elements in the buffer.
        val isSame = if (mLastLocations.size() == mLevel) {
            val pastIndex = mLastLocations.popFirst()
            // Check if the past element was the same.
            pastIndex == newIndex
        } else {
            false
        }
        mLastLocations.addLast(newIndex)
        return Pair(newIndex, isSame)
    }

    fun getNextLetterIndex(): Pair<Int, Boolean> {
        val newIndex = Random.nextInt(from = 0, until = mLetterCount)

        // Check if there are enough elements in the buffer.
        val isSame = if (mLastLetters.size() == mLevel) {
            val pastIndex = mLastLetters.popFirst()
            // Check if the past element was the same.
            pastIndex == newIndex
        } else {
            false
        }
        mLastLetters.addLast(newIndex)
        return Pair(newIndex, isSame)
    }

}