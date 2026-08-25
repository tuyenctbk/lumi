package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.sin

/**
 * SoundManager
 *
 * Plays high-pitched, positive audio feedback whenever a child completes a lesson
 * or gets an answer correct to reinforce engagement.
 * Also manages real-world sound effects (animals, vehicles, nature) via MediaPlayer / Freesound.
 */
class SoundManager private constructor(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.Default)
    private var activeMediaPlayer: MediaPlayer? = null

    @Volatile
    var isSoundEnabled: Boolean = true

    companion object {
        private const val TAG = "SoundManager"

        @Volatile
        private var instance: SoundManager? = null

        fun getInstance(context: Context): SoundManager {
            return instance ?: synchronized(this) {
                instance ?: SoundManager(context.applicationContext).also { instance = it }
            }
        }
    }

    /**
     * Plays a high-pitched, sparkling positive feedback chime (E6 -> G#6 -> B6 -> E7)
     * whenever a child answers correctly.
     */
    fun playPositiveAnswerChime() {
        if (!isSoundEnabled) return
        scope.launch {
            playTones(
                listOf(
                    Tone(freq = 1318.51, durationMs = 70, volume = 0.5f), // E6
                    Tone(freq = 1661.22, durationMs = 70, volume = 0.55f), // G#6
                    Tone(freq = 1975.53, durationMs = 85, volume = 0.6f), // B6
                    Tone(freq = 2637.02, durationMs = 240, volume = 0.65f) // E7 (High sparkle)
                )
            )
        }
    }

    /**
     * Plays a grand, triumphant high-pitched celebration fanfare when completing a lesson or island!
     */
    fun playLessonCompleteFanfare() {
        if (!isSoundEnabled) return
        scope.launch {
            playTones(
                listOf(
                    Tone(freq = 1046.50, durationMs = 110, volume = 0.5f), // C6
                    Tone(freq = 1318.51, durationMs = 110, volume = 0.55f), // E6
                    Tone(freq = 1567.98, durationMs = 120, volume = 0.6f), // G6
                    Tone(freq = 2093.00, durationMs = 260, volume = 0.7f), // C7
                    Tone(freq = 1760.00, durationMs = 110, volume = 0.6f), // A6
                    Tone(freq = 2093.00, durationMs = 120, volume = 0.65f), // C7
                    Tone(freq = 2637.02, durationMs = 450, volume = 0.75f) // E7
                )
            )
        }
    }

    /**
     * Plays a playful bubble pop sound for UI interactions.
     */
    fun playPop() {
        if (!isSoundEnabled) return
        scope.launch {
            playTones(
                listOf(
                    Tone(freq = 880.0, durationMs = 30, volume = 0.35f),
                    Tone(freq = 1760.0, durationMs = 50, volume = 0.4f)
                )
            )
        }
    }

    /**
     * Plays a gentle, encouraging descent when an answer is incorrect to keep spirits high.
     */
    fun playEncouragingOops() {
        if (!isSoundEnabled) return
        scope.launch {
            playTones(
                listOf(
                    Tone(freq = 440.0, durationMs = 100, volume = 0.35f),
                    Tone(freq = 370.0, durationMs = 160, volume = 0.3f)
                )
            )
        }
    }

    /**
     * Plays a star collection burst with high-frequency shimmer.
     */
    fun playStarBurst() {
        if (!isSoundEnabled) return
        scope.launch {
            playTones(
                listOf(
                    Tone(freq = 1567.98, durationMs = 60, volume = 0.45f),
                    Tone(freq = 2093.00, durationMs = 80, volume = 0.55f),
                    Tone(freq = 2793.83, durationMs = 180, volume = 0.65f) // F7
                )
            )
        }
    }

    /**
     * Plays a real sound effect stream (e.g. from Freesound API preview URL or fallback).
     */
    fun playSoundUrl(url: String, onCompletion: (() -> Unit)? = null) {
        scope.launch(Dispatchers.Main) {
            try {
                stopActiveSound()
                val player = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .build()
                    )
                    setDataSource(context, Uri.parse(url))
                    setOnPreparedListener { mp ->
                        mp.start()
                    }
                    setOnCompletionListener { mp ->
                        mp.release()
                        if (activeMediaPlayer == mp) {
                            activeMediaPlayer = null
                        }
                        onCompletion?.invoke()
                    }
                    setOnErrorListener { mp, what, extra ->
                        Log.w(TAG, "MediaPlayer error: what=$what extra=$extra")
                        mp.release()
                        if (activeMediaPlayer == mp) {
                            activeMediaPlayer = null
                        }
                        false
                    }
                    prepareAsync()
                }
                activeMediaPlayer = player
            } catch (e: Exception) {
                Log.e(TAG, "Failed to play sound url: $url", e)
                onCompletion?.invoke()
            }
        }
    }

    fun stopActiveSound() {
        try {
            activeMediaPlayer?.let {
                if (it.isPlaying) it.stop()
                it.release()
            }
        } catch (_: Exception) {}
        activeMediaPlayer = null
    }

    private data class Tone(val freq: Double, val durationMs: Int, val volume: Float = 0.5f)

    private suspend fun playTones(tones: List<Tone>) = withContext(Dispatchers.Default) {
        val sampleRate = 44100
        val totalMs = tones.sumOf { it.durationMs }
        val totalSamples = (sampleRate * (totalMs / 1000.0)).toInt()
        val buffer = ShortArray(totalSamples)

        var sampleIndex = 0
        for (tone in tones) {
            val noteSamples = (sampleRate * (tone.durationMs / 1000.0)).toInt()
            for (i in 0 until noteSamples) {
                if (sampleIndex >= buffer.size) break
                // Smooth attack and decay envelope to eliminate clicking
                val attack = (i.toDouble() / (sampleRate * 0.006)).coerceIn(0.0, 1.0)
                val decay = ((noteSamples - i).toDouble() / (sampleRate * 0.012)).coerceIn(0.0, 1.0)
                val envelope = attack * decay
                val angle = 2.0 * Math.PI * i / (sampleRate / tone.freq)
                val sampleValue = (sin(angle) * Short.MAX_VALUE * tone.volume * envelope).toInt()
                buffer[sampleIndex++] = sampleValue.toShort()
            }
        }

        try {
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
            Thread.sleep(totalMs.toLong() + 30)
            audioTrack.release()
        } catch (e: Exception) {
            Log.w(TAG, "AudioTrack synthesis error", e)
        }
    }
}
