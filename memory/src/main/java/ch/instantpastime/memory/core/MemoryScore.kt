package ch.instantpastime.memory.core

import android.content.Context
import ch.instantpastime.memory.MemoryActivity
import ch.instantpastime.memory.MemoryActivity.Companion.memorySettings

class MemoryScore() {

    val  max_score : Int = 1000
    var num_mathches : Int
    var num_trials : Int

    init{
        num_mathches =0
        num_trials = 0
    }

    fun resetScore() {
        num_mathches =0
        num_trials = 0
    }

    fun totalScore():Int{
        return num_mathches *(max_score / memorySettings.num_images) - num_trials *((max_score / memorySettings.num_images)/ 10)
    }
}
