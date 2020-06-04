package ch.instantpastime.memory.fragments

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.util.Range
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import ch.instantpastime.*

import ch.instantpastime.memory.MemoryActivity.Companion.prefManager

import ch.instantpastime.memory.bitmapClass
import ch.instantpastime.memory.maximizeBox
import kotlinx.android.synthetic.main.fragment_memory.*
import ch.instantpastime.PlaceInfo
import ch.instantpastime.memory.*
import ch.instantpastime.memory.MemoryActivity.Companion.gameOngoing
import ch.instantpastime.memory.MemoryActivity.Companion.memoryScore
import ch.instantpastime.memory.MemoryActivity.Companion.memorySettings
import ch.instantpastime.memory.MemoryActivity.Companion.memorySound

import ch.instantpastime.memory.R
import ch.instantpastime.memory.core.MemoryScore
import ch.instantpastime.memory.core.MemorySettings
import ch.instantpastime.memory.core.MemorySettings.Companion.DEFAULT_LEVEL
import ch.instantpastime.memory.ui.MemoryResource
import ch.instantpastime.memory.ui.MemoryResource.getStockImageName
import ch.instantpastime.memory.ui.MemoryTutoHelper.startTutoActivity
import ch.instantpastime.memory.ui.ScoreDialogHelper.showDialogScore
import kotlinx.android.synthetic.main.activity_memory.*
import kotlinx.android.synthetic.main.fragment_memory.view.*
import kotlinx.android.synthetic.main.memory_status_view.*
import org.json.JSONObject
import java.io.FileDescriptor
import java.nio.file.Path
import java.nio.file.Paths

/**
 * A simple [Fragment] subclass.
 * Use the [MemoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */



class MemoryFragment : Fragment() {

    companion object {
        var isMaximize: Boolean = false
        var mListener : reStartGame? = null

    }


    val backImage = R.drawable.back_card
    val myCards = ArrayList<ImageView>()
    var previousCardId: Int? = null
    var currentCardId: Int? = null

    var myBitmaps = ArrayList<bitmapClass>()

    var imgindex = 0
    var actionOngoing: Boolean = false

    var handler = Handler()
    var matching: Boolean = false

    private var frozenContextualImages: List<Bitmap> = listOf()
    private var keepContextualImages = true

    private var locationHelper: LocationHelper? = null
    private var googleMapApi: GoogleMapApi? = null
    private var googlePlaceApi: GooglePlaceApi? = null

    private var title :ImageView? = null

    var myFragmentview: View? =null

