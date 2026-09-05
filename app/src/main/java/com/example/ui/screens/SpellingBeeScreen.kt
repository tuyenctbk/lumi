package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.model.MascotMood
import com.example.model.VocabularyItem
import com.example.ui.components.ConfettiCanvas
import com.example.ui.components.FocusableCard
import com.example.ui.components.LumiMascot
import com.example.ui.theme.SleekBackground
import com.example.ui.theme.SleekCoral
import com.example.ui.theme.SleekEmerald
import com.example.ui.theme.SleekGold
import com.example.ui.theme.SleekOcean
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekSurfaceBorder
import com.example.ui.theme.SleekTextDark
import com.example.ui.theme.SleekTextMuted
import com.example.ui.viewmodel.LumiViewModel

/**
 * SpellingBeeScreen
 *
 * Word Builder / Spelling Bee Game.
 * Kids re-order scrambled letter tiles to spell the target vocabulary word!
 * Longer 8-round game mode with mascot voice encouragement, backspace editing, and confetti rewards.
 */
@Composable
fun SpellingBeeScreen(
    viewModel: LumiViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val soundManager = remember { SoundManager.getInstance(context) }
    val targetLanguage by viewModel.targetLanguage.collectAsState()

    // 8-word round setup
    val gameWords: List<VocabularyItem> = remember(targetLanguage) {
        viewModel.currentCategoryWords.shuffled().take(8)
    }

    var currentWordIndex by remember { mutableIntStateOf(0) }
    val currentItem = gameWords.getOrNull(currentWordIndex) ?: gameWords.firstOrNull()

    val targetText = remember(currentItem, targetLanguage) {
        val raw = currentItem?.translations?.get(targetLanguage.code) ?: currentItem?.englishWord ?: "CAT"
        // Clean up brackets or parenthetical hints like (Hon)
        raw.substringBefore("(").trim().uppercase()
    }

    val availableLetters = remember(targetText) {
        targetText.filter { it.isLetter() }.toList().shuffled()
    }

    val selectedTiles = remember { mutableStateListOf<Char>() }
    var isCorrect by remember { mutableStateOf<Boolean?>(null) }
    var score by remember { mutableIntStateOf(0) }
    var confettiTrigger by remember { mutableStateOf(false) }

    LaunchedEffect(currentWordIndex, currentItem) {
        selectedTiles.clear()
        isCorrect = null
        currentItem?.let { item ->
            viewModel.speakLumi(targetText, MascotMood.HAPPY)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = SleekBackground
    ) {
        ConfettiCanvas(trigger = confettiTrigger)

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
                    modifier = Modifier.size(48.dp).testTag("spelling_bee_back_btn")
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = SleekTextDark)
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.spelling_bee_title),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekTextDark
                    )
                    Text(
                        text = stringResource(R.string.spelling_bee_word_counter, currentWordIndex + 1, gameWords.size),
                        fontSize = 13.sp,
                        color = SleekTextMuted
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = SleekGold.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, SleekGold)
                ) {
                    Text(
                        text = stringResource(R.string.game_score_pts, score),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        fontWeight = FontWeight.Bold,
                        color = SleekTextDark,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = { (currentWordIndex + 1).toFloat() / gameWords.size },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = SleekGold,
                trackColor = SleekSurfaceBorder
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Word Target Image & Emoji Card
            currentItem?.let { item ->
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = SleekSurface),
                    border = BorderStroke(1.5.dp, SleekSurfaceBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = item.emoji, fontSize = 64.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FocusableCard(
                                onClick = {
                                    viewModel.speakLumi(targetText, MascotMood.HAPPY)
                                    soundManager.playPositiveAnswerChime()
                                },
                                shape = CircleShape,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.VolumeUp, contentDescription = "Listen", tint = SleekOcean)
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = item.soundPrompt,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = SleekTextMuted
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Letter Input Slots
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                modifier = Modifier.fillMaxWidth()
            ) {
                targetText.filter { it.isLetter() }.forEachIndexed { index, expectedChar ->
                    val typedChar = selectedTiles.getOrNull(index)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (typedChar != null) SleekOcean.copy(alpha = 0.15f) else SleekSurface,
                        border = BorderStroke(
                            2.dp,
                            if (isCorrect == true) SleekEmerald else if (typedChar != null) SleekOcean else SleekSurfaceBorder
                        ),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = typedChar?.toString() ?: "_",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (typedChar != null) SleekTextDark else SleekTextMuted
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Available Scrambled Letter Tiles
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                modifier = Modifier.fillMaxWidth()
            ) {
                availableLetters.forEachIndexed { index, char ->
                    val isUsed = selectedTiles.count { it == char } >= availableLetters.count { it == char }
                    FocusableCard(
                        onClick = {
                            if (!isUsed && selectedTiles.size < targetText.filter { it.isLetter() }.length) {
                                selectedTiles.add(char)
                                soundManager.playPop()

                                // Auto check when full word is spelled
                                val currentAttempt = selectedTiles.joinToString("")
                                val expectedWord = targetText.filter { it.isLetter() }
                                if (currentAttempt.length == expectedWord.length) {
                                    if (currentAttempt.equals(expectedWord, ignoreCase = true)) {
                                        isCorrect = true
                                        score += 20
                                        confettiTrigger = !confettiTrigger
                                        soundManager.playPositiveAnswerChime()
                                        viewModel.speakLumi("Awesome! You spelled $targetText correctly!", MascotMood.CELEBRATING)
                                        currentItem?.let { viewModel.onAnswerGiven(it.id, true) }
                                    } else {
                                        isCorrect = false
                                        soundManager.playEncouragingOops()
                                        viewModel.speakLumi("Try again! Let's check the letters.", MascotMood.ENCOURAGING)
                                        currentItem?.let { viewModel.onAnswerGiven(it.id, false) }
                                    }
                                }
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .size(54.dp)
                            .testTag("spelling_tile_$index")
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(if (isUsed) SleekSurfaceBorder else SleekGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = char.toString(),
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isUsed) SleekTextMuted else Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons: Backspace & Reset
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = {
                        if (selectedTiles.isNotEmpty()) {
                            selectedTiles.removeAt(selectedTiles.size - 1)
                            isCorrect = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SleekCoral),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Backspace, contentDescription = stringResource(R.string.button_delete), tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(stringResource(R.string.button_delete))
                }

                Button(
                    onClick = {
                        selectedTiles.clear()
                        isCorrect = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SleekSurface),
                    border = BorderStroke(1.dp, SleekSurfaceBorder),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.button_clear), tint = SleekTextDark)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(stringResource(R.string.button_clear), color = SleekTextDark)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Next Word / Completion Button
            if (isCorrect == true) {
                Button(
                    onClick = {
                        if (currentWordIndex < gameWords.size - 1) {
                            currentWordIndex++
                        } else {
                            onBack()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("spelling_next_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = SleekEmerald),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = if (currentWordIndex < gameWords.size - 1) stringResource(R.string.button_next_word) else stringResource(R.string.game_finish),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
