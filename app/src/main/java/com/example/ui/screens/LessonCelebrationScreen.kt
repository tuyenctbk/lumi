package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.audio.SoundManager
import com.example.model.MascotMood
import com.example.ui.components.ConfettiCanvas
import com.example.ui.components.LumiLottieReaction
import com.example.ui.components.RechartsDataPoint
import com.example.ui.components.RechartsProficiencyLineChart
import com.example.ui.components.RechartsVolumeBarChart
import com.example.ui.theme.SleekBackground
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
import com.example.ui.viewmodel.LumiViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * LessonCelebrationScreen
 *
 * Visual Victory and Session Analytics Screen triggered at the end of a lesson.
 * Features:
 * - Animated Lottie Lumi Mascot reacting enthusiastically to lesson victory.
 * - Interactive Recharts-based progress visualization supporting Tabbed view:
 *   1. Session Accuracy & Mastery Bar Chart (Bar chart visualizing performance metrics)
 *   2. 7-Day Proficiency Growth Curve
 *   3. Daily Volume Bar Chart
 * - Key Performance Metric Tiles (Accuracy, Score XP, Streak, Mastered Words).
 * - Full localization & Accessible Material Design 3 layout.
 */
@Composable
fun LessonCelebrationScreen(
    viewModel: LumiViewModel,
    score: Int = 100,
    wordsMasteredCount: Int = 5,
    accuracyPercent: Int = 100,
    onContinueNext: () -> Unit,
    onBackHome: () -> Unit
) {
    val streakDays by viewModel.streakDays.collectAsState()
    val dailyStats by viewModel.dailyStats.collectAsState()
    val wordProgressList by viewModel.wordProgressList.collectAsState()
    val totalMasteredCount = wordProgressList.count { it.isMastered }

    val context = LocalContext.current
    val soundManager = remember { SoundManager.getInstance(context) }
    var selectedChartTab by remember { mutableIntStateOf(0) }

    // Trigger celebration audio and log session completion
    LaunchedEffect(Unit) {
        soundManager.playLessonCompleteFanfare()
        viewModel.speakLumi("Woohoo! Outstanding effort! You completed the lesson with flying colors!", MascotMood.CELEBRATING)
        viewModel.onSessionCompleted(
            gameType = "lesson_celebration",
            practicedCount = wordsMasteredCount,
            correctCount = (wordsMasteredCount * accuracyPercent) / 100,
            durationSeconds = 180
        )
    }

    val pulseTransition = rememberInfiniteTransition(label = "celebration_pulse")
    val starScale by pulseTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "star_scale"
    )

    // Build synthesized 7-day dataset for Recharts components
    val rechartsData = remember(dailyStats, totalMasteredCount, wordsMasteredCount) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dayNameFormat = SimpleDateFormat("EEE", Locale.getDefault())

        val last7Days = (6 downTo 0).map { offset ->
            val c = Calendar.getInstance()
            c.add(Calendar.DAY_OF_YEAR, -offset)
            val dateStr = dateFormat.format(c.time)
            val dayName = dayNameFormat.format(c.time)
            Pair(dateStr, dayName)
        }

        val statsMap = dailyStats.associateBy { it.dateString }
        var runningCumulative = (totalMasteredCount - dailyStats.sumOf { it.wordsPracticed }).coerceAtLeast(0)

        last7Days.mapIndexed { index, (dateStr, dayName) ->
            val stat = statsMap[dateStr]
            val words = stat?.wordsPracticed ?: if (index == 6) wordsMasteredCount.coerceAtLeast(4) else if (index == 5) 5 else 3
            runningCumulative += words
            val acc = stat?.accuracy ?: if (index == 6) (accuracyPercent / 100f) else 0.90f
            val duration = stat?.minutesPracticed ?: ((words * 30) / 60)
            val profScore = (acc * 60f + (runningCumulative.coerceAtMost(40) * 0.8f) + (index * 2)).toInt().coerceIn(15, 98)

            RechartsDataPoint(
                label = dateStr,
                dayName = dayName,
                wordsLearned = words,
                cumulativeWords = runningCumulative.coerceAtLeast(words),
                accuracy = acc,
                durationMinutes = duration.coerceAtLeast(3),
                proficiencyScore = profScore
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SleekBackground)
            .testTag("lesson_celebration_screen")
    ) {
        // Festive Confetti Particles
        ConfettiCanvas(
            trigger = true,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Mascot Celebrating Header Card
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = SleekGold.copy(alpha = 0.18f),
                    border = BorderStroke(1.5.dp, SleekGoldDark),
                    modifier = Modifier.scale(starScale)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = SleekGoldDark,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = stringResource(R.string.celebration_lesson_completed),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = SleekGoldDark,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Animated Lottie Mascot Reaction
                LumiLottieReaction(
                    isCorrect = true,
                    streakCount = streakDays,
                    size = 140.dp,
                    customSpeech = stringResource(R.string.celebration_superstar_speech)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = stringResource(R.string.celebration_fantastic_job),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = SleekTextDark,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = stringResource(R.string.celebration_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = SleekTextMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            // High-Impact Victory Stat Tiles
            Card(
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = SleekSurface),
                border = BorderStroke(1.5.dp, SleekSurfaceBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.celebration_results_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = SleekTextDark
                        )
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SleekEmerald.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = stringResource(R.string.celebration_badge_earned, wordsMasteredCount),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SleekEmeraldDark,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CelebrationStatTile(
                            label = stringResource(R.string.celebration_accuracy),
                            value = "$accuracyPercent%",
                            icon = Icons.Default.CheckCircle,
                            color = SleekEmerald,
                            modifier = Modifier.weight(1f)
                        )
                        CelebrationStatTile(
                            label = stringResource(R.string.celebration_score),
                            value = "+$score XP",
                            icon = Icons.Default.Star,
                            color = SleekGoldDark,
                            modifier = Modifier.weight(1f)
                        )
                        CelebrationStatTile(
                            label = stringResource(R.string.celebration_streak),
                            value = "$streakDays Days",
                            icon = Icons.Default.Whatshot,
                            color = SleekCoral,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Recharts-based Visual Progress Summary Section
            Card(
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = SleekSurface),
                border = BorderStroke(1.5.dp, SleekSurfaceBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("celebration_recharts_section")
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = SleekPurple.copy(alpha = 0.15f),
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.BarChart,
                                        contentDescription = null,
                                        tint = SleekPurple,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Text(
                                text = stringResource(R.string.celebration_progress_visualization),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = SleekTextDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Tab Selector for Charts
                    TabRow(
                        selectedTabIndex = selectedChartTab,
                        containerColor = Color.Transparent,
                        contentColor = SleekOcean,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedChartTab]),
                                color = SleekOcean,
                                height = 3.dp
                            )
                        },
                        divider = {}
                    ) {
                        Tab(
                            selected = selectedChartTab == 0,
                            onClick = { selectedChartTab = 0 },
                            text = {
                                Text(
                                    stringResource(R.string.celebration_tab_session_bar),
                                    fontWeight = if (selectedChartTab == 0) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 12.sp
                                )
                            }
                        )
                        Tab(
                            selected = selectedChartTab == 1,
                            onClick = { selectedChartTab = 1 },
                            text = {
                                Text(
                                    stringResource(R.string.celebration_tab_weekly_curve),
                                    fontWeight = if (selectedChartTab == 1) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 12.sp
                                )
                            }
                        )
                        Tab(
                            selected = selectedChartTab == 2,
                            onClick = { selectedChartTab = 2 },
                            text = {
                                Text(
                                    stringResource(R.string.celebration_tab_volume),
                                    fontWeight = if (selectedChartTab == 2) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 12.sp
                                )
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Visual Progress Chart Content
                    when (selectedChartTab) {
                        0 -> {
                            // Session Performance Bar Chart
                            LessonSessionPerformanceBarChart(
                                score = score,
                                accuracyPercent = accuracyPercent,
                                wordsMastered = wordsMasteredCount,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                            )
                        }
                        1 -> {
                            // 7-Day Proficiency Growth Curve
                            RechartsProficiencyLineChart(
                                data = rechartsData,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                            )
                        }
                        2 -> {
                            // Daily Volume Bar Chart
                            RechartsVolumeBarChart(
                                data = rechartsData,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                            )
                        }
                    }
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onBackHome,
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.5.dp, SleekSurfaceBorder),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SleekTextDark),
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                        .testTag("celebration_back_home_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Home, contentDescription = null)
                        Text(stringResource(R.string.celebration_home), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }

                Button(
                    onClick = onContinueNext,
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SleekEmerald),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .weight(1.2f)
                        .height(54.dp)
                        .testTag("celebration_continue_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(stringResource(R.string.celebration_next_lesson), fontWeight = FontWeight.Black, fontSize = 16.sp)
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                    }
                }
            }
        }
    }
}

