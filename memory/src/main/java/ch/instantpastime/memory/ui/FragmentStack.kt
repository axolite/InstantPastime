package ch.instantpastime.memory.ui

import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import ch.instantpastime.ValueChange

class FragmentStack(
    val activity: AppCompatActivity,
    @IdRes
    val containerId: Int,
    var homeTag: String
) {
    private val cache: FragmentCache = FragmentCache(MyFragmentHelper)
    private val tagStack: MutableList<String> = mutableListOf()
    var currentTagChanged: ((ValueChange<String>) -> Unit)? = null


    init {
        activity.lifecycle.addObserver(cache)
    }

    var currentTag: String
        get() = _currentTag
        private set(value) {
            val oldValue = _currentTag
            if (oldValue != value) {
                _currentTag = value
                currentTagChanged?.invoke(ValueChange(oldValue = oldValue, newValue = value))
            }
        }
    var _currentTag: String = homeTag

    fun pushFragment(tag: String): Fragment? {
        val fragment = showFragment(tag)
        if (fragment != null && tagStack.lastOrNull() != tag) {
            tagStack.add(tag)
            currentTag = tag
        }
        return fragment
    }

    private fun showFragment(tag: String): Fragment? {
        val fragment = getOrCreateFragment(tag)
        if (fragment != null) {
            activity.supportFragmentManager.beginTransaction()
                .replace(containerId, fragment, tag)
                .commit()
        }
        return fragment
    }

    fun getOrCreateFragment(tag: String): Fragment? {
        val existing = activity.supportFragmentManager.findFragmentByTag(tag)
        return if (existing != null) {
            existing
        } else {
            cache.createAndAddFragment(tag)
        }
    }

    @ExperimentalStdlibApi
    fun popFragment() {
        val poppedTag = tagStack.removeLastOrNull()
        val topTag = tagStack.lastOrNull()
        if (topTag != null) {
            showFragment(topTag)
            currentTag = topTag
        } else {
            val homeTag = this.homeTag
            pushFragment(homeTag)
            currentTag = homeTag
        }
    }

}
