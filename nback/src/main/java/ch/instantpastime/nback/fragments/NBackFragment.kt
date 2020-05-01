package ch.instantpastime.nback.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager

import ch.instantpastime.nback.R
import ch.instantpastime.nback.core.NBackCountDown
import ch.instantpastime.nback.core.NBackBoard
import ch.instantpastime.nback.core.NBackGame
import ch.instantpastime.nback.core.NBackSound

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
    val nbackSound : NBackSound = NBackSound()
    private var lastIndex: Int = INVALID_INDEX;

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
     * True when it is actually the same location, otherwise false.
     */
    private var mSameLocation: Boolean = false
    /**
     * True when it is actually the same letter, otherwise false.
     */
    private var mSameLetter: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val nbackSettings = loadNBackSettings()
        game = NBackBoard(nbLetters = nbackSound.letterCount, nBackLevel = nbackSettings.level)
        timer?.totalMilliseconds = nbackSettings.time_per_trial
        val view = inflater.inflate(R.layout.fragment_nback, container, false)
        mRestartButton = view.findViewById<Button>(R.id.restart_button)
        mLocationButton = view.findViewById<Button>(R.id.locationButton)
        mLetterButton = view.findViewById<Button>(R.id.letterButton)
        mLocationFeedbackZone = view.findViewById(R.id.nback_location_feedback)
        mLetterFeedbackZone = view.findViewById(R.id.nback_letter_feedback)
        mPauseButton = view.findViewById(R.id.pause_button)
        mTimeBar = view.findViewById<ProgressBar>(R.id.nback_time_bar).apply {
            max = nbackSettings.time_per_trial
        }
        mScoreText = view.findViewById(R.id.status_score_text)
        mTrialCountText = view.findViewById(R.id.status_trial_count_text)

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
                if (lastSquare != null) {
                    context?.let { ctx ->
                        lastSquare.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorIdleSquare))
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
                Toast.makeText(context, context.getString(R.string.stop_game_short), Toast.LENGTH_SHORT).show()
            }
            NBackState.Paused -> {
                mPauseButton?.apply {
                    text = context.getString(R.string.resume_game_short)
                    visibility = View.VISIBLE
                }
                mLocationButton?.isEnabled = false
                mLetterButton?.isEnabled = false
                Toast.makeText(context, context.getString(R.string.pause_game_short), Toast.LENGTH_SHORT).show()
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
                Toast.makeText(context, context.getString(R.string.start_game_short), Toast.LENGTH_SHORT).show()
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
            else -> { }
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
     * Gets a color that corresponds to the actual answer (reality).
     */
    private fun getActualFeedbackColor(answer: Boolean, actual: Boolean, context: Context): Int {
        return if (actual) {
            if (answer) {
                // The user said "same" and it is: answer is correct.
                ContextCompat.getColor(context, R.color.colorNBackCorrect)
            } else {
                // The user didn't say "same" but it is: answer is wrong.
                ContextCompat.getColor(context, R.color.colorNBackActual)
            }
        } else {
            if (answer) {
                // The user said "same" but it's not: answer is wrong.
                ContextCompat.getColor(context, R.color.colorNBackWrong)
            } else {
                // The user didn't say "same" and it's not: answer is correct.
                ContextCompat.getColor(context, R.color.colorTransparent)
            }
        }
    }

    fun nextIndex() {
        val game = this.game ?: return
        val next = game.getNextTrial()
        val (index, sameLocation) = next.location
        val (letterIndex, sameLetter) = next.symbol

        activity?.runOnUiThread {
            val oldSquare = getSquare(lastIndex)
            val newSquare = getSquare(index)
            if (newSquare != null) {
                lastIndex = index
            } else {
                lastIndex = INVALID_INDEX;
            }

            context?.let {context ->
                // Give a feedback about the correct answer.
                score.updateScore(answer = mAnswerSameLocation, actual = mSameLocation)
                score.updateScore(answer = mAnswerSameLetter, actual = mSameLetter)
                val locationFeedbackColor = getActualFeedbackColor(answer = mAnswerSameLocation, actual = mSameLocation, context = context)
                val letterFeedbackColor = getActualFeedbackColor(answer = mAnswerSameLetter, actual = mSameLetter, context = context)
                mLocationFeedbackZone?.setBackgroundColor(locationFeedbackColor)
                mLetterFeedbackZone?.setBackgroundColor(letterFeedbackColor)

                // Colorize the next location.
                oldSquare?.setBackgroundColor(ContextCompat.getColor(context, R.color.colorIdleSquare))
                newSquare?.setBackgroundColor(ContextCompat.getColor(context, R.color.colorActiveSquare))
                // Play or show the next letter.
                nbackSound.playArticle(context, letterIndex)
                updateTrialCount(score.CorrectCount, score.TotalCount)
                updateScore(score.CorrectCount, score.TotalCount)
                when (val c = nbackSound.getLetter(letterIndex)) {
                    null -> { }
                    else -> { Toast.makeText(context, "Sound ${c.toUpperCase()}", Toast.LENGTH_SHORT).show() }
                }
            }
            //Update the actual values.
            mSameLocation = sameLocation
            mSameLetter = sameLetter
            // Reset user's answers.
            mAnswerSameLocation = false
            mAnswerSameLetter = false
        }

        timer?.startTimer()
        Log.d(javaClass.simpleName, "index is ${index}")
    }

    fun getSquare(index: Int): ImageView? {
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

    private fun loadNBackSettings(): NBackSettings {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val level = sharedPreferences.getInt(NBackSettings.NBACK_LEVEL_KEY, NBackBoard.DEFAULT_LEVEL)
        val time_per_trial = sharedPreferences.getInt(NBackSettings.NBACK_MILLISECONDS_KEY, NBackBoard.DEFAULT_MILLISEC)
        return NBackSettings(level, time_per_trial)
    }

    private fun applySettings(settings: NBackSettings, view: View) {
        view.findViewById<TextView>(R.id.status_level_text)?.let {
            it.text = getString(R.string.nback_status_level, settings.level)
        }
    }

    private fun updateTrialCount(doneCount: Int, totalCount: Int) {
        mTrialCountText?.text = getString(R.string.nback_trial_count, doneCount, totalCount)
    }

    private fun updateScore(score: Int, maxPossibleScore: Int) {
        mScoreText?.text = getString(R.string.nback_score, score)
    }
}
