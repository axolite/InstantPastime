package ch.instantpastime.memory.fragments

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import ch.instantpastime.*
import ch.instantpastime.memory.MemoryActivity.Companion.myscore
import ch.instantpastime.memory.MemoryActivity.Companion.num_images
import ch.instantpastime.memory.MemoryActivity.Companion.level
import ch.instantpastime.memory.MemoryActivity.Companion.num_cards
import ch.instantpastime.memory.MemoryActivity.Companion.prefManager
import ch.instantpastime.memory.MemoryActivity.Companion.tuto_images
import ch.instantpastime.memory.MemoryActivity.Companion.tuto_slides
import ch.instantpastime.memory.MemoryActivity.Companion.tuto_texts
import ch.instantpastime.memory.bitmapClass
import ch.instantpastime.memory.maximizeBox
import kotlinx.android.synthetic.main.fragment_memory.*
import ch.instantpastime.PlaceInfo
import ch.instantpastime.memory.*

import ch.instantpastime.memory.R
import kotlinx.android.synthetic.main.fragment_memory.view.*

/**
 * A simple [Fragment] subclass.
 * Use the [MemoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */



class MemoryFragment : Fragment() {

    companion object {
        var isMaximize: Boolean = false
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

    private var locationHelper: LocationHelper? = null
    private var googleMapApi: GoogleMapApi? = null
    private var googlePlaceApi: GooglePlaceApi? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_memory, container, false)

        if (locationHelper == null) {
            locationHelper = LocationHelper()
        }

        val ctx = context
        if (ctx != null) {
            if (googleMapApi == null) {
                googleMapApi = GoogleMapApi()
                googleMapApi?.init(ctx)
            }
            if (googlePlaceApi == null) {
                googlePlaceApi = GooglePlaceApi(
                    NumImages = num_images,
                    imageRequestReady = { imageRequestedReady(it) }
                )
                googlePlaceApi?.init(ctx)
            }
        }
        locationHelper?.getLocation(this, { processLocation(it) })

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
            if (myview is ImageView) {
                if (getResources().getResourceName(myview.id).contains(num_cards[level].toString())) {
                    myCards.add(myview as ImageView)
                }
                else myview.visibility = View.GONE
            }
            if (myview is LinearLayout) {
                if (! getResources().getResourceName(myview.id).contains(num_cards[level].toString())) {
                    myview.visibility = View.GONE
                }
            }
        }
        for (myview in viewsBox2) {
            if (myview is ImageView) {
                if (getResources().getResourceName(myview.id).contains(num_cards[level].toString())) {
                    myCards.add(myview as ImageView)
                }
                else myview.visibility = View.GONE
            }
            if (myview is LinearLayout) {
                if (! getResources().getResourceName(myview.id).contains(num_cards[level].toString())) {
                    myview.visibility = View.GONE
                }
            }
        }
        for (myview in viewsBox3) {
            if (myview is ImageView) {
                if (getResources().getResourceName(myview.id).contains(num_cards[level].toString())) {
                    myCards.add(myview as ImageView)
                }
                else myview.visibility = View.GONE
            }
            if (myview is LinearLayout) {
                if (! getResources().getResourceName(myview.id).contains(num_cards[level].toString())) {
                    myview.visibility = View.GONE
                }
            }
        }
        for (myview in viewsBox4) {
            if (myview is ImageView) {
                if (getResources().getResourceName(myview.id).contains(num_cards[level].toString())) {
                    myCards.add(myview as ImageView)
                }
                else myview.visibility = View.GONE
            }
            if (myview is LinearLayout) {
                if (! getResources().getResourceName(myview.id).contains(num_cards[level].toString())) {
                    myview.visibility = View.GONE
                }
            }
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
                        myscore.num_mathches +=1
                        Toast.makeText(
                            this@MemoryFragment.getContext(),
                            "Bravo!! Score: " +  myscore.totalScore(),
                            Toast.LENGTH_SHORT
                        )
                            .show();

                        showDialog("Description of the image", currentCardId!!)

                        if ( myscore.num_mathches== num_images){
                            //  End .. Show Score
                            var finalScore = DialogScore()
                            finalScore.showDialog(this@MemoryFragment.getContext()!!, myscore.totalScore().toString())
                            Toast.makeText(
                                this@MemoryFragment.getContext(),
                                "You have completed the memory!! Score: " +  myscore.totalScore(),
                                Toast.LENGTH_SHORT
                            )
                                .show();
                        }
                    } else {
                        matching = true
                        myscore.num_trials +=1
                        Toast.makeText(
                            this@MemoryFragment.getContext(),
                            "No match, try again!  Score: " +  myscore.totalScore(),
                            Toast.LENGTH_SHORT
                        ).show();
                        handler.postDelayed({
                            execPostDelayed()
                        }, 3000)

                    }
                }
            } else {
                lateinit var box: View
                val index = ((currentCard!!.tag as Int) / (num_cards[level]/4)) as Int
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


    fun imageRequestedReady(placePhoto: PlacePhoto) {
        val bitmap = placePhoto.response.bitmap
        val placeInfo = placePhoto.info

        if ((bitmap != null) and (imgindex < num_images)) {
            val reciv_img = bitmapClass(bitmap, placeInfo.placeDesc, placeInfo.placeLoc)
            myBitmaps.add(reciv_img)
            myBitmaps.add(reciv_img)

            //myCards[imgindex].setImageBitmap(scaleBitmap(bitmap,200,200))
        }

        imgindex += 1

        welcomeText.setText("Loading images " + (imgindex * 100 / num_images).toString() + "%")

        if (imgindex == num_images) {
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

    private fun processLocation(location: Location?) {
        val ctx = context
        if (location != null) {
            // Toast.makeText(
            //     ctx,
            //     "Your location is (${location.latitude}, ${location.longitude})",
            //     Toast.LENGTH_SHORT
            // ).show()
            googleMapApi?.requestNearbyPlaces(location) { processPlaces(it) }

            //**** Launch Tuto while the pictures are being loaded *********************
            if (prefManager.isFirstTimeLaunch()){
                callingFromSettings = false

                val intent = Intent(ctx, StartActivity::class.java)
                intent.putExtra( "tuto_slides",  tuto_slides )
                intent.putExtra("tuto_images", tuto_images )
                intent.putExtra("tuto_texts", tuto_texts )
                startActivity(intent)
                prefManager.setFirstTimeLaunch()

            }
            //**************************************************************************

        }
    }

    private fun processPlaces(places: ArrayList<PlaceInfo>) {
        googlePlaceApi?.getPhotoAndDetail(places)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val context = context
        if (context == null) {
            return
        }
        when (requestCode) {
            MY_PERMISSION_FINE_LOCATION ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationHelper?.processPermissionStatus(
                        PermissionStatus.Accepted,
                        context, { loc -> processLocation(loc) })
                } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    locationHelper?.processPermissionStatus(
                        PermissionStatus.RefusedOnce,
                        context, { loc -> processLocation(loc) })
                } else {
                    locationHelper?.processPermissionStatus(
                        PermissionStatus.AlwaysRefused,
                        context, { loc -> processLocation(loc) })
                }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

}
