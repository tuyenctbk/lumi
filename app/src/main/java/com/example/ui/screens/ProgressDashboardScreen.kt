package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.FocusableCard
import com.example.ui.components.RechartsDashboardView
import com.example.ui.theme.SleekBackground
import com.example.ui.theme.SleekCoral
import com.example.ui.theme.SleekEmerald
import com.example.ui.theme.SleekGoldDark
import com.example.ui.theme.SleekOcean
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekSurfaceBorder
import com.example.ui.theme.SleekTextDark
import com.example.ui.theme.SleekTextMuted
import com.example.ui.viewmodel.LumiViewModel

/**
 * ProgressDashboardScreen
 *
 * Standalone Analytics Dashboard screen utilizing Recharts to visualize the user's daily lesson progress
 * and mastery scores over time.
 */
@Composable
fun ProgressDashboardScreen(
    viewModel: LumiViewModel,
    onBack: () -> Unit
) {
    val wordProgressList by viewModel.wordProgressList.collectAsState()
    val dailyStats by viewModel.dailyStats.collectAsState()
    val streakDays by viewModel.streakDays.collectAsState()
    val points by viewModel.points.collectAsState()
    val physicalBreaksCompleted by viewModel.physicalBreaksCompleted.collectAsState()

    val masteredCount = wordProgressList.count { it.isMastered }
    val totalPracticed = wordProgressList.size

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SleekBackground)
            .testTag("progress_dashboard_screen")
    ) {
        LazyColumn(
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Header Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    FocusableCard(
                        onClick = onBack,
                        shape = RoundedCornerShape(16.dp),
                        backgroundColor = SleekSurface,
                        unfocusedBorderColor = SleekSurfaceBorder,
                        focusedBorderColor = SleekEmerald,
                        testTag = "progress_dashboard_back_button"
                    ) {
                        Box(modifier = Modifier.padding(12.dp), contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = SleekTextDark
                            )
                        }
                    }

                    Column {
                        Text(
                            text = "Learning Progress Dashboard",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = SleekTextDark
                        )
                        Text(
                            text = "Recharts analytics for daily practice, accuracy, and mastery curves",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SleekTextMuted
                        )
                    }
                }
            }

            // Stat Summary Cards Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Mastered",
                        value = "$masteredCount",
                        sub = "Out of $totalPracticed",
                        icon = Icons.Default.CheckCircle,
                        color = SleekEmerald,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Streak",
                        value = "$streakDays Days",
                        sub = "Daily goal",
                        icon = Icons.Default.Whatshot,
                        color = SleekCoral,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Stars",
                        value = "$points",
                        sub = "Total XP",
                        icon = Icons.Default.Star,
                        color = SleekGoldDark,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Breaks",
                        value = "$physicalBreaksCompleted",
                        sub = "Physical fun",
                        icon = Icons.Default.DirectionsRun,
                        color = SleekOcean,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Embedded Recharts Dashboard View
            item {
                RechartsDashboardView(
                    dailyStats = dailyStats,
                    totalMastered = masteredCount,
                    totalPracticed = totalPracticed
                )
            }
        }
    }
}
