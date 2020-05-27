package ch.instantpastime

import android.app.Dialog
import android.content.Context
import android.view.Window
import android.view.WindowManager
import android.widget.TextView


class DialogScore() {
     fun showDialog(context: Context, score: String) {


        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.activity_score)

        val score_text = dialog.findViewById(R.id.score_final_text) as TextView
       score_text.text = score

//        val okBtn = dialog .findViewById(R.id.button_popup) as Button
//        okBtn.setOnClickListener {
//            dialog .dismiss()
//        }
         val lp = WindowManager.LayoutParams()
         lp.copyFrom(dialog.getWindow()!!.getAttributes())
         lp.width = WindowManager.LayoutParams.MATCH_PARENT
         lp.height = WindowManager.LayoutParams.MATCH_PARENT
         dialog.show()
        dialog.show()
         dialog.getWindow()!!.setAttributes(lp)


     }
}