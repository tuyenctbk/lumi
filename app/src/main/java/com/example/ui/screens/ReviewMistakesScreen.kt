package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.audio.SoundFxHelper
import com.example.model.MascotMood
import com.example.ui.components.ConfettiCanvas
import com.example.ui.components.LumiMascot
import com.example.ui.theme.SleekCoral
import com.example.ui.theme.SleekEmerald
import com.example.ui.theme.SleekEmeraldDark
import com.example.ui.theme.SleekOcean
import com.example.ui.theme.SleekPurple
import com.example.ui.viewmodel.LumiViewModel

/**
 * ReviewMistakesScreen
 *
 * Dedicated practice screen extracting previously incorrectly answered questions
 * from the Room database (word_progress where errorCount > 0) to help users conquer weak points.
 */
@Composable
fun ReviewMistakesScreen(
    viewModel: LumiViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val missedWords by viewModel.missedWords.collectAsState()
    val targetLanguage by viewModel.targetLanguage.collectAsState()

    var currentPracticeIndex by remember { mutableIntStateOf(0) }
    var selectedAnswerIndex by remember { mutableStateOf<Int?>(null) }
    var isAnswerCorrect by remember { mutableStateOf<Boolean?>(null) }
    var showConfetti by remember { mutableStateOf(false) }

    val activeWord = if (missedWords.isNotEmpty() && currentPracticeIndex < missedWords.size) {
        missedWords[currentPracticeIndex]
    } else null

    val targetWordText = if (activeWord != null) {
        activeWord.translations[targetLanguage.code] ?: activeWord.englishWord
    } else ""

    // Options generator for active practice word
    val practiceOptions = remember(activeWord, missedWords) {
        if (activeWord == null) emptyList()
        else {
            val distractor1 = "Apple"
            val distractor2 = "Friend"
            val distractor3 = "Sun"
            listOf(activeWord.englishWord, distractor1, distractor2, distractor3).shuffled()
        }
    }

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .testTag("review_mistakes_screen")
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Top Navigation Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            SoundFxHelper.playPop()
                            onNavigateBack()
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                            .testTag("review_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🎯", fontSize = 18.sp, modifier = Modifier.padding(end = 6.dp))
                            Text(
                                text = "Review Weak Points",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "Targeted Room DB mistake practice for ${targetLanguage.displayName}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                if (missedWords.isEmpty()) {
                    // Empty State: All mistakes cleared!
                    Card(
                        shape = RoundedCornerShape(26.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.5.dp, SleekEmerald),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp)
                            .testTag("review_mistakes_empty_card")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            LumiMascot(
                                mood = MascotMood.CELEBRATING,
                                speechBubble = "Woohoo! Zero mistakes recorded in Room DB!",
                                size = 120.dp
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = "🎉 Master Polyglot Status!",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = SleekEmeraldDark
                            )

                            Text(
                                text = "You have 0 pending mistakes in ${targetLanguage.displayName}. All words were answered with 100% precision!",
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 8.dp)
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = onNavigateBack,
                                colors = ButtonDefaults.buttonColors(containerColor = SleekEmerald),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.testTag("review_empty_back_button")
                            ) {
                                Text("Return to Home Lessons", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else if (activeWord != null) {
                    // Practice Progress Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Question ${currentPracticeIndex + 1} of ${missedWords.size}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            shape = CircleShape,
                            color = SleekCoral.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "🔥 Reviewing weak point",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SleekCoral,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Active Mistake Card
                    Card(
                        shape = RoundedCornerShape(26.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = SleekPurple.copy(alpha = 0.15f),
                                modifier = Modifier.size(64.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(activeWord.emoji, fontSize = 32.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = targetWordText,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clickable {
                                        viewModel.speakWord(activeWord)
                                    }
                                    .padding(vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Listen",
                                    tint = SleekOcean,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = activeWord.phonetic,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = SleekOcean
                                )
                            }

                            Text(
                                text = "What is the English translation for '$targetWordText'?",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Practice Choices Grid
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(practiceOptions.size) { idx ->
                            val optionText = practiceOptions[idx]
                            val isSelected = selectedAnswerIndex == idx
                            val isCorrectOption = optionText == activeWord.englishWord

                            val cardBorder = when {
                                selectedAnswerIndex == null -> BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline)
                                isSelected && isAnswerCorrect == true -> BorderStroke(2.dp, SleekEmerald)
                                isSelected && isAnswerCorrect == false -> BorderStroke(2.dp, SleekCoral)
                                isCorrectOption && selectedAnswerIndex != null -> BorderStroke(2.dp, SleekEmerald)
                                else -> BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                            }

                            val cardBg = when {
                                selectedAnswerIndex == null -> MaterialTheme.colorScheme.surface
                                isSelected && isAnswerCorrect == true -> SleekEmerald.copy(alpha = 0.12f)
                                isSelected && isAnswerCorrect == false -> SleekCoral.copy(alpha = 0.12f)
                                else -> MaterialTheme.colorScheme.surface
                            }

                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = cardBg),
                                border = cardBorder,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(enabled = selectedAnswerIndex == null) {
                                        selectedAnswerIndex = idx
                                        if (optionText == activeWord.englishWord) {
                                            isAnswerCorrect = true
                                            showConfetti = true
                                            viewModel.onAnswerGiven(activeWord.id, true)
                                        } else {
                                            isAnswerCorrect = false
                                            viewModel.onAnswerGiven(activeWord.id, false)
                                        }
                                    }
                                    .testTag("review_option_$idx")
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text("${'A' + idx}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Text(
                                        text = optionText,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.weight(1f)
                                    )

                                    if (isSelected && isAnswerCorrect == true) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Correct",
                                            tint = SleekEmerald,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Bottom Navigation / Next Question Button
                    AnimatedVisibility(visible = selectedAnswerIndex != null) {
                        Button(
                            onClick = {
                                selectedAnswerIndex = null
                                isAnswerCorrect = null
                                showConfetti = false
                                if (currentPracticeIndex + 1 < missedWords.size) {
                                    currentPracticeIndex++
                                } else {
                                    currentPracticeIndex = 0
                                }
                            },
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SleekOcean),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("review_next_button")
                        ) {
                            Text(
                                text = if (currentPracticeIndex + 1 < missedWords.size) "Next Mistake Word ➔" else "Review Again 🔄",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            ConfettiCanvas(trigger = showConfetti)
        }
    }
}
