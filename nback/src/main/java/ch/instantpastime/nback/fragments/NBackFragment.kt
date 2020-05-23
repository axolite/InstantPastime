package ch.instantpastime.nback.fragments

import android.app.AlertDialog
import android.content.Context
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.ColorInt
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import ch.instantpastime.AsyncRun
import ch.instantpastime.nback.R
import ch.instantpastime.nback.core.*
import ch.instantpastime.nback.ui.NBackResource
import ch.instantpastime.nback.ui.NBackResource.getSquare

/**
 * A simple [Fragment] subclass.
 * Use the [NBackFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NBackFragment : Fragment(), INBackController {

    private enum class Transition {
        Start, Stop, Pause, Resume
    }

    private var state: NBackState = NBackState.Idle
    private var board: NBackBoard? = null

    /**
     * Timer for the N-Back count-down.
     * Must be started from the UI thread (sic).
     */
    private var timer: NBackCountDown? = null
    val nbackSound: NBackSound = NBackSound()

    private var mLocationFeedbackZone: ImageView? = null
    private var mLetterFeedbackZone: ImageView? = null

    private var mPauseButton: Button? = null
    private var mRestartButton: Button? = null
    private var mLocationButton: Button? = null
    private var mLetterButton: Button? = null
    private var mScoreText: TextView? = null
    private var mTrialCountText: TextView? = null
    private var mPastLocationsPanel: LinearLayout? = null
    private var mPastLettersPanel: LinearLayout? = null
    private var mLastLocationSquare: ImageView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val nbackSettings = loadNBackSettings()
        board = NBackBoard(
            nbLetters = nbackSound.letterCount, nBackLevel = nbackSettings.level,
            uiControl = this
        )
        timer?.totalMilliseconds = nbackSettings.time_per_trial
        val view = inflater.inflate(R.layout.fragment_nback, container, false)
        mRestartButton = view.safeFindViewById<Button>(R.id.restart_button)
        mLocationButton = view.safeFindViewById<Button>(R.id.locationButton)
        mLetterButton = view.safeFindViewById<Button>(R.id.letterButton)
        mLocationFeedbackZone = view.safeFindViewById(R.id.nback_location_feedback)
        mLetterFeedbackZone = view.safeFindViewById(R.id.nback_letter_feedback)
        mPauseButton = view.safeFindViewById(R.id.pause_button)
        mPastLocationsPanel = view.safeFindViewById(R.id.nback_past_locations_panel)
        mPastLettersPanel = view.safeFindViewById(R.id.nback_past_letters_panel)
        mScoreText = view.safeFindViewById(R.id.status_score_text)
        mTrialCountText = view.safeFindViewById(R.id.status_trial_count_text)

        context?.let {
            nbackSound.init(it)
            updateControls(NBackState.Idle)
        }

        mRestartButton?.setOnClickListener { restartButtonClicked() }
        mPauseButton?.setOnClickListener { pauseButtonClicked() }
        mLocationButton?.setOnClickListener { locationButtonClicked() }
        mLetterButton?.setOnClickListener { letterButtonClicked() }
        applySettings(nbackSettings, view)

        timer = NBackCountDown(totalMilliseconds = nbackSettings.time_per_trial,
            onTick = {
            },
            onFinish = {
                board?.tick()
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
        val newState = switchState(oldState, transition)
        state = newState
        switchAction(oldState = oldState, newState = newState)
    }

    private fun switchState(state: NBackState, transition: Transition): NBackState {
        return when (state) {
            NBackState.Idle -> when (transition) {
                Transition.Start -> NBackState.Running
                else -> state
            }
            NBackState.Running -> when (transition) {
                Transition.Pause -> NBackState.Paused
                Transition.Stop -> NBackState.Idle
                else -> state
            }
            NBackState.Paused -> when (transition) {
                Transition.Resume -> NBackState.Running
                Transition.Stop -> NBackState.Idle
                else -> state
            }
        }
    }

    private fun switchAction(oldState: NBackState, newState: NBackState) {
        if (oldState != newState) {
            when (newState) {
                NBackState.Idle -> {
                    // Update the controls.
                    updateControls(newState)
                    stopTimer()
                    board?.reset()
                }
                NBackState.Running -> {
                    // Update the controls.
                    updateControls(newState)
                    when (oldState) {
                        NBackState.Paused -> startTimer()
                        NBackState.Idle -> board?.drawNext()
                        else -> activity?.runOnUiThread {
                            Toast.makeText(
                                context, "Wrong source state: $oldState",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                NBackState.Paused -> {
                    // Update the controls.
                    updateControls(newState)
                    stopTimer()
                }
            }
        }
    }

    private fun updateControls(state: NBackState) {
        activity?.runOnUiThread {
            val context = context ?: return@runOnUiThread
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
                    mPastLocationsPanel?.apply {
                        removeAllViews()
                    }
                    mPastLettersPanel?.apply {
                        removeAllViews()
                    }
                    clearLocationSquare(mLastLocationSquare)
                    clearCorrection()
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
                        isEnabled = board?.expectAnswer == true
                        visibility = View.VISIBLE
                    }
                    mLetterButton?.apply {
                        isEnabled = board?.expectAnswer == true
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
    }

    private fun restartButtonClicked() {
        AsyncRun {
            when (state) {
                NBackState.Running, NBackState.Paused -> processTransition(Transition.Stop)
                NBackState.Idle -> processTransition(Transition.Start)
                else -> {
                }
            }
        }
    }

    private fun pauseButtonClicked() {
        AsyncRun {
            when (state) {
                NBackState.Running -> processTransition(Transition.Pause)
                NBackState.Paused -> processTransition(Transition.Resume)
                else -> {
                }
            }
        }
    }

    private fun locationButtonClicked() {
        val answer = board?.toggleLocationAnswer()
        context?.let {
            val color = getAnswerFeedbackColor(answer, it)
            mLocationFeedbackZone?.setBackgroundColor(color)
        }
    }

    private fun letterButtonClicked() {
        val answer = board?.toggleLetterAnswer()
        context?.let {
            val color = getAnswerFeedbackColor(answer, it)
            mLetterFeedbackZone?.setBackgroundColor(color)
        }
    }

    /**
     * Gets a color that corresponds to the user's answer.
     */
    private fun getAnswerFeedbackColor(answer: Boolean?, context: Context): Int {
        return if (answer == true) {
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
    private fun getEarlyFeedbackColor(correctness: NBackScore.Correctness?, context: Context): Int {
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
    private fun getActualFeedbackColor(correctness: NBackScore.Correctness, context: Context): Int {
        return when (correctness) {
            NBackScore.Correctness.CORRECT_SAME ->
                // The user said "same" and it is: answer is correct.
                ContextCompat.getColor(context, R.color.colorNBackCorrect)

            NBackScore.Correctness.WRONG_ACTUALLY_SAME ->
                // The user didn't say "same" but it is: answer is wrong.
                ContextCompat.getColor(context, R.color.colorNBackActual)

            NBackScore.Correctness.WRONG_ACTUALLY_DIFFERENT ->
                // The user said "same" but it's not: answer is wrong.
                ContextCompat.getColor(context, R.color.colorNBackWrong)

            NBackScore.Correctness.CORRECT_DIFFERENT ->
                // The user didn't say "same" and it's not: answer is correct.
                ContextCompat.getColor(context, R.color.colorTransparent)
        }
    }

    private fun updatePastLocations(trial: NBackTrial) {

        val context = context ?: return
        val drawableId = NBackResource.getMiniLocationId(trial.location.index)
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
        val board = board ?: return
        mPastLocationsPanel?.let { panel ->
            updatePastViewGroup(panel = panel, newView = imgView, maxCount = board.Level)
            panel.addView(imgView)
        }
    }

    private fun updatePastLetters(trial: NBackTrial) {
        val context = context ?: return
        val drawableId = NBackResource.getMiniLetterId(trial.symbol.index)
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
        val board = board ?: return
        mPastLettersPanel?.let { panel ->
            updatePastViewGroup(panel = panel, newView = imgView, maxCount = board.Level)
            panel.addView(imgView)
        }
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
            } else {
                newView.alpha = ALPHA_NO_FOCUS
            }
        }
    }

    override fun onCorrectResult(
        locationCorrectness: NBackScore.Correctness?,
        letterCorrectness: NBackScore.Correctness?
    ) {
        // Update the UI with the user's current answer.
        updateWithAnswer(locationCorrectness, letterCorrectness)
        val allCorrect = NBackScore.allCorrect(listOf(locationCorrectness, letterCorrectness))

        if (allCorrect == true) {
            continueOnCorrect()
        } else if (allCorrect == null) {
            board?.drawNext()
        } else {
            continueOnIncorrect()
        }
    }

    override fun onFinished(correct: Int, total: Int) {
        AlertDialog.Builder(context)
            .setTitle("Finished")
            .setPositiveButton("Got it") { _, _ -> }
            .setMessage("Score: $correct/$total")
            .show()
    }

    private fun continueOnCorrect() {
        // Clear the green color.
        AsyncRun {
            Thread.sleep(500)
            activity?.runOnUiThread {
                val context = context ?: return@runOnUiThread
                val transparentColor = ContextCompat.getColor(context, R.color.colorTransparent)
                mLocationFeedbackZone?.setBackgroundColor(transparentColor)
                mLetterFeedbackZone?.setBackgroundColor(transparentColor)
            }
        }
        // Continue without waiting the delay.
        board?.drawNext()
    }

    private fun continueOnIncorrect() {
        activity?.runOnUiThread {
            updateAnswerZone(false)
        }
        AsyncRun {
            Thread.sleep(500)
            // Clear correction of the previous trial.
            activity?.runOnUiThread {
                clearCorrection()
            }
            // Continue after the delay.
            board?.drawNext()
        }
    }

    private fun clearCorrection() {
        val context = context ?: return
        val transparentColor = ContextCompat.getColor(context, R.color.colorTransparent)
        mLocationFeedbackZone?.setBackgroundColor(transparentColor)
        mLetterFeedbackZone?.setBackgroundColor(transparentColor)
        updateAnswerZone(board?.expectAnswer == true)
    }

    private fun clearLocationSquare(oldSquare: ImageView?) {
        val context = context ?: return
        oldSquare?.setBackgroundColor(
            ContextCompat.getColor(context, R.color.colorIdleSquare)
        )
        oldSquare?.setImageDrawable(
            ContextCompat.getDrawable(
                context,
                R.drawable.ic_letter_placeholder
            )
        )
    }

    private fun updateAnswerZone(isEnabled: Boolean) {
        activity?.runOnUiThread {
            mLocationButton?.isEnabled = isEnabled
            mLetterButton?.isEnabled = isEnabled
        }
    }

    private fun updateWithTrial(last: NBackTrial?, next: NBackTrial) {
        activity?.runOnUiThread {
            val context = context ?: return@runOnUiThread
            val oldSquare = mLastLocationSquare
            val newSquare = getSquare(next) as? ImageView

            // Colorize the next location.
            clearLocationSquare(oldSquare)
            newSquare?.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.colorActiveSquare
                )
            )
            // Play or show the next letter.
            nbackSound.playArticle(context, next.symbol.index)
            when (val c = nbackSound.getLetter(next.symbol.index)) {
                null -> {
                }
                else -> {
                    val drawableId = NBackResource.getLetterDrawableId(c)
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
            mLastLocationSquare = newSquare
            updateAnswerZone(board?.expectAnswer == true)
        }
    }

    override fun onNextTrial(last: NBackTrial?, next: NBackTrial) {
        updateWithTrial(last = last, next = next)
        startTimer()
    }

    private fun updateWithAnswer(
        locationCorrectness: NBackScore.Correctness?,
        letterCorrectness: NBackScore.Correctness?
    ) {
        activity?.runOnUiThread {
            val board = board ?: return@runOnUiThread
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
            updateTrialCount(board.CorrectCount, board.drawCount)
            updateScore(board.CorrectCount, board.drawCount)
        }
    }

    private fun loadNBackSettings(): NBackSettings {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val level =
            sharedPreferences.getInt(NBackSettings.NBACK_LEVEL_KEY, NBackRun.DEFAULT_LEVEL)
        val time_per_trial = sharedPreferences.getInt(
            NBackSettings.NBACK_MILLISECONDS_KEY,
            NBackRun.DEFAULT_MILLISEC
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

    private fun startTimer() {
        activity?.runOnUiThread {
            timer?.startTimer()
        }
    }

    private fun stopTimer() {
        activity?.runOnUiThread {
            timer?.stopTimer()
        }
    }

    fun <T : View> Fragment.safeFindViewById(@IdRes id: Int): T? {
        return this.view?.safeFindViewById<View>(id) as? T
    }

    fun <T : View> View.safeFindViewById(@IdRes id: Int): T? {
        return this.findViewById<View>(id) as? T
    }
}
