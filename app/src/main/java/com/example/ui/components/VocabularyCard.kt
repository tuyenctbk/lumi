package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.model.TargetLanguage
import com.example.model.VocabularyItem
import com.example.ui.theme.SleekEmerald
import com.example.ui.theme.SleekGold
import com.example.ui.theme.SleekSurfaceBorder
import com.example.ui.theme.SleekTextDark
import com.example.ui.theme.SleekTextMuted

/**
 * VocabularyCard
 *
 * A TV-optimized card component supporting D-pad focus states with a 1.1x scaling animation,
 * integrating Coil for asynchronous image loading from API sources, and displaying phonetic
 * guides, translated words, and interactive pronunciation playback.
 */
@Composable
fun VocabularyCard(
    item: VocabularyItem,
    targetLanguage: TargetLanguage,
    modifier: Modifier = Modifier,
    isMastered: Boolean = false,
    errorCount: Int = 0,
    cardHeight: Dp = 230.dp,
    onCardClick: () -> Unit = {},
    onAudioClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val inputModeManager = androidx.compose.ui.platform.LocalInputModeManager.current
    val isTvDevice = remember {
        context.packageManager.hasSystemFeature(android.content.pm.PackageManager.FEATURE_LEANBACK) ||
        context.packageManager.hasSystemFeature(android.content.pm.PackageManager.FEATURE_TELEVISION) ||
        (context.resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_TYPE_MASK) == android.content.res.Configuration.UI_MODE_TYPE_TELEVISION
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isPressed by interactionSource.collectIsPressedAsState()
    val showFocus = isFocused && (isTvDevice || inputModeManager.inputMode == androidx.compose.ui.input.InputMode.Keyboard)

    // Subtle audio chime on TV D-pad focus
    androidx.compose.runtime.LaunchedEffect(showFocus) {
        if (showFocus) {
            com.example.audio.SoundFxHelper.playHoverBoop()
        }
    }

    // 1.1x Scaling Animation on D-pad Focus (TV only; on touch press uses 1.03x)
    val animatedScale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.98f
            showFocus -> 1.10f
            else -> 1.0f
        },
        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
        label = "tv_dpad_scale_anim"
    )

    // Dynamic Glowing Soft-Shadow Elevation on Focus
    val animatedElevation by animateDpAsState(
        targetValue = if (showFocus) 24.dp else 4.dp,
        animationSpec = tween(durationMillis = 220),
        label = "tv_dpad_elevation_anim"
    )

    // Glowing Border Color on Focus
    val primaryColor = Color(item.category.colorHex)
    val animatedBorderColor by animateColorAsState(
        targetValue = when {
            showFocus -> primaryColor
            isMastered -> SleekEmerald
            else -> SleekSurfaceBorder
        },
        animationSpec = tween(durationMillis = 200),
        label = "tv_dpad_border_anim"
    )

    val targetTranslation = item.translations[targetLanguage.code] ?: item.englishWord
    val phoneticGuide = item.pronunciations[targetLanguage.code] ?: item.phonetic

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
            }
            .zIndex(if (showFocus) 10f else 1f)
            .testTag("vocabulary_card_${item.id}")
            .shadow(
                elevation = animatedElevation,
                shape = RoundedCornerShape(22.dp),
                spotColor = if (showFocus) primaryColor.copy(alpha = 0.85f) else primaryColor.copy(alpha = 0.35f),
                ambientColor = if (showFocus) primaryColor.copy(alpha = 0.50f) else Color.Black.copy(alpha = 0.1f)
            )
            .clip(RoundedCornerShape(22.dp))
            .background(
                if (showFocus) {
                    Brush.verticalGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.08f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                } else {
                    Brush.linearGradient(
                        colors = listOf(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.surface)
                    )
                }
            )
            .border(
                width = if (showFocus) 3.5.dp else if (isMastered) 2.dp else 1.5.dp,
                color = animatedBorderColor,
                shape = RoundedCornerShape(22.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onCardClick
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(cardHeight)
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Row: Category Badge & Status Indicator / Audio trigger
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Chip
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = primaryColor.copy(alpha = 0.15f),
                    modifier = Modifier.padding(2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.category.emoji,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text(
                            text = item.category.title.take(10),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor
                        )
                    }
                }

                // Mastery Star / Audio Icon
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isMastered) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Mastered",
                            tint = SleekEmerald,
                            modifier = Modifier
                                .size(20.dp)
                                .padding(end = 4.dp)
                        )
                    } else if (errorCount > 0) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFFEBEE),
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Text(
                                text = "⚠️ $errorCount",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD32F2F),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // Audio Playback Button
                    Surface(
                        shape = CircleShape,
                        color = if (isFocused) primaryColor else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .size(32.dp)
                            .clickable(onClick = onAudioClick)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Pronounce Word",
                                tint = if (isFocused) Color.White else primaryColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Middle Section: Async Coil Image from API Sources with Fallback
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                primaryColor.copy(alpha = 0.12f),
                                primaryColor.copy(alpha = 0.04f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (item.imageUrl.isNotBlank()) {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(item.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = item.englishWord,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        loading = {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.5.dp,
                                    color = primaryColor
                                )
                            }
                        },
                        error = {
                            // Fallback to vibrant item emoji
                            Text(
                                text = item.emoji,
                                fontSize = 48.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    )
                } else {
                    Text(
                        text = item.emoji,
                        fontSize = 48.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Bottom Section: Translated Word, English, and Phonetic Guide
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Target Foreign Word
                Text(
                    text = targetTranslation,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = SleekTextDark,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // English Word & Phonetics
                Text(
                    text = if (phoneticGuide.isNotBlank()) "${item.englishWord} • [$phoneticGuide]" else item.englishWord,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isFocused) primaryColor else SleekTextMuted,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