    override fun onConfigurationChanged (newConfig: Configuration){
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation ==Configuration.ORIENTATION_LANDSCAPE ) {
            title!!.visibility = View.GONE
            /*for (myCard in myCards) {
                myCard.scaleType= ImageView.ScaleType.FIT_CENTER
            }*/
        }
        else
        {
            title!!.visibility = View.VISIBLE
           /* for (myCard in myCards) {
                myCard.scaleType= ImageView.ScaleType.FIT_XY
            }*/
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (myFragmentview != null) return myFragmentview

        val view = inflater.inflate(R.layout.fragment_memory, container, false)
        myFragmentview = view

        memoryScore = MemoryScore()
        memorySound.playSound(context!!,0)

        title = view.safeFindViewById<ImageView>(R.id.imageView2)

        view.safeFindViewById<TextView>(R.id.status_level_text)?.let {
            it.text = getString(R.string.memory_status_level, memorySettings.level)
        }
        view.safeFindViewById<TextView>(R.id.status_score_text)?.let {
            it.text = getString(R.string.memory_score, 0)
        }





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
                    if (getResources().getResourceName(myview.id).contains(memorySettings.num_cards.toString())) {
                        myCards.add(myview as ImageView)
                    } else myview.visibility = View.GONE
                }
                if (myview is LinearLayout) {
                    if (!getResources().getResourceName(myview.id).contains(memorySettings.num_cards.toString())) {
                        myview.visibility = View.GONE
                    }
                }
            }
            for (myview in viewsBox2) {
                if (myview is ImageView) {
                    if (getResources().getResourceName(myview.id).contains(memorySettings.num_cards.toString())) {
                        myCards.add(myview as ImageView)
                    } else myview.visibility = View.GONE
                }
                if (myview is LinearLayout) {
                    if (!getResources().getResourceName(myview.id).contains(memorySettings.num_cards.toString())) {
                        myview.visibility = View.GONE
                    }
                }
            }
            for (myview in viewsBox3) {
                if (myview is ImageView) {
                    if (getResources().getResourceName(myview.id).contains(memorySettings.num_cards.toString())) {
                        myCards.add(myview as ImageView)
                    } else myview.visibility = View.GONE
                }
                if (myview is LinearLayout) {
                    if (!getResources().getResourceName(myview.id).contains(memorySettings.num_cards.toString())) {
                        myview.visibility = View.GONE
                    }
                }
            }
            for (myview in viewsBox4) {
                if (myview is ImageView) {
                    if (getResources().getResourceName(myview.id).contains(memorySettings.num_cards.toString())) {
                        myCards.add(myview as ImageView)
                    } else myview.visibility = View.GONE
                }
                if (myview is LinearLayout) {
                    if (!getResources().getResourceName(myview.id).contains(memorySettings.num_cards.toString())) {
                        myview.visibility = View.GONE
                    }
                }
            }



            if (memorySettings.contextCards) {
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
                            NumImages = memorySettings.num_images,
                            imageRequestReady = { imageRequestedReady(it) }
                        )
                        googlePlaceApi?.init(ctx)
                    }
                }
                locationHelper?.getLocation(this, { processLocation(it) })
            } else {
                stockImagesLoad(context!!)
            }

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
                        memoryScore.num_mathches +=1
                        Toast.makeText(
                            this@MemoryFragment.getContext(),
                            "Bravo!! Score: " +  memoryScore.totalScore(),
                            Toast.LENGTH_SHORT
                        )
                            .show();
                        memorySound.playSound(context!!,1)
                        status_score_text.text = getString(R.string.memory_score, memoryScore.totalScore())
                        showDialog("You have matched:", currentCardId!!)


                    } else {
                        matching = true
                        memoryScore.num_trials +=1

                        Toast.makeText(
                            this@MemoryFragment.getContext(),
                            "No match, try again!  Score: " +  memoryScore.totalScore(),
                            Toast.LENGTH_SHORT
                        ).show();
                        memorySound.playSound(context!!,2)
                        status_score_text.text = getString(R.string.memory_score, memoryScore.totalScore())
                        handler.postDelayed({
                            execPostDelayed()
                        }, 3000)

                    }
                }
            } else {
                lateinit var box: View
                val index = ((currentCard!!.tag as Int) / (memorySettings.num_cards/4)) as Int
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
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.popup)

        val image = dialog.findViewById(R.id.imageView) as ImageView
        image.setImageBitmap(myBitmaps[index].img_original)
        val body = dialog.findViewById(R.id.textView_popup) as TextView
        body.text = title + "\n" + myBitmaps[index].img_desc + "\n it can be found in: \n" + myBitmaps[index].img_loc.toString()

        val okBtn = dialog .findViewById(R.id.ok_popup) as Button
        okBtn.setOnClickListener {
            if ( memoryScore.num_mathches== memorySettings.num_images){
                isMaximize=false
                showDialogScore(this@MemoryFragment.getContext()!!, memoryScore.totalScore().toString())
                memorySound.playSound(context!!,3)
            }
            dialog .dismiss()
        }
        dialog.show()

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.getWindow()!!.getAttributes())
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog.show()
        dialog.getWindow()!!.setAttributes(lp)
    }



    fun imageRequestedReady(placePhoto: PlacePhoto) {
        val bitmap = placePhoto.response.bitmap
        val placeInfo = placePhoto.info

        if ((bitmap != null) and (imgindex < memorySettings.num_images)) {
            val reciv_img = bitmapClass(bitmap, placeInfo.placeDesc, placeInfo.placeLoc)
            myBitmaps.add(reciv_img)
            myBitmaps.add(reciv_img)

            //myCards[imgindex].setImageBitmap(scaleBitmap(bitmap,200,200))
        }

        imgindex += 1

        //welcomeText.setText("Loading images " + (imgindex * 100 / memorySettings.num_images).toString() + "%")
        Toast.makeText(
            context,
            "Loading images " + (imgindex * 100 / memorySettings.num_images).toString() + "%",
            Toast.LENGTH_SHORT
        ).show()
        if (imgindex == memorySettings.num_images) {
            //welcomeText.setText("Ready!!. (Debugging mode -> Cards not shuffled)")
            Toast.makeText(
                context,
                "Ready!!",
                Toast.LENGTH_SHORT
            ).show()
            myBitmaps.shuffle()
            var i = 0
            for (myCard in myCards) {

                myCard.setOnClickListener { onClick_img(it) }
                myCard.tag = i
                i += 1

            }
            //stockImagesLoad(context!!)
        }

    }

     fun stockImagesLoad(context: Context){
         var CardsPath: Path?

         for (i in 0..(memorySettings.num_images-1)) {
             CardsPath = Paths.get(MemoryResource.CardImageFolderName, getStockImageName(i))
             if (CardsPath != null) {
                 var image = context.getAssets().open(CardsPath.toString())

                 if (image != null) {
                     var bmp = BitmapFactory.decodeStream(image)
                     val reciv_img = bitmapClass(
                         bmp,
                         getStockImageName(i)!!.split(".")[0],
                         JSONObject("""{"location":"Unknown"}""")
                     )
                     myBitmaps.add(reciv_img)
                     myBitmaps.add(reciv_img)

                 }
             }
         }
             myBitmaps.shuffle()
             Toast.makeText(
                 context,
                 "Ready!!",
                 Toast.LENGTH_SHORT
             ).show()
         var i = 0
         for (myCard in myCards) {

             myCard.setOnClickListener { onClick_img(it) }
             myCard.tag = i
             i += 1

         }


    }
    private fun processLocation(location: Location?) {

        if (location != null) {
            // Toast.makeText(
            //     ctx,
            //     "Your location is (${location.latitude}, ${location.longitude})",
            //     Toast.LENGTH_SHORT
            // ).show()
            googleMapApi?.requestNearbyPlaces(location) { processPlaces(it) }

            val context = context
            if (context == null) {
                return
            }
            startTutoActivity(context,false)

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
                    // Enable contextual images if the user accepted geolocation.
                    PrefManager.saveLocationPref(context, value = true)

                    locationHelper?.processPermissionStatus(
                        PermissionStatus.Accepted,
                        context, { loc -> processLocation(loc) })
                } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // Disable contextual images if the user refused geolocation.
                    PrefManager.saveLocationPref(context, value = false)

                    locationHelper?.processPermissionStatus(
                        PermissionStatus.RefusedOnce,
                        context, { loc -> processLocation(loc) })
                    startTutoActivity(context,false)
                    stockImagesLoad(context)


                } else {
                    // Disable contextual images if the user refused geolocation.
                    PrefManager.saveLocationPref(context, value = false)

                    locationHelper?.processPermissionStatus(
                        PermissionStatus.AlwaysRefused,
                        context, { loc -> processLocation(loc) })

                    startTutoActivity(context,false)
                    stockImagesLoad(context)

                }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    interface reStartGame{
        fun reStartGame()
    }

    override fun onAttach(context : Context){
        super.onAttach(context)
        if(context is reStartGame){
            mListener = context
        } else{
            throw RuntimeException(context.toString() +
                    " must implement reStartGame")
        }
    }

    override fun onDetach(){
        super.onDetach()
        mListener = null
    }

    fun disableListeners(){
        for (myCard in myCards) {
            myCard.setOnClickListener { null }
        }
    }

    inline fun <reified T : View> View.safeFindViewById(@IdRes id: Int): T? {
        val view = findViewById<View>(id)
        return if (view is T) {
            view
        } else {
            null
        }
    }

}
