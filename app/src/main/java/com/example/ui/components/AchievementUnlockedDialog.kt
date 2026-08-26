package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.audio.SoundFxHelper
import com.example.data.db.BadgeEntity
import com.example.model.MascotMood
import com.example.ui.theme.SleekCoral
import com.example.ui.theme.SleekEmerald
import com.example.ui.theme.SleekGold
import com.example.ui.theme.SleekGoldDark
import com.example.ui.theme.SleekOcean
import com.example.ui.theme.SleekOceanDark
import com.example.ui.theme.SleekPurple
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekSurfaceBorder
import com.example.ui.theme.SleekTextDark
import com.example.ui.theme.SleekTextMuted

/**
 * AchievementUnlockedDialog
 *
 * Celebratory popup dialog displayed whenever the user unlocks a new digital badge milestone.
 * Features rotating starburst effects, Lottie confetti bursts, mascot celebration,
 * and audio fanfare.
 */
@Composable
fun AchievementUnlockedDialog(
    badge: BadgeEntity,
    onDismiss: () -> Unit,
    onViewGallery: (() -> Unit)? = null
) {
    LaunchedEffect(badge) {
        SoundFxHelper.playCelebrationFanfare()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "badge_dialog_anim")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "halo_rotation"
    )

    val badgeScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "badge_scale"
    )

    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.JsonString(LumiMilestoneLottieSpecs.FIREWORK_CELEBRATION_JSON)
    )

    val lottieProgress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = Color.White,
            border = BorderStroke(3.dp, SleekGold),
            shadowElevation = 24.dp,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(16.dp)
                .testTag("achievement_unlocked_dialog")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Header Tag
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SleekGold.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, SleekGold)
                    ) {
                        Text(
                            text = "🏆 NEW ACHIEVEMENT UNLOCKED! 🏆",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = SleekGoldDark,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Rotating Sunburst & Badge Container
                    Box(
                        modifier = Modifier.size(160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (composition != null) {
                            LottieAnimation(
                                composition = composition,
                                progress = { lottieProgress },
                                modifier = Modifier.size(180.dp)
                            )
                        }

                        // Rotating outer halo
                        Surface(
                            shape = CircleShape,
                            color = SleekGold.copy(alpha = 0.2f),
                            border = BorderStroke(2.dp, SleekGold.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .size(140.dp)
                                .rotate(rotation)
                        ) {}

                        // Inner glowing Badge Icon
                        Surface(
                            shape = CircleShape,
                            color = SleekSurface,
                            border = BorderStroke(3.dp, SleekGold),
                            shadowElevation = 8.dp,
                            modifier = Modifier
                                .size(105.dp)
                                .scale(badgeScale)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = badge.iconEmoji.ifEmpty { "🌟" },
                                    fontSize = 50.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Title
                    Text(
                        text = badge.title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = SleekTextDark,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Description
                    Text(
                        text = badge.description,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = SleekTextMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Star Reward Pill
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SleekCoral.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, SleekCoral)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = "⭐", fontSize = 16.sp)
                            Text(
                                text = "+50 Bonus Stars Earned!",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = SleekCoral
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Action Buttons
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FocusableCard(
                            onClick = {
                                SoundFxHelper.playStarBurst()
                                onDismiss()
                            },
                            shape = RoundedCornerShape(20.dp),
                            backgroundColor = SleekOcean,
                            unfocusedBorderColor = SleekOceanDark,
                            focusedBorderColor = SleekGold,
                            focusedScale = 1.05f,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            testTag = "achievement_claim_button"
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Claim Reward! 🚀",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        if (onViewGallery != null) {
                            FocusableCard(
                                onClick = {
                                    SoundFxHelper.playPop()
                                    onDismiss()
                                    onViewGallery()
                                },
                                shape = RoundedCornerShape(20.dp),
                                backgroundColor = SleekSurface,
                                unfocusedBorderColor = SleekSurfaceBorder,
                                focusedBorderColor = SleekEmerald,
                                focusedScale = 1.05f,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp),
                                testTag = "achievement_view_gallery_button"
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "View Badge Gallery 🏆",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SleekGoldDark
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
