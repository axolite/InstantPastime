package ch.instantpastime.memory.ui

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.preference.PreferenceFragmentCompat
import ch.instantpastime.fragments.FragmentCreator

class FragmentCache(
    val fragmentCreator: FragmentCreator
) : LifecycleObserver {

    private val cachedFragments: MutableList<Fragment> = mutableListOf()

    fun createAndAddFragment(tag: String): Fragment? {
        val cached = cachedFragments.firstOrNull { it.javaClass.simpleName == tag }
        if (cached != null) {
            return cached
        } else {
            val newFragment = fragmentCreator.createFragment(tag)
            // Don't cache PreferenceFragmentCompat because it flickers when it is displayed.
            if (newFragment != null && !(newFragment is PreferenceFragmentCompat)) {
                cachedFragments.add(newFragment)
            }
            return newFragment
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    fun onAny() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        // Don't keep any reference to fragments once the activity is stopped.
        cachedFragments.clear()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
    }
}
