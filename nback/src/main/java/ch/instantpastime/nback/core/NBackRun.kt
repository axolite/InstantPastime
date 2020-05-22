package ch.instantpastime.nback.core

class NBackRun {

    companion object {
        const val MIN_LEVEL = 1
        const val MAX_LEVEL = 4
        const val DEFAULT_LEVEL = 2
        const val MIN_MILLISEC: Int = 1000
        const val MAX_MILLISEC: Int = 6000
        const val DEFAULT_MILLISEC: Int = 3000
    }

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

    fun getNextTrial(): NBackTrial {
        val nextLocation = getNextLocation()
        val nextSymbol = getNextSymbol()
        return NBackTrial(nextLocation, nextSymbol)
    }

    private fun getNextLocation(): NBackElement {
        return _randomLocation.getNextIndex()
    }

    private fun getNextSymbol(): NBackElement {
        return _randomLetter.getNextIndex()
    }

    fun reset() {
        _randomLocation.clear()
        _randomLetter.clear()
    }

}
