package ch.instantpastime

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ScoreActivity : AppCompatActivity() {

    companion object {
        const val SCORE_ARG: String = "score"
        const val GAME_NAME_ARG: String = "game_name"
    }

    var gameName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)

        val score = intent.getIntExtra(SCORE_ARG, -1)
        gameName = intent.getStringExtra(GAME_NAME_ARG) ?: "InstantPastime"

        findViewById<TextView>(R.id.score_final_text).apply {
            text = score.toString()
        }
        findViewById<Button>(R.id.score_replay_button).apply {
            setOnClickListener { replayButtonClicked() }
        }
        findViewById<Button>(R.id.score_install_button).apply {
            setOnClickListener { installButtonClicked() }
        }
    }

    private fun replayButtonClicked() {
        finish()
    }

    private fun installButtonClicked() {
        InstallDialogHelper.showDialog(this, gameName, closed = {
            // Also close the score activity if needed.
            // finish()
        })
    }
}
