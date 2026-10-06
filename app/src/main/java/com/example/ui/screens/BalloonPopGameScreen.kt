package com.example.ui.screens

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
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.MascotMood
import com.example.model.VocabularyItem
import com.example.ui.components.ConfettiCanvas
import com.example.ui.components.GameOverView
import com.example.ui.components.LumiLottieReaction
import com.example.ui.components.LumiMascot
import com.example.ui.theme.SleekEmerald
import com.example.ui.theme.SleekGold
import com.example.ui.theme.SleekOcean
import com.example.ui.theme.SleekOceanDark
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekTextDark
import com.example.ui.theme.SleekTextMuted
import com.example.ui.viewmodel.LumiViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random

data class BalloonData(
    val id: String,
    val wordItem: VocabularyItem,
    val color: Color,
    val floatSpeedOffset: Float,
    val isPopped: Boolean = false
)

val BalloonPalette = listOf(
    Color(0xFFFF5252), // Vibrant Red
    Color(0xFFFF7A00), // Bright Orange
    Color(0xFFFFC107), // Sunny Yellow
    Color(0xFF4CAF50), // Fresh Green
    Color(0xFF00BCD4), // Electric Cyan
    Color(0xFF3F51B5), // Deep Blue
    Color(0xFF9C27B0), // Royal Purple
    Color(0xFFE91E63)  // Playful Pink
)

@Composable
fun BalloonPopGameScreen(
    viewModel: LumiViewModel,
    onBack: () -> Unit
) {
    androidx.activity.compose.BackHandler { onBack() }
    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()
    val targetLanguage by viewModel.targetLanguage.collectAsState()
    val words = remember { viewModel.getAllWords() }

    var currentRound by remember { mutableIntStateOf(1) }
    val totalRounds = 5
    var score by remember { mutableIntStateOf(0) }
    var streak by remember { mutableIntStateOf(0) }
    var isGameOver by remember { mutableStateOf(false) }

    var targetWord by remember { mutableStateOf<VocabularyItem?>(null) }
    var balloons by remember { mutableStateOf<List<BalloonData>>(emptyList()) }
    var showConfetti by remember { mutableStateOf(false) }
    var reactionState by remember { mutableStateOf<MascotMood?>(null) }
    var mascotPrompt by remember { mutableStateOf("Pop the matching balloon! 🎈") }

    fun setupNewRound() {
        if (words.size < 4) return
        val roundTarget = words.random()
        val distractors = words.filter { it.id != roundTarget.id }.shuffled().take(3)
        val allOptions = (distractors + roundTarget).shuffled()

        balloons = allOptions.mapIndexed { index, word ->
            BalloonData(
                id = "${word.id}_$index",
                wordItem = word,
                color = BalloonPalette[index % BalloonPalette.size],
                floatSpeedOffset = Random.nextFloat() * 10f
            )
        }
        targetWord = roundTarget
        val targetTranslated = roundTarget.translations[targetLanguage.code] ?: roundTarget.englishWord
        mascotPrompt = "Pop: $targetTranslated!"
        viewModel.speakLumi("Pop the balloon with $targetTranslated", MascotMood.THINKING)
    }

    LaunchedEffect(words, targetLanguage) {
        if (words.isNotEmpty()) {
            setupNewRound()
        }
    }

    fun handleBalloonSelected(balloon: BalloonData) {
        if (balloon.isPopped || isGameOver) return
        val correct = balloon.wordItem.id == targetWord?.id

        if (correct) {
            score += 10 + (streak * 2)
            streak++
            showConfetti = true
            reactionState = MascotMood.SUPERSTAR
            mascotPrompt = "Awesome pop! ⭐"

            // Mark balloon as popped
            balloons = balloons.map {
                if (it.id == balloon.id) it.copy(isPopped = true) else it
            }

            viewModel.onAnswerGiven(balloon.wordItem.id, isCorrect = true)
            viewModel.speakLumi("Fantastic! You got it!", MascotMood.HAPPY)

            if (currentRound >= totalRounds) {
                isGameOver = true
                viewModel.onSessionCompleted("balloon_pop", totalRounds, score, 50)
            } else {
                currentRound++
                // Transition to next round after short delay
                coroutineScope.launch {
                    delay(1200)
                    showConfetti = false
                    reactionState = null
                    setupNewRound()
                }
            }
        } else {
            streak = 0
            reactionState = MascotMood.ENCOURAGING
            mascotPrompt = "Oops! Try another balloon! 🎈"
            viewModel.onAnswerGiven(balloon.wordItem.id, isCorrect = false)
            viewModel.speakLumi("Not quite, try again!", MascotMood.THINKING)
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE0F2FE), // Sky light blue top
                        Color(0xFFBAE6FD), // Soft horizon
                        Color(0xFFF0FDF4)  // Meadow green bottom
                    )
                )
            )
            .statusBarsPadding()
            .testTag("screen_balloon_pop")
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
                            .background(Color.White.copy(alpha = 0.9f), CircleShape)
                            .testTag("balloon_pop_back_btn")
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
                            text = stringResource(R.string.game_balloon_pop_title),
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
                    shadowElevation = 4.dp
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

            Spacer(modifier = Modifier.height(10.dp))

            // Target Word Prompt Banner
            targetWord?.let { word ->
                val translated = word.translations[targetLanguage.code] ?: word.englishWord

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.95f),
                    shadowElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(word.emoji, fontSize = if (isMobile) 36.sp else 44.sp)
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = translated,
                                        fontSize = if (isMobile) 20.sp else 26.sp,
                                        fontWeight = FontWeight.Black,
                                        color = SleekOceanDark
                                    )
                                    IconButton(
                                        onClick = {
                                            viewModel.speakLumi(translated, MascotMood.TALKING)
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                            contentDescription = "Listen",
                                            tint = SleekOcean,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = word.soundPrompt,
                                    fontSize = 12.sp,
                                    color = SleekTextMuted,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        if (streak > 1) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SleekEmerald
                            ) {
                                Text(
                                    text = "${streak}x Streak! 🔥",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Balloons Sky Playfield
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // 2x2 Floating Balloon Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    balloons.take(2).forEach { balloon ->
                        InteractiveBalloonItem(
                            balloon = balloon,
                            targetLanguageCode = targetLanguage.code,
                            isMobile = isMobile,
                            onPop = { handleBalloonSelected(balloon) }
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = if (isMobile) 130.dp else 160.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    balloons.drop(2).take(2).forEach { balloon ->
                        InteractiveBalloonItem(
                            balloon = balloon,
                            targetLanguageCode = targetLanguage.code,
                            isMobile = isMobile,
                            onPop = { handleBalloonSelected(balloon) }
                        )
                    }
                }
            }

            // Bottom Mascot Prompt
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                LumiMascot(
                    mood = reactionState ?: MascotMood.HAPPY,
                    speechBubble = mascotPrompt,
                    size = if (isMobile) 64.dp else 80.dp,
                    onClick = {
                        targetWord?.let {
                            val trans = it.translations[targetLanguage.code] ?: it.englishWord
                            viewModel.speakLumi("Pop the balloon with $trans!", MascotMood.HAPPY)
                        }
                    }
                )
            }
        }

        // Star confetti on correct pop
        if (showConfetti) {
            ConfettiCanvas(trigger = showConfetti, modifier = Modifier.fillMaxSize())
        }

        // Game Over View
        if (isGameOver) {
            GameOverView(
                correctCount = score,
                totalRounds = totalRounds,
                onPlayAgain = {
                    score = 0
                    currentRound = 1
                    streak = 0
                    isGameOver = false
                    setupNewRound()
                },
                onBack = onBack
            )
        }
    }
}

