package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.MascotMood
import com.example.ui.theme.SleekBackground
import com.example.ui.theme.SleekEmerald
import com.example.ui.theme.SleekGold
import com.example.ui.theme.SleekGoldDark
import com.example.ui.theme.SleekOcean
import com.example.ui.theme.SleekOceanDark
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekSurfaceBorder
import com.example.ui.theme.SleekTextDark
import com.example.ui.theme.SleekTextMuted

/**
 * ConsistentLoadingIndicator
 *
 * Material 3 CircularProgressIndicator component providing consistent visual feedback
 * across language learning activities, Gemini API curriculum generation, and data synchronization.
 */
@Composable
fun ConsistentLoadingIndicator(
    modifier: Modifier = Modifier,
    message: String = "Fetching lessons with Gemini AI...",
    subMessage: String? = "Personalizing vocabulary & interactive games",
    size: Dp = 56.dp,
    strokeWidth: Dp = 4.5.dp,
    color: Color = SleekOcean,
    trackColor: Color = SleekOcean.copy(alpha = 0.18f),
    showMascotCompanion: Boolean = true,
    testTag: String = "consistent_loading_indicator"
) {
    val infiniteTransition = rememberInfiniteTransition(label = "indicator_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = SleekSurface),
        border = BorderStroke(1.5.dp, SleekSurfaceBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = modifier
            .fillMaxWidth(0.88f)
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (showMascotCompanion) {
                Surface(
                    shape = CircleShape,
                    color = SleekGold.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, SleekGoldDark.copy(alpha = 0.3f)),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = SleekGoldDark,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "AI Curriculum Engine",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SleekGoldDark
                        )
                    }
                }
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(size + 24.dp)
                    .graphicsLayer {
                        scaleX = pulseScale
                        scaleY = pulseScale
                    }
            ) {
                // Background soft glow circle
                Surface(
                    shape = CircleShape,
                    color = color.copy(alpha = 0.08f),
                    modifier = Modifier.size(size + 16.dp)
                ) {}

                // Material 3 Circular Progress Indicator
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(size)
                        .testTag("${testTag}_spinner"),
                    color = color,
                    strokeWidth = strokeWidth,
                    trackColor = trackColor,
                    strokeCap = StrokeCap.Round
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = SleekTextDark,
                textAlign = TextAlign.Center
            )

            if (subMessage != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = subMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = SleekTextMuted,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Fullscreen Loading View wrapping ConsistentLoadingIndicator
 */
@Composable
fun FullscreenLoadingView(
    message: String = "Fetching lessons with Gemini AI...",
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .background(SleekBackground)
            .padding(24.dp)
            .testTag("fullscreen_loading_view")
    ) {
        ConsistentLoadingIndicator(message = message)
    }
}
