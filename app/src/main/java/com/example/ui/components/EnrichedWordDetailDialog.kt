package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.audio.SoundFxHelper
import com.example.model.TargetLanguage
import com.example.model.VocabularyItem
import com.example.ui.theme.SleekBackground
import com.example.ui.theme.SleekEmerald
import com.example.ui.theme.SleekEmeraldDark
import com.example.ui.theme.SleekGold
import com.example.ui.theme.SleekGoldDark
import com.example.ui.theme.SleekOcean
import com.example.ui.theme.SleekOceanDark
import com.example.ui.theme.SleekPurple
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekSurfaceBorder
import com.example.ui.theme.SleekTextDark
import com.example.ui.theme.SleekTextMuted
import com.example.ui.viewmodel.LumiViewModel

/**
 * EnrichedWordDetailDialog
 *
 * Kid-friendly interactive vocabulary modal integrating FreeDictionary and Tatoeba services.
 * Features:
 * - Phonetic pronunciation guide (IPA).
 * - Real human audio from FreeDictionary and SpeechHelper TTS playback.
 * - Part of speech & dictionary definition.
 * - Bilingual example sentence pairs from Tatoeba.
 * - Freesound real sound effects and mastery star toggle.
 */
@Composable
fun EnrichedWordDetailDialog(
    item: VocabularyItem,
    targetLanguage: TargetLanguage,
    viewModel: LumiViewModel,
    isMastered: Boolean = false,
    categoryColor: Color = SleekEmerald,
    onDismiss: () -> Unit
) {
    val enrichedDetailsMap by viewModel.enrichedWordDetails.collectAsState()
    val sentencePairsMap by viewModel.wordSentencePairs.collectAsState()

    val cleanWord = item.englishWord.lowercase().trim()
    val tatoebaKey = "${cleanWord}_${targetLanguage.code}"
    val enriched = enrichedDetailsMap[cleanWord]
    val sentencePairs = sentencePairsMap[tatoebaKey] ?: emptyList()

    LaunchedEffect(cleanWord) {
        viewModel.loadWordDetails(cleanWord)
        viewModel.loadSentencePairs(cleanWord)
    }

    val targetWord = item.translations[targetLanguage.code] ?: item.englishWord

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = SleekSurface),
            border = BorderStroke(2.dp, categoryColor.copy(alpha = 0.5f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 480.dp)
                .testTag("word_detail_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header Row with Category Badge and Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = categoryColor.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, categoryColor.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = item.category.title.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = categoryColor,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    FocusableCard(
                        onClick = onDismiss,
                        shape = CircleShape,
                        modifier = Modifier.size(36.dp),
                        backgroundColor = Color.Black.copy(alpha = 0.05f)
                    ) {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = SleekTextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Big Visual Mascot / Emoji with Radiant Aura
                Surface(
                    shape = RoundedCornerShape(32.dp),
                    color = categoryColor.copy(alpha = 0.12f),
                    border = BorderStroke(2.dp, categoryColor.copy(alpha = 0.35f)),
                    modifier = Modifier.size(96.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = item.emoji, fontSize = 54.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Target Word
                Text(
                    text = targetWord,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = SleekTextDark,
                    textAlign = TextAlign.Center
                )

                // English Meaning
                Text(
                    text = item.englishWord,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = SleekEmerald,
                    textAlign = TextAlign.Center
                )

                // Phonetic Transcription & Part of Speech Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 6.dp)
                ) {
                    val phonetic = enriched?.phonetic?.ifBlank { item.phonetic } ?: item.phonetic
                    if (phonetic.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SleekPurple.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, SleekPurple.copy(alpha = 0.25f))
                        ) {
                            Text(
                                text = phonetic,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SleekPurple,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    enriched?.partOfSpeech?.let { pos ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SleekGold.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, SleekGold.copy(alpha = 0.35f))
                        ) {
                            Text(
                                text = pos.lowercase(),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = SleekGoldDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Audio Playback Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FocusableCard(
                        onClick = {
                            SoundFxHelper.playPop()
                            if (!enriched?.audioUrl.isNullOrBlank()) {
                                SoundFxHelper.playSoundUrl(enriched!!.audioUrl!!) {
                                    viewModel.speakWord(item)
                                }
                            } else {
                                viewModel.speakWord(item)
                            }
                        },
                        shape = RoundedCornerShape(20.dp),
                        backgroundColor = SleekEmerald.copy(alpha = 0.12f),
                        unfocusedBorderColor = SleekEmerald.copy(alpha = 0.3f),
                        focusedBorderColor = SleekEmerald,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Pronounce",
                                tint = SleekEmeraldDark,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Pronounce",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = SleekEmeraldDark
                            )
                        }
                    }

                    FocusableCard(
                        onClick = {
                            SoundFxHelper.playPop()
                            viewModel.playRealSoundEffect(item.id)
                        },
                        shape = RoundedCornerShape(20.dp),
                        backgroundColor = SleekOcean.copy(alpha = 0.12f),
                        unfocusedBorderColor = SleekOcean.copy(alpha = 0.3f),
                        focusedBorderColor = SleekOcean,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = "Sound Effect",
                                tint = SleekOceanDark,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Sound FX",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = SleekOceanDark
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = SleekSurfaceBorder.copy(alpha = 0.6f))
                Spacer(modifier = Modifier.height(12.dp))

                // Dictionary Definition Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black.copy(alpha = 0.03f))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "📖 DICTIONARY INSIGHT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = SleekTextMuted,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    if (enriched != null) {
                        Text(
                            text = enriched.definition,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = SleekTextDark,
                            lineHeight = 18.sp
                        )

                        if (enriched.example.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "\"${enriched.example}\"",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SleekOceanDark
                            )
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            CircularProgressIndicator(
                                strokeWidth = 2.dp,
                                color = SleekEmerald,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Fetching free dictionary definition...",
                                fontSize = 12.sp,
                                color = SleekTextMuted
                            )
                        }
                    }
                }

                // Bilingual Sentences Card (Tatoeba)
                if (sentencePairs.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(categoryColor.copy(alpha = 0.06f))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Translate,
                                contentDescription = null,
                                tint = categoryColor,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "BILINGUAL EXAMPLE (${targetLanguage.displayName.substringBefore(" ")})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = categoryColor,
                                letterSpacing = 0.8.sp
                            )
                        }

                        val firstPair = sentencePairs.first()
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = firstPair.sourceText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SleekTextDark
                        )
                        Text(
                            text = firstPair.translatedText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = SleekEmeraldDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Close Button
                FocusableCard(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(18.dp),
                    backgroundColor = categoryColor,
                    focusedBorderColor = Color.White,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Awesome!",
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
