package ch.instantpastime.nback.core

import androidx.collection.CircularArray
import kotlin.random.*

class NBackDraw(val range: IntRange, val level: Int) {

    /**
     * Last indices drawn (tirés au sort).
     */
    private val lastIndices: CircularArray<Int> = CircularArray(level)

    var RandomnessPercentage: Int
        get() { return _randomnessPercentage }
        set(value) {
            _randomnessPercentage = if (0 < value) { 0 }
            else if (value > 100) { 100 }
            else { value }
        }
    private var _randomnessPercentage: Int = 100

    fun getNextIndex(): NBackElement {

        val (hasPastIndex, pastIndex) = tryPop()
        val newIndex = if (hasPastIndex) {
            if (useRandomness(RandomnessPercentage)) {
                getRandomIndex()
            } else {
                pastIndex
            }
        } else {
            getRandomIndex()
        }

        lastIndices.addLast(newIndex)
        return NBackElement(newIndex, newIndex == pastIndex)
    }

    private fun tryPop(): Pair<Boolean, Int> {
        return if (lastIndices.size() == level) {
            val index = lastIndices.popFirst()
            Pair(true, index)
        } else {
            Pair(false, 0)
        }
    }

    private fun useRandomness(randomnessPercentage: Int): Boolean {
        // Generate a random integer between 1 and 99 (until = 100 is an exclusive bound).
        return Random.nextInt(from = 1, until = 100).let {
            // Example:
            // randomnessPercentage == 0 => always false, because it > randomnessPercentage for all it.
            // randomnessPercentage == 100 => always true, because it < randomnessPercentage for all it.
            it < randomnessPercentage
        }
    }

    private fun getRandomIndex(): Int {
        return Random.nextInt(range)
    }
}
