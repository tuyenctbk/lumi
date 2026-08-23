package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.style.TextAlign
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
fun SoundMatchScreen(
    viewModel: LumiViewModel,
    onBack: () -> Unit
) {
    val targetLanguage by viewModel.targetLanguage.collectAsState()
    val mascotMood by viewModel.mascotMood.collectAsState()
    val mascotBubble by viewModel.mascotSpeechBubble.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()

    val totalRounds = 5
    val allWords = remember { viewModel.getAllWords() }
    val sessionWords = remember(targetLanguage) {
        viewModel.getPrioritizedWordsForSession(count = totalRounds)
    }

    var currentRound by remember { mutableIntStateOf(1) }
    var correctCount by remember { mutableIntStateOf(0) }
    var isGameOver by remember { mutableStateOf(false) }

    var targetItem by remember {
        mutableStateOf(sessionWords.firstOrNull() ?: allWords.first())
    }
    var options by remember { mutableStateOf(emptyList<VocabularyItem>()) }
    var selectedOptionId by remember { mutableStateOf<String?>(null) }
    var isRevealed by remember { mutableStateOf(false) }
    var confettiTrigger by remember { mutableStateOf(false) }

    fun nextRound(roundIdx: Int) {
        val target = sessionWords.getOrElse(roundIdx - 1) { allWords.random() }
        targetItem = target
        val distractors = allWords.filter { it.id != target.id }.shuffled().take(3)
        options = (distractors + target).shuffled()
        selectedOptionId = null
        isRevealed = false
        confettiTrigger = false
    }

    LaunchedEffect(currentRound) {
        nextRound(currentRound)
        delay(400)
        viewModel.speakWord(targetItem)
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
                .padding(if (isMobile) 14.dp else 24.dp)
        ) {
            // Header
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
                        focusedBorderColor = SleekEmerald,
                        testTag = "sound_match_back_button"
                    ) {
                        Box(modifier = Modifier.padding(10.dp), contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = SleekTextDark
                            )
                        }
                    }

                    Column {
                        Text(
                            text = "Sound Match 🎵",
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

                // Replay Sound & Stars Count
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FocusableCard(
                        onClick = { viewModel.speakWord(targetItem) },
                        shape = RoundedCornerShape(14.dp),
                        backgroundColor = SleekEmerald,
                        unfocusedBorderColor = SleekEmeraldDark,
                        focusedBorderColor = SleekGold,
                        testTag = "sound_match_replay_button"
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Hear Again",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Listen 🔊",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
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

            Spacer(modifier = Modifier.height(10.dp))

            if (!isGameOver) {
                // Prompt Bar
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = SleekSurface,
                    border = BorderStroke(1.5.dp, SleekSurfaceBorder),
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Which picture matches the sound you heard?",
                            fontSize = if (isMobile) 14.sp else 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = SleekTextDark,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Options: 2x2 Grid on Mobile, 4 in a Row on TV/Tablet
                if (isMobile) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(bottom = 60.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(options) { opt ->
                            SoundOptionCard(
                                opt = opt,
                                targetItem = targetItem,
                                targetLanguageCode = targetLanguage.code,
                                isRevealed = isRevealed,
                                isSelected = selectedOptionId == opt.id,
                                isMobile = true,
                                onClick = {
                                    if (!isRevealed) {
                                        selectedOptionId = opt.id
                                        isRevealed = true
                                        val correct = opt.id == targetItem.id
                                        viewModel.onAnswerGiven(targetItem.id, correct)
                                        if (correct) {
                                            correctCount++
                                            confettiTrigger = true
                                        }

                                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                            if (currentRound < totalRounds) {
                                                currentRound++
                                            } else {
                                                isGameOver = true
                                                viewModel.onSessionCompleted("sound_match", totalRounds, correctCount, 35)
                                            }
                                        }, 2000)
                                    }
                                }
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        options.forEach { opt ->
                            Box(modifier = Modifier.weight(1f)) {
                                SoundOptionCard(
                                    opt = opt,
                                    targetItem = targetItem,
                                    targetLanguageCode = targetLanguage.code,
                                    isRevealed = isRevealed,
                                    isSelected = selectedOptionId == opt.id,
                                    isMobile = false,
                                    onClick = {
                                        if (!isRevealed) {
                                            selectedOptionId = opt.id
                                            isRevealed = true
                                            val correct = opt.id == targetItem.id
                                            viewModel.onAnswerGiven(targetItem.id, correct)
                                            if (correct) {
                                                correctCount++
                                                confettiTrigger = true
                                            }

                                            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                                if (currentRound < totalRounds) {
                                                    currentRound++
                                                } else {
                                                    isGameOver = true
                                                    viewModel.onSessionCompleted("sound_match", totalRounds, correctCount, 35)
                                                }
                                            }, 2000)
                                        }
                                    }
                                )
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
                        nextRound(1)
                    },
                    onBack = onBack
                )
            }
        }

        // Mascot in Corner
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
                onClick = { viewModel.speakWord(targetItem) }
            )
        }

        ConfettiCanvas(trigger = confettiTrigger)
    }
}

@Composable
fun SoundOptionCard(
    opt: VocabularyItem,
    targetItem: VocabularyItem,
    targetLanguageCode: String,
    isRevealed: Boolean,
    isSelected: Boolean,
    isMobile: Boolean,
    onClick: () -> Unit
) {
    val isCorrect = opt.id == targetItem.id
    val targetWord = opt.translations[targetLanguageCode] ?: opt.englishWord

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
        onClick = onClick,
        shape = RoundedCornerShape(26.dp),
        backgroundColor = cardBg,
        unfocusedBorderColor = unfocusedBorderCol,
        focusedBorderColor = borderCol,
        focusedScale = 1.06f,
        elevation = 3.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(if (isMobile) 170.dp else 250.dp)
            .testTag("sound_match_option_${opt.id}")
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isMobile) 10.dp else 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(opt.colorHex).copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, Color(opt.colorHex).copy(alpha = 0.25f)),
                    modifier = Modifier.size(if (isMobile) 64.dp else 86.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = opt.emoji, fontSize = if (isMobile) 36.sp else 44.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (isRevealed) {
                    Text(
                        text = targetWord,
                        fontSize = if (isMobile) 14.sp else 18.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isCorrect) SleekEmerald else SleekCoral,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                } else {
                    Text(
                        text = opt.englishWord,
                        fontSize = if (isMobile) 13.sp else 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekTextDark,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
