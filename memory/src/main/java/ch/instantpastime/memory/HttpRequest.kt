package ch.instantpastime.memory

import android.os.AsyncTask
import android.util.Log
import java.net.HttpURLConnection
import java.net.URL

/*********************************************************************
 * HttpRequest Class:
 *  This is an Async Task for making a http request and get the result
 *  The get result is passed to the GoogleAPI by the function getResults(result)
 *  In order to have access to getResults from this thread the calling Activity has
 *  to set its activity to the object created on this class with the function setActivityContext
 */
class HttpRequest : AsyncTask<String, Int, String>() {

    var mActivity: GoogleAPI? =null


    fun setActivityContext(activity:GoogleAPI){
        mActivity=activity
    }

    override fun doInBackground(vararg params: String): String? {
        return try {
            params.first().let {
                val url = URL(it)
                val urlConnect = url.openConnection() as HttpURLConnection
                urlConnect.connectTimeout = 700
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
        Log.d("MainActivity", "onPostExecute")
        when {
            result != null -> {mActivity?.getResults(result)
                 }
            else -> {
                // some error
            }
        }
    }

}