package com.example.audio

import android.content.Context

/**
 * SoundFxHelper
 *
 * Facade for procedural high-pitched positive feedback audio,
 * celebratory fanfares, and interaction cues.
 */
object SoundFxHelper {
    private var soundManager: SoundManager? = null

    fun initialize(context: Context) {
        soundManager = SoundManager.getInstance(context)
    }

    fun setSoundEffectsEnabled(enabled: Boolean) {
        soundManager?.isSoundEnabled = enabled
    }

    fun playCorrectChime() {
        soundManager?.playPositiveAnswerChime()
    }

    fun playLessonCompleteFanfare() {
        soundManager?.playLessonCompleteFanfare()
    }

    fun playCelebrationFanfare() {
        soundManager?.playLessonCompleteFanfare()
    }

    fun playPop() {
        soundManager?.playPop()
    }

    fun playHoverBoop() {
        soundManager?.playPop()
    }

    fun playWrongOops() {
        soundManager?.playEncouragingOops()
    }

    fun playStarBurst() {
        soundManager?.playStarBurst()
    }

    fun playSoundUrl(url: String, onCompletion: (() -> Unit)? = null) {
        soundManager?.playSoundUrl(url, onCompletion)
    }
}
