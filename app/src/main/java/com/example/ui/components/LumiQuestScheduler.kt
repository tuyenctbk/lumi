package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeUp
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.window.Dialog
import com.example.model.MascotMood
import com.example.model.PhysicalBreakQuest
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
import kotlinx.coroutines.delay

/**
 * LumiQuestModal
 *
 * Interactive popup presented to children when the background periodic scheduler triggers
 * an offline physical activity quest (e.g. "Find something blue in your room!", "Do 5 star jumps!").
 */
@Composable
fun LumiQuestModal(
    quest: PhysicalBreakQuest,
    onCompleted: () -> Unit,
    onDismiss: () -> Unit,
    onReplayAudio: () -> Unit
) {
    var secondsLeft by remember { mutableIntStateOf(quest.durationSeconds) }
    var isTimerRunning by remember { mutableIntStateOf(1) }

    LaunchedEffect(quest.id) {
        while (secondsLeft > 0) {
            delay(1000)
            secondsLeft--
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = SleekSurface),
            border = BorderStroke(2.dp, quest.color.copy(alpha = 0.6f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 18.dp),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(10.dp)
                .testTag("lumi_quest_dialog")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Icon & Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = quest.color.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, quest.color.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsRun,
                                contentDescription = null,
                                tint = quest.colorDark,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Lumi Active Break Quest! 🏃",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = quest.colorDark
                            )
                        }
                    }

                    // Reward badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SleekGold.copy(alpha = 0.18f),
                        border = BorderStroke(1.dp, SleekGoldDark.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "+${quest.rewardPoints} ⭐",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = SleekGoldDark,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Lumi Mascot guiding the quest interaction with animated speech/movement
                LumiMascot(
                    mood = if (secondsLeft > 0) MascotMood.HAPPY else MascotMood.SUPERSTAR,
                    size = 110.dp,
                    thoughtBubbleEmoji = quest.emoji,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Text(
                    text = quest.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = SleekTextDark,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = quest.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SleekTextMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 10.dp)
                )

                // Timer / Action Pill
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFF8F9FA),
                    border = BorderStroke(1.dp, SleekSurfaceBorder),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (secondsLeft > 0) "⏳ ${secondsLeft}s movement timer" else "🎉 Time to complete!",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (secondsLeft > 0) SleekTextDark else SleekEmerald
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Replay voice prompt
                    FocusableCard(
                        onClick = onReplayAudio,
                        shape = RoundedCornerShape(16.dp),
                        backgroundColor = SleekSurface,
                        unfocusedBorderColor = SleekSurfaceBorder,
                        focusedBorderColor = quest.color,
                        modifier = Modifier.weight(1f),
                        testTag = "quest_replay_audio_button"
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Hear Again",
                                tint = SleekTextDark,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.size(6.dp))
                            Text(
                                text = "Hear Lumi 🔊",
                                fontWeight = FontWeight.Bold,
                                color = SleekTextDark,
                                fontSize = 13.sp
                            )
                        }
                    }

                    // Complete & claim stars
                    FocusableCard(
                        onClick = onCompleted,
                        shape = RoundedCornerShape(16.dp),
                        backgroundColor = SleekEmerald,
                        unfocusedBorderColor = SleekEmeraldDark,
                        focusedBorderColor = SleekGold,
                        modifier = Modifier.weight(1.3f),
                        testTag = "quest_complete_button"
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.size(6.dp))
                            Text(
                                text = "I Did It! ⭐",
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, SleekSurfaceBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("quest_skip_button")
                ) {
                    Text(
                        text = "Skip for Now",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekTextMuted
                    )
                }
            }
        }
    }
}