@Composable
fun InteractiveBalloonItem(
    balloon: BalloonData,
    targetLanguageCode: String,
    isMobile: Boolean,
    onPop: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "balloon_float_${balloon.id}")
    val floatAnim by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (1800 + balloon.floatSpeedOffset * 80).toInt(),
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float_offset"
    )

    var isFocused by remember { mutableStateOf(false) }
    val cardScale = if (isFocused) 1.12f else 1f

    val wordText = balloon.wordItem.translations[targetLanguageCode] ?: balloon.wordItem.englishWord

    AnimatedVisibility(
        visible = !balloon.isPopped,
        enter = scaleIn() + fadeIn(),
        exit = scaleOut(targetScale = 1.3f) + fadeOut()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .offset { IntOffset(0, floatAnim.roundToInt()) }
                .scale(cardScale)
                .onFocusChanged { isFocused = it.isFocused }
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onPop() }
                .testTag("balloon_${balloon.id}")
        ) {
            // Balloon Oval Body
            Surface(
                modifier = Modifier
                    .size(
                        width = if (isMobile) 130.dp else 165.dp,
                        height = if (isMobile) 150.dp else 190.dp
                    )
                    .shadow(
                        elevation = if (isFocused) 12.dp else 6.dp,
                        shape = RoundedCornerShape(percent = 50)
                    ),
                shape = RoundedCornerShape(percent = 50),
                color = balloon.color,
                border = if (isFocused) androidx.compose.foundation.BorderStroke(3.dp, SleekGold) else null
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Balloon Gloss Highlight Shine
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            color = Color.White.copy(alpha = 0.35f),
                            radius = size.width * 0.16f,
                            center = Offset(size.width * 0.30f, size.height * 0.28f)
                        )
                    }

                    // Content inside balloon
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(
                            text = balloon.wordItem.emoji,
                            fontSize = if (isMobile) 38.sp else 48.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.White.copy(alpha = 0.92f)
                        ) {
                            Text(
                                text = wordText,
                                fontSize = if (isMobile) 13.sp else 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = SleekTextDark,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // Balloon Knot & String
            Canvas(
                modifier = Modifier
                    .size(width = 24.dp, height = 28.dp)
            ) {
                // Little knot triangle
                val knotPath = Path().apply {
                    moveTo(size.width / 2f - 6f, 0f)
                    lineTo(size.width / 2f + 6f, 0f)
                    lineTo(size.width / 2f, 8f)
                    close()
                }
                drawPath(knotPath, color = balloon.color)

                // Wavy String
                val stringPath = Path().apply {
                    moveTo(size.width / 2f, 8f)
                    cubicTo(
                        size.width / 2f - 6f, 14f,
                        size.width / 2f + 6f, 20f,
                        size.width / 2f, size.height
                    )
                }
                drawPath(
                    path = stringPath,
                    color = Color.Gray.copy(alpha = 0.6f),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
                )
            }
        }
    }
}
