package ch.instantpastime.fragments

import androidx.annotation.IdRes

interface MenuFragmentTag {
    /**
     * Gets the menu item ID from the tag of the associated fragment.
     */
    @IdRes
    fun getMenuIdFromTag(tag: String): Int?

    /**
     * Gets the tag of a fragment associated to a menu item ID.
     */
    fun getTagFromMenuId(@IdRes menuId: Int): String?
}
