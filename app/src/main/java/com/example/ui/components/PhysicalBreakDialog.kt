package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.VolumeUp
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
import androidx.compose.ui.res.stringResource
import com.example.R
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.PhysicalBreakQuest
import com.example.ui.theme.SleekCoral
import com.example.ui.theme.SleekEmerald
import com.example.ui.theme.SleekEmeraldDark
import com.example.ui.theme.SleekGold
import com.example.ui.theme.SleekGoldDark
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekSurfaceBorder
import com.example.ui.theme.SleekTextDark
import com.example.ui.theme.SleekTextMuted
import kotlinx.coroutines.delay

/**
 * PhysicalBreakDialog
 *
 * An engaging pop-up card for Android TV prompting children to perform an offline
 * physical activity (e.g. "Find something blue in your room!"), reducing sedentary time.
 */
@Composable
fun PhysicalBreakDialog(
    quest: PhysicalBreakQuest,
    onCompleted: () -> Unit,
    onDismiss: () -> Unit,
    onReplayAudio: () -> Unit = {}
) {
    var secondsRemaining by remember(quest.id) { mutableIntStateOf(quest.durationSeconds) }

    LaunchedEffect(quest.id) {
        while (secondsRemaining > 0) {
            delay(1000)
            secondsRemaining--
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        BoxWithConstraints {
            val isCompact = maxWidth < 400.dp
            Card(
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = SleekSurface),
                border = BorderStroke(2.dp, quest.color),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
                modifier = Modifier
                    .fillMaxWidth(if (isCompact) 0.98f else 0.85f)
                    .widthIn(max = 480.dp)
                    .padding(if (isCompact) 4.dp else 16.dp)
                    .testTag("physical_break_dialog")
            ) {
                Column(
                    modifier = Modifier.padding(if (isCompact) 16.dp else 26.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Tag
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = quest.color.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, quest.color.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsRun,
                                contentDescription = null,
                                tint = quest.colorDark,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = if (isCompact) "LUMI'S MOVEMENT BREAK" else "LUMI'S ACTIVE MOVEMENT BREAK",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = quest.colorDark,
                                letterSpacing = 0.8.sp,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }

                Spacer(modifier = Modifier.height(14.dp))

                // Big Quest Emoji
                Surface(
                    shape = RoundedCornerShape(26.dp),
                    color = quest.color.copy(alpha = 0.12f),
                    border = BorderStroke(1.5.dp, quest.color.copy(alpha = 0.3f)),
                    modifier = Modifier.size(95.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = quest.emoji, fontSize = 52.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = quest.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = SleekTextDark,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = quest.description,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = SleekTextMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
                )

                // Timer Pill & Stars Badge
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFFFF9F2),
                        border = BorderStroke(1.dp, SleekSurfaceBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = SleekGoldDark,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = if (secondsRemaining > 0) "${secondsRemaining}s" else "Time's Up!",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = SleekGoldDark
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SleekGold.copy(alpha = 0.18f),
                        border = BorderStroke(1.dp, SleekGoldDark.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "+${quest.rewardPoints} Stars ⭐",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = SleekGoldDark,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Re-read Audio Button
                    FocusableCard(
                        onClick = onReplayAudio,
                        shape = RoundedCornerShape(20.dp),
                        backgroundColor = SleekSurface,
                        unfocusedBorderColor = SleekSurfaceBorder,
                        focusedBorderColor = quest.color,
                        modifier = Modifier.weight(0.35f),
                        testTag = "physical_break_replay_audio"
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 14.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Listen again",
                                tint = quest.colorDark
                            )
                        }
                    }

                    // Complete Button
                    FocusableCard(
                        onClick = onCompleted,
                        shape = RoundedCornerShape(20.dp),
                        backgroundColor = SleekEmerald,
                        unfocusedBorderColor = SleekEmeraldDark,
                        focusedBorderColor = SleekGold,
                        focusedScale = 1.06f,
                        elevation = 4.dp,
                        modifier = Modifier.weight(0.65f),
                        testTag = "physical_break_done_button"
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 14.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "I Did It! 🌟",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                FocusableCard(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = SleekSurface,
                    unfocusedBorderColor = SleekSurfaceBorder,
                    focusedBorderColor = SleekCoral,
                    focusedScale = 1.04f,
                    testTag = "physical_break_later_button",
                    modifier = Modifier.widthIn(min = 120.dp)
                ) {
                    Text(
                        text = stringResource(R.string.button_later),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekTextMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp)
                    )
                }
            }
        }
    }
}
}
