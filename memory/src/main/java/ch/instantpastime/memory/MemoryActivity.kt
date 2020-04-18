package ch.instantpastime.memory

import android.R.drawable
import android.app.Dialog
import android.os.Bundle
import android.os.Debug
import android.os.Handler
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_memory.*
import java.lang.reflect.Field


class MemoryActivity : AppCompatActivity() {
    val imageListId = ArrayList<Int>()
    val drawables = R.drawable::class.java.fields
    val backImage=R.drawable.vacation
    val myCards= ArrayList<ImageView>()
    var previousCardId:Int?=null
    var currentCardId:Int?=null

    var actionOngoing:Boolean=false
    companion object{
        var isMaximize:Boolean=false

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memory)
        box1.setOnClickListener {maximizeBox(back,it,box1,box2,box3,box4)}
        box2.setOnClickListener {maximizeBox(back,it, box1,box2,box3,box4)}
        box3.setOnClickListener {maximizeBox(back,it, box1,box2,box3,box4)}
        box4.setOnClickListener {maximizeBox(back,it, box1,box2,box3,box4)}
        back.setOnClickListener {maximizeBox(back,it,box1,box2,box3,box4)}
        //android.os.Debug.waitForDebugger()

        val viewsBox1 = box1.getAllViews()
        val viewsBox2 = box2.getAllViews()
        val viewsBox3 = box3.getAllViews()
        val viewsBox4 = box4.getAllViews()

        for (myview in viewsBox1){
           if (myview is ImageView) myCards.add(myview)
        }
        for (myview in viewsBox2){
            if (myview is ImageView) myCards.add(myview)
        }
        for (myview in viewsBox3){
            if (myview is ImageView) myCards.add(myview)
        }
        for (myview in viewsBox4) {
            if (myview is ImageView) myCards.add(myview)
        }


        var i=0

        for (f in drawables) { //if the drawable name contains "pic" in the filename...

            //if (f.getName().contains("image"))
            imageListId.add(getResources().getIdentifier(f.name,"drawable","ch.instantpastime.memory"))
        }
        //for (imgResourceId in imageListId) {

        //}

        for (myCard in myCards){
            myCard.setOnClickListener {onClick_img(it)}
            myCard.tag=i
            i+=1
        }

        //disableListeners()

    }
    var handler=Handler()
    var matching:Boolean=false
    fun onClick_img(currentCard:View?){
        if (matching) {
            handler.removeCallbacksAndMessages(null)
            execPostDelayed()
        }
        if (!actionOngoing) {
            currentCardId = currentCard!!.tag as Int
            actionOngoing = true
            if (isMaximize) {
                val index = (currentCard!!.tag as Int) % 16
                (currentCard as ImageView).setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        imageListId[index]
                    )
                )
                currentCard.setEnabled(false)

                if (previousCardId == null) {
                    previousCardId = currentCard.tag as Int


                } else {
                    if ((myCards[previousCardId!!].tag as Int) % 16 == (currentCard.tag as Int) % 16) {

                        currentCard.setEnabled(false)
                        previousCardId = null
                        Toast.makeText(this@MemoryActivity, "Bravo!!", Toast.LENGTH_SHORT)
                            .show();
                        showDialog("Description of the image",index)
                    } else {
                        matching = true
                        Toast.makeText(this@MemoryActivity, "No match, try again!", Toast.LENGTH_SHORT).show();

                        handler.postDelayed({
                            execPostDelayed()
                        }, 3000)

                    }
                }
            } else {
                lateinit var box: View
                val index = ((currentCard!!.tag as Int) / 16) as Int
                if (index == 0) box = box1
                else if (index == 1) box = box2
                else if (index == 2) box = box3
                else box = box4

                maximizeBox(back, box, box1, box2, box3, box4)

            }

            actionOngoing = false
        }
    }

        //Toast.makeText(this@MemoryActivity, img.tag.toString(), Toast.LENGTH_SHORT).show();
        fun execPostDelayed() {
            val currentCard=myCards[currentCardId!!]
            currentCard.setImageDrawable(ContextCompat.getDrawable(this, backImage))
            myCards[previousCardId!!].setImageDrawable(ContextCompat.getDrawable(this,backImage))
            currentCard.setEnabled(true)
            myCards[previousCardId!!].setEnabled(true)
            previousCardId = null
            matching = false
        }


    private fun showDialog(title: String,index:Int) {
        val dialog = Dialog(this)
        dialog .requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog .setCancelable(true)
        dialog .setContentView(R.layout.popup)
        val image = dialog .findViewById(R.id.imageView) as ImageView
        image.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                imageListId[index]
            )
        )
        val body = dialog .findViewById(R.id.textView_popup) as TextView
        body.text = title
//        val okBtn = dialog .findViewById(R.id.button_popup) as Button
//        okBtn.setOnClickListener {
//            dialog .dismiss()
//        }
        dialog .show()

    }
}
