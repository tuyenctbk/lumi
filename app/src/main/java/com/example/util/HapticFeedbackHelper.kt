package com.example.util

import android.content.Context
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * HapticFeedbackHelper
 *
 * Provides tailored haptic sensations for quiz game interactions, answer validations,
 * button clicks, and celebratory milestone achievements.
 */
object HapticFeedbackHelper {

    private var appContext: Context? = null
    private var vibrator: Vibrator? = null

    fun initialize(context: Context) {
        appContext = context.applicationContext
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    /**
     * Subtle, crisp double-pulse vibration for a correct answer selection.
     */
    fun vibrateCorrect() {
        val vib = vibrator ?: return
        if (!vib.hasVibrator()) return

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Quick double tap: 40ms pulse, 30ms rest, 60ms pulse
                val timings = longArrayOf(0, 40, 30, 60)
                val amplitudes = intArrayOf(0, 180, 0, 240)
                vib.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                vib.vibrate(longArrayOf(0, 40, 30, 60), -1)
            }
        } catch (_: Exception) {
            // Graceful fallback
        }
    }

    /**
     * Gentle buzz / nudge for an incorrect answer attempt.
     */
    fun vibrateIncorrect() {
        val vib = vibrator ?: return
        if (!vib.hasVibrator()) return

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Soft single buzz
                vib.vibrate(VibrationEffect.createOneShot(70, 110))
            } else {
                @Suppress("DEPRECATION")
                vib.vibrate(70)
            }
        } catch (_: Exception) {
            // Graceful fallback
        }
    }

    /**
     * Subtle click feedback for option selections and interactive buttons.
     */
    fun vibrateClick() {
        val vib = vibrator ?: return
        if (!vib.hasVibrator()) return

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vib.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vib.vibrate(VibrationEffect.createOneShot(20, 90))
            } else {
                @Suppress("DEPRECATION")
                vib.vibrate(20)
            }
        } catch (_: Exception) {
            // Graceful fallback
        }
    }

    /**
     * Celebratory fanfare burst haptic pattern for lesson completion or badge unlock.
     */
    fun vibrateCelebration() {
        val vib = vibrator ?: return
        if (!vib.hasVibrator()) return

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0, 50, 40, 70, 40, 120)
                val amplitudes = intArrayOf(0, 160, 0, 200, 0, 255)
                vib.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                vib.vibrate(longArrayOf(0, 50, 40, 70, 40, 120), -1)
            }
        } catch (_: Exception) {
            // Graceful fallback
        }
    }
}
