package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MascotMood
import com.example.ui.components.ConfettiCanvas
import com.example.ui.components.FocusableCard
import com.example.ui.components.LumiMascot
import com.example.ui.theme.SleekBackground
import com.example.ui.theme.SleekCoral
import com.example.ui.theme.SleekEmerald
import com.example.ui.theme.SleekEmeraldDark
import com.example.ui.theme.SleekGold
import com.example.ui.theme.SleekGoldDark
import com.example.ui.theme.SleekOcean
import com.example.ui.theme.SleekOceanDark
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekSurfaceBorder
import com.example.ui.theme.SleekTextDark
import com.example.ui.theme.SleekTextMuted
import com.example.ui.viewmodel.LumiViewModel

/**
 * LessonCelebrationScreen
 *
 * Dedicated festive victory screen triggered when a lesson or quiz is completed.
 * Utilizes LumiMascot in CELEBRATING / SUPERSTAR state with an animated festive confetti
 * overlay, score statistics breakdown, and streak milestone callouts.
 */
@Composable
fun LessonCelebrationScreen(
    viewModel: LumiViewModel,
    score: Int = 100,
    wordsMasteredCount: Int = 5,
    accuracyPercent: Int = 100,
    onContinueNext: () -> Unit,
    onBackHome: () -> Unit
) {
    val streakDays by viewModel.streakDays.collectAsState()
    val points by viewModel.points.collectAsState()

    val context = androidx.compose.ui.platform.LocalContext.current
    val soundManager = androidx.compose.runtime.remember { com.example.audio.SoundManager.getInstance(context) }

    // Trigger celebration audio and mascot animation on load
    LaunchedEffect(Unit) {
        soundManager.playLessonCompleteFanfare()
        viewModel.speakLumi("Woohoo! Outstanding effort! You completed the lesson with flying colors!", MascotMood.CELEBRATING)
        viewModel.onSessionCompleted(
            gameType = "lesson_celebration",
            practicedCount = wordsMasteredCount,
            correctCount = (wordsMasteredCount * accuracyPercent) / 100,
            durationSeconds = 180
        )
    }

    val pulseTransition = rememberInfiniteTransition(label = "celebration_pulse")
    val starScale by pulseTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "star_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SleekBackground)
            .testTag("lesson_celebration_screen")
    ) {
        // Festive Confetti Particles Overlay
        ConfettiCanvas(
            trigger = true,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Main Mascot Celebrating Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = SleekGold.copy(alpha = 0.2f),
                    border = BorderStroke(1.5.dp, SleekGoldDark),
                    modifier = Modifier.scale(starScale)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = SleekGoldDark,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "LESSON COMPLETED!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = SleekGoldDark,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Lumi Mascot in CELEBRATING state
                LumiMascot(
                    mood = MascotMood.CELEBRATING,
                    speechBubble = "You're a Language Superstar!",
                    size = 150.dp,
                    onClick = {
                        viewModel.speakLumi("Hooray! Keep up the momentum!", MascotMood.SUPERSTAR)
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Fantastic Job!",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                    color = SleekTextDark,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "You've earned new stars and expanded your vocabulary memory!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = SleekTextMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp)
                )
            }

            // Lesson Stats Summary Card
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = SleekSurface),
                border = BorderStroke(1.5.dp, SleekSurfaceBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Lesson Results",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = SleekTextDark
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        VictoryStatTile(
                            label = "Accuracy",
                            value = "$accuracyPercent%",
                            icon = Icons.Default.CheckCircle,
                            color = SleekEmerald,
                            modifier = Modifier.weight(1f)
                        )
                        VictoryStatTile(
                            label = "Score",
                            value = "+$score XP",
                            icon = Icons.Default.Star,
                            color = SleekGoldDark,
                            modifier = Modifier.weight(1f)
                        )
                        VictoryStatTile(
                            label = "Streak",
                            value = "$streakDays Days",
                            icon = Icons.Default.Whatshot,
                            color = SleekCoral,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onBackHome,
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.5.dp, SleekSurfaceBorder),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SleekTextDark),
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                        .testTag("celebration_back_home_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Home, contentDescription = null)
                        Text("Home", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }

                Button(
                    onClick = onContinueNext,
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SleekEmerald),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .weight(1.2f)
                        .height(54.dp)
                        .testTag("celebration_continue_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Next Lesson", fontWeight = FontWeight.Black, fontSize = 16.sp)
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                    }
                }
            }
        }
    }
}

@Composable
private fun VictoryStatTile(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = SleekTextDark
            )
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = SleekTextMuted
            )
        }
    }
}
