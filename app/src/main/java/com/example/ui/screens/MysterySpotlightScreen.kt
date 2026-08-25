package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun MysterySpotlightScreen(
    viewModel: LumiViewModel,
    onBack: () -> Unit
) {
    val targetLanguage by viewModel.targetLanguage.collectAsState()
    val mascotMood by viewModel.mascotMood.collectAsState()
    val mascotBubble by viewModel.mascotSpeechBubble.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()

    val totalRounds = 5
    val sessionWords = remember(targetLanguage) {
        viewModel.getPrioritizedWordsForSession(count = totalRounds)
    }
    val allWords = remember { viewModel.getAllWords() }

    var currentRound by remember { mutableIntStateOf(1) }
    var correctCount by remember { mutableIntStateOf(0) }
    var isGameOver by remember { mutableStateOf(false) }

    var targetWordItem by remember {
        mutableStateOf(sessionWords.firstOrNull() ?: allWords.first())
    }
    var currentOptions by remember { mutableStateOf(emptyList<VocabularyItem>()) }
    var selectedOptionId by remember { mutableStateOf<String?>(null) }
    var isAnswerRevealed by remember { mutableStateOf(false) }
    var confettiTrigger by remember { mutableStateOf(false) }

    fun setupNewRound(roundIndex: Int) {
        val target = sessionWords.getOrElse(roundIndex - 1) { allWords.random() }
        targetWordItem = target
        val distractors = allWords.filter { it.id != target.id }.shuffled().take(2)
        currentOptions = (distractors + target).shuffled()
        selectedOptionId = null
        isAnswerRevealed = false
        confettiTrigger = false
    }

    LaunchedEffect(currentRound) {
        setupNewRound(currentRound)
        delay(400)
        val targetWord = targetWordItem.translations[targetLanguage.code] ?: targetWordItem.englishWord
        viewModel.speakLumi("Can you spot the $targetWord?")
        delay(1200)
        viewModel.speakWord(targetWordItem)
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(SleekBackground)
    ) {
        val isMobile = maxWidth < 600.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(
                    start = if (isMobile) 14.dp else 24.dp,
                    end = if (isMobile) 14.dp else 24.dp,
                    top = if (isMobile) 4.dp else 8.dp,
                    bottom = if (isMobile) 90.dp else 110.dp
                )
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FocusableCard(
                        onClick = onBack,
                        shape = RoundedCornerShape(16.dp),
                        backgroundColor = SleekSurface,
                        unfocusedBorderColor = SleekSurfaceBorder,
                        focusedBorderColor = SleekGold,
                        testTag = "spotlight_back_button"
                    ) {
                        Box(
                            modifier = Modifier.padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = SleekTextDark
                            )
                        }
                    }

                    Column {
                        Text(
                            text = "Mystery Spotlight 🔦",
                            style = if (isMobile) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = SleekTextDark
                        )
                        Text(
                            text = "Round $currentRound of $totalRounds",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SleekEmerald
                        )
                    }
                }

                // Star Score Pill & Pronunciation Button
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FocusableCard(
                        onClick = { viewModel.speakWord(targetWordItem) },
                        shape = RoundedCornerShape(14.dp),
                        backgroundColor = SleekGold,
                        unfocusedBorderColor = SleekGoldDark,
                        focusedBorderColor = SleekEmerald,
                        testTag = "spotlight_repeat_sound"
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Repeat",
                                tint = SleekTextDark,
                                modifier = Modifier.size(16.dp)
                            )
                            if (!isMobile) {
                                Text(
                                    text = "Hear Word",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = SleekTextDark
                                )
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = SleekSurface,
                        border = BorderStroke(1.dp, SleekSurfaceBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = SleekGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "$correctCount ⭐",
                                fontWeight = FontWeight.Bold,
                                color = SleekTextDark,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (!isGameOver) {
                // Prompt Bar
                val targetText = targetWordItem.translations[targetLanguage.code] ?: targetWordItem.englishWord
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = SleekSurface,
                    border = BorderStroke(1.5.dp, SleekSurfaceBorder),
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Spot the: ",
                            fontSize = if (isMobile) 16.sp else 22.sp,
                            fontWeight = FontWeight.Medium,
                            color = SleekTextMuted
                        )
                        Text(
                            text = "\"$targetText\"",
                            fontSize = if (isMobile) 22.sp else 30.sp,
                            fontWeight = FontWeight.Black,
                            color = SleekEmerald
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 3 Spotlight Cards (Adaptive: Row for TV/Tablet, Responsive Row with adaptive padding on mobile)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(end = if (isMobile) 85.dp else 0.dp),
                    horizontalArrangement = Arrangement.spacedBy(if (isMobile) 8.dp else 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    currentOptions.forEach { opt ->
                        val isSelected = selectedOptionId == opt.id
                        val isCorrect = opt.id == targetWordItem.id

                        SpotlightOptionCard(
                            item = opt,
                            isRevealed = isAnswerRevealed,
                            isCorrect = isCorrect,
                            isSelected = isSelected,
                            isMobile = isMobile,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                if (!isAnswerRevealed) {
                                    selectedOptionId = opt.id
                                    isAnswerRevealed = true
                                    val correct = opt.id == targetWordItem.id
                                    viewModel.onAnswerGiven(targetWordItem.id, correct)
                                    if (correct) {
                                        correctCount++
                                        confettiTrigger = true
                                    }

                                    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                        if (currentRound < totalRounds) {
                                            currentRound++
                                        } else {
                                            isGameOver = true
                                            viewModel.onSessionCompleted("mystery_spotlight", totalRounds, correctCount, 40)
                                        }
                                    }, 2000)
                                }
                            }
                        )
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
                        setupNewRound(1)
                    },
                    onBack = onBack
                )
            }
        }

        // Mascot in Bottom Corner
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = if (isMobile) 10.dp else 24.dp, bottom = if (isMobile) 8.dp else 12.dp)
        ) {
            LumiMascot(
                mood = mascotMood,
                isSpeaking = isSpeaking,
                thoughtBubbleEmoji = mascotBubble,
                size = if (isMobile) 75.dp else 110.dp,
                onClick = { viewModel.speakWord(targetWordItem) }
            )
        }

        ConfettiCanvas(trigger = confettiTrigger)
    }
}

