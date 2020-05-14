package ch.instantpastime

import android.content.Context
import android.location.Location
import org.json.JSONObject

/**
 * Allows to get places near a location.
 */
class GoogleMapApi {

    var GoogleKey: String? = null

    fun init(context: Context) {
        GoogleKey = context.getString(R.string.google_maps_key)
    }

    /**
     * Requests information on places near the given location.
     */
    fun requestNearbyPlaces(location: Location, processPlaces: (ArrayList<PlaceInfo>) -> Unit) {
        val url = buildPlaceUrl(location)
        val httpRequest = HttpRequestTask { processRequestResult(it, processPlaces) }
        httpRequest.execute(url)
    }

    fun processRequestResult(jsonText: String, processPlaces: (ArrayList<PlaceInfo>) -> Unit) {
        val jsonResponse = JSONObject(jsonText)
        val results = jsonResponse.getJSONArray("results")
        val placesInfo = ArrayList<PlaceInfo>()

        for (i in 0 until results.length()) {
            val placeId = results.getJSONObject(i).getString("place_id")
            val placeDesc = results.getJSONObject(i).getString("name")
            val placeLoc = results.getJSONObject(i).getJSONObject("geometry")
                .getJSONObject("location")
            placesInfo.add(PlaceInfo(placeId = placeId, placeDesc = placeDesc, placeLoc = placeLoc))
        }

        processPlaces(placesInfo)
    }

    fun buildPlaceUrl(location: Location): String {
        val radius = 3500
        val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                "location=${location.latitude},${location.longitude}" +
                "&radius=$radius&key=$GoogleKey"
        return url
    }

}
