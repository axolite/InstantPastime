package ch.instantpastime.memory.ui

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import ch.instantpastime.memory.R
import java.nio.file.Path
import java.nio.file.Paths

object MemoryResource {

    const val SoundFolderName = "sounds"
    const val CardImageFolderName = "cards"

    fun openAsset(context: Context, fileName: String): AssetFileDescriptor? {
        return try {
            context.assets.openFd(fileName)
        } catch (ex: Exception) {
            Log.d(javaClass.simpleName, "Error loading sound asset '$fileName'", ex)
            null
        }
    }

    fun getStockCardImage(context: Context, index: Int): Bitmap? {
        val fileName = getStockImageName(index)
        if (fileName != null) {
            val filePath: Path? = Paths.get(CardImageFolderName, fileName)
            if (filePath != null) {
                val stream = context.assets?.open(filePath.toString())
                if (stream != null) {
                    val bitmap = BitmapFactory.decodeStream(stream)
                    return bitmap
                }
            }
        }
        return null
    }





    /**
     * Gets the name of the file (including extension) of the stock image
     * that corresponds to the given index.
     */
    fun getStockImageName(index: Int): String? {
        return when (index) {
            0 -> "chillon.jpg"
            1 -> "edelweiss.jpg"
            2 -> "emmental.jpg"
            3 -> "lucerne.jpg"
            4 -> "matterhorn.jpg"
            5 -> "swan.jpg"
            6 -> "water-jet-geneva.jpg"
            7 -> "zurich.jpg"
            8 -> "almuneda.jpg"
            9 -> "big-ben.jpg"
            10 -> "bison.jpg"
            11 -> "brandenburg.jpg"
            12-> "cabine.jpg"
            13 -> "cern.jpg"
            14 -> "coeur.jpg"
            15 -> "colibri.jpg"
            16 -> "drapeau.jpg"
            17 -> "eiffel-tower.jpg"
            18 -> "fishing.jpg"
            19 -> "fondue.jpg"
            20 -> "geneve.jpg"
            21 -> "genevelac.jpg"
            22 -> "grand-canyon-horseshoe.jpg"
            23 -> "great-wall-china.jpg"
            24 -> "gruyere.jpg"
            25 -> "lavaux.jpg"
            26 -> "maison.jpg"
            27 -> "montre.jpg"
            28 -> "monument-valley.jpg"
            29 -> "raclette.jpg"
            30 -> "statue-liberty.jpg"
            31 -> "sunflower.jpg"
            32 -> "swan-family.jpg"
            33 -> "torii.jpg"
            34 -> "train.jpg"
            35 -> "vaches.jpg"
            36 -> "virus.jpg"


            else -> null
        }
    }
}
