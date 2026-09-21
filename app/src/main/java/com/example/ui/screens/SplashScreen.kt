package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.audio.SoundFxHelper
import com.example.model.MascotMood
import com.example.ui.components.LumiMascot
import com.example.ui.theme.SleekBackground
import com.example.ui.theme.SleekEmerald
import com.example.ui.theme.SleekGold
import com.example.ui.theme.SleekOcean
import com.example.ui.theme.SleekOceanDark
import kotlinx.coroutines.delay

/**
 * SplashScreen
 *
 * Custom animated splash screen showing Lumi mascot with radiant ambient glow,
 * twinkling stardust canvas, floating bobbing animation, and smooth navigation.
 */
@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val scale = remember { Animatable(0.85f) }
    var currentMood by remember { mutableStateOf(MascotMood.HAPPY) }
    var isSkipping by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "splash_stars")
    val starGlow by infiniteTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "star_glow"
    )

    val mascotBob by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mascot_bob"
    )

    val progressValue by infiniteTransition.animateFloat(
        initialValue = 0.05f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "loading_progress"
    )

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(400, easing = FastOutSlowInEasing)
        )
        delay(2200)
        if (!isSkipping) {
            onSplashFinished()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A), // Midnight Slate
                        Color(0xFF1E1B4B), // Cosmic Deep Indigo
                        Color(0xFF0F4C81)  // Deep Ocean Blue
                    )
                )
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (!isSkipping) {
                    isSkipping = true
                    currentMood = MascotMood.SUPERSTAR
                    SoundFxHelper.playPop()
                    onSplashFinished()
                }
            }
            .testTag("splash_screen")
    ) {
        // Ambient Twinkling Stars Background Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val starPositions = listOf(
                Offset(size.width * 0.15f, size.height * 0.18f),
                Offset(size.width * 0.85f, size.height * 0.14f),
                Offset(size.width * 0.22f, size.height * 0.72f),
                Offset(size.width * 0.78f, size.height * 0.68f),
                Offset(size.width * 0.50f, size.height * 0.08f),
                Offset(size.width * 0.08f, size.height * 0.45f),
                Offset(size.width * 0.92f, size.height * 0.48f),
                Offset(size.width * 0.35f, size.height * 0.35f),
                Offset(size.width * 0.65f, size.height * 0.38f)
            )

            // Radial Glow Behind Mascot
            drawCircle(
                color = Color(0x33FDE047),
                radius = size.width * 0.35f * starGlow,
                center = Offset(size.width * 0.5f, size.height * 0.42f)
            )

            starPositions.forEachIndexed { index, pos ->
                val radius = if (index % 2 == 0) 3.5f * starGlow else 2.5f * starGlow
                drawCircle(
                    color = if (index % 3 == 0) Color(0xFFFDE047).copy(alpha = 0.85f * starGlow) else Color.White.copy(alpha = 0.75f * starGlow),
                    radius = radius,
                    center = pos
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Mascot & App Brand Name Container
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                    translationY = mascotBob
                }
            ) {
                LumiMascot(
                    mood = currentMood,
                    speechBubble = "Hello! Let's Learn Together! ✨",
                    size = 150.dp
                )

                Spacer(modifier = Modifier.height(28.dp))

                // App Brand Name & Tagline
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "LUMI",
                        fontSize = 46.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 4.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "⭐",
                        fontSize = 32.sp
                    )
                }

                Text(
                    text = stringResource(R.string.splash_tagline),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SleekGold,
                    letterSpacing = 0.5.sp
                )
            }

            // Loading Bar & Skip Prompt Container
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                LinearProgressIndicator(
                    progress = { progressValue },
                    modifier = Modifier
                        .width(220.dp)
                        .height(6.dp),
                    color = SleekGold,
                    trackColor = Color.White.copy(alpha = 0.2f),
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = stringResource(R.string.splash_loading),
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.12f),
                    modifier = Modifier.clickable {
                        if (!isSkipping) {
                            isSkipping = true
                            currentMood = MascotMood.SUPERSTAR
                            SoundFxHelper.playPop()
                            onSplashFinished()
                        }
                    }
                ) {
                    Text(
                        text = stringResource(R.string.splash_tap_skip),
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}
