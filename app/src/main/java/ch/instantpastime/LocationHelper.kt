package ch.instantpastime

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

const val MY_PERMISSION_FINE_LOCATION = 101

/**
 * Helps to check and ask for location permission.
 */
class LocationHelper {

    fun getLocation(activity: Activity, processLocation: (Location?) -> Unit) {
        try {
            val context: Context = activity
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                processPermissionStatus(PermissionStatus.Accepted, context, processLocation)
            } else {
                //request permissions
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    activity.requestPermissions(
                        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                        MY_PERMISSION_FINE_LOCATION
                    )
                }
            }
        } catch (ex: Exception) {
        }
    }

    fun getLocation(fragment: Fragment, processLocation: (Location?) -> Unit) {
        try {
            val context: Context? = fragment.context
            if (context != null) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    )
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    processPermissionStatus(PermissionStatus.Accepted, context, processLocation)
                } else {
                    //request permissions
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        fragment.requestPermissions(
                            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                            MY_PERMISSION_FINE_LOCATION
                        )
                    }
                }
            }
        } catch (ex: Exception) {
        }
    }

    fun processPermissionStatus(
        permissionStatus: PermissionStatus, context: Context,
        processLocation: (Location?) -> Unit
    ) {
        when (permissionStatus) {
            PermissionStatus.Accepted -> {
                Toast.makeText(context, "Location accepted", Toast.LENGTH_SHORT).show()
                requestLocation(context, processLocation)
            }
            PermissionStatus.AlwaysRefused -> {
                Toast.makeText(context, "Location always refused", Toast.LENGTH_SHORT).show()
            }
            PermissionStatus.RefusedOnce -> {
                Toast.makeText(context, "Location refused once", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(context, "Location not accepted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun requestLocation(context: Context, processLocation: (Location?) -> Unit) {
        val locationClient: FusedLocationProviderClient? =
            LocationServices.getFusedLocationProviderClient(context)
        locationClient?.lastLocation?.addOnSuccessListener {
            processLocation(it)
        }
    }

    companion object {

        /**
         * True if location is authorized.
         */
        fun canUseLocation(context: Context): Boolean {
            return ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }

        /**
         * True if the user wants to use location,
         * as recorded in the shared preference.
         */
        fun wantUseLocation(context: Context, defValue: Boolean): Boolean {
            return PrefManager.getLocationPref(context, defValue)
        }

        /**
         * True if the user should be asked whether they want to use location.
         */
        fun needLocationActivity(context: Context): Boolean {
            return wantUseLocation(context, defValue = true) && !canUseLocation(context)
        }

        /**
         * Starts an activity @see LocationActivity to ask the user for location.
         */
        fun startLocationActivity(context: Context, nbImages: Int) {
            val intent = Intent(context, LocationActivity::class.java).apply {
                putExtra(LocationActivity.NB_IMAGES_ARG, nbImages)
            }
            context.startActivity(intent)
        }
    }

}
