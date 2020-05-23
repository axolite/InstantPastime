package ch.instantpastime.nback.core

class NBackPlay(val board: INBackBoard) {
    sealed class Transition {
        object Draw : Transition()
        data class Drawn(val last: NBackTrial?, val next: NBackTrial) : Transition()
        object Timeout : Transition()
        object TotalReached : Transition()
        object Reset : Transition()
    }
    sealed class State {
        object Blank : State()
        object Drawing : State()
        data class Waiting(val last: NBackTrial?, val next: NBackTrial) : State()
        object Correcting : State()
        object Finished : State()
    }
//    private enum class Transition {
//        StartDraw, Drawn, Timeout, TotalReached, Reset,
//    }
//    private enum class State {
//        Blank, Drawing, Waiting, Correcting, Finished
//    }

    private var state: State = State.Blank


    fun raiseDraw() {
        processTransition(Transition.Draw)
    }

    fun raiseTotalReached() {
        processTransition(Transition.TotalReached)
    }

    fun raiseTick() {
        processTransition(Transition.Timeout)
    }

    fun raiseDrawn(last: NBackTrial?, next: NBackTrial) {
        processTransition(Transition.Drawn(last, next))
    }

    fun raiseReset() {
        processTransition(Transition.Reset)
    }

    private fun processTransition(transition: Transition) {
        val oldState = state
        val newState = switchState(oldState, transition)
        state = newState
        switchAction(oldState = oldState, newState = newState)
    }

    private fun switchState(state: State, transition: Transition): State {
        return when (state) {
            State.Blank -> when (transition) {
                Transition.Draw -> State.Drawing
                Transition.Reset -> State.Blank
                else -> state
            }
            State.Drawing -> when (transition) {
                is Transition.Drawn -> State.Waiting(
                    last = transition.last,
                    next = transition.next)
                Transition.Reset -> State.Blank
                else -> state
            }
            is State.Waiting -> when (transition) {
                Transition.Timeout -> State.Correcting
                Transition.Draw -> State.Drawing
                Transition.Reset -> State.Blank
                else -> state
            }
            State.Correcting -> when (transition) {
                Transition.Draw -> State.Drawing
                Transition.TotalReached -> State.Finished
                Transition.Reset -> State.Blank
                else -> state
            }
            State.Finished -> when (transition) {
                Transition.Reset -> State.Blank
                else -> state
            }
        }
    }

    private fun switchAction(oldState: State, newState: State) {

        if (oldState == newState) {
            // Perform actions on state.
            when (oldState) {
                else -> {
                }
            }
        } else {
            // Perform actions on exit.
            when (oldState) {
                else -> {
                }
            }
            // Perform actions on entry.
            when (newState) {
                State.Blank -> board.onEnterBlank()
                State.Drawing -> board.onEnterDrawing()
                is State.Waiting -> board.onEnterWaiting(
                    last = newState.last,
                    next = newState.next)
                State.Correcting -> board.onEnterCorrecting()
                State.Finished -> board.onEnterFinished()
            }
        }
    }

}
