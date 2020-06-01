package ch.instantpastime.nback.core

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.util.Log
import ch.instantpastime.nback.ui.NBackResource
import java.nio.file.Path
import java.nio.file.Paths

class NBackSound {

    private var _player: MediaPlayer? = null
    private val _sounds: List<String> =
        listOf("c.wav", "h.wav", "k.wav", "l.wav", "q.wav", "r.wav", "s.wav", "t.wav")

    val letterCount: Int
        get() = _sounds.size

    fun init() {
        val player = MediaPlayer().apply {
        }
        _player = player
    }

    fun playArticle(context: Context, index: Int): Boolean {
        if (index in 0 until letterCount) {
            val soundPath: Path? = Paths.get(NBackResource.SoundFolderName, _sounds[index])
            if (soundPath != null) {
                val ret = playSound(context, soundPath)
                return ret
            }
        }
        return false
    }

    fun playSound(context: Context, soundPath: Path): Boolean {
        val afd = NBackResource.openAsset(context, soundPath.toString())
        if (afd != null) {
            val ret = playSound(afd)
            return ret
        }
        return false
    }

    fun playSound(afd: AssetFileDescriptor): Boolean {
        _player?.apply {
            reset()

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
        return false
    }

    /**
     * Helper method for debug.
     */
    fun getLetter(index: Int): Char? {
        return if (0 <= index && index < _sounds.size) {
            _sounds[index].firstOrNull()
        } else {
            null
        }
    }

    fun exit() {
        _player?.release()
    }

}
