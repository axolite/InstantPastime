package ch.instantpastime.fragments

import androidx.fragment.app.Fragment

interface FragmentCreator {

    /**
     * Creates a fragment instance from the given tag.
     */
    fun createFragment(tag: String): Fragment?
}
