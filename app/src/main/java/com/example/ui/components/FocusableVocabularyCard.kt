package com.example.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
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
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.audio.SoundFxHelper
import com.example.model.VocabularyItem
import com.example.ui.theme.SleekEmerald
import com.example.ui.theme.SleekEmeraldDark
import com.example.ui.theme.SleekGold
import com.example.ui.theme.SleekGoldDark
import com.example.ui.theme.SleekOcean
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekSurfaceBorder
import com.example.ui.theme.SleekTextDark
import com.example.ui.theme.SleekTextMuted

enum class CardVisualMode {
    EMOJI,
    REAL_PHOTO,
    ACTION_GIF
}

/**
 * FocusableVocabularyCard
 *
 * A Jetpack Compose for TV & Mobile card supporting D-pad navigation with an animated 1.1x
 * scaling effect on focus, polished rounded surfaces, Freesound real-world audio trigger,
 * Pixabay safe photo support, and Giphy motion support.
 */
@Composable
fun FocusableVocabularyCard(
    item: VocabularyItem,
    targetWord: String,
    modifier: Modifier = Modifier,
    isBilingual: Boolean = true,
    isMastered: Boolean = false,
    categoryColor: Color = SleekEmerald,
    visualMode: CardVisualMode = CardVisualMode.EMOJI,
    realPhotoUrl: String? = null,
    gifUrl: String? = null,
    onClick: () -> Unit = {},
    onPlayRealSound: (() -> Unit)? = null,
    focusedScale: Float = 1.08f,
    shape: Shape = RoundedCornerShape(28.dp),
    backgroundColor: Color = SleekSurface,
    unfocusedBorderColor: Color = SleekSurfaceBorder,
    focusedBorderColor: Color = SleekEmerald,
    elevation: Dp = 4.dp,
    testTag: String = "vocab_card_${item.id}"
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    // Subtle audio chime/boop on TV D-pad focus
    LaunchedEffect(isFocused) {
        if (isFocused) {
            SoundFxHelper.playHoverBoop()
        }
    }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) focusedScale else 1.0f,
        animationSpec = tween(durationMillis = 180),
        label = "focusable_vocab_card_scale"
    )

    val currentBorder = if (isFocused) {
        BorderStroke(4.dp, focusedBorderColor)
    } else {
        BorderStroke(1.5.dp, unfocusedBorderColor)
    }

    val animatedElevation by animateDpAsState(
        targetValue = if (isFocused) 22.dp else elevation,
        animationSpec = tween(durationMillis = 200),
        label = "focusable_card_elevation"
    )

    Card(
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = currentBorder,
        elevation = CardDefaults.cardElevation(
            defaultElevation = animatedElevation
        ),
        modifier = modifier
            .testTag(testTag)
            .scale(scale)
            .shadow(
                elevation = animatedElevation,
                shape = shape,
                spotColor = if (isFocused) focusedBorderColor.copy(alpha = 0.85f) else categoryColor.copy(alpha = 0.35f),
                ambientColor = if (isFocused) focusedBorderColor.copy(alpha = 0.45f) else Color.Black.copy(alpha = 0.1f)
            )
            .focusable(interactionSource = interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.material3.ripple(),
                onClick = onClick
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
        ) {
            // Mastered Star Badge (Top Right)
            if (isMastered) {
                Surface(
                    shape = CircleShape,
                    color = SleekGold,
                    border = BorderStroke(1.5.dp, SleekGoldDark),
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Mastered",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Real Sound FX Quick Button (Top Left)
            if (onPlayRealSound != null) {
                Surface(
                    shape = CircleShape,
                    color = SleekOcean.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, SleekOcean.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .size(30.dp)
                        .clickable { onPlayRealSound() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "Real Sound Effect",
                            tint = SleekOcean,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Visual Asset Area (Emoji, Pixabay Real Photo, or Giphy Motion GIF)
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .padding(top = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        visualMode == CardVisualMode.REAL_PHOTO && !realPhotoUrl.isNullOrBlank() -> {
                            AsyncImage(
                                model = realPhotoUrl,
                                contentDescription = item.englishWord,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(20.dp))
                            )
                        }
                        visualMode == CardVisualMode.ACTION_GIF && !gifUrl.isNullOrBlank() -> {
                            AsyncImage(
                                model = gifUrl,
                                contentDescription = item.englishWord,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(20.dp))
                            )
                        }
                        else -> {
                            Surface(
                                shape = RoundedCornerShape(22.dp),
                                color = categoryColor.copy(alpha = 0.12f),
                                border = BorderStroke(1.dp, categoryColor.copy(alpha = 0.25f)),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = item.emoji, fontSize = 44.sp)
                                }
                            }
                        }
                    }
                }

                // Word Labels & Sound cue
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    // Target Language Word
                    Text(
                        text = targetWord,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = SleekTextDark,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )

                    // Optional English / Bilingual Translation
                    if (isBilingual) {
                        Text(
                            text = item.englishWord,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = SleekEmerald,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 1.dp),
                            maxLines = 1
                        )
                    }

                    // Playful Sound Cue / Sound prompt
                    if (item.soundPrompt.isNotBlank()) {
                        Text(
                            text = item.soundPrompt,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = SleekTextMuted,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 2.dp),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
