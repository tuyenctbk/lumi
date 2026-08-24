package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MascotMood
import com.example.model.VocabularyItem
import com.example.ui.components.CardVisualMode
import com.example.ui.components.ConfettiCanvas
import com.example.ui.components.FocusableVocabularyCard
import com.example.ui.components.GameOverView
import com.example.ui.components.LumiMascot
import com.example.ui.theme.SleekEmerald
import com.example.ui.theme.SleekGold
import com.example.ui.theme.SleekOcean
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekTextDark
import com.example.ui.theme.SleekTextMuted
import com.example.ui.viewmodel.LumiViewModel
import kotlinx.coroutines.delay

/**
 * FindItGameScreen
 *
 * Interactive game mode where Lumi speaks a word in the target language via the Android TTS engine,
 * and the child selects the matching image card on screen.
 */
@Composable
fun FindItGameScreen(
    viewModel: LumiViewModel,
    onBack: () -> Unit
) {
    val targetLanguage by viewModel.targetLanguage.collectAsState()
    val mascotMood by viewModel.mascotMood.collectAsState()
    val mascotBubble by viewModel.mascotSpeechBubble.collectAsState()
    val allWords = remember { viewModel.getAllWords() }

    var currentRound by remember { mutableIntStateOf(1) }
    val totalRounds = 5

    var targetItem by remember { mutableStateOf<VocabularyItem?>(null) }
    var candidateOptions by remember { mutableStateOf<List<VocabularyItem>>(emptyList()) }

    var correctCount by remember { mutableIntStateOf(0) }
    var selectedItem by remember { mutableStateOf<VocabularyItem?>(null) }
    var isAnswered by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }
    var isGameFinished by remember { mutableStateOf(false) }
    var visualMode by remember { mutableStateOf(CardVisualMode.EMOJI) }

    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()

    // Setup round targets
    fun setupRound() {
        if (allWords.size < 4) return
        val prioritized = viewModel.getPrioritizedWordsForSession(count = 4)
        val target = prioritized.random()
        val distractors = allWords.filter { it.id != target.id }.shuffled().take(3)
        targetItem = target
        candidateOptions = (distractors + target).shuffled()
        isAnswered = false
        selectedItem = null
    }

    LaunchedEffect(currentRound) {
        setupRound()
    }

    // Speak prompt when target changes
    LaunchedEffect(targetItem) {
        val currentTarget = targetItem
        if (currentTarget != null && !isGameFinished) {
            delay(400)
            val foreignWord = currentTarget.translations[targetLanguage.code] ?: currentTarget.englishWord
            viewModel.speakLumi("Find the $foreignWord!", MascotMood.HAPPY)
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(SleekSurface)
            .testTag("find_it_game_screen")
    ) {
        val isLandscape = maxWidth > maxHeight

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 16.dp)
        ) {
            // Top Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("find_it_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = SleekTextDark
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SleekOcean.copy(alpha = 0.15f),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = "🔎 FIND IT MODE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SleekOcean,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SleekGold.copy(alpha = 0.2f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Stars",
                                tint = SleekGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Round $currentRound/$totalRounds",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = SleekTextDark
                            )
                        }
                    }
                }
            }

            if (!isGameFinished) {
                val currentTarget = targetItem
                val targetTranslation = currentTarget?.translations?.get(targetLanguage.code) ?: currentTarget?.englishWord ?: ""

                // Mascot Prompt Section
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    LumiMascot(
                        mood = mascotMood,
                        speechBubble = mascotBubble,
                        size = if (isLandscape) 90.dp else 110.dp,
                        onClick = {
                            if (currentTarget != null) {
                                viewModel.speakLumi("Find $targetTranslation!", MascotMood.HAPPY)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = "Listen closely to Lumi!",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = SleekTextMuted
                        )
                        Text(
                            text = "Find: \"$targetTranslation\"",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = SleekEmerald
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Audio Replay Button
                        Button(
                            onClick = {
                                if (currentTarget != null) {
                                    viewModel.speakWord(currentTarget)
                                }
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SleekOcean),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Replay Audio",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Listen Again 🔊",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Options Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(if (isLandscape) 4 else 2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("find_it_options_grid"),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(candidateOptions, key = { it.id }) { item ->
                        val isTarget = item.id == currentTarget?.id
                        val isChosen = item.id == selectedItem?.id

                        FocusableVocabularyCard(
                            item = item,
                            targetWord = item.translations[targetLanguage.code] ?: item.englishWord,
                            categoryColor = Color(item.category.colorHex),
                            visualMode = visualMode,
                            onClick = {
                                if (!isAnswered && currentTarget != null) {
                                    selectedItem = item
                                    isAnswered = true

                                    if (isTarget) {
                                        correctCount++
                                        showConfetti = true
                                        viewModel.onAnswerGiven(item.id, isCorrect = true)
                                        viewModel.speakLumi("Hooray! That's correct! 🌟", MascotMood.SUPERSTAR)
                                    } else {
                                        viewModel.onAnswerGiven(item.id, isCorrect = false)
                                        viewModel.speakLumi("Oops! Try listening again!", MascotMood.ENCOURAGING)
                                    }

                                    // Auto advance round
                                    coroutineScope.launch {
                                        delay(1800)
                                        showConfetti = false
                                        if (currentRound < totalRounds) {
                                            currentRound++
                                        } else {
                                            isGameFinished = true
                                            viewModel.onSessionCompleted("find_it", totalRounds, correctCount, 45)
                                        }
                                    }
                                }
                            },
                            onPlayRealSound = {
                                viewModel.playRealSoundEffect(item.id)
                            },
                            focusedBorderColor = if (isAnswered && isChosen) {
                                if (isTarget) SleekEmerald else Color.Red
                            } else SleekEmerald,
                            testTag = "find_it_card_${item.id}"
                        )
                    }
                }
            } else {
                // Game Finished View
                GameOverView(
                    correctCount = correctCount,
                    totalRounds = totalRounds,
                    onPlayAgain = {
                        currentRound = 1
                        correctCount = 0
                        isGameFinished = false
                    },
                    onBack = onBack
                )
            }
        }

        // Celebration Confetti
        ConfettiCanvas(
            trigger = showConfetti,
            modifier = Modifier.fillMaxSize()
        )
    }
}
