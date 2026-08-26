package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.db.DailyLearningStatsEntity
import com.example.ui.theme.SleekCoral
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
import kotlin.math.min

/**
 * ProgressSummary
 *
 * Canvas-based visual component representing user's daily lesson progress,
 * exercise mastery rate, weekly volume velocity, and streak status.
 */
@Composable
fun ProgressSummary(
    dailyStats: List<DailyLearningStatsEntity>,
    completedLessonsCount: Int,
    targetDailyLessons: Int = 3,
    streakDays: Int = 1,
    accuracyPercent: Int = 95,
    modifier: Modifier = Modifier,
    onViewAnalyticsClick: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val progressFraction = (completedLessonsCount.toFloat() / targetDailyLessons.toFloat().coerceAtLeast(1f)).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progressFraction,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "progress_summary_radial_anim"
    )

    Card(
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = SleekSurface),
        border = BorderStroke(
            if (isFocused) 3.dp else 1.5.dp,
            if (isFocused) SleekEmerald else SleekSurfaceBorder
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isFocused) 8.dp else 3.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("progress_summary_card")
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.material3.ripple(),
                onClick = onViewAnalyticsClick
            )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = SleekEmerald.copy(alpha = 0.15f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.ShowChart,
                                contentDescription = null,
                                tint = SleekEmeraldDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = "Daily Progress Summary",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = SleekTextDark
                        )
                        Text(
                            text = "$completedLessonsCount of $targetDailyLessons lessons completed today",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = SleekTextMuted
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (progressFraction >= 1f) SleekEmerald.copy(alpha = 0.15f) else SleekGold.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = if (progressFraction >= 1f) "Goal Met 🎉" else "${(progressFraction * 100).toInt()}% Done",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (progressFraction >= 1f) SleekEmeraldDark else SleekGoldDark,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            // Canvas Gauge & Weekly Bar Chart in Side-by-Side or Stacked layout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Radial Arc Progress Gauge (Canvas)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(110.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = 10.dp.toPx()
                        val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
                        val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)

                        // Background Track
                        drawArc(
                            color = SleekSurfaceBorder,
                            startAngle = 135f,
                            sweepAngle = 270f,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )

                        // Filled Progress Arc with Gradient
                        drawArc(
                            brush = Brush.sweepGradient(
                                listOf(SleekOcean, SleekEmerald, SleekGold, SleekOcean)
                            ),
                            startAngle = 135f,
                            sweepAngle = 270f * animatedProgress,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${(animatedProgress * 100).toInt()}%",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = SleekTextDark
                        )
                        Text(
                            text = "Daily Goal",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SleekTextMuted
                        )
                    }
                }

                // 7-Day Mini Sparkline Bars (Canvas)
                DailyMiniBarChart(
                    dailyStats = dailyStats,
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)
                )
            }

            // Key Metrics Pill Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ProgressPill(
                    icon = Icons.Default.CheckCircle,
                    label = "Accuracy",
                    value = "$accuracyPercent%",
                    color = SleekEmerald,
                    modifier = Modifier.weight(1f)
                )
                ProgressPill(
                    icon = Icons.Default.Whatshot,
                    label = "Streak",
                    value = "$streakDays Days",
                    color = SleekCoral,
                    modifier = Modifier.weight(1f)
                )
                ProgressPill(
                    icon = Icons.Default.EmojiEvents,
                    label = "Lessons",
                    value = "$completedLessonsCount Done",
                    color = SleekOcean,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun DailyMiniBarChart(
    dailyStats: List<DailyLearningStatsEntity>,
    modifier: Modifier = Modifier
) {
    val days = remember(dailyStats) {
        val defaultLabels = listOf("M", "T", "W", "T", "F", "S", "S")
        if (dailyStats.size >= 7) {
            dailyStats.take(7).reversed().mapIndexed { i, stat ->
                Pair(stat.dayLabel.take(1), (stat.wordsPracticed / 15f).coerceIn(0.15f, 1f))
            }
        } else {
            defaultLabels.mapIndexed { index, label ->
                Pair(label, if (index < 3) 0.6f + (index * 0.15f) else 0.2f)
            }
        }
    }

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height - 18.dp.toPx()
            val barWidth = 14.dp.toPx()
            val slotWidth = w / days.size

            days.forEachIndexed { index, (_, progress) ->
                val barHeight = (progress * h).coerceIn(6.dp.toPx(), h)
                val left = index * slotWidth + (slotWidth - barWidth) / 2
                val top = h - barHeight

                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = if (progress >= 0.5f) listOf(SleekEmerald, SleekOcean) else listOf(Color.LightGray, Color.LightGray.copy(alpha = 0.5f)),
                        startY = top,
                        endY = top + barHeight
                    ),
                    topLeft = Offset(left, top),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
            }
        }

        // X-Axis Day Labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            days.forEach { (label, _) ->
                Text(
                    text = label,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = SleekTextMuted,
                    modifier = Modifier.width(14.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun ProgressPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.10f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.25f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Column {
                Text(
                    text = value,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = SleekTextDark,
                    maxLines = 1
                )
                Text(
                    text = label,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium,
                    color = SleekTextMuted,
                    maxLines = 1
                )
            }
        }
    }
}
