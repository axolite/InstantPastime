package ch.instantpastime

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint

object BitmapHelper {

    /**
     * Returns a scaled copy of a bitmap with the given parameters.
     */
    fun scaleBitmap(bitmap: Bitmap, wantedWidth: Int, wantedHeight: Int): Bitmap? {
        val output = Bitmap.createBitmap(wantedWidth, wantedHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val m = Matrix()
        m.setScale(
            wantedWidth.toFloat() / bitmap.width,
            wantedHeight.toFloat() / bitmap.height
        )
        canvas.drawBitmap(bitmap, m, Paint())
        return output
    }

}
