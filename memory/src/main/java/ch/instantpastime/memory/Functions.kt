package ch.instantpastime.memory

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import ch.instantpastime.memory.MemoryActivity.Companion.isMaximize


fun maximizeBox(back: Button, box: View, box1: View, box2: View, box3: View, box4: View) {
    var boxName: String = ""
    when (box.getId()) {
        R.id.box1 -> {
            boxName = "Box1"
            if (box2.visibility == View.GONE) {
                box2.visibility = View.VISIBLE
                box3.visibility = View.VISIBLE
                box4.visibility = View.VISIBLE
                back.visibility= View.GONE
                box1.setEnabled(true)
                box2.setEnabled(true)
                box3.setEnabled(true)
                box4.setEnabled(true)
                isMaximize=false
            } else {
                back.visibility= View.VISIBLE
                box2.visibility = View.GONE
                box3.visibility = View.GONE
                box4.visibility = View.GONE
                box1.setEnabled(false)
                isMaximize=true
            }
        }
        R.id.box2 -> {
            boxName = "Box2"
            if (box3.visibility == View.GONE) {
                box1.visibility = View.VISIBLE
                box3.visibility = View.VISIBLE
                box4.visibility = View.VISIBLE
                back.visibility= View.GONE
                box1.setEnabled(true)
                box2.setEnabled(true)
                box3.setEnabled(true)
                box4.setEnabled(true)
                isMaximize=false
            } else {
                back.visibility= View.VISIBLE
                box1.visibility = View.GONE
                box3.visibility = View.GONE
                box4.visibility = View.GONE
                box2.setEnabled(false)
                isMaximize=true
            }
        }
        R.id.box3 -> {
            boxName = "Box3"
            if (box2.visibility == View.GONE) {
                box2.visibility = View.VISIBLE
                box1.visibility = View.VISIBLE
                box4.visibility = View.VISIBLE
                back.visibility= View.GONE
                box1.setEnabled(true)
                box2.setEnabled(true)
                box3.setEnabled(true)
                box4.setEnabled(true)
                isMaximize=false
            } else {
                back.visibility= View.VISIBLE
                box2.visibility = View.GONE
                box1.visibility = View.GONE
                box4.visibility = View.GONE
                box3.setEnabled(false)
                isMaximize=true
            }
        }
        R.id.box4 -> {
            boxName = "Box4"
            if (box3.visibility == View.GONE) {
                box2.visibility = View.VISIBLE
                box3.visibility = View.VISIBLE
                box1.visibility = View.VISIBLE
                back.visibility= View.GONE
                box1.setEnabled(true)
                box2.setEnabled(true)
                box3.setEnabled(true)
                box4.setEnabled(true)
                isMaximize=false
            } else {
                back.visibility= View.VISIBLE
                box2.visibility = View.GONE
                box3.visibility = View.GONE
                box1.visibility = View.GONE
                box4.setEnabled(false)
                isMaximize=true
            }
        }
        R.id.back ->{
            box1.visibility = View.VISIBLE
            box2.visibility = View.VISIBLE
            box3.visibility = View.VISIBLE
            box4.visibility = View.VISIBLE
            back.visibility= View.GONE
            box1.setEnabled(true)
            box2.setEnabled(true)
            box3.setEnabled(true)
            box4.setEnabled(true)
            isMaximize=false
        }
    }
    //Toast.makeText(this@MemoryActivity, boxName, Toast.LENGTH_SHORT).show();

}

fun View.setMarginLeft(leftMargin: Int) {
    val params = layoutParams as ViewGroup.MarginLayoutParams
    params.setMargins(left, params.topMargin, params.rightMargin, params.bottomMargin)
    layoutParams = params
}


fun View.getAllViews(): List<View> {
    if (this !is ViewGroup || childCount == 0) return listOf(this)

    return children
        .toList()
        .flatMap { it.getAllViews() }
        .plus(this as View)
}

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
