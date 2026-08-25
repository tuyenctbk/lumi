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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.audio.SoundFxHelper
import com.example.model.MascotMood
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

/**
 * Predefined Lottie JSON Vector Specifications for Milestone Celebrations
 */
object LumiMilestoneLottieSpecs {
    val FIREWORK_CELEBRATION_JSON = """
    {
      "v": "5.5.7", "fr": 60, "ip": 0, "op": 90, "w": 150, "h": 150, "nm": "firework_burst",
      "layers": [
        {
          "ddd": 0, "ind": 1, "ty": 4, "nm": "burst_ring", "sr": 1,
          "ks": {
            "o": {"a": 1, "k": [{"t": 0, "s": [100]}, {"t": 45, "s": [80]}, {"t": 90, "s": [0]}]},
            "r": {"a": 1, "k": [{"t": 0, "s": [0]}, {"t": 90, "s": [180]}]},
            "p": {"a": 0, "k": [75, 75, 0]},
            "s": {"a": 1, "k": [{"t": 0, "s": [30, 30, 100]}, {"t": 60, "s": [120, 120, 100]}, {"t": 90, "s": [140, 140, 100]}]}
          },
          "shapes": [
            {
              "ty": "sr", "sy": 1, "pt": {"a": 0, "k": 8}, "p": {"a": 0, "k": [0, 0]},
              "r": {"a": 0, "k": 0}, "or": {"a": 0, "k": 50}, "os": {"a": 0, "k": 0},
              "ir": {"a": 0, "k": 25}, "is": {"a": 0, "k": 0}
            },
            {
              "ty": "fl", "c": {"a": 0, "k": [1, 0.75, 0.1, 0.8]}, "o": {"a": 0, "k": 100}
            }
          ],
          "ip": 0, "op": 90, "st": 0
        },
        {
          "ddd": 0, "ind": 2, "ty": 4, "nm": "sparkles", "sr": 1,
          "ks": {
            "o": {"a": 1, "k": [{"t": 0, "s": [0]}, {"t": 30, "s": [100]}, {"t": 90, "s": [0]}]},
            "r": {"a": 1, "k": [{"t": 0, "s": [0]}, {"t": 90, "s": [360]}]},
            "p": {"a": 0, "k": [75, 75, 0]},
            "s": {"a": 1, "k": [{"t": 0, "s": [40, 40, 100]}, {"t": 90, "s": [110, 110, 100]}]}
          },
          "shapes": [
            {
              "ty": "el", "p": {"a": 0, "k": [0, -35]}, "s": {"a": 0, "k": [10, 10]}
            },
            {
              "ty": "el", "p": {"a": 0, "k": [35, 0]}, "s": {"a": 0, "k": [10, 10]}
            },
            {
              "ty": "el", "p": {"a": 0, "k": [0, 35]}, "s": {"a": 0, "k": [10, 10]}
            },
            {
              "ty": "el", "p": {"a": 0, "k": [-35, 0]}, "s": {"a": 0, "k": [10, 10]}
            },
            {
              "ty": "fl", "c": {"a": 0, "k": [0.3, 0.7, 1.0, 0.9]}, "o": {"a": 0, "k": 100}
            }
          ],
          "ip": 0, "op": 90, "st": 0
        }
      ]
    }
    """.trimIndent()

    val TROPHY_BURST_JSON = """
    {
      "v": "5.5.7", "fr": 60, "ip": 0, "op": 120, "w": 150, "h": 150, "nm": "trophy_burst",
      "layers": [
        {
          "ddd": 0, "ind": 1, "ty": 4, "nm": "golden_crown_pulse", "sr": 1,
          "ks": {
            "o": {"a": 1, "k": [{"t": 0, "s": [40]}, {"t": 60, "s": [100]}, {"t": 120, "s": [40]}]},
            "r": {"a": 1, "k": [{"t": 0, "s": [-10]}, {"t": 60, "s": [10]}, {"t": 120, "s": [-10]}]},
            "p": {"a": 0, "k": [75, 75, 0]},
            "s": {"a": 1, "k": [{"t": 0, "s": [90, 90, 100]}, {"t": 60, "s": [125, 125, 100]}, {"t": 120, "s": [90, 90, 100]}]}
          },
          "shapes": [
            {
              "ty": "el", "p": {"a": 0, "k": [0, 0]}, "s": {"a": 0, "k": [90, 90]}
            },
            {
              "ty": "fl", "c": {"a": 0, "k": [1, 0.84, 0.0, 0.5]}, "o": {"a": 0, "k": 100}
            }
          ],
          "ip": 0, "op": 120, "st": 0
        }
      ]
    }
    """.trimIndent()
}

