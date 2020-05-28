package ch.instantpastime

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.CompoundButton
import android.widget.Switch

class GeneralPreferenceDialog : Dialog {

    val answered: (Boolean) -> Unit

    constructor(
        context: Context,
        answered: (Boolean) -> Unit
    ) : super(context, R.style.AppTheme) {
        this.answered = answered
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(true)
        setContentView(R.layout.dialog_general_preference)
        findViewById<Switch>(R.id.gen_contextual_images_switch)
            .setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
                answered(b)
        }
    }
}
