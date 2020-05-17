package ch.instantpastime.nback.fragments

import android.content.Context
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import ch.instantpastime.nback.R
import ch.instantpastime.nback.core.*

/**
 * A simple [Fragment] subclass.
 * Use the [NBackFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NBackFragment : Fragment() {

    companion object {
        const val INVALID_INDEX: Int = -1
    }

    private enum class Transition {
        Start, Stop, Pause, Resume
    }

    private var state: NBackState = NBackState.Idle
    private var game: NBackBoard? = null
    private val score: NBackGame = NBackGame()

    //private var timer: NBackTimer = NBackTimer(NBackGame.DEFAULT_MILLISEC.toLong(), { -> nextIndex() })
    private var timer: NBackCountDown? = null
    val nbackSound: NBackSound = NBackSound()
    private var lastIndex: Int = INVALID_INDEX

    private var mLocationFeedbackZone: ImageView? = null
    private var mLetterFeedbackZone: ImageView? = null

    private var mPauseButton: Button? = null
    private var mRestartButton: Button? = null
    private var mLocationButton: Button? = null
    private var mLetterButton: Button? = null
    private var mTimeBar: ProgressBar? = null
    private var mScoreText: TextView? = null
    private var mTrialCountText: TextView? = null

    /**
     * True when the user says it is the same location, otherwise false.
     */
    private var mAnswerSameLocation: Boolean = false

    /**
     * True when the user says it is the same letter, otherwise false.
     */
    private var mAnswerSameLetter: Boolean = false

    /**
     * True when it is actually the same location, false when different,
     * null when there isn't enough elements to compare.
     */
    private var mSameLocation: Boolean? = null

    /**
     * True when it is actually the same letter, false when different,
     * null when there isn't enough elements to compare.
     */
    private var mSameLetter: Boolean? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val nbackSettings = loadNBackSettings()
        game = NBackBoard(nbLetters = nbackSound.letterCount, nBackLevel = nbackSettings.level)
        timer?.totalMilliseconds = nbackSettings.time_per_trial
        val view = inflater.inflate(R.layout.fragment_nback, container, false)
        mRestartButton = view.safeFindViewById<Button>(R.id.restart_button)
        mLocationButton = view.safeFindViewById<Button>(R.id.locationButton)
        mLetterButton = view.safeFindViewById<Button>(R.id.letterButton)
        mLocationFeedbackZone = view.safeFindViewById(R.id.nback_location_feedback)
        mLetterFeedbackZone = view.safeFindViewById(R.id.nback_letter_feedback)
        mPauseButton = view.safeFindViewById(R.id.pause_button)
