

package ch.instantpastime.memory

import android.graphics.Bitmap
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import org.json.JSONArray
import org.json.JSONObject
import java.sql.Timestamp
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import ch.instantpastime.memory.MemoryActivity.Companion.num_images


class GoogleAPI(mActivity:MemoryActivity)  {

    /********************************************************************************
     * Google Places
     ********************************************************************************/
    lateinit var placesClient: PlacesClient
    var mActivity: MemoryActivity =mActivity

    lateinit var GoogleKey:String
    init {
        GoogleKey =mActivity.getString(R.string.google_maps_key)
        Places.initialize(mActivity, GoogleKey!!)
        placesClient= Places.createClient(mActivity)
    }
    var num_img_count = 0
    var i = 0

    public fun getPhotoAndDetail(placeIds:ArrayList<String>,placesDesc:ArrayList<String>,placesLoc:ArrayList<JSONObject>){

        for (i in 0..placeIds.count()-1){
            var placeId=placeIds[i]
            var placeDesc=placesDesc[i]
            var placesLoc=placesLoc[i]

            if (num_img_count > num_images) break
            val placeRequest= FetchPlaceRequest.builder(placeId!!,
                Arrays.asList(Place.Field.PHOTO_METADATAS,
                    Place.Field.LAT_LNG)).build()
            placesClient.fetchPlace((placeRequest))
                .addOnSuccessListener { fetchPlaceResponse ->
                    val place = fetchPlaceResponse.place
                    val bitmap: Bitmap?=null


                    if (place.photoMetadatas != null) {
                        for (photo in place.photoMetadatas!!) {
                            if (num_img_count > num_images) break
                            val photoMetaData = photo
                            val photoRequest = FetchPhotoRequest.builder(photoMetaData).build()
                            num_img_count+=1
                            placesClient.fetchPhoto(photoRequest)
                                .addOnSuccessListener { fetchPhotoResponse ->
                                    val bitmap = fetchPhotoResponse.bitmap

                                    mActivity.imageRequestedReady(bitmap!!,placeDesc,placesLoc)
                                }
                        }
                    }
                    else{
                        //mActivity.imageRequestedReady(isOrigin)
                    }



                }
        }
    }

    fun requestImages(location:String){

        var mylocation = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" +
                location +
                "&radius=3500&key=" + GoogleKey

        val httpRequest= HttpRequest()
        httpRequest.setActivityContext(this )
        httpRequest.execute(mylocation)

    }


    fun getResults(json:String){


        val jsonResponse = JSONObject(json)
        //val path: MutableList<List<LatLng>> = ArrayList()


        val results = jsonResponse.getJSONArray("results")
        var placesId = ArrayList<String>()
        var placesDesc = ArrayList<String>()
        var placesLoc = ArrayList<JSONObject>()


        for (i in 0 until results.length()) {
            placesId.add(results.getJSONObject(i).getString("place_id"))
            placesDesc.add(results.getJSONObject(i).getString("name"))
            placesLoc.add(results.getJSONObject(i).getJSONObject("geometry").getJSONObject("location"))

        }

        getPhotoAndDetail(placesId,placesDesc,placesLoc)



        //mActivity.PlaceRequestedReady(places[4])


    }

}


