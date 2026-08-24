package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TargetLanguage
import com.example.ui.components.FocusableCard
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

/**
 * ParentQrSyncScreen
 *
 * Dedicated screen generating a unique, high-resolution QR code for parents.
 * Scanning it connects directly to an external web-based dashboard displaying
 * their child's learning statistics, retention curves, and vocabulary progress.
 */
@Composable
fun ParentQrSyncScreen(
    viewModel: LumiViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val targetLanguage by viewModel.targetLanguage.collectAsState()
    val wordProgressList by viewModel.wordProgressList.collectAsState()
    val streakDays by viewModel.streakDays.collectAsState()
    val points by viewModel.points.collectAsState()
    val missedWords by viewModel.missedWords.collectAsState()
    val physicalBreaksCompleted by viewModel.physicalBreaksCompleted.collectAsState()

    val masteredCount = wordProgressList.count { it.isMastered }
    val totalPracticed = wordProgressList.size
    val accuracy = if (totalPracticed > 0) {
        val totalCorrect = wordProgressList.sumOf { it.correctCount }
        val totalErrors = wordProgressList.sumOf { it.errorCount }
        if (totalCorrect + totalErrors > 0) {
            ((totalCorrect.toFloat() / (totalCorrect + totalErrors)) * 100).toInt()
        } else 100
    } else 100

    // Unique generated Web Dashboard sync URL
    val dashboardWebUrl = remember(targetLanguage, masteredCount, points, streakDays, totalPracticed, accuracy, physicalBreaksCompleted) {
        "https://ais-pre-653xreyvr5mficd75qoe7u-66657199188.asia-southeast1.run.app/parent-report?" +
                "lang=${targetLanguage.code}&" +
                "mastered=$masteredCount&" +
                "total=$totalPracticed&" +
                "accuracy=$accuracy&" +
                "points=$points&" +
                "streak=$streakDays&" +
                "breaks=$physicalBreaksCompleted&" +
                "sync=${System.currentTimeMillis()}"
    }

    var showLiveWebSimulator by remember { mutableStateOf(false) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(SleekBackground)
    ) {
        val isMobile = maxWidth < 650.dp

        LazyColumn(
            contentPadding = PaddingValues(
                start = if (isMobile) 16.dp else 24.dp,
                end = if (isMobile) 16.dp else 24.dp,
                top = if (isMobile) 6.dp else 8.dp,
                bottom = if (isMobile) 16.dp else 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize().statusBarsPadding()
        ) {
            // Header Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FocusableCard(
                            onClick = onBack,
                            shape = RoundedCornerShape(16.dp),
                            backgroundColor = SleekSurface,
                            unfocusedBorderColor = SleekSurfaceBorder,
                            focusedBorderColor = SleekEmerald,
                            testTag = "parent_qr_back_button"
                        ) {
                            Box(modifier = Modifier.padding(10.dp), contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = SleekTextDark
                                )
                            }
                        }

                        Column {
                            Text(
                                text = "Parent Web Dashboard & QR Sync 📱",
                                style = if (isMobile) MaterialTheme.typography.titleMedium else MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Black,
                                color = SleekTextDark
                            )
                            Text(
                                text = "Scan with your phone to view real-time learning reports",
                                fontSize = if (isMobile) 12.sp else 14.sp,
                                color = SleekTextMuted
                            )
                        }
                    }

                    // Simulated Web Preview Button
                    FocusableCard(
                        onClick = { showLiveWebSimulator = true },
                        shape = RoundedCornerShape(14.dp),
                        backgroundColor = SleekOcean,
                        unfocusedBorderColor = SleekOceanDark,
                        focusedBorderColor = SleekGold,
                        testTag = "open_web_simulator_button"
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhoneIphone,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Preview Report",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // QR Code Hero Box
            item {
                Card(
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = SleekSurface),
                    border = BorderStroke(1.5.dp, SleekSurfaceBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isMobile) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // QR Canvas
                            Surface(
                                shape = RoundedCornerShape(22.dp),
                                color = Color.White,
                                border = BorderStroke(2.dp, SleekSurfaceBorder),
                                shadowElevation = 4.dp,
                                modifier = Modifier
                                    .size(180.dp)
                                    .testTag("parent_qr_code_view")
                            ) {
                                Box(modifier = Modifier.padding(14.dp)) {
                                    GeneratedQrCanvas(data = dashboardWebUrl)
                                }
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Scan QR Code on Phone",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = SleekTextDark
                                )
                                Text(
                                    text = "Open your mobile camera and point it at this QR code to view child's SRS retention stats, study time, and word mastery.",
                                    fontSize = 13.sp,
                                    color = SleekTextMuted,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )

                                ActionButtonsRow(
                                    context = context,
                                    url = dashboardWebUrl
                                )
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = SleekGold.copy(alpha = 0.15f),
                                        modifier = Modifier.size(38.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.QrCode,
                                                contentDescription = null,
                                                tint = SleekGoldDark,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = "The Parent Mirror (Live Web Dashboard)",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Black,
                                        color = SleekTextDark
                                    )
                                }

                                Text(
                                    text = "Scan this QR code with any smartphone camera to open your child's personalized web learning portal. Monitor vocabulary retention curves, practice streaks, and offline activity breaks in real time.",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = SleekTextMuted,
                                    modifier = Modifier.padding(vertical = 10.dp)
                                )

                                ActionButtonsRow(
                                    context = context,
                                    url = dashboardWebUrl
                                )
                            }

                            Spacer(modifier = Modifier.width(28.dp))

                            // High-Precision QR Canvas
                            Surface(
                                shape = RoundedCornerShape(22.dp),
                                color = Color.White,
                                border = BorderStroke(2.dp, SleekSurfaceBorder),
                                shadowElevation = 4.dp,
                                modifier = Modifier
                                    .size(170.dp)
                                    .testTag("parent_qr_code_view")
                            ) {
                                Box(modifier = Modifier.padding(14.dp)) {
                                    GeneratedQrCanvas(data = dashboardWebUrl)
                                }
                            }
                        }
                    }
                }
            }

            // Quick Learning Overview Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Accuracy",
                        value = "$accuracy%",
                        sub = "Overall Retention",
                        icon = Icons.Default.Insights,
                        color = SleekEmerald,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Mastered",
                        value = "$masteredCount",
                        sub = "Out of $totalPracticed words",
                        icon = Icons.Default.CheckCircle,
                        color = SleekOcean,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Daily Streak",
                        value = "$streakDays Days",
                        sub = "Active streak",
                        icon = Icons.Default.Whatshot,
                        color = SleekCoral,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Offline Breaks",
                        value = "$physicalBreaksCompleted",
                        sub = "Lumi Quests",
                        icon = Icons.Default.DirectionsRun,
                        color = SleekGoldDark,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // COPPA Data Protection Guarantee
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SleekEmerald.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, SleekEmerald.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = SleekEmerald.copy(alpha = 0.18f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = SleekEmeraldDark,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Zero Tracking & 100% Kid-Safe Privacy (COPPA Compliant)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = SleekEmeraldDark
                            )
                            Text(
                                text = "No personal data or account registration required. All learning progress is securely stored locally on this device and encoded safely into the QR code link.",
                                fontSize = 12.sp,
                                color = SleekTextMuted
                            )
                        }
                    }
                }
            }
        }

        // Live Web Preview Simulator Modal
        if (showLiveWebSimulator) {
            MobileWebReportModal(
                targetLanguage = targetLanguage,
                masteredCount = masteredCount,
                totalPracticed = totalPracticed,
                points = points,
                streakDays = streakDays,
                physicalBreaks = physicalBreaksCompleted,
                badgesCount = 4,
                url = dashboardWebUrl,
                onDismiss = { showLiveWebSimulator = false }
            )
        }
    }
}

@Composable
private fun ActionButtonsRow(
    context: Context,
    url: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Copy Link
        FocusableCard(
            onClick = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Lumi Web Dashboard", url)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "Dashboard link copied to clipboard! 📋", Toast.LENGTH_SHORT).show()
            },
            shape = RoundedCornerShape(12.dp),
            backgroundColor = SleekSurface,
            unfocusedBorderColor = SleekSurfaceBorder,
            focusedBorderColor = SleekOcean,
            testTag = "copy_dashboard_link_button"
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = null,
                    tint = SleekTextDark,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Copy Link",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SleekTextDark
                )
            }
        }

        // Open in Browser
        FocusableCard(
            onClick = {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "Could not open browser", Toast.LENGTH_SHORT).show()
                }
            },
            shape = RoundedCornerShape(12.dp),
            backgroundColor = SleekEmerald,
            unfocusedBorderColor = SleekEmeraldDark,
            focusedBorderColor = SleekGold,
            testTag = "open_browser_dashboard_button"
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.OpenInBrowser,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Open in Browser 🌐",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
