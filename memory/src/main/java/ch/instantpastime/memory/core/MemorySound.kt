package ch.instantpastime.memory.core

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import ch.instantpastime.memory.MemoryActivity.Companion.memorySettings
import ch.instantpastime.memory.ui.MemoryResource
import java.nio.file.Path
import java.nio.file.Paths

class MemorySound() {

    private var _player: MediaPlayer? = null
    private val _sounds: List<String> =
        listOf("intro.wav", "match.wav", "fail.wav", "endgame.wav")

    val SoundsCount: Int
        get() = _sounds.size

    init {
        val player = MediaPlayer().apply {
        }
        _player = player
    }

    fun playSound(context: Context, index: Int): Boolean {
        if ((index in 0 until SoundsCount) and (memorySettings.soundOn)) {
            _player?.apply {
                reset()
                val soundPath: Path? = Paths.get(MemoryResource.SoundFolderName, _sounds[index])
                if (soundPath != null) {
                    val afd = MemoryResource.openAsset(context, soundPath.toString())
                    if (afd != null) {

                        // Set the sound file to play.
                        try {
                            setDataSource(afd)
                        } catch (ex: Exception) {
                            Log.d(
                                javaClass.simpleName,
                                "Error setting player source",
                                ex
                            )
                        }

                        // Set an error listener.
                        setOnErrorListener { mp, what, extra ->
                            Log.d(javaClass.simpleName, "Player error occurred")
                            true
                        }

                        // Prepare and play when player is prepared.
                        prepareAsync()
                        setOnPreparedListener {
                            it.start()
                            Log.d(javaClass.simpleName, "Player is prepared")
                        }
                        return true
                    }
                }
            }
        }
        return false
    }

    fun exit() {
        _player?.release()
    }

}
