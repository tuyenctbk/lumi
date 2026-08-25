package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.model.MascotMood
import com.example.model.VocabularyItem
import com.example.ui.components.FocusableCard
import com.example.ui.components.LumiMascot
import com.example.ui.theme.SleekBackground
import com.example.ui.theme.SleekCoral
import com.example.ui.theme.SleekCoralDark
import com.example.ui.theme.SleekEmerald
import com.example.ui.theme.SleekEmeraldDark
import com.example.ui.theme.SleekGold
import com.example.ui.theme.SleekGoldDark
import com.example.ui.theme.SleekOcean
import com.example.ui.theme.SleekOceanDark
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekSurfaceBorder
import com.example.ui.theme.SleekTextDark
import com.example.ui.theme.SleekTextMuted
import com.example.ui.viewmodel.LumiViewModel

/**
 * PronunciationPracticeScreen
 *
 * Practice target language vocabulary pronunciation using Android SpeechRecognizer.
 * Features:
 * - Dynamic microphone permission request flow via Compose ActivityResultLauncher.
 * - Speech-to-Text listening with visual ripple animation.
 * - Pronunciation accuracy comparison with target word translations.
 * - Lumi Mascot reactions based on speaking performance.
 */
@Composable
fun PronunciationPracticeScreen(
    viewModel: LumiViewModel,
    onBack: () -> Unit,
    onFinishLesson: (score: Int, wordsCount: Int) -> Unit
) {
    val context = LocalContext.current
    val targetLanguage by viewModel.targetLanguage.collectAsState()
    val wordProgressList by viewModel.wordProgressList.collectAsState()

    // Pick 5 vocabulary items for practice
    val practiceItems: List<VocabularyItem> = remember(wordProgressList, targetLanguage) {
        viewModel.currentCategoryWords.shuffled().take(5)
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    val currentItem = practiceItems.getOrNull(currentIndex) ?: practiceItems.firstOrNull()
    val targetTranslation: String = currentItem?.translations?.get(targetLanguage.code)
        ?: currentItem?.englishWord
        ?: "Hello"

    // Speech recognition states
    var isListening by remember { mutableStateOf(false) }
    var spokenText by remember { mutableStateOf("") }
    var isCorrect by remember { mutableStateOf<Boolean?>(null) }
    var score by remember { mutableIntStateOf(0) }

    // Microphone Permission Check
    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasMicPermission = granted
        if (!granted) {
            viewModel.speakLumi("I need microphone access to hear your awesome pronunciation!", MascotMood.ENCOURAGING)
        }
    }

    // SpeechRecognizer lifecycle
    val speechRecognizer = remember {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            SpeechRecognizer.createSpeechRecognizer(context)
        } else null
    }

    DisposableEffect(Unit) {
        onDispose {
            speechRecognizer?.destroy()
        }
    }

    fun startListening() {
        if (!hasMicPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            return
        }

        if (speechRecognizer == null) {
            viewModel.speakLumi("Speech recognition is unavailable on this device, but let's practice listening!", MascotMood.ENCOURAGING)
            return
        }

        spokenText = "Listening..."
        isListening = true
        isCorrect = null

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, targetLanguage.code)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Say $targetTranslation")
        }

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) { isListening = true }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() { isListening = false }
            override fun onError(error: Int) {
                isListening = false
                spokenText = "Try speaking closer to mic"
                viewModel.speakLumi("Give it another try! You can do it!", MascotMood.ENCOURAGING)
            }

            override fun onResults(results: Bundle?) {
                isListening = false
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val heard = matches?.firstOrNull() ?: ""
                spokenText = heard

                val matched = heard.equals(targetTranslation, ignoreCase = true) ||
                        heard.contains(targetTranslation, ignoreCase = true) ||
                        targetTranslation.contains(heard, ignoreCase = true)

                isCorrect = matched
                currentItem?.let { item ->
                    viewModel.onAnswerGiven(item.id, matched)
                }

                if (matched) {
                    score += 20
                    viewModel.speakLumi("Spot on! Outstanding pronunciation!", MascotMood.HAPPY)
                } else {
                    viewModel.speakLumi("Good try! Let me demonstrate the sound once more.", MascotMood.ENCOURAGING)
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        try {
            speechRecognizer.startListening(intent)
        } catch (e: Exception) {
            isListening = false
            spokenText = "Speech engine error"
        }
    }

    // Pulse animation while mic is active
    val pulseTransition = rememberInfiniteTransition(label = "mic_pulse")
    val micScale by pulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isListening) 1.25f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mic_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SleekBackground)
            .testTag("pronunciation_practice_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FocusableCard(
                    onClick = onBack,
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = SleekSurface,
                    unfocusedBorderColor = SleekSurfaceBorder,
                    focusedBorderColor = SleekEmerald,
                    testTag = "pronunciation_back_button"
                ) {
                    Box(modifier = Modifier.padding(12.dp), contentAlignment = Alignment.Center) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = SleekTextDark)
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Pronunciation Lab",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = SleekTextDark
                    )
                    Text(
                        text = "Word ${currentIndex + 1} of ${practiceItems.size}",
                        fontSize = 12.sp,
                        color = SleekTextMuted,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = SleekGold.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "★ $score XP",
                        fontWeight = FontWeight.Black,
                        color = SleekGoldDark,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            // Target Vocabulary Card
            currentItem?.let { item ->
                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = SleekSurface),
                    border = BorderStroke(1.5.dp, SleekSurfaceBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = item.emoji, fontSize = 64.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = targetTranslation,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            color = SleekTextDark,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = item.englishWord,
                            fontSize = 14.sp,
                            color = SleekTextMuted,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Audio Playback
                        FocusableCard(
                            onClick = { viewModel.speakLumi(targetTranslation, MascotMood.HAPPY) },
                            shape = RoundedCornerShape(16.dp),
                            backgroundColor = SleekOcean.copy(alpha = 0.12f),
                            unfocusedBorderColor = SleekOcean.copy(alpha = 0.3f),
                            focusedBorderColor = SleekOcean,
                            testTag = "listen_native_audio_button"
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null, tint = SleekOceanDark)
                                Text("Listen Native Audio", fontWeight = FontWeight.Bold, color = SleekOceanDark, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            // Mascot Companion Reaction
            LumiMascot(
                mood = if (isCorrect == true) MascotMood.HAPPY else if (isListening) MascotMood.THINKING else MascotMood.ENCOURAGING,
                speechBubble = if (isListening) "I'm listening! Speak now..." else if (isCorrect == true) "Spot on! Perfect!" else "Tap the Mic to speak!",
                size = 110.dp
            )

            // Spoken Transcription & Mic Trigger
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (spokenText.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isCorrect == true) SleekEmerald.copy(alpha = 0.15f) else SleekSurface,
                        border = BorderStroke(1.dp, if (isCorrect == true) SleekEmerald else SleekSurfaceBorder)
                    ) {
                        Text(
                            text = "You said: \"$spokenText\"",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCorrect == true) SleekEmeraldDark else SleekTextDark,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (!hasMicPermission) {
                    Button(
                        onClick = { permissionLauncher.launch(Manifest.permission.RECORD_AUDIO) },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SleekCoral),
                        modifier = Modifier.testTag("grant_mic_permission_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Icon(imageVector = Icons.Default.MicOff, contentDescription = null)
                            Text("Grant Mic Permission to Speak", fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    Surface(
                        onClick = { startListening() },
                        shape = CircleShape,
                        color = if (isListening) SleekCoral else SleekEmerald,
                        border = BorderStroke(3.dp, if (isListening) SleekCoralDark else SleekEmeraldDark),
                        shadowElevation = 8.dp,
                        modifier = Modifier
                            .size(76.dp)
                            .scale(micScale)
                            .testTag("mic_record_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Record Speech",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }
            }

            // Next Word or Finish
            Button(
                onClick = {
                    if (currentIndex < practiceItems.lastIndex) {
                        currentIndex++
                        spokenText = ""
                        isCorrect = null
                    } else {
                        onFinishLesson(score, practiceItems.size)
                    }
                },
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SleekOcean),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("pronunciation_next_button")
            ) {
                Text(
                    text = if (currentIndex < practiceItems.lastIndex) "Next Word ➔" else "Finish Practice 🎉",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}
