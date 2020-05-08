package ch.instantpastime.memory

import androidx.appcompat.app.AppCompatActivity

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.fragment.app.Fragment
import ch.instantpastime.memory.fragments.MemoryFragment.Companion.mGoogleAPI
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

const val MY_PERMISSION_FINE_LOCATION = 101


class GPS_localistation (val mActivity: Fragment)  {
    var myFusedLocationClient: FusedLocationProviderClient? = null
    var locationRequest: LocationRequest? =null
    private var currentGPSLocation: LatLng?=null


    fun get_localitation(){
        try
        {
            myFusedLocationClient = LocationServices.getFusedLocationProviderClient(mActivity.getContext()!!)
            locationRequest = LocationRequest()

            val context = mActivity.getContext()
            if (context != null && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                getPlaces()
            }
            else{
                //request permissions
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mActivity.requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSION_FINE_LOCATION)
                }
            }

        }
        catch (ex: Exception) {
            Log.d(javaClass.simpleName, "${ex.javaClass.simpleName} ${ex.message}")
            var d = 0
            d++
        }

    }

    fun getPlaces() {
        myFusedLocationClient?.lastLocation?.addOnSuccessListener {
            currentGPSLocation = LatLng(it.latitude, it.longitude)
            val mylocation: String = it.latitude.toString() + ", " + it.longitude.toString()
            mGoogleAPI.requestImages(mylocation) //"46.136883, 6.132194")
        }
    }

}
