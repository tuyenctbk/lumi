package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.LearningCategory
import com.example.model.MascotMood
import com.example.model.TargetLanguage
import com.example.ui.components.DailyLearningStreakCanvas
import com.example.ui.components.FocusableCard
import com.example.ui.components.LearningMilestoneType
import com.example.ui.components.GlobalLoadingWrapper
import com.example.ui.components.LumiMascot
import com.example.ui.components.LumiMilestoneCelebrationDialog
import com.example.ui.components.LumiQuestModal
import com.example.ui.components.ParentalGate
import com.example.ui.components.PhysicalBreakDialog
import com.example.ui.components.ProgressSummary
import com.example.ui.components.TopBarHeader
import com.example.ui.components.TvFocusableCard
import com.example.ui.components.TvShelfRow
import com.example.ui.theme.SleekBackground
import com.example.ui.theme.SleekCoral
import com.example.ui.theme.SleekCoralDark
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
import com.example.ui.theme.SleekTextSubtle
import com.example.ui.viewmodel.LumiViewModel

data class GameShowItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color,
    val colorDark: Color,
    val badge: String
)

@Composable
fun HomeScreen(
    viewModel: LumiViewModel,
    onNavigateCategory: (LearningCategory) -> Unit,
    onNavigateGame: (String) -> Unit,
    onNavigateParentHub: () -> Unit,
    onOpenLanguagePicker: () -> Unit,
    onNavigateWorldMap: () -> Unit = { onNavigateGame("world_map") },
    onNavigateParentQrSync: () -> Unit = { onNavigateGame("parent_qr_sync") }
) {
    val targetLanguage by viewModel.targetLanguage.collectAsState()
    val mascotMood by viewModel.mascotMood.collectAsState()
    val mascotBubble by viewModel.mascotSpeechBubble.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val bilingualMode by viewModel.bilingualMode.collectAsState()
    val points by viewModel.points.collectAsState()
    val streakDays by viewModel.streakDays.collectAsState()
    val dailyStats by viewModel.dailyStats.collectAsState()
    val activeMilestone by viewModel.activeMilestone.collectAsState()

    val activeBreakQuest by viewModel.activePhysicalBreak.collectAsState()
    val isBreakVisible by viewModel.isPhysicalBreakVisible.collectAsState()
    val userPreferences by viewModel.userPreferences.collectAsState()
    val missedWords by viewModel.missedWords.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val gameShows = remember {
        listOf(
            GameShowItem(
                id = "mystery_spotlight",
                title = "Mystery Spotlight",
                subtitle = "Flashlight Discovery",
                icon = Icons.Default.Search,
                color = SleekGold,
                colorDark = Color(0xFFD99B16),
                badge = "Show #1"
            ),
            GameShowItem(
                id = "sound_match",
                title = "Sound & Word Match",
                subtitle = "Listen & Pop!",
                icon = Icons.Default.Hearing,
                color = SleekEmerald,
                colorDark = SleekEmeraldDark,
                badge = "Show #2"
            ),
            GameShowItem(
                id = "shadow_guess",
                title = "Shadow Silhouette",
                subtitle = "Guess the Creature",
                icon = Icons.Default.Visibility,
                color = SleekPurple,
                colorDark = Color(0xFF5B1AA8),
                badge = "Show #3"
            ),
            GameShowItem(
                id = "movement_quest",
                title = "Movement Quest",
                subtitle = "Physical Fun Break",
                icon = Icons.Default.DirectionsRun,
                color = SleekOcean,
                colorDark = SleekOceanDark,
                badge = "Active!"
            ),
            GameShowItem(
                id = "color_mixer",
                title = "Color Magic Mixer",
                subtitle = "Paint with Words",
                icon = Icons.Default.Palette,
                color = SleekCoral,
                colorDark = SleekCoralDark,
                badge = "Show #4"
            ),
            GameShowItem(
                id = "find_it",
                title = "Find It Audio Matching",
                subtitle = "Listen to Lumi & Tap!",
                icon = Icons.Default.Hearing,
                color = SleekOcean,
                colorDark = SleekOceanDark,
                badge = "NEW!"
            ),
            GameShowItem(
                id = "quiz",
                title = "Quiz Challenge",
                subtitle = "Multiple-Choice Fun",
                icon = Icons.Default.AutoAwesome,
                color = SleekEmerald,
                colorDark = SleekEmeraldDark,
                badge = "HOT!"
            ),
            GameShowItem(
                id = "pronunciation",
                title = "Pronunciation Lab",
                subtitle = "Practice Speaking",
                icon = Icons.Default.RecordVoiceOver,
                color = SleekOcean,
                colorDark = SleekOceanDark,
                badge = "MIC"
            )
        )
    }

    GlobalLoadingWrapper(
        isLoading = isLoading,
        errorMessage = errorMessage,
        onRetry = { viewModel.reloadContent() }
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(SleekBackground)
        ) {
        val isMobile = maxWidth < 600.dp
        val isTv = maxWidth >= 840.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = if (isMobile) 80.dp else 100.dp)
        ) {
            // Lean-back TV & Mobile Top Bar
            TopBarHeader(
                targetLanguage = targetLanguage,
                points = points,
                streakDays = streakDays,
                onOpenLanguagePicker = onOpenLanguagePicker
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Daily Progress Summary (Visual Canvas & Progress Metrics)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = if (isMobile) 14.dp else 24.dp, vertical = 4.dp)
            ) {
                ProgressSummary(
                    dailyStats = dailyStats,
                    completedLessonsCount = (points / 50).coerceAtMost(3),
                    targetDailyLessons = 3,
                    streakDays = streakDays,
                    accuracyPercent = 96,
                    onViewAnalyticsClick = { onNavigateGame("analytics") }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Review Mistakes Quick Banner (Extracts Room DB weak points)
            if (missedWords.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = if (isMobile) 14.dp else 24.dp, vertical = 6.dp)
                ) {
                    FocusableCard(
                        onClick = { onNavigateGame("review_mistakes") },
                        shape = RoundedCornerShape(22.dp),
                        backgroundColor = Color(0xFFFFF3E0),
                        unfocusedBorderColor = SleekCoral,
                        focusedBorderColor = SleekOcean,
                        testTag = "review_mistakes_quick_banner"
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text("💡", fontSize = 24.sp)
                                Column {
                                    Text(
                                        text = "Practice Weak Points (${missedWords.size} Words)",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp,
                                        color = SleekTextDark
                                    )
                                    Text(
                                        text = "Review incorrectly answered Room DB words to level up!",
                                        fontSize = 12.sp,
                                        color = SleekCoralDark,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SleekCoral
                            ) {
                                Text(
                                    text = "Practice ➔",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Section 1: Explore Worlds & Islands
            Column(modifier = Modifier.padding(horizontal = if (isMobile) 14.dp else 24.dp, vertical = 8.dp)) {
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
                            color = Color(0xFFFFF0C2),
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = SleekGoldDark,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = stringResource(R.string.home_world_explorer_islands),
                                style = if (isMobile) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = SleekTextDark
                            )
                            Text(
                                text = stringResource(R.string.home_choose_island_subtitle),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = SleekTextSubtle,
                                letterSpacing = 0.8.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Responsive Architecture: TvLazyRow on TV / wide screens vs Multi-card scroll on mobile
                LazyRow(
                    contentPadding = PaddingValues(end = if (isMobile) 14.dp else 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(LearningCategory.entries.toList()) { category ->
                        CategoryIslandCard(
                            category = category,
                            isMobile = isMobile,
                            onClick = {
                                viewModel.setActiveCategory(category)
                                onNavigateCategory(category)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Section 2: Gamified Learning Shows
            Column(modifier = Modifier.padding(horizontal = if (isMobile) 14.dp else 24.dp, vertical = 6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.home_lumi_game_shows),
                            style = if (isMobile) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = SleekTextDark,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = stringResource(R.string.home_play_games_subtitle),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SleekTextSubtle,
                            letterSpacing = 0.8.sp,
                            maxLines = 1
                        )
                    }

                    // Quick Physical Break Trigger
                    FocusableCard(
                        onClick = { viewModel.triggerPhysicalActivitySuggestion() },
                        shape = RoundedCornerShape(14.dp),
                        backgroundColor = SleekOcean,
                        unfocusedBorderColor = SleekOceanDark,
                        focusedBorderColor = SleekGold,
                        testTag = "home_quick_physical_break"
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsRun,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Active Break 🏃",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    contentPadding = PaddingValues(end = if (isMobile) 14.dp else 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(gameShows) { show ->
                        GameShowCard(
                            item = show,
                            isMobile = isMobile,
                            onClick = { onNavigateGame(show.id) }
                        )
                    }
                }
            }
        }

        // Mascot Companion Pin in Bottom Corner
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = if (isMobile) 12.dp else 24.dp, bottom = if (isMobile) 10.dp else 16.dp)
        ) {
            LumiMascot(
                mood = mascotMood,
                isSpeaking = isSpeaking,
                thoughtBubbleEmoji = mascotBubble,
                size = if (isMobile) 86.dp else 125.dp,
                onClick = {
                    val greetings = listOf(
                        "You are doing amazing!",
                        "Which island should we visit next?",
                        "I love learning new words with you!",
                        "Tap the World Map to explore!"
                    )
                    viewModel.speakLumi(greetings.random(), MascotMood.HAPPY)
                }
            )
        }

        // Periodic Background Physical Activity (Lumi Quest) Dialog
        if (isBreakVisible && activeBreakQuest != null) {
            LumiQuestModal(
                quest = activeBreakQuest!!,
                onCompleted = { viewModel.completePhysicalActivity(activeBreakQuest!!) },
                onDismiss = { viewModel.dismissPhysicalActivity() },
                onReplayAudio = { viewModel.speakLumi(activeBreakQuest!!.spokenPrompt) }
            )
        }

        // Lottie Mascot Learning Milestone Celebration Dialog
        if (activeMilestone != null) {
            LumiMilestoneCelebrationDialog(
                milestone = activeMilestone!!,
                onDismiss = { viewModel.dismissMilestone() },
                onSpeak = { speechText -> viewModel.speakLumi(speechText, MascotMood.SUPERSTAR) }
            )
        }
    }
}
}

@Composable
fun CategoryIslandCard(
    category: LearningCategory,
    isMobile: Boolean = false,
    onClick: () -> Unit
) {
    val categoryColor = Color(category.colorHex)

    TvFocusableCard(
        onClick = onClick,
        shape = RoundedCornerShape(26.dp),
        backgroundColor = SleekSurface,
        unfocusedBorderColor = SleekSurfaceBorder,
        focusedBorderColor = categoryColor,
        focusedScale = 1.08f,
        elevation = 3.dp,
        modifier = Modifier
            .width(if (isMobile) 155.dp else 190.dp)
            .height(if (isMobile) 180.dp else 215.dp)
            .testTag("island_${category.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isMobile) 12.dp else 16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = categoryColor.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, categoryColor.copy(alpha = 0.25f)),
                modifier = Modifier.size(if (isMobile) 64.dp else 80.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = category.emoji, fontSize = if (isMobile) 34.sp else 42.sp)
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = category.title,
                    fontSize = if (isMobile) 14.sp else 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = SleekTextDark,
                    lineHeight = if (isMobile) 17.sp else 20.sp
                )
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = categoryColor.copy(alpha = 0.15f),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = "EXPLORE",
                        fontSize = 10.sp,
                        color = categoryColor,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun GameShowCard(
    item: GameShowItem,
    isMobile: Boolean = false,
    onClick: () -> Unit
) {
    TvFocusableCard(
        onClick = onClick,
        shape = RoundedCornerShape(26.dp),
        backgroundColor = SleekSurface,
        unfocusedBorderColor = SleekSurfaceBorder,
        focusedBorderColor = item.color,
        focusedScale = 1.08f,
        elevation = 3.dp,
        modifier = Modifier
            .width(if (isMobile) 170.dp else 210.dp)
            .height(if (isMobile) 170.dp else 195.dp)
            .testTag("game_card_${item.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isMobile) 12.dp else 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = item.color,
                    border = BorderStroke(1.dp, item.colorDark)
                ) {
                    Text(
                        text = item.badge,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = item.color.copy(alpha = 0.14f),
                    modifier = Modifier.size(if (isMobile) 36.dp else 40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            tint = item.colorDark,
                            modifier = Modifier.size(if (isMobile) 18.dp else 20.dp)
                        )
                    }
                }
            }

            Column {
                Text(
                    text = item.title,
                    fontSize = if (isMobile) 15.sp else 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = SleekTextDark
                )
                Text(
                    text = item.subtitle,
                    fontSize = if (isMobile) 11.sp else 12.sp,
                    color = SleekTextMuted,
                    modifier = Modifier.padding(top = 1.dp)
                )
            }
        }
    }
}
