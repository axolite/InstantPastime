package ch.instantpastime

import android.app.Dialog
import android.content.Context
import android.view.Window
import android.view.WindowManager
import android.widget.Button

object CreditDialogHelper {

    fun showCredits(context: Context) {

        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.layout_credits)

        dialog.findViewById<Button>(R.id.credits_close_button).setOnClickListener {
            dialog.dismiss()
        }

        val lp = WindowManager.LayoutParams()
        val dialogWindow = dialog.window
        if (dialogWindow != null) {
            lp.copyFrom(dialogWindow.getAttributes())
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.MATCH_PARENT
            dialog.show()
            dialogWindow.setAttributes(lp)
        }
    }

}
