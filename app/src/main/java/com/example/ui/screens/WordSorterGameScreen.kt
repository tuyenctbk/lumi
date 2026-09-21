package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.LearningCategory
import com.example.model.MascotMood
import com.example.model.VocabularyItem
import com.example.ui.components.ConfettiCanvas
import com.example.ui.components.GameOverView
import com.example.ui.components.LumiMascot
import com.example.ui.components.TvFocusableCard
import com.example.ui.theme.SleekEmerald
import com.example.ui.theme.SleekEmeraldDark
import com.example.ui.theme.SleekGold
import com.example.ui.theme.SleekOcean
import com.example.ui.theme.SleekOceanDark
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekTextDark
import com.example.ui.theme.SleekTextMuted
import com.example.ui.viewmodel.LumiViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * WordSorterGameScreen (Category Train / Sorter)
 *
 * Players sort mystery vocabulary words into the correct category train cars (e.g. Animals vs Food).
 * Optimized with D-Pad focus (Left/Right) for Leanback Android TV & high-touch mobile feedback!
 */
@Composable
fun WordSorterGameScreen(
    viewModel: LumiViewModel,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val targetLanguage by viewModel.targetLanguage.collectAsState()
    val allWords = remember { viewModel.getAllWords() }

    var currentRound by remember { mutableIntStateOf(1) }
    val totalRounds = 6
    var score by remember { mutableIntStateOf(0) }
    var streak by remember { mutableIntStateOf(0) }
    var isGameOver by remember { mutableStateOf(false) }

    var categoryA by remember { mutableStateOf(LearningCategory.ANIMALS) }
    var categoryB by remember { mutableStateOf(LearningCategory.FOOD) }
    var currentItem by remember { mutableStateOf<VocabularyItem?>(null) }
    var showConfetti by remember { mutableStateOf(false) }
    var reactionState by remember { mutableStateOf<MascotMood?>(null) }
    var promptMessage by remember { mutableStateOf("Sort the card into the right train car! 🚂") }

    fun setupRound() {
        if (allWords.size < 6) return

        // Pick 2 distinct categories that have words
        val availableCategories = allWords.map { it.category }.distinct()
        if (availableCategories.size < 2) return

        val chosen = availableCategories.shuffled().take(2)
        categoryA = chosen[0]
        categoryB = chosen[1]

        val targetPool = allWords.filter { it.category == categoryA || it.category == categoryB }
        if (targetPool.isNotEmpty()) {
            val nextItem = targetPool.random()
            currentItem = nextItem
            val trans = nextItem.translations[targetLanguage.code] ?: nextItem.englishWord
            promptMessage = "Where does $trans belong? 🤔"
            viewModel.speakLumi("Where does $trans belong?", MascotMood.THINKING)
        }
    }

    LaunchedEffect(allWords, targetLanguage) {
        if (allWords.isNotEmpty()) {
            setupRound()
        }
    }

    fun handleSort(selectedCategory: LearningCategory) {
        val word = currentItem ?: return
        val isCorrect = word.category == selectedCategory

        if (isCorrect) {
            score += 15 + (streak * 3)
            streak++
            showConfetti = true
            reactionState = MascotMood.SUPERSTAR
            promptMessage = "Spot on! That's in ${selectedCategory.title}! 🌟"

            viewModel.onAnswerGiven(word.id, isCorrect = true)
            viewModel.speakLumi("Correct! Great job!", MascotMood.HAPPY)

            if (currentRound >= totalRounds) {
                isGameOver = true
                viewModel.onSessionCompleted("word_sorter", totalRounds, score, 60)
            } else {
                currentRound++
                coroutineScope.launch {
                    delay(1100)
                    showConfetti = false
                    reactionState = null
                    setupRound()
                }
            }
        } else {
            streak = 0
            reactionState = MascotMood.ENCOURAGING
            promptMessage = "Oops, let's try again! 🚂"
            viewModel.onAnswerGiven(word.id, isCorrect = false)
            viewModel.speakLumi("Try the other car!", MascotMood.THINKING)
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF3E8FF), // Lavender top
                        Color(0xFFEDE9FE), // Soft purple
                        Color(0xFFF8FAFC)  // Clean white
                    )
                )
            )
            .statusBarsPadding()
            .testTag("screen_word_sorter")
    ) {
        val isMobile = maxWidth < 600.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = if (isMobile) 16.dp else 32.dp, vertical = 12.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color.White, CircleShape)
                            .testTag("word_sorter_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = SleekTextDark
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = stringResource(R.string.game_word_sorter_title),
                            fontSize = if (isMobile) 18.sp else 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = SleekTextDark
                        )
                        Text(
                            text = stringResource(R.string.game_round_counter, currentRound, totalRounds),
                            fontSize = 12.sp,
                            color = SleekTextMuted,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Score Badge
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SleekGold,
                    shadowElevation = 3.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = stringResource(R.string.game_pts_upper, score),
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Center Mystery Word Card
            currentItem?.let { item ->
                val translated = item.translations[targetLanguage.code] ?: item.englishWord

                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 6.dp,
                    border = BorderStroke(2.dp, SleekOcean.copy(alpha = 0.2f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = if (isMobile) 12.dp else 40.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 16.dp, horizontal = 20.dp)
                    ) {
                        Text(
                            text = item.emoji,
                            fontSize = if (isMobile) 56.sp else 72.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = translated,
                                fontSize = if (isMobile) 24.sp else 30.sp,
                                fontWeight = FontWeight.Black,
                                color = SleekTextDark
                            )
                            IconButton(
                                onClick = {
                                    viewModel.speakLumi(translated, MascotMood.TALKING)
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Pronounce",
                                    tint = SleekOcean,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Text(
                            text = item.phonetic,
                            fontSize = 13.sp,
                            color = SleekOceanDark,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Train Cars Destination Shelves (Left: Category A, Right: Category B)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(if (isMobile) 12.dp else 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Train Car A
                TrainCarDestination(
                    category = categoryA,
                    isMobile = isMobile,
                    arrowText = "◀ LEFT",
                    modifier = Modifier.weight(1f),
                    onSelect = { handleSort(categoryA) }
                )

                // Train Car B
                TrainCarDestination(
                    category = categoryB,
                    isMobile = isMobile,
                    arrowText = "RIGHT ▶",
                    modifier = Modifier.weight(1f),
                    onSelect = { handleSort(categoryB) }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Mascot Cheer Bottom Area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LumiMascot(
                    mood = reactionState ?: MascotMood.HAPPY,
                    speechBubble = promptMessage,
                    size = if (isMobile) 68.dp else 84.dp,
                    onClick = {
                        currentItem?.let {
                            val trans = it.translations[targetLanguage.code] ?: it.englishWord
                            viewModel.speakLumi("Is $trans in ${categoryA.title} or ${categoryB.title}?", MascotMood.HAPPY)
                        }
                    }
                )
            }
        }

        if (showConfetti) {
            ConfettiCanvas(trigger = showConfetti, modifier = Modifier.fillMaxSize())
        }

        if (isGameOver) {
            GameOverView(
                correctCount = score,
                totalRounds = totalRounds,
                onPlayAgain = {
                    score = 0
                    currentRound = 1
                    streak = 0
                    isGameOver = false
                    setupRound()
                },
                onBack = onBack
            )
        }
    }
}

@Composable
fun TrainCarDestination(
    category: LearningCategory,
    isMobile: Boolean,
    arrowText: String,
    modifier: Modifier = Modifier,
    onSelect: () -> Unit
) {
    val categoryColor = Color(category.colorHex)

    TvFocusableCard(
        onClick = onSelect,
        shape = RoundedCornerShape(24.dp),
        backgroundColor = Color.White,
        unfocusedBorderColor = categoryColor.copy(alpha = 0.35f),
        focusedBorderColor = categoryColor,
        focusedScale = 1.08f,
        elevation = 4.dp,
        modifier = modifier
            .height(if (isMobile) 160.dp else 200.dp)
            .testTag("train_car_${category.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Category Badge Header
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = categoryColor.copy(alpha = 0.15f)
            ) {
                Text(
                    text = arrowText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = categoryColor,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }

            // Big Category Emoji
            Text(
                text = category.emoji,
                fontSize = if (isMobile) 44.sp else 56.sp
            )

            // Category Title
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = category.title,
                    fontSize = if (isMobile) 14.sp else 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = SleekTextDark,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Load Here ➔",
                    fontSize = 11.sp,
                    color = categoryColor,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
