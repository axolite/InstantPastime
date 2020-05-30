package ch.instantpastime

import androidx.annotation.IdRes

data class MenuTagEntry(

    /**
     * Menu id (android:id="@+id/...") in the XML menu file.
     */
    @IdRes val menuId: Int,

    /**
     * Fragment tag used to identifying it from other fragments.
     * The of the class can be used, like MyFragment::class.java.simpleName.
     */
    val tag: String
)
