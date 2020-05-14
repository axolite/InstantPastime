package ch.instantpastime

import android.os.AsyncTask
import android.util.Log
import java.net.URL

/**
 * Allows to make an HTTP request as an async task.
 */
class HttpRequestTask(val processResults: (String) -> Unit) : AsyncTask<String, Int, String>() {

    val connectTimeout: Int = 700

    override fun doInBackground(vararg params: String?): String? {
        return try {
            params.first().let {
                val url = URL(it)
                val urlConnect = url.openConnection()
                urlConnect.connectTimeout = connectTimeout
                publishProgress(100)
                urlConnect.inputStream.bufferedReader().readText()
            }
        } catch (e: Exception) {
            null
        }
    }

    override fun onProgressUpdate(vararg values: Int?) {
        for (it in values) {
            Log.d("onProgressUpdate", it.toString())
        }
    }

    override fun onPostExecute(result: String?) {
        Log.d(javaClass.simpleName, "onPostExecute")
        when {
            result != null -> {
                processResults(result)
            }
            else -> {
                // some error
                Log.e(javaClass.simpleName, "Http result is null.")
            }
        }
    }
}
