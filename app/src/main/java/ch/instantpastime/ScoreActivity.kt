package ch.instantpastime

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class ScoreActivity : AppCompatActivity() {

    companion object {
        const val SCORE_ARG: String = "score"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)

        val score = intent.getIntExtra(SCORE_ARG, -1)

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
        Toast.makeText(this, "Installing...", Toast.LENGTH_SHORT).show()
    }
}
