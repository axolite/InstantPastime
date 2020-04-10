package ch.instantpastime.nback.core

import androidx.collection.CircularArray
import kotlin.random.Random

class NBackGame {

    private val _randomLocation: NBackDraw
    private val _randomLetter: NBackDraw
    val _letterCount: Int
    val _level: Int

    constructor(nbLetters: Int, nBackLevel: Int) {
        _letterCount = if (nbLetters >= 0) nbLetters else 0
        _level = if (nBackLevel > 0) nBackLevel else 1
        _randomLocation = NBackDraw(0 until 9, _level)
        _randomLetter = NBackDraw(0 until _letterCount, _level)
    }

    fun getNextIndex(): Pair<Int, Boolean> {
        return _randomLocation.getNextIndex()
    }

    fun getNextLetterIndex(): Pair<Int, Boolean> {
        return _randomLetter.getNextIndex()
    }

}
