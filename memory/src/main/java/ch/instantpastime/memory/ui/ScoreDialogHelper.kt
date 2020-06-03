package ch.instantpastime.memory.ui

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import ch.instantpastime.InstallDialogHelper
import ch.instantpastime.memory.MemoryActivity
import ch.instantpastime.memory.fragments.MemoryFragment
import ch.instantpastime.memory.fragments.MemoryFragment.Companion.mListener
import com.google.android.material.internal.ContextUtils.getActivity

object ScoreDialogHelper {

    fun showDialogScore(context: Context, score: String) {


        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(ch.instantpastime.R.layout.activity_score)

        val score_text = dialog.findViewById(ch.instantpastime.R.id.score_final_text) as TextView
        score_text.text = score

        val PlayAgainBtn = dialog .findViewById(ch.instantpastime.R.id.score_replay_button) as Button
        PlayAgainBtn.setOnClickListener {

            mListener!!.reStartGame()

            dialog .dismiss()
        }
        val InstallBtn = dialog .findViewById(ch.instantpastime.R.id.score_install_button) as Button
        InstallBtn.setOnClickListener {
            InstallDialogHelper.showDialog(context, "Memory")
        }

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.getWindow()!!.getAttributes())
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog.show()
        dialog.getWindow()!!.setAttributes(lp)


    }

}
