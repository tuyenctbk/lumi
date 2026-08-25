package com.example.ui.screens

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MascotMood
import com.example.ui.components.FocusableCard
import com.example.ui.components.LumiMascot
import com.example.ui.theme.SleekBackground
import com.example.ui.theme.SleekEmerald
import com.example.ui.theme.SleekGold
import com.example.ui.theme.SleekGoldDark
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekSurfaceBorder
import com.example.ui.theme.SleekTextDark
import com.example.ui.theme.SleekTextMuted
import com.example.ui.viewmodel.LumiViewModel

data class DisplaySticker(
    val id: String,
    val title: String,
    val emoji: String,
    val isUnlocked: Boolean
)

@Composable
fun StickerBookScreen(
    viewModel: LumiViewModel,
    onBack: () -> Unit
) {
    val mascotMood by viewModel.mascotMood.collectAsState()
    val mascotBubble by viewModel.mascotSpeechBubble.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val badges by viewModel.badges.collectAsState()
    val points by viewModel.points.collectAsState()

    val allStickers = remember(badges) {
        listOf(
            DisplaySticker("star_1", "Explorer Star", "🌟", true),
            DisplaySticker("cat", "Kitty Master", "🐱", badges.any { it.id.contains("cat") } || true),
            DisplaySticker("dog", "Puppy Master", "🐶", badges.any { it.id.contains("dog") } || true),
            DisplaySticker("lion", "Jungle King", "🦁", badges.any { it.id.contains("lion") }),
            DisplaySticker("apple", "Fruit Champ", "🍎", badges.any { it.id.contains("apple") } || true),
            DisplaySticker("rocket", "Space Hero", "🚀", badges.any { it.id.contains("rocket") }),
            DisplaySticker("crown", "Superstar Crown", "👑", points >= 50),
            DisplaySticker("rainbow", "Color Wizard", "🌈", points >= 80),
            DisplaySticker("trophy", "Language Master", "🏆", points >= 120)
        )
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(SleekBackground)
    ) {
        val isMobile = maxWidth < 600.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(
                    start = if (isMobile) 14.dp else 24.dp,
                    end = if (isMobile) 14.dp else 24.dp,
                    top = 8.dp,
                    bottom = if (isMobile) 90.dp else 110.dp
                )
        ) {
            // Header
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
                        focusedBorderColor = SleekGold,
                        testTag = "sticker_back_button"
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = SleekGoldDark
                            )
                            Text(
                                text = "Sticker & Trophy Room",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = SleekTextDark
                            )
                        }
                        Text(
                            text = "${allStickers.count { it.isUnlocked }} of ${allStickers.size} Stickers Collected",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = SleekEmerald
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = SleekGold.copy(alpha = 0.2f),
                    border = BorderStroke(1.5.dp, SleekGoldDark.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = SleekGoldDark
                        )
                        Text(
                            text = "$points Stars",
                            fontWeight = FontWeight.Black,
                            color = SleekTextDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Sticker Grid
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                contentPadding = PaddingValues(bottom = 80.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(allStickers) { sticker ->
                    FocusableCard(
                        onClick = {
                            if (sticker.isUnlocked) {
                                viewModel.speakLumi("That's your ${sticker.title} sticker! Woohoo!", MascotMood.SUPERSTAR)
                            } else {
                                viewModel.speakLumi("Keep playing games to unlock this shiny sticker!", MascotMood.ENCOURAGING)
                            }
                        },
                        shape = RoundedCornerShape(24.dp),
                        backgroundColor = SleekSurface,
                        unfocusedBorderColor = SleekSurfaceBorder,
                        focusedBorderColor = if (sticker.isUnlocked) SleekGold else SleekEmerald,
                        focusedScale = 1.08f,
                        elevation = 3.dp,
                        testTag = "sticker_${sticker.id}"
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (sticker.isUnlocked) SleekGold.copy(alpha = 0.15f) else SleekSurfaceBorder.copy(alpha = 0.5f),
                                border = BorderStroke(1.dp, if (sticker.isUnlocked) SleekGoldDark.copy(alpha = 0.3f) else SleekSurfaceBorder),
                                modifier = Modifier.size(76.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    if (sticker.isUnlocked) {
                                        Text(text = sticker.emoji, fontSize = 42.sp)
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = "Locked",
                                            tint = SleekTextMuted,
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = sticker.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (sticker.isUnlocked) SleekTextDark else SleekTextMuted,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = if (sticker.isUnlocked) "⭐ Unlocked" else "Locked",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (sticker.isUnlocked) SleekGoldDark else SleekTextMuted,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // Mascot
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    end = if (isMobile) 10.dp else 24.dp,
                    bottom = if (isMobile) 8.dp else 12.dp
                )
        ) {
            LumiMascot(
                mood = mascotMood,
                isSpeaking = isSpeaking,
                thoughtBubbleEmoji = mascotBubble,
                size = if (isMobile) 75.dp else 110.dp,
                onClick = { viewModel.speakLumi("Look at all your awesome trophies and shiny stickers!") }
            )
        }
    }
}

