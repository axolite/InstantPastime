package ch.instantpastime

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
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

        dialog.findViewById<View>(R.id.credits_brain_workshop_panel).setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("http://brainworkshop.sourceforge.net/"))
            context.startActivity(browserIntent)
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
