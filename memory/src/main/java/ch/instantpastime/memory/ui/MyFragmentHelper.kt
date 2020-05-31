package ch.instantpastime.memory.ui

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import ch.instantpastime.MenuTagEntry
import ch.instantpastime.fragments.FragmentCreator
import ch.instantpastime.fragments.GeneralPreferenceFragment
import ch.instantpastime.fragments.MenuFragmentTag
import ch.instantpastime.memory.R
import ch.instantpastime.memory.fragments.MemoryFragment
import ch.instantpastime.memory.fragments.MemoryPreferenceFragment

object MyFragmentHelper : MenuFragmentTag, FragmentCreator {

    val table = listOf(
        MenuTagEntry(
            menuId = R.id.navigation_home,
            tag = MemoryFragment::class.java.simpleName
        ),
        MenuTagEntry(
            menuId = R.id.navigation_option,
            tag = MemoryPreferenceFragment::class.java.simpleName
        )
    )

    /**
     * Gets the menu item ID from the tag of the associated fragment.
     */
    @IdRes
    override fun getMenuIdFromTag(tag: String): Int? {
        return table.firstOrNull { it.tag == tag }?.menuId
    }

    /**
     * Gets the tag of a fragment associated to a menu item ID.
     */
    override fun getTagFromMenuId(@IdRes menuId: Int): String? {
        return table.firstOrNull { it.menuId == menuId }?.tag
    }

    /**
     * Creates a fragment instance from the given tag.
     */
    override fun createFragment(tag: String): Fragment? {
        return when (tag) {
            MemoryFragment::class.java.simpleName -> MemoryFragment()
            MemoryPreferenceFragment::class.java.simpleName -> MemoryPreferenceFragment()
            GeneralPreferenceFragment::class.java.simpleName -> GeneralPreferenceFragment()
            else -> null
        }
    }
}
