package ch.instantpastime.nback.core

interface INBackBoard {
    fun onEnterBlank()
    fun onEnterDrawing()
    fun onEnterWaiting(last: NBackTrial?, next: NBackTrial)
    fun onEnterCorrecting()
    fun onEnterFinished()
}
