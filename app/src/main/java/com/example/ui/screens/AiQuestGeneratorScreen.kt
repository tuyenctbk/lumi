package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.audio.SoundManager
import com.example.data.api.OpenApiLessonRepository
import com.example.model.MascotMood
import com.example.model.VocabularyItem
import com.example.ui.components.FocusableCard
import com.example.ui.theme.SleekBackground
import com.example.ui.theme.SleekCoral
import com.example.ui.theme.SleekEmerald
import com.example.ui.theme.SleekGold
import com.example.ui.theme.SleekOcean
import com.example.ui.theme.SleekPurple
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekSurfaceBorder
import com.example.ui.theme.SleekTextDark
import com.example.ui.theme.SleekTextMuted
import com.example.ui.viewmodel.LumiViewModel
import kotlinx.coroutines.launch

/**
 * AiQuestGeneratorScreen
 *
 * Open API / Gemini AI Dynamic Lesson Creator.
 * Generates custom 8-10 word lesson packs on demand for any user-input topic!
 */
@Composable
fun AiQuestGeneratorScreen(
    viewModel: LumiViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val soundManager = remember { SoundManager.getInstance(context) }
    val openApiRepo = remember { OpenApiLessonRepository() }
    val scope = rememberCoroutineScope()
    val targetLanguage by viewModel.targetLanguage.collectAsState()

    var topicInput by remember { mutableStateOf("Dinosaurs") }
    var isLoading by remember { mutableStateOf(false) }
    var generatedWords by remember { mutableStateOf<List<VocabularyItem>>(emptyList()) }

    val presetTopics = listOf("Dinosaurs 🦖", "Ocean Creatures 🐬", "Music Instruments 🎷", "Sports & Games ⚽", "Superheroes 🦸", "Weather & Sky 🌧️")

    LaunchedEffect(Unit) {
        viewModel.speakLumi("Type any topic to create an AI Lesson Quest!", MascotMood.HAPPY)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = SleekBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FocusableCard(
                    onClick = onBack,
                    shape = CircleShape,
                    modifier = Modifier.size(48.dp).testTag("ai_quest_back_btn")
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = SleekTextDark)
                    }
                }

                Text(
                    text = stringResource(R.string.ai_quest_title),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = SleekTextDark
                )

                Spacer(modifier = Modifier.size(48.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Topic Input Box
            OutlinedTextField(
                value = topicInput,
                onValueChange = { topicInput = it },
                label = { Text(stringResource(R.string.ai_quest_input_label)) },
                placeholder = { Text(stringResource(R.string.ai_quest_input_placeholder)) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ai_quest_topic_input"),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Preset Topic Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(presetTopics) { topicChip ->
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = SleekPurple.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, SleekPurple.copy(alpha = 0.4f)),
                        modifier = Modifier.clickable {
                            topicInput = topicChip.substringBefore(" ").trim()
                        }
                    ) {
                        Text(
                            text = topicChip,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = SleekTextDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Generate Button
            Button(
                onClick = {
                    if (topicInput.isNotBlank() && !isLoading) {
                        isLoading = true
                        soundManager.playPop()
                        scope.launch {
                            val items = openApiRepo.generateCustomAiLesson(topicInput)
                            generatedWords = items
                            isLoading = false
                            soundManager.playPositiveAnswerChime()
                            viewModel.speakLumi("Generated ${items.size} words for $topicInput!", MascotMood.CELEBRATING)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("ai_quest_generate_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = SleekPurple),
                shape = RoundedCornerShape(18.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(stringResource(R.string.ai_quest_building))
                } else {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.ai_quest_generate_btn), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Results Grid
            if (generatedWords.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.ai_quest_items_count, generatedWords.size),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = SleekTextDark,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(generatedWords) { item ->
                        val wordTrans = item.translations[targetLanguage.code] ?: item.englishWord
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = SleekSurface),
                            border = BorderStroke(1.dp, SleekSurfaceBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.speakLumi(wordTrans, MascotMood.HAPPY)
                                    soundManager.playPositiveAnswerChime()
                                }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = item.emoji, fontSize = 36.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = wordTrans,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SleekTextDark
                                )
                                Text(
                                    text = item.soundPrompt,
                                    fontSize = 12.sp,
                                    color = SleekTextMuted
                                )
                            }
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}
