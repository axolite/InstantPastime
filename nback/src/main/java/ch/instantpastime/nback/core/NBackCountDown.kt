package ch.instantpastime.nback.core

import android.os.CountDownTimer
import android.util.Log

class NBackCountDown(totalMilliseconds: Int, val onTick: () -> Unit, val onFinish: () -> Unit) {

    companion object {
        const val INTERVAL_DIVIDER = 16
        const val stepMillisec: Int = 100
    }

    private var _timer: CountDownTimer? = null

    var totalMilliseconds: Int
        get() { return _totalMillisec }
        set(value) {
            if (value != _totalMillisec) {
                val newValue = if (value < NBackBoard.MIN_MILLISEC) { NBackBoard.MIN_MILLISEC }
                else if (value > NBackBoard.MAX_MILLISEC) { NBackBoard.MAX_MILLISEC }
                else { value }

                _totalMillisec = newValue
                resetTimer()
            }
        }
    private var _totalMillisec: Int = NBackBoard.DEFAULT_MILLISEC

    init {
        this.totalMilliseconds = totalMilliseconds
    }

    fun startTimer() {
        // Get the existing timer or a new one.
        val timer = _timer.let {
            if (it == null ) {
                // Build a new timer.
                val newTimer = buildTimer(totalMillisec = totalMilliseconds, stepMillisec = stepMillisec)
                _timer = newTimer
                newTimer
            } else {
                // Cancel the pending tasks of the existing timer.
                it.cancel()
                it
            }
        }

        // Assign a new task to the timer.
        timer.start()
    }

    fun stopTimer() {
        val timer = _timer
        if (timer != null) {
            _timer = null
            try {
                timer.cancel()
            } catch (ex: Exception) {
                Log.d(javaClass.simpleName, "stopTimer" , ex)
            }
        }
    }

    private fun buildTimer(totalMillisec: Int, stepMillisec: Int): CountDownTimer {
        return object : CountDownTimer(totalMillisec.toLong(), stepMillisec.toLong()) {
            override fun onFinish() {
                this@NBackCountDown.onFinish()
            }
            override fun onTick(millisUntilFinished: Long) {
                this@NBackCountDown.onTick()
            }
        }
    }

    private fun resetTimer() {
        val timer = _timer
        if (timer != null) {
            _timer = null
            timer.cancel()
        }
        _timer = buildTimer(totalMillisec = totalMilliseconds, stepMillisec = stepMillisec)
    }
}
