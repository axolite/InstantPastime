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

    companion object {
        // Shared preferences file name
        const val PREF_NAME = "start_guide"
        const val IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch"
        const val PRIVATE_MODE = 0
        const val LOCATION_CAT_KEY = "catLocation"
        const val USE_LOCATION_PREF_KEY = "useLocation"

        fun SharedPreferences.setBoolean(key: String, value: Boolean) {
            edit()?.run {
                putBoolean(key, value)
                apply()
            }
        }

        fun SharedPreferences.setFloat(key: String, value: Float) {
            edit()?.run {
                putFloat(key, value)
                apply()
            }
        }

        fun SharedPreferences.setInt(key: String, value: Int) {
            edit()?.run {
                putInt(key, value)
                apply()
            }
        }

        fun getLocationPref(context: Context, defValue: Boolean): Boolean {
            return context.getSharedPreferences(LOCATION_CAT_KEY, PRIVATE_MODE)?.getBoolean(
                USE_LOCATION_PREF_KEY, defValue
            ) ?: defValue
        }

        fun saveLocationPref(context: Context, value: Boolean) {
            context.getSharedPreferences(LOCATION_CAT_KEY, PRIVATE_MODE)?.setBoolean(
                USE_LOCATION_PREF_KEY, value
            )
        }

        fun getFirstTimeLaunch(context: Context, defValue: Boolean): Boolean {
            return context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)?.getBoolean(
                IS_FIRST_TIME_LAUNCH, defValue
            ) ?: defValue
        }

        fun setFirstTime(context: Context, value: Boolean) {
            context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)?.setBoolean(
                IS_FIRST_TIME_LAUNCH, value
            )
        }
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
