package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FormatColorFill
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LearningCategory
import com.example.model.MascotMood
import com.example.model.VocabularyItem
import com.example.ui.components.ConfettiCanvas
import com.example.ui.components.FocusableCard
import com.example.ui.components.GameOverView
import com.example.ui.components.LumiMascot
import com.example.ui.theme.SleekBackground
import com.example.ui.theme.SleekCoral
import com.example.ui.theme.SleekEmerald
import com.example.ui.theme.SleekEmeraldDark
import com.example.ui.theme.SleekGold
import com.example.ui.theme.SleekGoldDark
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekSurfaceBorder
import com.example.ui.theme.SleekTextDark
import com.example.ui.theme.SleekTextMuted
import com.example.ui.viewmodel.LumiViewModel
import kotlinx.coroutines.delay

@Composable
fun ColorMixerScreen(
    viewModel: LumiViewModel,
    onBack: () -> Unit
) {
    val targetLanguage by viewModel.targetLanguage.collectAsState()
    val mascotMood by viewModel.mascotMood.collectAsState()
    val mascotBubble by viewModel.mascotSpeechBubble.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()

    val colorWords = remember { viewModel.getWordsForCategory(LearningCategory.COLORS) }

    var currentRound by remember { mutableIntStateOf(1) }
    val totalRounds = 4
    var correctCount by remember { mutableIntStateOf(0) }
    var isGameOver by remember { mutableStateOf(false) }

    var targetColorItem by remember { mutableStateOf(colorWords.first()) }
    var options by remember { mutableStateOf(colorWords.shuffled()) }
    var paintedColor by remember { mutableStateOf<Color?>(null) }
    var confettiTrigger by remember { mutableStateOf(false) }

    fun nextRound() {
        val target = colorWords.shuffled().first()
        targetColorItem = target
        options = colorWords.shuffled()
        paintedColor = null
        confettiTrigger = false
    }

    LaunchedEffect(currentRound) {
        nextRound()
        delay(400)
        val targetWord = targetColorItem.translations[targetLanguage.code] ?: targetColorItem.englishWord
        viewModel.speakLumi("Paint the easel $targetWord!")
        delay(1200)
        viewModel.speakWord(targetColorItem)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SleekBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    FocusableCard(
                        onClick = onBack,
                        shape = RoundedCornerShape(16.dp),
                        backgroundColor = SleekSurface,
                        unfocusedBorderColor = SleekSurfaceBorder,
                        focusedBorderColor = SleekCoral,
                        testTag = "color_mixer_back_button"
                    ) {
                        Box(modifier = Modifier.padding(12.dp), contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = SleekTextDark
                            )
                        }
                    }

                    Column {
                        Text(
                            text = "🎨 Color Magic Mixer",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = SleekTextDark
                        )
                        Text(
                            text = "Round $currentRound of $totalRounds",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = SleekCoral
                        )
                    }
                }
            }

            if (!isGameOver) {
                Spacer(modifier = Modifier.height(16.dp))

                // Magic Easel Stage
                val targetText = targetColorItem.translations[targetLanguage.code] ?: targetColorItem.englishWord
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(32.dp),
                        color = paintedColor ?: SleekSurface,
                        border = BorderStroke(2.dp, if (paintedColor != null) Color.Transparent else SleekSurfaceBorder),
                        shadowElevation = 6.dp,
                        modifier = Modifier.size(150.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.FormatColorFill,
                                contentDescription = "Color Splash",
                                tint = if (paintedColor != null) Color.White else SleekTextMuted,
                                modifier = Modifier.size(72.dp)
                            )
                        }
                    }
                }

                Text(
                    text = "Pick the bucket for: \"$targetText\"",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = SleekTextDark,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Paint Buckets Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    options.forEach { opt ->
                        val targetWord = opt.translations[targetLanguage.code] ?: opt.englishWord
                        val isCorrect = opt.id == targetColorItem.id

                        FocusableCard(
                            onClick = {
                                if (paintedColor == null) {
                                    val chosenColor = Color(opt.colorHex)
                                    paintedColor = chosenColor
                                    viewModel.onAnswerGiven(targetColorItem.id, isCorrect)
                                    if (isCorrect) {
                                        correctCount++
                                        confettiTrigger = true
                                    }

                                    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                        if (currentRound < totalRounds) {
                                            currentRound++
                                        } else {
                                            isGameOver = true
                                            viewModel.onSessionCompleted("color_mixer", totalRounds, correctCount, 30)
                                        }
                                    }, 2200)
                                }
                            },
                            shape = RoundedCornerShape(26.dp),
                            backgroundColor = Color(opt.colorHex),
                            unfocusedBorderColor = Color(opt.colorHex),
                            focusedBorderColor = SleekTextDark,
                            focusedScale = 1.12f,
                            elevation = 4.dp,
                            modifier = Modifier
                                .weight(1f)
                                .height(160.dp)
                                .testTag("color_bucket_${opt.id}")
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "🪣", fontSize = 42.sp)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = targetWord,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                GameOverView(
                    correctCount = correctCount,
                    totalRounds = totalRounds,
                    onPlayAgain = {
                        currentRound = 1
                        correctCount = 0
                        isGameOver = false
                        nextRound()
                    },
                    onBack = onBack
                )
            }
        }

        // Mascot
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 12.dp)
        ) {
            LumiMascot(
                mood = mascotMood,
                isSpeaking = isSpeaking,
                thoughtBubbleEmoji = mascotBubble,
                size = 110.dp,
                onClick = { viewModel.speakWord(targetColorItem) }
            )
        }

        ConfettiCanvas(trigger = confettiTrigger)
    }
}

