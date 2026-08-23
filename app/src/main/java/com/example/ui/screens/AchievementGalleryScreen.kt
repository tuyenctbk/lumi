package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AchievementCatalog
import com.example.model.MascotMood
import com.example.ui.components.StreakCounter
import com.example.ui.theme.SleekCoral
import com.example.ui.theme.SleekEmerald
import com.example.ui.theme.SleekGold
import com.example.ui.theme.SleekGoldDark
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekSurfaceBorder
import com.example.ui.theme.SleekTextDark
import com.example.ui.theme.SleekTextMuted
import com.example.ui.viewmodel.LumiViewModel

/**
 * AchievementGalleryScreen
 *
 * Displays unlocked digital badges and locked milestone progress in a responsive gallery grid,
 * featuring consecutive day streak counters and interactive Lumi audio congratulations.
 */
@Composable
fun AchievementGalleryScreen(
    viewModel: LumiViewModel,
    onBack: () -> Unit
) {
    val unlockedBadges by viewModel.badges.collectAsState()
    val streakDays by viewModel.streakDays.collectAsState()
    val wordProgressList by viewModel.wordProgressList.collectAsState()
    val physicalBreaks by viewModel.physicalBreaksCompleted.collectAsState()
    val recentSessions by viewModel.recentSessions.collectAsState()

    val masteredCount = remember(wordProgressList) { wordProgressList.count { it.isMastered } }
    val totalSessionsCount = recentSessions.size

    val unlockedIds = remember(unlockedBadges) { unlockedBadges.map { it.id }.toSet() }

    var selectedCategoryFilter by remember { mutableStateOf("All") }
    val categories = listOf("All", "Words", "Streaks", "Quests", "Games")

    val achievements = remember { AchievementCatalog.ALL_ACHIEVEMENTS }
    val filteredAchievements = remember(selectedCategoryFilter, achievements) {
        if (selectedCategoryFilter == "All") achievements
        else achievements.filter { it.category.equals(selectedCategoryFilter, ignoreCase = true) }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(SleekSurface)
            .testTag("achievement_gallery_screen")
    ) {
        val isLandscape = maxWidth > maxHeight

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Top Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("achievement_gallery_back")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = SleekTextDark
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Column {
                        Text(
                            text = "🏆 Digital Badge Gallery",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = SleekTextDark
                        )
                        Text(
                            text = "${unlockedIds.size} of ${achievements.size} Badges Unlocked",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = SleekEmerald
                        )
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = SleekGold.copy(alpha = 0.2f),
                    border = BorderStroke(1.5.dp, SleekGold)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Points",
                            tint = SleekGoldDark,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${unlockedIds.size * 50} Stars",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SleekTextDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Streak Counter Progress Section
            StreakCounter(
                currentStreakDays = streakDays,
                targetStreakGoal = 7,
                compact = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Filter Categories Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategoryFilter == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategoryFilter = category },
                        label = {
                            Text(
                                text = category,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SleekEmerald,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Achievements Gallery Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(if (isLandscape) 4 else 2),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("achievement_badge_grid"),
                contentPadding = PaddingValues(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredAchievements, key = { it.id }) { item ->
                    val isUnlocked = item.id in unlockedIds ||
                            (item.progressExtractor(masteredCount, streakDays, physicalBreaks, totalSessionsCount) >= item.targetGoal)

                    val currentProgress = item.progressExtractor(masteredCount, streakDays, physicalBreaks, totalSessionsCount)
                        .coerceAtMost(item.targetGoal)
                    val progressFraction = (currentProgress.toFloat() / item.targetGoal).coerceIn(0f, 1f)

                    Card(
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUnlocked) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        border = BorderStroke(
                            width = if (isUnlocked) 2.5.dp else 1.dp,
                            color = if (isUnlocked) SleekGold else SleekSurfaceBorder
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isUnlocked) 6.dp else 1.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .testTag("badge_card_${item.id}")
                            .clickable {
                                if (isUnlocked) {
                                    viewModel.speakLumi("Hooray! You unlocked ${item.title}!", MascotMood.SUPERSTAR)
                                } else {
                                    viewModel.speakLumi("Keep going! $currentProgress of ${item.targetGoal} to unlock ${item.title}!", MascotMood.ENCOURAGING)
                                }
                            }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Badge Icon Circle
                            Box(
                                modifier = Modifier.size(56.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (isUnlocked) SleekGold.copy(alpha = 0.25f) else Color.Gray.copy(alpha = 0.15f),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = if (isUnlocked) item.iconEmoji else "🔒",
                                            fontSize = 28.sp
                                        )
                                    }
                                }
                            }

                            // Badge Title & Description
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = item.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isUnlocked) SleekTextDark else SleekTextMuted,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Text(
                                    text = item.description,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = SleekTextMuted,
                                    textAlign = TextAlign.Center,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }

                            // Unlock Status or Progress Bar
                            if (isUnlocked) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = SleekEmerald.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "✓ UNLOCKED",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = SleekEmerald,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            } else {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    LinearProgressIndicator(
                                        progress = { progressFraction },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp)),
                                        color = SleekCoral,
                                        trackColor = Color.Black.copy(alpha = 0.08f)
                                    )
                                    Text(
                                        text = "$currentProgress / ${item.targetGoal}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SleekTextMuted,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 2.dp)
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
