package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Icon
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
import com.example.ui.theme.SleekEmerald
import com.example.ui.theme.SleekEmeraldDark
import com.example.ui.theme.SleekGold
import com.example.ui.theme.SleekGoldDark
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekSurfaceBorder
import com.example.ui.theme.SleekTextDark
import com.example.ui.theme.SleekTextMuted
import com.example.ui.viewmodel.LumiViewModel
import kotlinx.coroutines.delay

data class MovementTask(
    val title: String,
    val description: String,
    val emoji: String,
    val spokenPrompt: String
)

@Composable
fun MovementQuestScreen(
    viewModel: LumiViewModel,
    onBack: () -> Unit
) {
    val mascotMood by viewModel.mascotMood.collectAsState()
    val mascotBubble by viewModel.mascotSpeechBubble.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()

    val quests = remember {
        listOf(
            MovementTask(
                "Find Something Blue!",
                "Explore your room and touch or pick up something BLUE!",
                "🔵",
                "Stand up! Find something blue in your house and touch it!"
            ),
            MovementTask(
                "Froggy Jump Time!",
                "Jump up high like a happy little frog 5 times!",
                "🐸",
                "Let's get moving! Jump high like a frog five times! Ribbit!"
            ),
            MovementTask(
                "Space Rocket Stretch!",
                "Reach your hands up to the sky as high as a rocket!",
                "🚀",
                "Stretch your arms way up to the stars like a rocket blast off!"
            ),
            MovementTask(
                "Find Something Round!",
                "Can you spot a ball, clock, or round toy in your room?",
                "⚽",
                "Quick! Look around your room for something that is round like a ball!"
            )
        )
    }

    var currentQuest by remember { mutableStateOf(quests.random()) }
    var secondsLeft by remember { mutableIntStateOf(15) }
    var isTimerRunning by remember { mutableStateOf(true) }
    var isCompleted by remember { mutableStateOf(false) }
    var confettiTrigger by remember { mutableStateOf(false) }

    LaunchedEffect(currentQuest) {
        secondsLeft = 15
        isTimerRunning = true
        isCompleted = false
        confettiTrigger = false
        delay(400)
        viewModel.speakLumi(currentQuest.spokenPrompt, MascotMood.HAPPY)
    }

    LaunchedEffect(isTimerRunning, secondsLeft) {
        if (isTimerRunning && secondsLeft > 0) {
            delay(1000)
            secondsLeft--
        } else if (secondsLeft == 0) {
            isTimerRunning = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SleekBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FocusableCard(
                    onClick = onBack,
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = SleekSurface,
                    unfocusedBorderColor = SleekSurfaceBorder,
                    focusedBorderColor = SleekEmerald,
                    testTag = "movement_back_button"
                ) {
                    Box(modifier = Modifier.padding(12.dp), contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = SleekTextDark
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsRun,
                        contentDescription = null,
                        tint = SleekEmerald
                    )
                    Text(
                        text = "Active Movement Quest",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = SleekTextDark
                    )
                }

                Spacer(modifier = Modifier.size(48.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (!isCompleted) {
                // Quest Stage
                Surface(
                    shape = RoundedCornerShape(32.dp),
                    color = SleekSurface,
                    border = BorderStroke(1.5.dp, SleekSurfaceBorder),
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .padding(vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = SleekEmerald.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, SleekEmerald.copy(alpha = 0.25f)),
                            modifier = Modifier.size(90.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = currentQuest.emoji,
                                    fontSize = 50.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = currentQuest.title,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = SleekTextDark,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = currentQuest.description,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = SleekTextMuted,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )

                        // Timer Circle
                        Surface(
                            shape = CircleShape,
                            color = SleekGold.copy(alpha = 0.20f),
                            border = BorderStroke(1.5.dp, SleekGoldDark.copy(alpha = 0.3f)),
                            modifier = Modifier.size(76.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "$secondsLeft",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SleekGoldDark
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        FocusableCard(
                            onClick = {
                                isCompleted = true
                                isTimerRunning = false
                                confettiTrigger = true
                                viewModel.onSessionCompleted("movement_quest", 1, 1, 15)
                            },
                            shape = RoundedCornerShape(22.dp),
                            backgroundColor = SleekEmerald,
                            unfocusedBorderColor = SleekEmeraldDark,
                            focusedBorderColor = SleekGold,
                            focusedScale = 1.08f,
                            elevation = 4.dp,
                            testTag = "movement_done_button"
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 32.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                                Text(
                                    text = "I Did It! 🌟",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            } else {
                // Completed View
                Column(
                    modifier = Modifier.padding(top = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = CircleShape,
                        color = SleekGold.copy(alpha = 0.2f),
                        border = BorderStroke(2.dp, SleekGoldDark),
                        modifier = Modifier.size(100.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "🌟", fontSize = 54.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Awesome Job Moving!",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = SleekTextDark
                    )
                    Text(
                        text = "You earned +15 bonus stars for staying active!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SleekEmerald,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        FocusableCard(
                            onClick = {
                                currentQuest = quests.shuffled().first()
                            },
                            shape = RoundedCornerShape(20.dp),
                            backgroundColor = SleekGold,
                            unfocusedBorderColor = SleekGoldDark,
                            focusedBorderColor = SleekEmerald,
                            testTag = "another_quest_button"
                        ) {
                            Text(
                                text = "Another Quest",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = SleekTextDark,
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 14.dp)
                            )
                        }

                        FocusableCard(
                            onClick = onBack,
                            shape = RoundedCornerShape(20.dp),
                            backgroundColor = SleekSurface,
                            unfocusedBorderColor = SleekSurfaceBorder,
                            focusedBorderColor = SleekEmerald,
                            testTag = "quest_back_button"
                        ) {
                            Text(
                                text = "Back to Islands",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = SleekTextDark,
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 14.dp)
                            )
                        }
                    }
                }
            }
        }

        // Mascot
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 12.dp)
        ) {
            LumiMascot(
                mood = mascotMood,
                isSpeaking = isSpeaking,
                thoughtBubbleEmoji = mascotBubble,
                size = 110.dp,
                onClick = { viewModel.speakLumi(currentQuest.spokenPrompt) }
            )
        }

        ConfettiCanvas(trigger = confettiTrigger)
    }
}

