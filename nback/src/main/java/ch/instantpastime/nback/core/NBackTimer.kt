package ch.instantpastime.nback.core

import android.util.Log
import java.util.*
import kotlin.concurrent.schedule

class NBackTimer(val intervalMillisec: UShort, val callback: () -> Unit) {

    private var mTimer: Timer? = null
    private var mTimerTask: TimerTask? = null

    val isStarted: Boolean
        get() = mTimer != null

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
        val timerTask = timer.schedule(intervalMillisec.toLong()) {
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
                Log.d(NBackGame::class.java.simpleName, "stopTimer" , ex)
            }
        }
    }

}