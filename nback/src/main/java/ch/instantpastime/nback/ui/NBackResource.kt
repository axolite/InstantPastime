package ch.instantpastime.nback.ui

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.util.Log
import android.view.View
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import ch.instantpastime.nback.R
import ch.instantpastime.nback.core.NBackTrial

object NBackResource {

    const val SoundFolderName = "letters"

    fun openAsset(context: Context, fileName: String): AssetFileDescriptor? {
        return try {
            context.assets.openFd(fileName)
        } catch (ex: Exception) {
            Log.d(javaClass.simpleName, "Error loading sound asset '$fileName'", ex)
            null
        }
    }

    /**
     * Gets the cell in the n-back grid that corresponds to the given index.
     */
    fun Fragment.getSquare(index: Int): View? {
        return when (index) {
            0 -> view?.findViewById(R.id.case0)
            1 -> view?.findViewById(R.id.case1)
            2 -> view?.findViewById(R.id.case2)
            3 -> view?.findViewById(R.id.case3)
            4 -> view?.findViewById(R.id.case4)
            5 -> view?.findViewById(R.id.case5)
            6 -> view?.findViewById(R.id.case6)
            7 -> view?.findViewById(R.id.case7)
            8 -> view?.findViewById(R.id.case8)
            else -> null
        }
    }

    fun Fragment.getSquare(trial: NBackTrial?): View? {
        return if (trial != null) {
            getSquare(trial.location.index)
        } else {
            null
        }
    }

    /**
     * Gets the ID of the vector image that corresponds to the given letter.
     */
    @DrawableRes
    fun getLetterDrawableId(letter: Char): Int {
        return when (letter) {
            'c' -> R.drawable.ic_letter_c
            'h' -> R.drawable.ic_letter_h
            'k' -> R.drawable.ic_letter_k
            'l' -> R.drawable.ic_letter_l
            'q' -> R.drawable.ic_letter_q
            'r' -> R.drawable.ic_letter_r
            's' -> R.drawable.ic_letter_s
            't' -> R.drawable.ic_letter_t
            else -> 0
        }
    }

    /**
     * Gets the ID of the N-back location thumbnail
     * that corresponds to the given index.
     */
    @DrawableRes
    fun getMiniLocationId(index: Int): Int {
        return when (index) {
            0 -> R.drawable.ic_nback_case0
            1 -> R.drawable.ic_nback_case1
            2 -> R.drawable.ic_nback_case2
            3 -> R.drawable.ic_nback_case3
            4 -> R.drawable.ic_nback_case4
            5 -> R.drawable.ic_nback_case5
            6 -> R.drawable.ic_nback_case6
            7 -> R.drawable.ic_nback_case7
            8 -> R.drawable.ic_nback_case8
            else -> 0
        }
    }

    /**
     * Gets the ID of the N-back letter thumbnail
     * that corresponds to the given index.
     */
    @DrawableRes
    fun getMiniLetterId(index: Int): Int {
        return when (index) {
            0 -> R.drawable.ic_letter_c
            1 -> R.drawable.ic_letter_h
            2 -> R.drawable.ic_letter_k
            3 -> R.drawable.ic_letter_l
            4 -> R.drawable.ic_letter_q
            5 -> R.drawable.ic_letter_r
            6 -> R.drawable.ic_letter_s
            7 -> R.drawable.ic_letter_t
            else -> 0
        }
    }
}
