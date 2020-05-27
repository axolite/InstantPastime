package ch.instantpastime.memory

class MemoryScore {

    val  max_score : Int = 1000
    var num_mathches : Int = 0
    var num_trials : Int = 0

    fun resetScore() {
        num_mathches =0
        num_trials = 0
    }

    fun totalScore():Int{
        return num_mathches *(max_score / MemoryActivity.num_images) - num_trials *((max_score / MemoryActivity.num_images)/ 10)
    }
}
