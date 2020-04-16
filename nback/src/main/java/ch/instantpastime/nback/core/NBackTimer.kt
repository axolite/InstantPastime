package ch.instantpastime.nback.core

import android.util.Log
import java.util.*
import kotlin.concurrent.schedule

class NBackTimer(milliseconds: Long, val callback: () -> Unit) {

    private var mTimer: Timer? = null

    var milliseconds: Long
        get() { return _intervalMillisec}
        set(value) {
            _intervalMillisec = if (value < 0) { 0 } else value
        }
    var _intervalMillisec: Long = 0

    val isStarted: Boolean
        get() = mTimer != null

    init {
        this.milliseconds = milliseconds
    }

    fun toggle() {
        if (mTimer == null) {
            startTimer()
        } else {
            stopTimer()
        }
    }

    fun startTimer() {
        // Get the existing timer or a new one.
        val timer = mTimer.let {
            if (it == null ) {
                // Build a new timer.
                val newTimer = Timer()
                mTimer = newTimer
                newTimer
            } else {
                // Purge the cancelled tasks of the existing timer.
                it.purge()
                it
            }
        }

        // Assign a new task to the timer.
        timer.schedule(milliseconds.toLong()) {
            // Cancel this TimerTask.
            cancel()
            callback()
        }
    }

    fun stopTimer() {
        val timer = mTimer
        if (timer != null) {
            mTimer = null
            try {
                timer.cancel()
            } catch (ex: Exception) {
                //NBackGame::stopTimer::name.name
                Log.d(javaClass.simpleName, "stopTimer" , ex)
            }
        }
    }

}
