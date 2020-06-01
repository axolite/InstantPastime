package ch.instantpastime

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView

object InstallDialogHelper {

    fun showDialog(context: Context, appName: String) {

        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.layout_install)

        dialog.findViewById<Button>(R.id.install_close_button).setOnClickListener {
            dialog.dismiss()
        }

        dialog.findViewById<TextView>(R.id.install_app_title).text = appName

        val lp = WindowManager.LayoutParams()
        val dialogWindow = dialog.window
        if (dialogWindow != null) {
            lp.copyFrom(dialogWindow.getAttributes())
//            lp.width = WindowManager.LayoutParams.MATCH_PARENT
//            lp.height = WindowManager.LayoutParams.MATCH_PARENT
            dialog.show()
            dialogWindow.setAttributes(lp)
        }
    }

}
