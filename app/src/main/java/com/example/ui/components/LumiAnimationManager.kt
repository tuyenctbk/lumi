package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.model.MascotMood
import com.example.ui.theme.SleekCoral
import com.example.ui.theme.SleekEmerald
import com.example.ui.theme.SleekGold
import com.example.ui.theme.SleekGoldDark
import com.example.ui.theme.SleekOcean
import com.example.ui.theme.SleekPurple
import kotlinx.coroutines.delay

/**
 * Quiz feedback types for Lumi Mascot performance animations.
 */
enum class QuizFeedbackType(
    val title: String,
    val subtitle: String,
    val emoji: String,
    val mascotMood: MascotMood,
    val color: Color
) {
    SUCCESS(
        title = "Super Job! 🌟",
        subtitle = "+10 Stars Earned!",
        emoji = "✨",
        mascotMood = MascotMood.HAPPY,
        color = SleekEmerald
    ),
    PERFECT_STREAK(
        title = "Incredible Streak! 🔥",
        subtitle = "3 in a row! You're on fire!",
        emoji = "⚡",
        mascotMood = MascotMood.SUPERSTAR,
        color = SleekGold
    ),
    CELEBRATION(
        title = "Lesson Complete! 🏆",
        subtitle = "All targets mastered!",
        emoji = "🎉",
        mascotMood = MascotMood.SUPERSTAR,
        color = SleekPurple
    ),
    ENCOURAGEMENT(
        title = "Good Try! 💪",
        subtitle = "Listen closely and try again!",
        emoji = "💡",
        mascotMood = MascotMood.ENCOURAGING,
        color = SleekOcean
    )
}

/**
 * Lightweight Lottie Vector Specs for quiz performance feedback
 */
object LumiQuizLottieSpecs {
    val SUCCESS_STARS_JSON = """
    {
      "v": "5.5.7",
      "fr": 60,
      "ip": 0,
      "op": 60,
      "w": 120,
      "h": 120,
      "nm": "success_stars",
      "ddd": 0,
      "assets": [],
      "layers": [
        {
          "ddd": 0,
          "ind": 1,
          "ty": 4,
          "nm": "star_pop",
          "sr": 1,
          "ks": {
            "o": {"a": 1, "k": [{"t": 0, "s": [0]}, {"t": 15, "s": [100]}, {"t": 60, "s": [0]}]},
            "r": {"a": 1, "k": [{"t": 0, "s": [-30]}, {"t": 60, "s": [90]}]},
            "p": {"a": 1, "k": [{"t": 0, "s": [60, 70, 0]}, {"t": 60, "s": [60, 45, 0]}]},
            "a": {"a": 0, "k": [0, 0, 0]},
            "s": {"a": 1, "k": [{"t": 0, "s": [40, 40, 100]}, {"t": 20, "s": [120, 120, 100]}, {"t": 60, "s": [90, 90, 100]}]}
          },
          "ao": 0,
          "shapes": [
            {
              "ty": "sr",
              "sy": 1,
              "pt": {"a": 0, "k": 5},
              "p": {"a": 0, "k": [0, 0]},
              "r": {"a": 0, "k": 0},
              "or": {"a": 0, "k": 32},
              "os": {"a": 0, "k": 0},
              "ir": {"a": 0, "k": 16},
              "is": {"a": 0, "k": 0}
            },
            {
              "ty": "fl",
              "c": {"a": 0, "k": [1, 0.84, 0.2, 1]},
              "o": {"a": 0, "k": 100}
            }
          ],
          "ip": 0,
          "op": 60,
          "st": 0,
          "bm": 0
        }
      ]
    }
    """.trimIndent()

    val ENCOURAGEMENT_BUBBLE_JSON = """
    {
      "v": "5.5.7",
      "fr": 60,
      "ip": 0,
      "op": 60,
      "w": 100,
      "h": 100,
      "nm": "encourage_bubble",
      "ddd": 0,
      "assets": [],
      "layers": [
        {
          "ddd": 0,
          "ind": 1,
          "ty": 4,
          "nm": "bubble_ring",
          "sr": 1,
          "ks": {
            "o": {"a": 1, "k": [{"t": 0, "s": [20]}, {"t": 30, "s": [90]}, {"t": 60, "s": [20]}]},
            "r": {"a": 1, "k": [{"t": 0, "s": [-15]}, {"t": 60, "s": [15]}]},
            "p": {"a": 0, "k": [50, 50, 0]},
            "a": {"a": 0, "k": [0, 0, 0]},
            "s": {"a": 1, "k": [{"t": 0, "s": [85, 85, 100]}, {"t": 30, "s": [115, 115, 100]}, {"t": 60, "s": [85, 85, 100]}]}
          },
          "ao": 0,
          "shapes": [
            {
              "ty": "el",
              "p": {"a": 0, "k": [0, 0]},
              "s": {"a": 0, "k": [65, 65]}
            },
            {
              "ty": "st",
              "c": {"a": 0, "k": [0.3, 0.65, 0.95, 0.8]},
              "o": {"a": 0, "k": 100},
              "w": {"a": 0, "k": 5}
            }
          ],
          "ip": 0,
          "op": 60,
          "st": 0,
          "bm": 0
        }
      ]
    }
    """.trimIndent()
}

/**
 * LumiQuizFeedbackOverlay
 *
 * Full overlay triggered during quiz interactions.
 * Combines Lottie animation, Mascot mood reaction, and rewarding visual badges.
 */
@Composable
fun LumiQuizFeedbackOverlay(
    feedbackType: QuizFeedbackType,
    visible: Boolean,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isDisplayed by remember { mutableStateOf(false) }

    LaunchedEffect(visible, feedbackType) {
        if (visible) {
            isDisplayed = true
            delay(1600)
            isDisplayed = false
            onFinished()
        }
    }

    AnimatedVisibility(
        visible = isDisplayed,
        enter = fadeIn(tween(200)) + scaleIn(tween(250, easing = FastOutSlowInEasing), initialScale = 0.7f),
        exit = fadeOut(tween(200)) + scaleOut(tween(200), targetScale = 0.9f),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = Color.White,
                border = BorderStroke(2.5.dp, feedbackType.color),
                shadowElevation = 16.dp,
                modifier = Modifier
                    .padding(24.dp)
                    .testTag("lumi_quiz_feedback_card")
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)
                ) {
                    // Mascot & Lottie Particle Box
                    Box(
                        modifier = Modifier.size(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val specJson = when (feedbackType) {
                            QuizFeedbackType.SUCCESS, QuizFeedbackType.PERFECT_STREAK -> LumiQuizLottieSpecs.SUCCESS_STARS_JSON
                            QuizFeedbackType.CELEBRATION -> LumiMilestoneLottieSpecs.FIREWORK_CELEBRATION_JSON
                            QuizFeedbackType.ENCOURAGEMENT -> LumiQuizLottieSpecs.ENCOURAGEMENT_BUBBLE_JSON
                        }

                        val composition by rememberLottieComposition(
                            spec = LottieCompositionSpec.JsonString(specJson)
                        )
                        val progress by animateLottieCompositionAsState(
                            composition = composition,
                            iterations = 1,
                            isPlaying = true
                        )

                        if (composition != null) {
                            LottieAnimation(
                                composition = composition,
                                progress = { progress },
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        LumiMascot(
                            mood = feedbackType.mascotMood,
                            size = 80.dp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = feedbackType.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = feedbackType.color,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = feedbackType.subtitle,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.DarkGray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
