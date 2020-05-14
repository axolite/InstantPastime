package ch.instantpastime

import android.content.Context
import android.util.Log
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.*

/**
 * Allows to get images of a place.
 */
class GooglePlaceApi(
    val NumImages: Int,
    val imageRequestReady: (PlacePhoto) -> Unit
) {

    private var placesClient: PlacesClient? = null

    var ImageCount: Int = 0
        private set

    var GoogleKey: String? = null

    fun init(context: Context) {
        context.getString(R.string.google_maps_key).let {
            GoogleKey = it
            Places.initialize(context, it)
        }
        placesClient = Places.createClient(context)
    }

    fun getPhotoAndDetail(placeInfos: ArrayList<PlaceInfo>) {

        for (i in 0..placeInfos.count() - 1) {
            val placeInfo = placeInfos[i]
            if (ImageCount >= NumImages) break
            val placeRequest = FetchPlaceRequest.builder(
                placeInfo.placeId,
                listOf(
                    Place.Field.PHOTO_METADATAS,
                    Place.Field.LAT_LNG
                )
            ).build()
            placesClient?.fetchPlace((placeRequest))
                ?.addOnSuccessListener { fetchPlaceResponse ->
                    onPlaceFetched(fetchPlaceResponse, placeInfo)
                }
        }
    }

    private fun onPlaceFetched(fetchPlaceResponse: FetchPlaceResponse, placeInfo: PlaceInfo) {

        val place = fetchPlaceResponse.place
        val photoMetadatas = place.photoMetadatas
        if (photoMetadatas != null) {
            for (photoMetadata in photoMetadatas) {
                if (ImageCount >= NumImages) break
                val photoRequest = FetchPhotoRequest.builder(photoMetadata).build()
                ImageCount += 1
                placesClient?.apply {
                    fetchPhoto(photoRequest).addOnSuccessListener { fetchPhotoResponse ->
                        onPhotoFetched(fetchPhotoResponse, placeInfo)
                    }
                }
            }
        } else {
            Log.d(javaClass.simpleName, "")
        }
    }

    private fun onPhotoFetched(fetchPhotoResponse: FetchPhotoResponse, placeInfo: PlaceInfo) {
        imageRequestReady(PlacePhoto(fetchPhotoResponse, placeInfo))
    }

}
