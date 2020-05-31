package ch.instantpastime.nback.ui

import android.content.Context
import android.content.Intent
import ch.instantpastime.StartActivity
import ch.instantpastime.nback.R

object NBackTutoHelper {

    val tuto_slides = intArrayOf(
    ch.instantpastime.R.layout.activity_start_content01,
    ch.instantpastime.R.layout.activity_start_content01,
    ch.instantpastime.R.layout.activity_start_content01
    )
    /* ******* Images ************************************************ */
    val tuto_images = intArrayOf(
    R.drawable.tutoslide01,
    R.drawable.tutoslide02,
    R.drawable.tutoslide03

    )
    /* ******* Texts ************************************************* */
    val tuto_texts = intArrayOf(
    R.string.nback_tuto_text1,
    R.string.nback_tuto_text2,
    R.string.nback_tuto_text3
    )

    fun startTutoActivity(context: Context) {
        val intent = Intent(context, StartActivity::class.java)
        intent.putExtra( "tuto_slides",  tuto_slides )
        intent.putExtra("tuto_images", tuto_images )
        intent.putExtra("tuto_texts", tuto_texts )
        context.startActivity(intent)
    }
}
