package ch.instantpastime.nback.core

interface INBackBoard {

    /**
     * Performs actions when entering the @see NBackPlay.State.Blank state.
     */
    fun onEnterBlank()

    /**
     * Performs actions when entering the @see NBackPlay.State.Drawing state.
     */
    fun onEnterDrawing()

    /**
     * Performs actions when entering the @see NBackPlay.State.Waiting state.
     */
    fun onEnterWaiting(last: NBackTrial?, next: NBackTrial)

    /**
     * Performs actions when entering the @see NBackPlay.State.Correcting state.
     */
    fun onEnterCorrecting()

    /**
     * Performs actions when entering the @see NBackPlay.State.Finished state.
     */
    fun onEnterFinished()
}
