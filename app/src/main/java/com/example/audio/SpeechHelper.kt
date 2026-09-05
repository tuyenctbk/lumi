package com.example.audio

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.example.model.TargetLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class SpeechHelper(context: Context) {
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private val callbacks = mutableMapOf<String, () -> Unit>()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _isOfflineMode = MutableStateFlow(true)
    val isOfflineMode: StateFlow<Boolean> = _isOfflineMode.asStateFlow()

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isInitialized = true
                tts?.language = Locale.US
                tts?.setPitch(1.65f) // Lumi's signature high-pitched, cheerful voice setting
                tts?.setSpeechRate(0.92f)
                applyOfflineVoiceOptimization()
            }
        }

        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _isSpeaking.value = true
            }

            override fun onDone(utteranceId: String?) {
                _isSpeaking.value = false
                utteranceId?.let { id ->
                    callbacks.remove(id)?.invoke()
                }
            }

            override fun onError(utteranceId: String?) {
                _isSpeaking.value = false
                utteranceId?.let { id ->
                    callbacks.remove(id)
                }
            }
        })
    }

    fun setOfflineMode(enabled: Boolean) {
        _isOfflineMode.value = enabled
        if (enabled) {
            applyOfflineVoiceOptimization()
        }
    }

    private fun applyOfflineVoiceOptimization() {
        if (!isInitialized) return
        try {
            // Check for locally installed, non-network dependent voice
            tts?.voices?.find { !it.isNetworkConnectionRequired }?.let { localVoice ->
                tts?.voice = localVoice
            }
        } catch (_: Exception) {
            // Fallback gracefully on standard engine default
        }
    }

    /**
     * Speaks in Lumi's high-pitched, cheerful companion voice to guide lessons.
     */
    fun speakLumi(text: String, onDone: (() -> Unit)? = null) {
        speakInstruction(text, flush = true, onDone = onDone)
    }

    /**
     * Dynamic queue-flushing TTS speech synthesizer for Lumi's high-pitched instructions.
     */
    fun speakInstruction(instructionText: String, flush: Boolean = true, onDone: (() -> Unit)? = null) {
        if (!isInitialized) return
        tts?.setPitch(1.65f) // High-pitched child-friendly mascot voice
        tts?.setSpeechRate(0.92f)
        tts?.language = Locale.US
        val queueMode = if (flush) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
        val utteranceId = "lumi_instruction_${System.currentTimeMillis()}"
        if (onDone != null) {
            callbacks[utteranceId] = onDone
        }
        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
        }
        tts?.speak(instructionText, queueMode, params, utteranceId)
    }

    /**
     * Speaks a target vocabulary word in its native language with crystal-clear phonetic pronunciation.
     */
    fun speakWord(word: String, targetLanguage: TargetLanguage, onSpeechDone: (() -> Unit)? = null) {
        if (!isInitialized) return

        val locale = when (targetLanguage) {
            TargetLanguage.SPANISH -> Locale("es", "ES")
            TargetLanguage.FRENCH -> Locale("fr", "FR")
            TargetLanguage.GERMAN -> Locale("de", "DE")
            TargetLanguage.ITALIAN -> Locale("it", "IT")
            TargetLanguage.JAPANESE -> Locale("ja", "JP")
            TargetLanguage.KOREAN -> Locale("ko", "KR")
            TargetLanguage.MANDARIN -> Locale("zh", "CN")
            TargetLanguage.ENGLISH -> Locale("en", "US")
            TargetLanguage.VIETNAMESE -> Locale("vi", "VN")
        }

        // Set clear native pronunciation
        try {
            if (_isOfflineMode.value) {
                // In offline mode, find an installed local on-device voice matching language
                val localVoice = tts?.voices?.firstOrNull { voice ->
                    !voice.isNetworkConnectionRequired &&
                            (voice.locale.language.equals(locale.language, ignoreCase = true))
                }
                if (localVoice != null) {
                    tts?.voice = localVoice
                } else {
                    tts?.language = locale
                }
            } else {
                tts?.language = locale
            }
        } catch (_: Exception) {
            tts?.language = locale
        }

        tts?.setPitch(1.35f) // High, engaging tone
        tts?.setSpeechRate(0.85f) // Slightly slower for clear child phonetics

        val utteranceId = "word_${System.currentTimeMillis()}"
        if (onSpeechDone != null) {
            callbacks[utteranceId] = onSpeechDone
        }
        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
        }
        tts?.speak(word, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
    }

    fun stop() {
        tts?.stop()
        _isSpeaking.value = false
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}
