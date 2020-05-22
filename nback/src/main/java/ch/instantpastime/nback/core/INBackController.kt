package ch.instantpastime.nback.core

interface INBackController {
    fun onNextTrial(last: NBackTrial?, next: NBackTrial)
    fun onCorrectResult(locationCorrectness: NBackScore.Correctness?,
                        letterCorrectness: NBackScore.Correctness?)
}
