package ch.instantpastime.memory

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_memory.*
import org.json.JSONArray
import org.json.JSONObject


class MemoryActivity : AppCompatActivity() {
    val imageListId = ArrayList<Int>()
    val drawables = R.drawable::class.java.fields
    val backImage=R.drawable.vacation
    val myCards= ArrayList<ImageView>()
    var previousCardId:Int?=null
    var currentCardId:Int?=null

    open class bitmapClass(image:Bitmap,desc:String,loc:JSONObject){
        var img_reduce: Bitmap? = null
        var img_original: Bitmap? = null
        var img_desc: String? = null
        var img_loc: JSONObject? = null


        init{
            img_reduce=scaleBitmap(image,200,200)
            img_original=image
            img_desc=desc
            img_loc=loc
        }

    }
    var myBitmaps= ArrayList<bitmapClass>()

    var imgindex=0

    var actionOngoing:Boolean=false
    companion object{
        var isMaximize:Boolean=false
        val num_images = 32

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memory)

        //android.os.Debug.waitForDebugger()


        val mGoogleAPI=GoogleAPI(this)
        mGoogleAPI.requestImages("46.136883, 6.132194") //arrival time format yyyy-MM-dd HH:mm:ss





        box1.setOnClickListener {maximizeBox(back,it,box1,box2,box3,box4)}
        box2.setOnClickListener {maximizeBox(back,it, box1,box2,box3,box4)}
        box3.setOnClickListener {maximizeBox(back,it, box1,box2,box3,box4)}
        box4.setOnClickListener {maximizeBox(back,it, box1,box2,box3,box4)}
        back.setOnClickListener {maximizeBox(back,it,box1,box2,box3,box4)}

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
//                val index = (currentCard!!.tag as Int) % 16
//                (currentCard as ImageView).setImageDrawable(
//                    ContextCompat.getDrawable(
//                        this,
//                        imageListId[index]
//                    )
//                )
                (currentCard as ImageView).setImageBitmap(myBitmaps[currentCardId!!].img_reduce)
                currentCard.setEnabled(false)

                if (previousCardId == null) {
                    previousCardId = currentCard.tag as Int


                } else {
                    val previousCard_bitmap = (myCards[previousCardId!!].getDrawable() as BitmapDrawable).getBitmap()
                    val currentCard_bitmap= (currentCard.getDrawable() as BitmapDrawable).getBitmap()
                    if ( previousCard_bitmap == currentCard_bitmap) {

                        currentCard.setEnabled(false)
                        previousCardId = null
                        Toast.makeText(this@MemoryActivity, "Bravo!!", Toast.LENGTH_SHORT)
                            .show();
                        showDialog("Description of the image",currentCardId!!)
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
        image.setImageBitmap(myBitmaps[index].img_original)
        val body = dialog .findViewById(R.id.textView_popup) as TextView
        body.text = myBitmaps[index].img_desc + "\n Location: " +   myBitmaps[index].img_loc.toString()

//        val okBtn = dialog .findViewById(R.id.button_popup) as Button
//        okBtn.setOnClickListener {
//            dialog .dismiss()
//        }
        dialog .show()

    }



    fun imageRequestedReady(bitmap: Bitmap,placeDesc:String,placeLoc:JSONObject){

        if ((bitmap!=null) and (imgindex<32)) {
            val reciv_img= bitmapClass(bitmap,placeDesc,placeLoc)
            myBitmaps.add(reciv_img)
            myBitmaps.add(reciv_img)

            //myCards[imgindex].setImageBitmap(scaleBitmap(bitmap,200,200))
        }

        imgindex+=1

        welcomeText.setText("Loading images " + (imgindex*100/ num_images).toString() + "%")

        if(imgindex==33){
            welcomeText.setText("Ready!!. (Debugging mode -> Cards not shuffled)")
            //myBitmaps.shuffle()

        }

    }


}
