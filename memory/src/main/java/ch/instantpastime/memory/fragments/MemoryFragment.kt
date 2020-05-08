package ch.instantpastime.memory.fragments

import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import ch.instantpastime.memory.*
import ch.instantpastime.memory.R.id.box1
import com.google.android.gms.location.LocationRequest
import kotlinx.android.synthetic.main.fragment_memory.*
import kotlinx.android.synthetic.main.fragment_memory.view.*
import org.json.JSONObject


/**
 * A simple [Fragment] subclass.
 * Use the [MemoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

const val num_images = 32


class MemoryFragment : Fragment() {

    companion object {
        var isMaximize: Boolean = false
        lateinit var mGoogleAPI: GoogleAPI
    }

    //val imageListId = ArrayList<Int>()
    //val drawables = R.drawable::class.java.fields
    val backImage = R.drawable.back_card
    val myCards = ArrayList<ImageView>()
    var previousCardId: Int? = null
    var currentCardId: Int? = null

    var myBitmaps = ArrayList<bitmapClass>()

    var imgindex = 0
    var actionOngoing: Boolean = false

    var handler = Handler()
    var matching: Boolean = false

    var mGpsLocalistation: GPS_localistation? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_memory, container, false)

        //android.os.Debug.waitForDebugger()


        mGoogleAPI = GoogleAPI(this, num_images)
        mGpsLocalistation = GPS_localistation(this)
        mGpsLocalistation?.get_localitation()

        view.box1.setOnClickListener {
            maximizeBox(
                view.back,
                it,
                view.box1,
                view.box2,
                view.box3,
                view.box4
            )
        }
        view.box2.setOnClickListener {
            maximizeBox(
                view.back,
                it,
                view.box1,
                view.box2,
                view.box3,
                view.box4
            )
        }
        view.box3.setOnClickListener {
            maximizeBox(
                view.back,
                it,
                view.box1,
                view.box2,
                view.box3,
                view.box4
            )
        }
        view.box4.setOnClickListener {
            maximizeBox(
                view.back,
                it,
                view.box1,
                view.box2,
                view.box3,
                view.box4
            )
        }
        view.back.setOnClickListener {
            maximizeBox(
                view.back,
                it,
                view.box1,
                view.box2,
                view.box3,
                view.box4
            )
        }

        val viewsBox1 = view.box1.getAllViews()
        val viewsBox2 = view.box2.getAllViews()
        val viewsBox3 = view.box3.getAllViews()
        val viewsBox4 = view.box4.getAllViews()

        for (myview in viewsBox1) {
            if (myview is ImageView) myCards.add(myview)
        }
        for (myview in viewsBox2) {
            if (myview is ImageView) myCards.add(myview)
        }
        for (myview in viewsBox3) {
            if (myview is ImageView) myCards.add(myview)
        }
        for (myview in viewsBox4) {
            if (myview is ImageView) myCards.add(myview)
        }


//        for (f in drawables) {
//
//            imageListId.add(getResources().getIdentifier(f.name,"drawable","ch.instantpastime.memory"))
//        }


        //disableListeners()
        return view
    }

    fun onClick_img(currentCard: View?) {
        if (matching) {
            handler.removeCallbacksAndMessages(null)
            execPostDelayed()
        }
        if (!actionOngoing) {
            currentCardId = currentCard!!.tag as Int
            actionOngoing = true
            if (isMaximize) {
                (currentCard as ImageView).setImageBitmap(myBitmaps[currentCardId!!].img_reduce)
                currentCard.setEnabled(false)

                if (previousCardId == null) {
                    previousCardId = currentCard.tag as Int


                } else {
                    val previousCard_bitmap =
                        (myCards[previousCardId!!].getDrawable() as BitmapDrawable).getBitmap()
                    val currentCard_bitmap =
                        (currentCard.getDrawable() as BitmapDrawable).getBitmap()
                    if (previousCard_bitmap == currentCard_bitmap) {

                        currentCard.setEnabled(false)
                        previousCardId = null
                        Toast.makeText(
                            this@MemoryFragment.getContext(),
                            "Bravo!!",
                            Toast.LENGTH_SHORT
                        )
                            .show();
                        showDialog("Description of the image", currentCardId!!)
                    } else {
                        matching = true
                        Toast.makeText(
                            this@MemoryFragment.getContext(),
                            "No match, try again!",
                            Toast.LENGTH_SHORT
                        ).show();

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

    private fun execPostDelayed() {
        val currentCard = myCards[currentCardId!!]

        currentCard.setImageDrawable(
            ContextCompat.getDrawable(
                this@MemoryFragment.getContext()!!,
                backImage
            )
        )
        myCards[previousCardId!!].setImageDrawable(
            ContextCompat.getDrawable(
                this@MemoryFragment.getContext()!!,
                backImage
            )
        )
        currentCard.setEnabled(true)
        myCards[previousCardId!!].setEnabled(true)
        previousCardId = null
        matching = false
    }


    private fun showDialog(title: String, index: Int) {


        val dialog = Dialog(this@MemoryFragment.getContext()!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.popup)

        val image = dialog.findViewById(R.id.imageView) as ImageView
        image.setImageBitmap(myBitmaps[index].img_original)
        val body = dialog.findViewById(R.id.textView_popup) as TextView
        body.text =
            myBitmaps[index].img_desc + "\n Location: " + myBitmaps[index].img_loc.toString()

//        val okBtn = dialog .findViewById(R.id.button_popup) as Button
//        okBtn.setOnClickListener {
//            dialog .dismiss()
//        }
        dialog.show()

    }


    fun imageRequestedReady(bitmap: Bitmap, placeDesc: String, placeLoc: JSONObject) {

        if ((bitmap != null) and (imgindex < 32)) {
            val reciv_img = bitmapClass(bitmap, placeDesc, placeLoc)
            myBitmaps.add(reciv_img)
            myBitmaps.add(reciv_img)

            //myCards[imgindex].setImageBitmap(scaleBitmap(bitmap,200,200))
        }

        imgindex += 1

        welcomeText.setText("Loading images " + (imgindex * 100 / ch.instantpastime.memory.num_images).toString() + "%")

        if (imgindex == 33) {
            welcomeText.setText("Ready!!. (Debugging mode -> Cards not shuffled)")
            myBitmaps.shuffle()
            var i = 0
            for (myCard in myCards) {
                myCard.setOnClickListener { onClick_img(it) }
                myCard.tag = i
                i += 1
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSION_FINE_LOCATION ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mGpsLocalistation?.locationRequest = LocationRequest()
                    mGpsLocalistation?.getPlaces()
                } else {
                    Toast.makeText(
                        context,
                        "This app requires location permissions to be granted",
                        Toast.LENGTH_SHORT
                    ).show()
                    activity?.finish()
                }
        }
    }
}
