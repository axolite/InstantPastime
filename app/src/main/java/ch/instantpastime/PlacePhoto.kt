package ch.instantpastime

import android.graphics.Bitmap
import com.google.android.libraries.places.api.net.FetchPhotoResponse

/**
 * Contains an image of a place.
 * Typically a response from Google Place API.
 */
data class PlacePhoto(val response: FetchPhotoResponse, val info: PlaceInfo) {
    val bitmap: Bitmap
        get() = response.bitmap
}
