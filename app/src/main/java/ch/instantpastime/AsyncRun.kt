package ch.instantpastime

import android.os.AsyncTask

/**
 * Allows to perform async tasks like this:
 * AsyncRun { doSomething() }
 * Source: https://stackoverflow.com/a/51006260
 */
class AsyncRun(val handler: () -> Unit) : AsyncTask<Void, Void, Unit>() {
    init {
        execute()
    }

    override fun doInBackground(vararg params: Void?) {
        handler()
    }
}
