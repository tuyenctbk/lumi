package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundFxHelper
import com.example.model.MascotMood
import com.example.model.VocabularyItem
import com.example.ui.components.ConfettiCanvas
import com.example.ui.components.FocusableCard
import com.example.ui.components.GameOverView
import com.example.ui.components.LumiMascot
import com.example.ui.theme.SleekBackground
import com.example.ui.theme.SleekCoral
import com.example.ui.theme.SleekEmerald
import com.example.ui.theme.SleekGold
import com.example.ui.theme.SleekGoldDark
import com.example.ui.theme.SleekOcean
import com.example.ui.theme.SleekPurple
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekSurfaceBorder
import com.example.ui.theme.SleekTextDark
import com.example.ui.theme.SleekTextMuted
import com.example.ui.viewmodel.LumiViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random

data class FallingStar(
    val id: String,
    val item: VocabularyItem,
    val targetWord: String,
    val isTarget: Boolean,
    val initialXRatio: Float,
    val color: Color
)

/**
 * StarCatcherGameScreen
 *
 * An arcade language learning game where glowing word stars drift down from the cosmic night sky.
 * Children listen to Lumi pronounce the target word, then catch the matching star!
 * Full touch and Android TV D-Pad Remote support.
 */
@Composable
fun StarCatcherGameScreen(
    viewModel: LumiViewModel,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

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
    var score by remember { mutableIntStateOf(0) }
    var isGameOver by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    var currentTarget by remember {
        mutableStateOf(sessionWords.firstOrNull() ?: allWords.first())
    }
    var options by remember { mutableStateOf(listOf<VocabularyItem>()) }
    var selectedWordId by remember { mutableStateOf<String?>(null) }
    var isAnswerEvaluated by remember { mutableStateOf(false) }

    val targetWordTranslation = remember(currentTarget, targetLanguage) {
        currentTarget.translations[targetLanguage.code] ?: currentTarget.englishWord
    }

    fun setupRound(roundIndex: Int) {
        val target = sessionWords.getOrNull(roundIndex - 1) ?: allWords.random()
        currentTarget = target
        val distractors = allWords.filter { it.id != target.id }.shuffled().take(3)
        options = (distractors + target).shuffled()
        selectedWordId = null
        isAnswerEvaluated = false
        showConfetti = false

        viewModel.setGameActive(true)
        viewModel.speakWord(target)
    }

    LaunchedEffect(currentRound, targetLanguage) {
        setupRound(currentRound)
    }

    val coroutineScope = rememberCoroutineScope()

    fun handleSelection(item: VocabularyItem) {
        if (isAnswerEvaluated || isGameOver) return
        selectedWordId = item.id
        isAnswerEvaluated = true

        val isCorrect = item.id == currentTarget.id
        viewModel.onAnswerGiven(currentTarget.id, isCorrect)

        if (isCorrect) {
            score++
            showConfetti = true
            SoundFxHelper.playCorrectChime()
            SoundFxHelper.playStarBurst()
            viewModel.setMascotMood(MascotMood.SUPERSTAR, 2500)
        } else {
            SoundFxHelper.playWrongOops()
            viewModel.setMascotMood(MascotMood.ENCOURAGING, 2500)
        }

        coroutineScope.launch {
            delay(1800)
            if (currentRound < totalRounds) {
                currentRound++
            } else {
                isGameOver = true
                viewModel.onSessionCompleted(
                    gameType = "star_catcher",
                    practicedCount = totalRounds,
                    correctCount = score,
                    durationSeconds = 60
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E1B4B),
                        SleekBackground
                    )
                )
            )
            .statusBarsPadding()
            .testTag("star_catcher_game_screen")
    ) {
        if (showConfetti) {
            ConfettiCanvas()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FocusableCard(
                    onClick = onBack,
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = SleekSurface.copy(alpha = 0.85f),
                    unfocusedBorderColor = SleekSurfaceBorder,
                    focusedBorderColor = SleekOcean,
                    testTag = "star_catcher_back_button"
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = SleekOcean
                        )
                        Text(
                            text = "Back",
                            fontWeight = FontWeight.Bold,
                            color = SleekOcean
                        )
                    }
                }

                // Round & Score Badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = SleekSurface.copy(alpha = 0.9f),
                    border = BorderStroke(1.5.dp, SleekGold.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = SleekGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Round $currentRound/$totalRounds",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = SleekGold
                        )
                        Text(
                            text = "• $score ⭐",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = SleekEmerald
                        )
                    }
                }
            }

            if (!isGameOver) {
                Spacer(modifier = Modifier.height(8.dp))

                // Prompt Header with Speaker & Mascot
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = SleekSurface.copy(alpha = 0.92f),
                    border = BorderStroke(1.5.dp, SleekSurfaceBorder),
                    shadowElevation = 6.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        LumiMascot(
                            mood = mascotMood,
                            speechBubble = mascotBubble,
                            isSpeaking = isSpeaking,
                            size = 72.dp,
                            onClick = {
                                viewModel.speakWord(currentTarget)
                            }
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Catch the falling star with:",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = SleekTextMuted
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = targetWordTranslation,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SleekGold
                                )
                                IconButton(
                                    onClick = { viewModel.speakWord(currentTarget) },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(SleekOcean.copy(alpha = 0.15f), CircleShape)
                                        .testTag("star_catcher_speak_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = "Speak word",
                                        tint = SleekOcean,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Falling Star Cards Grid
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    val cardCount = options.size
                    val isWideScreen = maxWidth > 600.dp

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val chunked = options.chunked(if (isWideScreen) 4 else 2)
                        for (rowItems in chunked) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                for (item in rowItems) {
                                    val isSelected = selectedWordId == item.id
                                    val isCorrectTarget = item.id == currentTarget.id
                                    val cardTranslation = item.translations[targetLanguage.code] ?: item.englishWord

                                    val cardColor = when {
                                        isAnswerEvaluated && isCorrectTarget -> SleekEmerald
                                        isAnswerEvaluated && isSelected -> SleekCoral
                                        else -> SleekSurface
                                    }

                                    val borderColor = when {
                                        isAnswerEvaluated && isCorrectTarget -> SleekEmerald
                                        isAnswerEvaluated && isSelected -> SleekCoral
                                        else -> SleekGold.copy(alpha = 0.7f)
                                    }

                                    val interactionSource = remember { MutableInteractionSource() }
                                    val isFocused by interactionSource.collectIsFocusedAsState()

                                    val infiniteStarTransition = rememberInfiniteTransition(label = "star_twinkle")
                                    val floatY by infiniteStarTransition.animateFloat(
                                        initialValue = -4f,
                                        targetValue = 4f,
                                        animationSpec = infiniteRepeatable(
                                            animation = tween(1200 + (item.id.hashCode() % 400).coerceAtLeast(0), easing = FastOutSlowInEasing),
                                            repeatMode = RepeatMode.Reverse
                                        ),
                                        label = "star_float"
                                    )

                                    FocusableCard(
                                        onClick = { handleSelection(item) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(140.dp)
                                            .offset(y = floatY.dp)
                                            .testTag("star_card_${item.id}"),
                                        shape = RoundedCornerShape(24.dp),
                                        backgroundColor = cardColor,
                                        unfocusedBorderColor = borderColor,
                                        focusedBorderColor = SleekGold,
                                        focusedScale = 1.08f
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(12.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = item.emoji,
                                                fontSize = 44.sp
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = cardTranslation,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isAnswerEvaluated && (isCorrectTarget || isSelected)) Color.White else SleekTextDark,
                                                textAlign = TextAlign.Center,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Game Over Screen
                GameOverView(
                    correctCount = score,
                    totalRounds = totalRounds,
                    onPlayAgain = {
                        currentRound = 1
                        score = 0
                        isGameOver = false
                        setupRound(1)
                    },
                    onBack = onBack,
                    modifier = Modifier.testTag("star_catcher_game_over")
                )
            }
        }
    }
}
