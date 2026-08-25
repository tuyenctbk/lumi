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
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.R
import com.example.model.MascotMood
import com.example.ui.theme.SleekCoral
import com.example.ui.theme.SleekEmerald
import com.example.ui.theme.SleekGold
import com.example.ui.theme.SleekGoldDark
import com.example.ui.theme.SleekOcean
import com.example.ui.theme.SleekOceanDark
import com.example.ui.theme.SleekPurple
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekSurfaceBorder
import com.example.ui.theme.SleekTextDark
import com.example.ui.theme.SleekTextMuted

/**
 * Lottie JSON Vector Specifications for Interactive Mascot Reactions
 */
object LumiLottieVectorSpecs {
    // Confetti and Star Burst for Correct Answers
    val CORRECT_STAR_BURST_JSON = """
    {
      "v": "5.5.7",
      "fr": 60,
      "ip": 0,
      "op": 60,
      "w": 120,
      "h": 120,
      "nm": "lumi_correct_star",
      "ddd": 0,
      "assets": [],
      "layers": [
        {
          "ddd": 0,
          "ind": 1,
          "ty": 4,
          "nm": "burst_ring",
          "sr": 1,
          "ks": {
            "o": {"a": 1, "k": [{"t": 0, "s": [100]}, {"t": 45, "s": [80]}, {"t": 60, "s": [0]}]},
            "r": {"a": 1, "k": [{"t": 0, "s": [-20]}, {"t": 60, "s": [120]}]},
            "p": {"a": 0, "k": [60, 60, 0]},
            "a": {"a": 0, "k": [0, 0, 0]},
            "s": {"a": 1, "k": [{"t": 0, "s": [30, 30, 100]}, {"t": 35, "s": [125, 125, 100]}, {"t": 60, "s": [140, 140, 100]}]}
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
              "ir": {"a": 0, "k": 15},
              "is": {"a": 0, "k": 0}
            },
            {
              "ty": "fl",
              "c": {"a": 0, "k": [1.0, 0.85, 0.1, 0.9]},
              "o": {"a": 0, "k": 100}
            }
          ],
          "ip": 0,
          "op": 60,
          "st": 0,
          "bm": 0
        },
        {
          "ddd": 0,
          "ind": 2,
          "ty": 4,
          "nm": "outer_sparks",
          "sr": 1,
          "ks": {
            "o": {"a": 1, "k": [{"t": 0, "s": [0]}, {"t": 15, "s": [100]}, {"t": 60, "s": [0]}]},
            "r": {"a": 1, "k": [{"t": 0, "s": [0]}, {"t": 60, "s": [180]}]},
            "p": {"a": 0, "k": [60, 60, 0]},
            "a": {"a": 0, "k": [0, 0, 0]},
            "s": {"a": 1, "k": [{"t": 0, "s": [50, 50, 100]}, {"t": 60, "s": [130, 130, 100]}]}
          },
          "ao": 0,
          "shapes": [
            {
              "ty": "gr",
              "it": [
                {
                  "ty": "el",
                  "p": {"a": 0, "k": [0, -42]},
                  "s": {"a": 0, "k": [10, 10]}
                },
                {
                  "ty": "el",
                  "p": {"a": 0, "k": [40, 0]},
                  "s": {"a": 0, "k": [8, 8]}
                },
                {
                  "ty": "el",
                  "p": {"a": 0, "k": [-38, 12]},
                  "s": {"a": 0, "k": [9, 9]}
                },
                {
                  "ty": "fl",
                  "c": {"a": 0, "k": [0.1, 0.8, 0.45, 0.95]},
                  "o": {"a": 0, "k": 100}
                }
              ]
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

    // Encouraging Pulse Bubble for Incorrect Answers
    val INCORRECT_ENCOURAGEMENT_JSON = """
    {
      "v": "5.5.7",
      "fr": 60,
      "ip": 0,
      "op": 90,
      "w": 120,
      "h": 120,
      "nm": "lumi_encourage_pulse",
      "ddd": 0,
      "assets": [],
      "layers": [
        {
          "ddd": 0,
          "ind": 1,
          "ty": 4,
          "nm": "gentle_wave",
          "sr": 1,
          "ks": {
            "o": {"a": 1, "k": [{"t": 0, "s": [80]}, {"t": 45, "s": [30]}, {"t": 90, "s": [80]}]},
            "r": {"a": 1, "k": [{"t": 0, "s": [-10]}, {"t": 45, "s": [10]}, {"t": 90, "s": [-10]}]},
            "p": {"a": 0, "k": [60, 60, 0]},
            "a": {"a": 0, "k": [0, 0, 0]},
            "s": {"a": 1, "k": [{"t": 0, "s": [90, 90, 100]}, {"t": 45, "s": [115, 115, 100]}, {"t": 90, "s": [90, 90, 100]}]}
          },
          "ao": 0,
          "shapes": [
            {
              "ty": "el",
              "p": {"a": 0, "k": [0, 0]},
              "s": {"a": 0, "k": [75, 75]}
            },
            {
              "ty": "st",
              "c": {"a": 0, "k": [0.25, 0.65, 0.98, 0.7]},
              "o": {"a": 0, "k": 100},
              "w": {"a": 0, "k": 6}
            },
            {
              "ty": "fl",
              "c": {"a": 0, "k": [0.25, 0.65, 0.98, 0.15]},
              "o": {"a": 0, "k": 100}
            }
          ],
          "ip": 0,
          "op": 90,
          "st": 0,
          "bm": 0
        }
      ]
    }
    """.trimIndent()

    // Blazing Streak Fire for Multi-Streak Correct Answers
    val STREAK_FIRE_BURST_JSON = """
    {
      "v": "5.5.7",
      "fr": 60,
      "ip": 0,
      "op": 60,
      "w": 120,
      "h": 120,
      "nm": "lumi_streak_fire",
      "ddd": 0,
      "assets": [],
      "layers": [
        {
          "ddd": 0,
          "ind": 1,
          "ty": 4,
          "nm": "flame_blast",
          "sr": 1,
          "ks": {
            "o": {"a": 1, "k": [{"t": 0, "s": [90]}, {"t": 30, "s": [100]}, {"t": 60, "s": [85]}]},
            "r": {"a": 1, "k": [{"t": 0, "s": [-15]}, {"t": 30, "s": [15]}, {"t": 60, "s": [-15]}]},
            "p": {"a": 1, "k": [{"t": 0, "s": [60, 65, 0]}, {"t": 30, "s": [60, 50, 0]}, {"t": 60, "s": [60, 65, 0]}]},
            "a": {"a": 0, "k": [0, 0, 0]},
            "s": {"a": 1, "k": [{"t": 0, "s": [100, 100, 100]}, {"t": 30, "s": [130, 130, 100]}, {"t": 60, "s": [100, 100, 100]}]}
          },
          "ao": 0,
          "shapes": [
            {
              "ty": "sr",
              "sy": 1,
              "pt": {"a": 0, "k": 6},
              "p": {"a": 0, "k": [0, 0]},
              "r": {"a": 0, "k": 0},
              "or": {"a": 0, "k": 40},
              "os": {"a": 0, "k": 0},
              "ir": {"a": 0, "k": 20},
              "is": {"a": 0, "k": 0}
            },
            {
              "ty": "fl",
              "c": {"a": 0, "k": [1.0, 0.45, 0.05, 0.85]},
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
}

/**
 * Animated Lottie Component for the Lumi Mascot that reacts to correct/incorrect answers during lessons.
 */
@Composable
fun LumiLottieReaction(
    isCorrect: Boolean?,
    modifier: Modifier = Modifier,
    streakCount: Int = 0,
    size: Dp = 100.dp,
    customSpeech: String? = null
) {
    val mascotMood = when {
        isCorrect == true && streakCount >= 3 -> MascotMood.SUPERSTAR
        isCorrect == true -> MascotMood.HAPPY
        isCorrect == false -> MascotMood.ENCOURAGING
        else -> MascotMood.IDLE
    }

    val selectedLottieJson = when {
        isCorrect == true && streakCount >= 3 -> LumiLottieVectorSpecs.STREAK_FIRE_BURST_JSON
        isCorrect == true -> LumiLottieVectorSpecs.CORRECT_STAR_BURST_JSON
        isCorrect == false -> LumiLottieVectorSpecs.INCORRECT_ENCOURAGEMENT_JSON
        else -> null
    }

    val composition by rememberLottieComposition(
        spec = if (selectedLottieJson != null) LottieCompositionSpec.JsonString(selectedLottieJson) else LottieCompositionSpec.JsonString(LumiLottieVectorSpecs.CORRECT_STAR_BURST_JSON)
    )

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = if (isCorrect != null) 1 else LottieConstants.IterateForever,
        isPlaying = isCorrect != null
    )

    val speechText = customSpeech ?: when {
        isCorrect == true && streakCount >= 3 -> "🔥 ${streakCount} in a Row! Superstar!"
        isCorrect == true -> "🌟 Excellent! High five!"
        isCorrect == false -> "💪 Good try! Listen closely!"
        else -> "Tap an answer!"
    }

    Box(
        modifier = modifier
            .testTag("lumi_lottie_reaction_container"),
        contentAlignment = Alignment.Center
    ) {
        // Background Lottie Aura Particles
        if (selectedLottieJson != null && composition != null) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier
                    .size(size * 1.4f)
                    .align(Alignment.Center)
            )
        }

        // Mascot Character
        LumiMascot(
            mood = mascotMood,
            speechBubble = speechText,
            size = size
        )
    }
}

/**
 * Animated Answer Feedback Banner with Lottie particles, reaction badge, and encouraging helper text.
 */
@Composable
fun LumiAnswerReactionBanner(
    isCorrect: Boolean,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
    streakCount: Int = 0,
    correctTranslation: String? = null
) {
    val bannerColor = if (isCorrect) SleekEmerald else SleekOcean
    val iconVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Lightbulb

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        border = BorderStroke(2.dp, bannerColor),
        shadowElevation = 8.dp,
        modifier = modifier
            .fillMaxWidth()
            .testTag("lumi_answer_reaction_banner")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Reactive Mascot with Lottie
                LumiLottieReaction(
                    isCorrect = isCorrect,
                    streakCount = streakCount,
                    size = 54.dp,
                    customSpeech = null
                )

                Column {
                    Text(
                        text = if (isCorrect) {
                            if (streakCount >= 3) "🔥 Streak Bonus!" else "🌟 Correct! +10 XP"
                        } else {
                            "💪 Keep Learning!"
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = bannerColor
                    )

                    if (!isCorrect && !correctTranslation.isNullOrBlank()) {
                        Text(
                            text = "Correct: $correctTranslation",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = SleekTextDark
                        )
                    } else {
                        Text(
                            text = if (isCorrect) "Fantastic memory retention!" else "Practice makes perfect!",
                            fontSize = 12.sp,
                            color = SleekTextMuted
                        )
                    }
                }
            }

            Surface(
                onClick = onContinue,
                shape = RoundedCornerShape(14.dp),
                color = bannerColor,
                modifier = Modifier.testTag("lumi_reaction_next_button")
            ) {
                Text(
                    text = "Next ➔",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }
        }
    }
}