@Composable
fun SpotlightOptionCard(
    item: VocabularyItem,
    isRevealed: Boolean,
    isCorrect: Boolean,
    isSelected: Boolean,
    isMobile: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val cardBackground = when {
        isRevealed && isSelected && isCorrect -> SleekEmerald.copy(alpha = 0.15f)
        isRevealed && isSelected && !isCorrect -> SleekCoral.copy(alpha = 0.15f)
        isRevealed && isCorrect -> SleekEmerald.copy(alpha = 0.12f)
        else -> SleekSurface
    }

    val borderColor = when {
        isRevealed && isSelected && isCorrect -> SleekEmerald
        isRevealed && isSelected && !isCorrect -> SleekCoral
        isRevealed && isCorrect -> SleekEmerald
        else -> SleekGold
    }

    val unfocusedBorderColor = when {
        isRevealed && isSelected && isCorrect -> SleekEmerald
        isRevealed && isSelected && !isCorrect -> SleekCoral
        isRevealed && isCorrect -> SleekEmerald
        else -> SleekSurfaceBorder
    }

    FocusableCard(
        onClick = onClick,
        shape = RoundedCornerShape(26.dp),
        backgroundColor = cardBackground,
        unfocusedBorderColor = unfocusedBorderColor,
        focusedBorderColor = borderColor,
        focusedScale = 1.06f,
        elevation = 4.dp,
        modifier = modifier
            .height(if (isMobile) 200.dp else 270.dp)
            .testTag("spotlight_option_${item.id}")
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isMobile) 8.dp else 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(item.colorHex).copy(alpha = 0.14f),
                    border = BorderStroke(1.dp, Color(item.colorHex).copy(alpha = 0.3f)),
                    modifier = Modifier.size(if (isMobile) 64.dp else 100.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = item.emoji, fontSize = if (isMobile) 36.sp else 54.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = item.englishWord,
                    fontSize = if (isMobile) 13.sp else 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = SleekTextDark,
                    maxLines = 1
                )

                if (isRevealed && isSelected) {
                    Text(
                        text = if (isCorrect) "Awesome! ✨" else "Try again! 💪",
                        fontSize = if (isMobile) 11.sp else 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCorrect) SleekEmerald else SleekCoral,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}
