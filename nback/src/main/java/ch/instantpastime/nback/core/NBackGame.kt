package ch.instantpastime.nback.core

import androidx.collection.CircularArray
import kotlin.random.Random

class NBackGame {

    companion object {
        const val INDEX_MIN: Int = 0
        const val INDEX_MAX: Int = 8
    }

    val _letterCount: Int
    val _level: Int

    constructor(nbLetters: Int, nBackLevel: Int) {
        _letterCount = if (nbLetters >= 0) nbLetters else 0
        _level = if (nBackLevel > 0) nBackLevel else 1
        _lastLocations = CircularArray(_level)
        _lastLetters = CircularArray(_level)
    }

    private val _lastLocations: CircularArray<Int>
    private val _lastLetters: CircularArray<Int>

    fun getNextIndex(): Pair<Int, Boolean> {
        val newIndex = Random.nextInt(from = INDEX_MIN, until = INDEX_MAX + 1)

        // Check if there are enough elements in the buffer.
        val isSame = if (_lastLocations.size() == _level) {
            val pastIndex = _lastLocations.popFirst()
            // Check if the past element was the same.
            pastIndex == newIndex
        } else {
            false
        }
        _lastLocations.addLast(newIndex)
        return Pair(newIndex, isSame)
    }

    fun getNextLetterIndex(): Pair<Int, Boolean> {
        val newIndex = Random.nextInt(from = 0, until = _letterCount)

        // Check if there are enough elements in the buffer.
        val isSame = if (_lastLetters.size() == _level) {
            val pastIndex = _lastLetters.popFirst()
            // Check if the past element was the same.
            pastIndex == newIndex
        } else {
            false
        }
        _lastLetters.addLast(newIndex)
        return Pair(newIndex, isSame)
    }

}