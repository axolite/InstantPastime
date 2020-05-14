package ch.instantpastime

import org.json.JSONObject

/**
 * Contains information about a place.
 * Typically a response from Google Maps API.
 */
data class PlaceInfo(val placeId: String, val placeDesc: String, val placeLoc: JSONObject)
