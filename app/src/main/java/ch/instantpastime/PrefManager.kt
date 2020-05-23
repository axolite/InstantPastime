/* Inspired by and adapted from
https://www.androidhive.info/2016/05/android-build-intro-slider-app/
 */

package ch.instantpastime

/* Import ******************************************************** */
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
/* *************************************************************** */

/* PrefManager *************************************************** */
/* Used for the first-time launch app detection ****************** */

class PrefManager(var _context : Context) {
    lateinit var pref : SharedPreferences
    lateinit var editor : Editor
    private var PRIVATE_MODE = 0

    companion object {
        // Shared preferences file name
        private const val PREF_NAME = "start_guide"
        private const val IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch"
    }

    fun prefManager(context : Context?) {
        _context = context!!
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
    }

    fun setFirstTimeLaunch() {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, false)
        editor.commit()
    }

    fun isFirstTimeLaunch() : Boolean {
        prefManager(_context)
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true)

    }
}