//        mTimeBar = view.safeFindViewById<ProgressBar>(R.id.nback_time_bar).apply {
//            max = nbackSettings.time_per_trial
//        }
        mScoreText = view.safeFindViewById(R.id.status_score_text)
        mTrialCountText = view.safeFindViewById(R.id.status_trial_count_text)

        context?.let {
            context?.let { nbackSound.init(it) }
            updateControls(NBackState.Idle, it)
        }

        mRestartButton?.setOnClickListener { restartButtonClicked() }
        mPauseButton?.setOnClickListener { pauseButtonClicked() }
        mLocationButton?.setOnClickListener { locationButtonClicked() }
        mLetterButton?.setOnClickListener { letterButtonClicked() }
        applySettings(nbackSettings, view)

        timer = NBackCountDown(totalMilliseconds = nbackSettings.time_per_trial,
            onTick = {
                mTimeBar?.apply {
                    progress += NBackCountDown.stepMillisec
                }
            },
            onFinish = {
                nextIndex()
                mTimeBar?.apply {
                    progress = 0
                }
            }
        )

        return view
    }

    override fun onPause() {
        processTransition(Transition.Pause)
        super.onPause()
    }

    override fun onStop() {
        processTransition(Transition.Pause)
        super.onStop()
    }

    private fun processTransition(transition: Transition) {
        val oldState = state
        val newState = progressState(oldState, transition)
        if (newState != oldState) {
            state = newState
            applyState(oldState = oldState, newState = newState)
        }
    }

    private fun progressState(state: NBackState, transition: Transition): NBackState {
        return when (transition) {
            Transition.Start -> when (state) {
                NBackState.Idle -> NBackState.Running
                else -> state
            }
            Transition.Pause -> when (state) {
                NBackState.Running -> NBackState.Paused
                else -> state
            }
            Transition.Resume -> when (state) {
                NBackState.Paused -> NBackState.Running
                else -> state
            }
            Transition.Stop -> when (state) {
                NBackState.Running, NBackState.Paused -> NBackState.Idle
                else -> state
            }
        }
    }

    private fun applyState(oldState: NBackState, newState: NBackState) {
        when (newState) {
            NBackState.Idle -> {
                // Update the controls.
                context?.let { ctx -> updateControls(newState, ctx) }
                // Reset the color of the last active square.
                val lastSquare = getSquare(lastIndex)
                if (lastSquare is ImageView) {
                    context?.let { ctx ->
                        lastSquare.setBackgroundColor(
                            ContextCompat.getColor(
                                ctx,
                                R.color.colorIdleSquare
                            )
                        )
                    }
                }
                timer?.stopTimer()
                score.reset()
            }
            NBackState.Running -> {
                // Update the controls.
                context?.let { ctx -> updateControls(newState, ctx) }
                timer?.startTimer()
            }
            NBackState.Paused -> {
                // Update the controls.
                context?.let { ctx -> updateControls(newState, ctx) }
                timer?.stopTimer()
            }
        }
    }

    private fun updateControls(state: NBackState, context: Context) {
        when (state) {
            NBackState.Idle -> {
                mPauseButton?.visibility = View.INVISIBLE
                mRestartButton?.apply {
                    text = context.getString(R.string.start_game_short)
                }
                mLocationButton?.visibility = View.INVISIBLE
                mLetterButton?.visibility = View.INVISIBLE
                updateTrialCount(0, 0)
                updateScore(0, 0)
                Toast.makeText(
                    context,
                    context.getString(R.string.stop_game_short),
                    Toast.LENGTH_SHORT
                ).show()
                safeFindViewById<LinearLayout>(R.id.nback_past_locations_panel)?.let { panel ->
                    panel.removeAllViews()
                }
            }
            NBackState.Paused -> {
                mPauseButton?.apply {
                    text = context.getString(R.string.resume_game_short)
                    visibility = View.VISIBLE
                }
                mLocationButton?.isEnabled = false
                mLetterButton?.isEnabled = false
                Toast.makeText(
                    context,
                    context.getString(R.string.pause_game_short),
                    Toast.LENGTH_SHORT
                ).show()
            }
            NBackState.Running -> {
                mPauseButton?.apply {
                    text = context.getString(R.string.pause_game_short)
                    visibility = View.VISIBLE
                }
                mRestartButton?.apply {
                    text = context.getString(R.string.stop_game_short)
                }
                mLocationButton?.apply {
                    isEnabled = true
                    visibility = View.VISIBLE
                }
                mLetterButton?.apply {
                    isEnabled = true
                    visibility = View.VISIBLE
                }
                updateTrialCount(0, 0)
                updateScore(0, 0)
                Toast.makeText(
                    context,
                    context.getString(R.string.start_game_short),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun restartButtonClicked() {
        when (state) {
            NBackState.Running, NBackState.Paused -> processTransition(Transition.Stop)
            NBackState.Idle -> processTransition(Transition.Start)
        }
    }

    private fun pauseButtonClicked() {
        when (state) {
            NBackState.Running -> processTransition(Transition.Pause)
            NBackState.Paused -> processTransition(Transition.Resume)
            else -> {
            }
        }
    }

    private fun locationButtonClicked() {
        mAnswerSameLocation = !mAnswerSameLocation
        context?.let {
            val color = getAnswerFeedbackColor(mAnswerSameLocation, it)
            mLocationFeedbackZone?.setBackgroundColor(color)
        }
    }

    private fun letterButtonClicked() {
        mAnswerSameLetter = !mAnswerSameLetter
        context?.let {
            val color = getAnswerFeedbackColor(mAnswerSameLetter, it)
            mLetterFeedbackZone?.setBackgroundColor(color)
        }
    }

    /**
     * Gets a color that corresponds to the user's answer.
     */
    private fun getAnswerFeedbackColor(answer: Boolean, context: Context): Int {
        return if (answer) {
            // User thinks it is the same (location, letter, etc.)
            ContextCompat.getColor(context, R.color.colorNBackAnswer)
        } else {
            // User thinks it's not the same (location, letter, etc.)
            ContextCompat.getColor(context, R.color.colorTransparent)
        }
    }

    /**
     * Gets a color that corresponds to the actual answer (reality),
     * even when not enough trials have been drawn.
     */
    @ColorInt
    private fun getEarlyFeedbackColor(correctness: NBackGame.Correctness?, context: Context): Int {
        return if (correctness == null) {
            ContextCompat.getColor(context, R.color.colorTransparent)
        } else {
            getActualFeedbackColor(correctness, context)
        }
    }

    /**
     * Gets a color that corresponds to the actual answer (reality).
     */
    @ColorInt
    private fun getActualFeedbackColor(correctness: NBackGame.Correctness, context: Context): Int {
        return when (correctness) {
            NBackGame.Correctness.CORRECT_SAME ->
                // The user said "same" and it is: answer is correct.
                ContextCompat.getColor(context, R.color.colorNBackCorrect)

            NBackGame.Correctness.WRONG_ACTUALLY_SAME ->
                // The user didn't say "same" but it is: answer is wrong.
                ContextCompat.getColor(context, R.color.colorNBackActual)

            NBackGame.Correctness.WRONG_ACTUALLY_DIFFERENT ->
                // The user said "same" but it's not: answer is wrong.
                ContextCompat.getColor(context, R.color.colorNBackWrong)

            NBackGame.Correctness.CORRECT_DIFFERENT ->
                // The user didn't say "same" and it's not: answer is correct.
                ContextCompat.getColor(context, R.color.colorTransparent)
        }
    }

    private fun updatePastLocations(trial: NBackTrial) {

        val context = context ?: return
        val drawableId = getMiniLocationId(trial.location.index)
        if (drawableId == 0) {
            return
        }
        val drawable = ContextCompat.getDrawable(context, drawableId)?.apply {
            setTintMode(PorterDuff.Mode.SRC_ATOP)
            setTint(ContextCompat.getColor(context, R.color.colorActiveSquare))
        }
        val imgView = ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(40, 40)
            scaleType = ImageView.ScaleType.FIT_CENTER
            setImageDrawable(drawable)
        }
        val game = game ?: return
        val panel = safeFindViewById<LinearLayout>(R.id.nback_past_locations_panel) ?: return
        updatePastViewGroup(panel = panel, newView = imgView, maxCount = game._level)
        panel.addView(imgView)
    }

    private fun updatePastLetters(trial: NBackTrial) {
        val context = context ?: return
        val drawableId = getMiniLetterId(trial.symbol.index)
        if (drawableId == 0) {
            return
        }
        val drawable = ContextCompat.getDrawable(context, drawableId)?.apply {
        }
        val imgView = ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(40, 40)
            scaleType = ImageView.ScaleType.FIT_CENTER
            setImageDrawable(drawable)
        }
        val game = game ?: return
        val panel = safeFindViewById<LinearLayout>(R.id.nback_past_letters_panel) ?: return
        updatePastViewGroup(panel = panel, newView = imgView, maxCount = game._level)
        panel.addView(imgView)
    }

    private fun updatePastViewGroup(panel: ViewGroup, newView: View, maxCount: Int) {
        val ALPHA_NO_FOCUS = 0.5F
        val ALPHA_FOCUS = 1.0F
        val iterator = panel.children.iterator()
        if (iterator.hasNext()) {
            val first = iterator.next()
            if (panel.childCount > maxCount) {
                if (iterator.hasNext()) {
                    val second = iterator.next()
                    second.alpha = ALPHA_FOCUS
                    if (iterator.hasNext()) {
                        var last = iterator.next()
                        while (iterator.hasNext()) {
                            last = iterator.next()
                        }
                        last.alpha = ALPHA_NO_FOCUS
                    }
                }
                panel.removeView(first)
                newView.alpha = ALPHA_FOCUS
            } else if (panel.childCount == maxCount) {
                newView.alpha = ALPHA_FOCUS
            }
            else {
                newView.alpha = ALPHA_NO_FOCUS
            }
        }
    }

    fun nextIndex() {
        val game = this.game ?: return
        val next = game.getNextTrial()
        val (index, sameLocation) = next.location
        val (letterIndex, sameLetter) = next.symbol

        // Check the user's current answer.
        checkCurrentAnswer()

        activity?.runOnUiThread {
            val context = context ?: return@runOnUiThread
            val oldSquare = getSquare(lastIndex) as ImageView?
            val newSquare = getSquare(index) as ImageView?
            if (newSquare != null) {
                lastIndex = index
            } else {
                lastIndex = INVALID_INDEX
            }

            // Colorize the next location.
            oldSquare?.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.colorIdleSquare
                )
            )
            oldSquare?.setImageDrawable(ContextCompat.getDrawable(context,
                R.drawable.ic_letter_placeholder)
            )
            newSquare?.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.colorActiveSquare
                )
            )
            // Play or show the next letter.
            nbackSound.playArticle(context, letterIndex)
            when (val c = nbackSound.getLetter(letterIndex)) {
                null -> {
                }
                else -> {
                    val drawableId = getLetterDrawableId(c)
                    val drawable = ContextCompat.getDrawable(context, drawableId)
                    if (drawable != null) {
                        newSquare?.setImageDrawable(drawable)
                    }
                    Toast.makeText(context, "Sound ${c.toUpperCase()}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            updatePastLocations(next)
            updatePastLetters(next)

            //Update the actual values.
            mSameLocation = sameLocation
            mSameLetter = sameLetter
        }

        timer?.startTimer()
        Log.d(javaClass.simpleName, "index is ${index}")
    }

    private fun checkCurrentAnswer() {

        val locationCorrectness = score.updateScore(answer = mAnswerSameLocation, actual = mSameLocation)
        val letterCorrectness = score.updateScore(answer = mAnswerSameLetter, actual = mSameLetter)
        activity?.runOnUiThread {
            val context = context ?: return@runOnUiThread

            // Give a feedback about the correct answer.
            val locationFeedbackColor = getEarlyFeedbackColor(
                correctness = locationCorrectness,
                context = context
            )
            val letterFeedbackColor = getEarlyFeedbackColor(
                correctness = letterCorrectness,
                context = context
            )
            mLocationFeedbackZone?.setBackgroundColor(locationFeedbackColor)
            mLetterFeedbackZone?.setBackgroundColor(letterFeedbackColor)

            // Update score and counters.
            updateTrialCount(score.CorrectCount, score.TotalCount)
            updateScore(score.CorrectCount, score.TotalCount)
            // Reset user's answers.
            mAnswerSameLocation = false
            mAnswerSameLetter = false
        }
    }

    /**
     * Gets the cell in the n-back grid that corresponds to the given index.
     */
    fun getSquare(index: Int): View? {
        return when (index) {
            0 -> view?.findViewById(R.id.case0)
            1 -> view?.findViewById(R.id.case1)
            2 -> view?.findViewById(R.id.case2)
            3 -> view?.findViewById(R.id.case3)
            4 -> view?.findViewById(R.id.case4)
            5 -> view?.findViewById(R.id.case5)
            6 -> view?.findViewById(R.id.case6)
            7 -> view?.findViewById(R.id.case7)
            8 -> view?.findViewById(R.id.case8)
            else -> null
        }
    }

    /**
     * Gets the ID of the vector image that corresponds to the given letter.
     */
    @DrawableRes
    private fun getLetterDrawableId(letter: Char) : Int {
        return when(letter) {
            'c' -> R.drawable.ic_letter_c
            'h' -> R.drawable.ic_letter_h
            'k' -> R.drawable.ic_letter_k
            'l' -> R.drawable.ic_letter_l
            'q' -> R.drawable.ic_letter_q
            'r' -> R.drawable.ic_letter_r
            's' -> R.drawable.ic_letter_s
            't' -> R.drawable.ic_letter_t
            else -> 0
        }
    }

    /**
     * Gets the ID of the N-back location thumbnail
     * that corresponds to the given index.
     */
    @DrawableRes
    private fun getMiniLocationId(index: Int): Int {
        return when (index) {
            0 -> R.drawable.ic_nback_case0
            1 -> R.drawable.ic_nback_case1
            2 -> R.drawable.ic_nback_case2
            3 -> R.drawable.ic_nback_case3
            4 -> R.drawable.ic_nback_case4
            5 -> R.drawable.ic_nback_case5
            6 -> R.drawable.ic_nback_case6
            7 -> R.drawable.ic_nback_case7
            8 -> R.drawable.ic_nback_case8
            else -> 0
        }
    }

    /**
     * Gets the ID of the N-back letter thumbnail
     * that corresponds to the given index.
     */
    @DrawableRes
    private fun getMiniLetterId(index: Int): Int {
        return when (index) {
            0 -> R.drawable.ic_letter_c
            1 -> R.drawable.ic_letter_h
            2 -> R.drawable.ic_letter_k
            3 -> R.drawable.ic_letter_l
            4 -> R.drawable.ic_letter_q
            5 -> R.drawable.ic_letter_r
            6 -> R.drawable.ic_letter_s
            7 -> R.drawable.ic_letter_t
            else -> 0
        }
    }

    private fun loadNBackSettings(): NBackSettings {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val level =
            sharedPreferences.getInt(NBackSettings.NBACK_LEVEL_KEY, NBackBoard.DEFAULT_LEVEL)
        val time_per_trial = sharedPreferences.getInt(
            NBackSettings.NBACK_MILLISECONDS_KEY,
            NBackBoard.DEFAULT_MILLISEC
        )
        return NBackSettings(level, time_per_trial)
    }

    private fun applySettings(settings: NBackSettings, view: View) {
        view.safeFindViewById<TextView>(R.id.status_level_text)?.let {
            it.text = getString(R.string.nback_status_level, settings.level)
        }
    }

    private fun updateTrialCount(doneCount: Int, totalCount: Int) {
        mTrialCountText?.text = getString(R.string.nback_trial_count, doneCount, totalCount)
    }

    private fun updateScore(score: Int, maxPossibleScore: Int) {
        mScoreText?.text = getString(R.string.nback_score, score)
    }

    fun <T:View> Fragment.safeFindViewById(@IdRes id: Int): T? {
        return this.view?.safeFindViewById<View>(id) as? T
    }

    fun <T:View> View.safeFindViewById(@IdRes id: Int): T? {
        return this.findViewById<View>(id) as? T
    }
}
