package ch.instantpastime.memory

import androidx.appcompat.app.AppCompatActivity

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import ch.instantpastime.memory.MemoryActivity.Companion.mGoogleAPI
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

const val MY_PERMISSION_FINE_LOCATION = 101


object GPS_localistation : AppCompatActivity ()  {
    lateinit var myFusedLocationClient: FusedLocationProviderClient
    private var locationRequest: LocationRequest? =null
    private var currentGPSLocation: LatLng?=null


    fun get_localitation(mActivity: AppCompatActivity){
        myFusedLocationClient = LocationServices.getFusedLocationProviderClient(mActivity)
        locationRequest = LocationRequest()

        if (ActivityCompat.checkSelfPermission(mActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            myFusedLocationClient.lastLocation.addOnSuccessListener {
                currentGPSLocation = LatLng(it.latitude, it.longitude)
                val mylocation: String = it.latitude.toString() + ", " + it.longitude.toString()
                mGoogleAPI.requestImages(mylocation) //"46.136883, 6.132194")

            }
        }
        else{
            //request permissions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSION_FINE_LOCATION)
            }
        }


    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSION_FINE_LOCATION ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    GPS_localistation.locationRequest = LocationRequest()

                } else {
                    Toast.makeText(applicationContext, "This app requires location permissions to be granted", Toast.LENGTH_SHORT).show()
                    finish()
                }
        }

}


}