enum class LearningMilestoneType(
    val title: String,
    val description: String,
    val emoji: String,
    val bonusStars: Int,
    val celebrationVoice: String
) {
    STREAK_3_DAYS(
        title = "3-Day Streak Spark! 🔥",
        description = "You have practiced for 3 days in a row! Lumi is super proud of you!",
        emoji = "🔥",
        bonusStars = 30,
        celebrationVoice = "Woohoo! Three days in a row! You are on fire!"
    ),
    STREAK_7_DAYS(
        title = "7-Day Superstar Champion! 🏆",
        description = "A full week of language learning! You earned the Golden Trophy!",
        emoji = "🏆",
        bonusStars = 70,
        celebrationVoice = "Incredible job! A full seven day streak! You are a language superstar!"
    ),
    FIRST_WORD_MASTERED(
        title = "First Word Mastered! 🌟",
        description = "You answered your first vocabulary word perfectly!",
        emoji = "🌟",
        bonusStars = 20,
        celebrationVoice = "Hooray! You mastered your first word! Keep going!"
    ),
    WORDS_MASTERED_5(
        title = "5 Words Scout! 📚",
        description = "You have mastered 5 different vocabulary words!",
        emoji = "📚",
        bonusStars = 40,
        celebrationVoice = "Awesome scouting! 5 whole words in your memory bank!"
    ),
    PERFECT_LESSON(
        title = "100% Perfect Lesson! ✨",
        description = "All answers were correct! High five from Lumi!",
        emoji = "✨",
        bonusStars = 25,
        celebrationVoice = "Flawless score! High five from Lumi!"
    )
}

/**
 * LumiMilestoneCelebrationDialog
 *
 * Fullscreen celebration overlay that pairs Lottie mascot animations, confetti effects,
 * audio fanfare, and spoken encouragement when a learning milestone is achieved.
 */
@Composable
fun LumiMilestoneCelebrationDialog(
    milestone: LearningMilestoneType,
    onDismiss: () -> Unit,
    onSpeak: (String) -> Unit = {}
) {
    LaunchedEffect(milestone) {
        SoundFxHelper.playCelebrationFanfare()
        onSpeak(milestone.celebrationVoice)
    }

    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.JsonString(
            if (milestone == LearningMilestoneType.STREAK_7_DAYS) {
                LumiMilestoneLottieSpecs.TROPHY_BURST_JSON
            } else {
                LumiMilestoneLottieSpecs.FIREWORK_CELEBRATION_JSON
            }
        )
    )

    val lottieProgress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true
    )

    val infiniteTransition = rememberInfiniteTransition(label = "milestone_anim")
    val bounceScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            border = BorderStroke(3.dp, SleekGold),
            shadowElevation = 16.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("lumi_milestone_dialog")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Top Celebration Badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SleekGold.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, SleekGold)
                    ) {
                        Text(
                            text = "🎉 MILESTONE UNLOCKED 🎉",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = SleekGoldDark,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Lottie Mascot with Celebration Halo
                    Box(
                        modifier = Modifier.size(150.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (composition != null) {
                            LottieAnimation(
                                composition = composition,
                                progress = { lottieProgress },
                                modifier = Modifier.size(170.dp)
                            )
                        }

                        LumiMascot(
                            mood = MascotMood.SUPERSTAR,
                            size = 110.dp,
                            speechBubble = milestone.emoji,
                            modifier = Modifier.scale(bounceScale)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = milestone.title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = SleekTextDark,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = milestone.description,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = SleekTextMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Bonus Stars Pill
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SleekCoral.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, SleekCoral)
                    ) {
                        Text(
                            text = "+${milestone.bonusStars} Bonus Stars Awarded! ⭐",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = SleekCoral,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Continue Button
                    Button(
                        onClick = {
                            SoundFxHelper.playStarBurst()
                            onDismiss()
                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SleekOcean
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("milestone_continue_button")
                    ) {
                        Text(
                            text = "Awesome! Keep Learning 🚀",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
