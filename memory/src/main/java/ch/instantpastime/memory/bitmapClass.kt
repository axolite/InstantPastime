package ch.instantpastime.memory

import android.graphics.Bitmap
import org.json.JSONObject

open class bitmapClass(image: Bitmap, desc:String, loc: JSONObject){
    var img_reduce: Bitmap? = null
    var img_original: Bitmap? = null
    var img_desc: String? = null
    var img_loc: JSONObject? = null


    init{
        img_reduce=scaleBitmap(image,200,200)
        img_original=image
        img_desc=desc
        img_loc=loc
    }

}