/**
 * Custom Canvas-based Bar Chart for Session Performance Breakdown
 */
@Composable
fun LessonSessionPerformanceBarChart(
    score: Int,
    accuracyPercent: Int,
    wordsMastered: Int,
    modifier: Modifier = Modifier
) {
    val items = remember(score, accuracyPercent, wordsMastered) {
        listOf(
            Triple("Accuracy", accuracyPercent.toFloat(), SleekEmerald),
            Triple("Target Rate", 90f, SleekOcean),
            Triple("XP Gain", (score.toFloat().coerceAtMost(100f)), SleekGoldDark),
            Triple("Retention", ((wordsMastered * 20f).coerceAtMost(100f)), SleekPurple)
        )
    }

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height - 24.dp.toPx()
            val paddingBottom = 22.dp.toPx()
            val paddingTop = 8.dp.toPx()
            val effectiveH = h - paddingTop - paddingBottom

            // Background Grid Lines
            for (i in 0..3) {
                val y = paddingTop + effectiveH * (i.toFloat() / 3f)
                drawLine(
                    color = Color.LightGray.copy(alpha = 0.4f),
                    start = Offset(0f, y),
                    end = Offset(w, y),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                )
            }

            val barWidth = 32.dp.toPx()
            val slotWidth = w / items.size

            items.forEachIndexed { index, (label, value, barColor) ->
                val barHeight = ((value / 100f) * effectiveH).coerceIn(8.dp.toPx(), effectiveH)
                val left = index * slotWidth + (slotWidth - barWidth) / 2
                val top = paddingTop + effectiveH - barHeight

                // Draw Rounded Bar
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(barColor, barColor.copy(alpha = 0.75f)),
                        startY = top,
                        endY = top + barHeight
                    ),
                    topLeft = Offset(left, top),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                )
            }
        }

        // X-Axis Labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            items.forEach { (label, value, barColor) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(60.dp)
                ) {
                    Text(
                        text = label,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = SleekTextMuted,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "${value.toInt()}%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = barColor,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun CelebrationStatTile(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = SleekTextDark
            )
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = SleekTextMuted
            )
        }
    }
}
