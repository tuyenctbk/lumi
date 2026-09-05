package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.graphicsLayer
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
import com.example.ui.theme.SleekBackground
import com.example.ui.theme.SleekEmerald
import com.example.ui.theme.SleekGold
import com.example.ui.theme.SleekOcean
import com.example.ui.theme.SleekPurple
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekSurfaceBorder
import com.example.ui.theme.SleekTextDark
import com.example.ui.theme.SleekTextMuted
import com.example.ui.viewmodel.LumiViewModel
import kotlinx.coroutines.delay

data class MemoryCard(
    val id: Int,
    val item: VocabularyItem,
    val displayType: CardContentDisplay,
    var isFlipped: Boolean = false,
    var isMatched: Boolean = false
)

enum class CardContentDisplay { EMOJI, WORD }

/**
 * MemoryMatchScreen
 *
 * Interactive 3x4 Memory Pair Matching game.
 * Kids flip cards to pair target words with their matching emoji!
 */
@Composable
fun MemoryMatchScreen(
    viewModel: LumiViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val soundManager = remember { SoundManager.getInstance(context) }
    val targetLanguage by viewModel.targetLanguage.collectAsState()

    // Setup 6 unique pairs (12 total cards)
    val cards = remember(targetLanguage) {
        val selectedItems = viewModel.currentCategoryWords.shuffled().take(6)
        val list = mutableListOf<MemoryCard>()
        var idCounter = 0
        selectedItems.forEach { item ->
            list.add(MemoryCard(idCounter++, item, CardContentDisplay.EMOJI))
            list.add(MemoryCard(idCounter++, item, CardContentDisplay.WORD))
        }
        list.shuffled().toMutableList()
    }

    val flippedIndices = remember { mutableStateListOf<Int>() }
    val matchedPairs = remember { mutableStateListOf<String>() }
    var score by remember { mutableIntStateOf(0) }
    var confettiTrigger by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.speakLumi("Match the word with its picture card!", MascotMood.HAPPY)
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
                    modifier = Modifier.size(48.dp).testTag("memory_match_back_btn")
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = SleekTextDark)
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.memory_match_title),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekTextDark
                    )
                    Text(
                        text = stringResource(R.string.memory_match_pairs, matchedPairs.size, 6),
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

            Spacer(modifier = Modifier.height(20.dp))

            // 3x4 Grid of Cards
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                itemsIndexed(cards) { index, card ->
                    val isFlipped = card.isFlipped || card.isMatched || flippedIndices.contains(index)
                    val rotation by animateFloatAsState(
                        targetValue = if (isFlipped) 180f else 0f,
                        animationSpec = tween(durationMillis = 350),
                        label = "card_flip_rotation"
                    )

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (card.isMatched) SleekEmerald.copy(alpha = 0.2f) else SleekSurface
                        ),
                        border = BorderStroke(
                            2.dp,
                            if (card.isMatched) SleekEmerald else if (isFlipped) SleekOcean else SleekSurfaceBorder
                        ),
                        modifier = Modifier
                            .aspectRatio(0.8f)
                            .graphicsLayer {
                                rotationY = rotation
                                cameraDistance = 8 * density
                            }
                            .clickable(enabled = !isFlipped && flippedIndices.size < 2) {
                                soundManager.playPop()
                                flippedIndices.add(index)

                                if (flippedIndices.size == 2) {
                                    val idx1 = flippedIndices[0]
                                    val idx2 = flippedIndices[1]
                                    val card1 = cards[idx1]
                                    val card2 = cards[idx2]

                                    if (card1.item.id == card2.item.id) {
                                        // Match found!
                                        cards[idx1] = card1.copy(isMatched = true)
                                        cards[idx2] = card2.copy(isMatched = true)
                                        matchedPairs.add(card1.item.id)
                                        score += 25
                                        soundManager.playPositiveAnswerChime()

                                        val wordTrans = card1.item.translations[targetLanguage.code] ?: card1.item.englishWord
                                        viewModel.speakLumi(wordTrans, MascotMood.CELEBRATING)
                                        viewModel.onAnswerGiven(card1.item.id, true)

                                        if (matchedPairs.size == 6) {
                                            confettiTrigger = !confettiTrigger
                                            viewModel.speakLumi("Hooray! You matched all the cards!", MascotMood.CELEBRATING)
                                        }

                                        flippedIndices.clear()
                                    } else {
                                        // Not matched, flip back after brief delay
                                        soundManager.playEncouragingOops()
                                    }
                                }
                            }
                            .testTag("memory_card_$index")
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (rotation > 90f) {
                                // Front Side (Word or Emoji)
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.graphicsLayer { rotationY = 180f }
                                ) {
                                    if (card.displayType == CardContentDisplay.EMOJI) {
                                        Text(text = card.item.emoji, fontSize = 40.sp)
                                    } else {
                                        val translatedWord = card.item.translations[targetLanguage.code] ?: card.item.englishWord
                                        Text(
                                            text = translatedWord.substringBefore("(").trim(),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SleekTextDark,
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }
                                }
                            } else {
                                // Back Side
                                Icon(
                                    imageVector = Icons.Default.QuestionMark,
                                    contentDescription = "Card Back",
                                    tint = SleekPurple,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Reset flipped cards if mismatch
            LaunchedEffect(flippedIndices.size) {
                if (flippedIndices.size == 2) {
                    val idx1 = flippedIndices[0]
                    val idx2 = flippedIndices[1]
                    if (cards[idx1].item.id != cards[idx2].item.id) {
                        delay(1000)
                        flippedIndices.clear()
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (matchedPairs.size == 6) {
                Button(
                    onClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("memory_match_finish_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = SleekEmerald),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = stringResource(R.string.game_finish),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
