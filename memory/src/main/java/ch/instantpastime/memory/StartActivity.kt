/* Inspired by and adapted from
https://www.androidhive.info/2016/05/android-build-intro-slider-app/
 */

package ch.instantpastime.memory

/* Import ******************************************************** */
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import ch.instantpastime.PrefManager

/* *************************************************************** */

/* Global variables ********************************************** */
var mAdapter : StartActivity.ViewPagerAdapter? = null
lateinit var slides: IntArray
lateinit var images: IntArray
lateinit var texts: IntArray
lateinit var layoutDots : LinearLayout
lateinit var startNext : Button
lateinit var startSkip : Button
lateinit var viewSlides : ViewPager
var callingFromSettings : Boolean = false


class StartActivity : AppCompatActivity(){

    //private lateinit var prefManager : PrefManager

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)

        /*prefManager = PrefManager(this)
        if((!prefManager.isFirstTimeLaunch()) and
            (!callingFromSettings)){
            callingFromSettings = false
            launchHomeScreen()
            finish()
        }*/

        setContentView(ch.instantpastime.R.layout.activity_start)

        viewSlides = findViewById(ch.instantpastime.R.id.view_slides)
        layoutDots  = findViewById(ch.instantpastime.R.id.layout_dots)
        startNext = findViewById(ch.instantpastime.R.id.start_button_next)
        startSkip = findViewById(ch.instantpastime.R.id.start_button_skip)

        /* *************************************************************** */
        /* Define Slides ************************************************* */
        /* ******* Layouts *********************************************** */
        slides = intArrayOf(
            R.layout.activity_start_content01,
            R.layout.activity_start_content01,
            R.layout.activity_start_content01
        )
        /* ******* Images ************************************************ */
        images = intArrayOf(
            R.drawable.tutoslide01,
            R.drawable.tutoslide02,
            R.drawable.tutoslide03

        )
        /* ******* Texts ************************************************* */
        texts = intArrayOf(
            R.string.start01,
            R.string.start02,
            R.string.start03
        )
        /* *************************************************************** */


        mAdapter = ViewPagerAdapter(this)
        mAdapter!!.setActivity(this)

        viewSlides.adapter = mAdapter
        viewSlides.addOnPageChangeListener(slideChangeListener)

        startSkip.setOnClickListener { launchHomeScreen() }

        startNext.setOnClickListener {
            val current : Int = getItem(+ 1)
            if(current < slides.count()){
                viewSlides.currentItem = current
            } else {
                launchHomeScreen()
            }
        }
    }

    private fun navigationDots(currentPage : Int){
        val dots : MutableList<TextView> =
            MutableList(slides.count(), { index -> TextView(this) } )

        layoutDots.removeAllViews()
        for(dot in dots){
            dot.text = HtmlCompat.fromHtml("&#8226;", HtmlCompat.FROM_HTML_MODE_LEGACY)
            dots[currentPage].setTextColor(ContextCompat.getColor(this, ch.instantpastime.R.color.colorPrimaryDark))
            layoutDots.addView(dot)
        }
        if(dots.count() > 0){
            dots[currentPage].setTextColor(ContextCompat.getColor(this, ch.instantpastime.R.color.colorDots))
        }
    }

    private fun getItem(i : Int) : Int {
        return viewSlides.currentItem + i
    }

    private fun launchHomeScreen() {
        //startActivity(Intent(this@StartActivity, MemoryActivity::class.java))
        finish()
    }

    private var slideChangeListener :
            ViewPager.OnPageChangeListener = object :
        ViewPager.OnPageChangeListener {
        override fun onPageSelected(position: Int){
            navigationDots(position)
            if(position == slides.count() - 1){
                startNext.text = getString(ch.instantpastime.R.string.action_got)
                startSkip.visibility = View.GONE
            } else {
                startNext.text = getString(ch.instantpastime.R.string.action_next)
                startSkip.visibility = View.VISIBLE
            }
        }

        override fun onPageScrolled(arg0 : Int, arg1 : Float, arg2 : Int){}
        override fun onPageScrollStateChanged(arg0 : Int){}
    }

    class ViewPagerAdapter : PagerAdapter{

        private var layoutInflater : LayoutInflater? = null
        private var mActivity : StartActivity? = null
        //private var images : IntArray
        //private var texts : IntArray

        constructor(context : Context):super(){

        }

        fun setActivity(activity : StartActivity){
            mActivity = activity
        }
        override fun instantiateItem(container : ViewGroup, position : Int) : Any {
            val image : ImageView
            val text : TextView

            layoutInflater =
                mActivity!!.applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)
                        as LayoutInflater?

            val view : View = layoutInflater!!.inflate(R.layout.activity_start_content01, container, false)

            image = view.findViewById(R.id.start_image)
            image.setImageResource(images[position])

            text = view.findViewById(R.id.start_text)
            text.setText(texts[position])

            container.addView(view)
            return view
        }

        override fun getCount() : Int {
            return slides.count()
        }

        override fun isViewFromObject(view : View, obj : Any) : Boolean {
            return view === obj
        }

        override fun destroyItem(
            container : ViewGroup,
            position : Int,
            `object` : Any
        ){
            val view : View = `object` as View
            container.removeView(view)
        }
    }
}