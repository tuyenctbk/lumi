package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TargetLanguage
import com.example.ui.components.FocusableCard
import com.example.ui.components.ParentalGate
import com.example.ui.theme.SleekBackground
import com.example.ui.theme.SleekCoral
import com.example.ui.theme.SleekEmerald
import com.example.ui.theme.SleekEmeraldDark
import com.example.ui.theme.SleekGold
import com.example.ui.theme.SleekGoldDark
import com.example.ui.theme.SleekOcean
import com.example.ui.theme.SleekOceanDark
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekSurfaceBorder
import com.example.ui.theme.SleekTextDark
import com.example.ui.theme.SleekTextMuted
import com.example.ui.viewmodel.LumiViewModel

/**
 * SettingsScreen
 *
 * App settings composable featuring:
 * 1. Parental verification gate with math challenge
 * 2. Offline Text-To-Speech toggle switch
 * 3. Parent QR mirror progress dashboard link & mobile preview
 * 4. Active curriculum target language selection
 */
@Composable
fun SettingsScreen(
    viewModel: LumiViewModel,
    onBack: () -> Unit
) {
    val targetLanguage by viewModel.targetLanguage.collectAsState()
    val wordProgressList by viewModel.wordProgressList.collectAsState()
    val streakDays by viewModel.streakDays.collectAsState()
    val points by viewModel.points.collectAsState()
    val physicalBreaks by viewModel.physicalBreaksCompleted.collectAsState()
    val ttsOfflineMode by viewModel.ttsOfflineMode.collectAsState()

    val masteredCount = remember(wordProgressList) { wordProgressList.count { it.isMastered } }
    val totalPracticed = wordProgressList.size

    var isParentUnlocked by remember { mutableStateOf(false) }
    var showParentGateModal by remember { mutableStateOf(false) }
    var showMobilePreviewModal by remember { mutableStateOf(false) }

    val parentWebReportUrl = remember(targetLanguage, masteredCount, points, streakDays, totalPracticed) {
        "https://ais-pre-653xreyvr5mficd75qoe7u-66657199188.asia-southeast1.run.app/parent-report?" +
                "lang=${targetLanguage.code}&" +
                "mastered=$masteredCount&" +
                "points=$points&" +
                "streak=$streakDays&" +
                "practiced=$totalPracticed&" +
                "breaks=$physicalBreaks"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SleekBackground)
            .testTag("settings_screen")
    ) {
        LazyColumn(
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxSize()
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
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        FocusableCard(
                            onClick = onBack,
                            shape = RoundedCornerShape(16.dp),
                            backgroundColor = SleekSurface,
                            unfocusedBorderColor = SleekSurfaceBorder,
                            focusedBorderColor = SleekEmerald,
                            testTag = "settings_back_button"
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = null,
                                    tint = SleekTextDark,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "App Settings & Parental Controls",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Black,
                                    color = SleekTextDark
                                )
                            }
                            Text(
                                text = "Configure voice synthesis, target languages, and parent QR analytics",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = SleekTextMuted
                            )
                        }
                    }

                    // Parental Lock Toggle Button
                    FocusableCard(
                        onClick = {
                            if (isParentUnlocked) {
                                isParentUnlocked = false
                            } else {
                                showParentGateModal = true
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        backgroundColor = if (isParentUnlocked) SleekEmerald else SleekGold,
                        unfocusedBorderColor = if (isParentUnlocked) SleekEmeraldDark else SleekGoldDark,
                        focusedBorderColor = SleekOcean,
                        testTag = "settings_parent_gate_button"
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (isParentUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = if (isParentUnlocked) "Parent Mode: UNLOCKED" else "Unlock Parent Mode",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // Parental Verification Status Banner
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isParentUnlocked) SleekEmerald.copy(alpha = 0.12f) else SleekGold.copy(alpha = 0.15f)
                    ),
                    border = BorderStroke(
                        width = 1.5.dp,
                        color = if (isParentUnlocked) SleekEmerald else SleekGold
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (isParentUnlocked) SleekEmerald else SleekGold,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            Column {
                                Text(
                                    text = if (isParentUnlocked) "🔒 Parental Gate Unlocked" else "🛡️ Protected by Math Challenge Gate",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SleekTextDark
                                )
                                Text(
                                    text = if (isParentUnlocked)
                                        "Parent controls and progress mirror dashboard are now accessible."
                                    else
                                        "Solve the simple math challenge to configure parent progress dashboards.",
                                    fontSize = 12.sp,
                                    color = SleekTextMuted
                                )
                            }
                        }

                        if (!isParentUnlocked) {
                            FocusableCard(
                                onClick = { showParentGateModal = true },
                                shape = RoundedCornerShape(14.dp),
                                backgroundColor = SleekGold,
                                unfocusedBorderColor = SleekGoldDark,
                                focusedBorderColor = SleekOcean,
                                testTag = "solve_gate_challenge_button"
                            ) {
                                Text(
                                    text = "Solve Math Challenge 🧮",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Section 1: Offline Text-To-Speech Settings
            item {
                Card(
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = SleekSurface),
                    border = BorderStroke(1.5.dp, SleekSurfaceBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(22.dp)) {
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
                                    color = if (ttsOfflineMode) SleekEmerald.copy(alpha = 0.15f) else SleekOcean.copy(alpha = 0.15f),
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = if (ttsOfflineMode) Icons.Default.WifiOff else Icons.Default.RecordVoiceOver,
                                            contentDescription = null,
                                            tint = if (ttsOfflineMode) SleekEmeraldDark else SleekOceanDark,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }

                                Column {
                                    Text(
                                        text = "Text-to-Speech Offline Mode",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SleekTextDark
                                    )
                                    Text(
                                        text = if (ttsOfflineMode)
                                            "⚡ Local On-Device TTS Active (Zero data usage)"
                                        else
                                            "🌐 Cloud / Network TTS Engine Enabled",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (ttsOfflineMode) SleekEmeraldDark else SleekTextMuted
                                    )
                                }
                            }

                            FocusableCard(
                                onClick = { viewModel.toggleTtsOfflineMode() },
                                shape = RoundedCornerShape(16.dp),
                                backgroundColor = if (ttsOfflineMode) SleekEmerald else SleekSurface,
                                unfocusedBorderColor = if (ttsOfflineMode) SleekEmeraldDark else SleekSurfaceBorder,
                                focusedBorderColor = SleekGold,
                                testTag = "settings_toggle_tts_offline"
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = if (ttsOfflineMode) "Offline Mode: ON" else "Offline Mode: OFF",
                                        fontWeight = FontWeight.Bold,
                                        color = if (ttsOfflineMode) Color.White else SleekTextDark,
                                        fontSize = 13.sp
                                    )
                                    Switch(
                                        checked = ttsOfflineMode,
                                        onCheckedChange = { viewModel.toggleTtsOfflineMode() },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color.White,
                                            checkedTrackColor = SleekEmeraldDark,
                                            uncheckedThumbColor = SleekTextMuted,
                                            uncheckedTrackColor = SleekSurfaceBorder
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "When Offline Mode is toggled ON, Lumi uses pre-loaded local phonetic synthesizers so your child can continue learning without requiring an active internet connection.",
                            fontSize = 13.sp,
                            color = SleekTextMuted,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Section 2: Active Target Curriculum Language
            item {
                Card(
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = SleekSurface),
                    border = BorderStroke(1.5.dp, SleekSurfaceBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(22.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                tint = SleekEmerald
                            )
                            Text(
                                text = "Active Target Curriculum Language",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = SleekTextDark
                            )
                        }

                        Text(
                            text = "Select the primary target language spoken by Lumi across all flashcards, game shows, and TTS pronunciation guides:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = SleekTextMuted,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(TargetLanguage.entries.toList()) { lang ->
                                val isSelected = lang == targetLanguage
                                FocusableCard(
                                    onClick = { viewModel.setLanguage(lang) },
                                    shape = RoundedCornerShape(16.dp),
                                    backgroundColor = if (isSelected) SleekEmerald else SleekSurface,
                                    unfocusedBorderColor = if (isSelected) SleekEmeraldDark else SleekSurfaceBorder,
                                    focusedBorderColor = SleekGold,
                                    testTag = "settings_lang_${lang.code}"
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(text = lang.flagEmoji, fontSize = 18.sp)
                                        Text(
                                            text = lang.displayName,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else SleekTextDark,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Section 3: QR-Based Parent Mirror Progress Dashboard
            item {
                Card(
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = SleekSurface),
                    border = BorderStroke(1.5.dp, SleekSurfaceBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(22.dp)) {
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
                                    color = SleekGold.copy(alpha = 0.15f),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.QrCode,
                                            contentDescription = null,
                                            tint = SleekGoldDark,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = "QR Parent Mirror Progress Dashboard",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black,
                                    color = SleekTextDark
                                )
                            }

                            FocusableCard(
                                onClick = { showMobilePreviewModal = true },
                                shape = RoundedCornerShape(14.dp),
                                backgroundColor = SleekOcean,
                                unfocusedBorderColor = SleekOceanDark,
                                focusedBorderColor = SleekGold,
                                testTag = "settings_preview_mobile_web_button"
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
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
                                        text = "Preview Mobile Web Dashboard",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Scan this QR code with a mobile camera to mirror your child's learning statistics live on your phone web browser.",
                                    fontSize = 13.sp,
                                    color = SleekTextMuted
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Real-time quick stats row
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = SleekEmerald.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = "🎓 $masteredCount Mastered",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SleekEmeraldDark,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = SleekCoral.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = "🔥 $streakDays Days Streak",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SleekCoral,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = SleekGold.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "⭐ $points Stars",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SleekGoldDark,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // Interactive QR Canvas
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = Color.White,
                                border = BorderStroke(1.5.dp, SleekSurfaceBorder),
                                shadowElevation = 3.dp,
                                modifier = Modifier
                                    .size(130.dp)
                                    .testTag("settings_qr_code")
                            ) {
                                Box(modifier = Modifier.padding(10.dp)) {
                                    GeneratedQrCanvas(data = parentWebReportUrl)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Parental Gate Challenge Dialog
        if (showParentGateModal) {
            ParentalGate(
                onDismiss = { showParentGateModal = false },
                onSuccess = {
                    showParentGateModal = false
                    isParentUnlocked = true
                }
            )
        }

        // Mobile Web Preview Modal
        if (showMobilePreviewModal) {
            MobileWebReportModal(
                targetLanguage = targetLanguage,
                masteredCount = masteredCount,
                totalPracticed = totalPracticed,
                points = points,
                streakDays = streakDays,
                physicalBreaks = physicalBreaks,
                badgesCount = viewModel.badges.collectAsState().value.size,
                url = parentWebReportUrl,
                onDismiss = { showMobilePreviewModal = false }
            )
        }
    }
}
