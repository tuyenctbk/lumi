package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SleekCoral
import com.example.ui.theme.SleekGold
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekTextDark
import com.example.ui.theme.SleekTextMuted

/**
 * StreakCounter
 *
 * Visual progress bar component displaying consecutive days spent learning,
 * integrated with Room database persistence.
 */
@Composable
fun StreakCounter(
    currentStreakDays: Int,
    targetStreakGoal: Int = 7,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val progressFraction = (currentStreakDays.toFloat() / targetStreakGoal).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progressFraction,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "streak_progress_anim"
    )

    // Pulsing flame animation for active streak
    val infiniteTransition = rememberInfiniteTransition(label = "flame_pulse")
    val flameScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flame_scale"
    )

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = SleekSurface,
        border = BorderStroke(1.5.dp, SleekCoral.copy(alpha = 0.35f)),
        shadowElevation = 3.dp,
        modifier = modifier
            .fillMaxWidth()
            .testTag("streak_counter")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (compact) 12.dp else 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Streak Flame Icon & Badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = SleekCoral.copy(alpha = 0.15f),
                        modifier = Modifier.size(if (compact) 36.dp else 44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "🔥",
                                fontSize = if (compact) 20.sp else 26.sp,
                                modifier = Modifier.graphicsLayer {
                                    val s = if (currentStreakDays > 0) flameScale else 1.0f
                                    scaleX = s
                                    scaleY = s
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "$currentStreakDays Day Streak",
                            style = if (compact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = SleekTextDark
                        )
                        Text(
                            text = if (currentStreakDays >= targetStreakGoal) "7-Day Goal Reached! 🎉" else "$currentStreakDays of $targetStreakGoal Days Goal",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = SleekCoral
                        )
                    }
                }

                // Days Badge Pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SleekGold.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, SleekGold)
                ) {
                    Text(
                        text = "⭐ Goal: $targetStreakGoal Days",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekTextDark,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Animated Visual Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(Color.Black.copy(alpha = 0.08f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .height(14.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    SleekCoral,
                                    SleekGold
                                )
                            )
                        )
                )
            }

            // Milestone Labels Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Day 1 🌟", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SleekTextMuted)
                Text(text = "Day 3 🔥", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SleekTextMuted)
                Text(text = "Day 7 🏆", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = SleekGold)
            }
        }
    }
}
