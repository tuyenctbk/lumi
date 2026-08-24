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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun ShadowGuessScreen(
    viewModel: LumiViewModel,
    onBack: () -> Unit
) {
    val targetLanguage by viewModel.targetLanguage.collectAsState()
    val mascotMood by viewModel.mascotMood.collectAsState()
    val mascotBubble by viewModel.mascotSpeechBubble.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()

    val allWords = remember { viewModel.getAllWords() }

    var currentRound by remember { mutableIntStateOf(1) }
    val totalRounds = 5
    var correctCount by remember { mutableIntStateOf(0) }
    var isGameOver by remember { mutableStateOf(false) }

    var targetItem by remember { mutableStateOf(allWords.random()) }
    var options by remember { mutableStateOf(emptyList<VocabularyItem>()) }
    var selectedOptionId by remember { mutableStateOf<String?>(null) }
    var isRevealed by remember { mutableStateOf(false) }
    var confettiTrigger by remember { mutableStateOf(false) }

    fun nextRound() {
        val target = allWords.random()
        targetItem = target
        val distractors = allWords.filter { it.id != target.id }.shuffled().take(2)
        options = (distractors + target).shuffled()
        selectedOptionId = null
        isRevealed = false
        confettiTrigger = false
    }

    LaunchedEffect(currentRound) {
        nextRound()
        delay(400)
        viewModel.speakLumi("Who is hiding in this mystery shadow?")
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
                .padding(24.dp)
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
                        focusedBorderColor = SleekEmerald,
                        testTag = "shadow_back_button"
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
                            text = "👤 Shadow Silhouette Guess",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = SleekTextDark
                        )
                        Text(
                            text = "Round $currentRound of $totalRounds",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = SleekEmerald
                        )
                    }
                }
            }

            if (!isGameOver) {
                Spacer(modifier = Modifier.height(16.dp))

                // Shadow Stage in the Center
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(32.dp),
                        color = if (isRevealed) SleekEmerald.copy(alpha = 0.15f) else SleekSurface,
                        border = BorderStroke(2.dp, if (isRevealed) SleekEmerald else SleekSurfaceBorder),
                        shadowElevation = 3.dp,
                        modifier = Modifier.size(130.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (isRevealed) {
                                Text(text = targetItem.emoji, fontSize = 68.sp)
                            } else {
                                // Black silhouette effect
                                Text(
                                    text = targetItem.emoji,
                                    fontSize = 68.sp,
                                    modifier = Modifier.alpha(0.20f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 3 Options
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    options.forEach { opt ->
                        val isSelected = selectedOptionId == opt.id
                        val isCorrect = opt.id == targetItem.id
                        val targetWord = opt.translations[targetLanguage.code] ?: opt.englishWord

                        val cardBg = when {
                            isRevealed && isSelected && isCorrect -> SleekEmerald.copy(alpha = 0.15f)
                            isRevealed && isSelected && !isCorrect -> SleekCoral.copy(alpha = 0.15f)
                            else -> SleekSurface
                        }

                        val borderCol = when {
                            isRevealed && isSelected && isCorrect -> SleekEmerald
                            isRevealed && isSelected && !isCorrect -> SleekCoral
                            else -> SleekEmerald
                        }

                        val unfocusedBorderCol = when {
                            isRevealed && isSelected && isCorrect -> SleekEmerald
                            isRevealed && isSelected && !isCorrect -> SleekCoral
                            else -> SleekSurfaceBorder
                        }

                        FocusableCard(
                            onClick = {
                                if (!isRevealed) {
                                    selectedOptionId = opt.id
                                    isRevealed = true
                                    val correct = opt.id == targetItem.id
                                    viewModel.onAnswerGiven(targetItem.id, correct)
                                    viewModel.speakWord(targetItem)
                                    if (correct) {
                                        correctCount++
                                        confettiTrigger = true
                                    }

                                    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                        if (currentRound < totalRounds) {
                                            currentRound++
                                        } else {
                                            isGameOver = true
                                            viewModel.onSessionCompleted("shadow_guess", totalRounds, correctCount, 40)
                                        }
                                    }, 2400)
                                }
                            },
                            shape = RoundedCornerShape(28.dp),
                            backgroundColor = cardBg,
                            unfocusedBorderColor = unfocusedBorderCol,
                            focusedBorderColor = borderCol,
                            focusedScale = 1.10f,
                            elevation = 4.dp,
                            modifier = Modifier
                                .weight(1f)
                                .height(220.dp)
                                .testTag("shadow_option_${opt.id}")
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(22.dp),
                                        color = Color(opt.colorHex).copy(alpha = 0.12f),
                                        border = BorderStroke(1.dp, Color(opt.colorHex).copy(alpha = 0.25f)),
                                        modifier = Modifier.size(80.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(text = opt.emoji, fontSize = 42.sp)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text(
                                        text = if (isRevealed) targetWord else opt.englishWord,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SleekTextDark,
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
                onClick = { viewModel.speakLumi("Match the shape to reveal the colors!") }
            )
        }

        ConfettiCanvas(trigger = confettiTrigger)
    }
}

