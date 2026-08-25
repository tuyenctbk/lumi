package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
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
import com.example.model.LearningCategory
import com.example.model.VocabularyItem
import com.example.ui.components.CardVisualMode
import com.example.ui.components.ConfettiCanvas
import com.example.ui.components.FocusableCard
import com.example.ui.components.FocusableVocabularyCard
import com.example.ui.components.LumiMascot
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
import com.example.ui.theme.SleekTextSubtle
import com.example.ui.viewmodel.LumiViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ExploreCategoryScreen(
    category: LearningCategory,
    viewModel: LumiViewModel,
    onBack: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
    sharedTransitionScope: SharedTransitionScope? = null
) {
    val targetLanguage by viewModel.targetLanguage.collectAsState()
    val mascotMood by viewModel.mascotMood.collectAsState()
    val mascotBubble by viewModel.mascotSpeechBubble.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val bilingualMode by viewModel.bilingualMode.collectAsState()
    val wordProgressList by viewModel.wordProgressList.collectAsState()

    val words = remember(category) { viewModel.getWordsForCategory(category) }
    var selectedWord by remember { mutableStateOf<VocabularyItem?>(words.firstOrNull()) }
    var confettiTrigger by remember { mutableStateOf(false) }

    var visualMode by remember { mutableStateOf(CardVisualMode.EMOJI) }

    // Map to cache loaded Pixabay photos and Giphy GIFs for cards
    val photosMap = remember { mutableStateMapOf<String, String>() }
    val gifsMap = remember { mutableStateMapOf<String, String>() }

    LaunchedEffect(category, visualMode) {
        if (visualMode == CardVisualMode.REAL_PHOTO) {
            words.forEach { item ->
                if (!photosMap.containsKey(item.id)) {
                    val url = viewModel.pixabayRepository.getImageForWord(item.id)
                    if (url != null) photosMap[item.id] = url
                }
            }
        } else if (visualMode == CardVisualMode.ACTION_GIF) {
            words.forEach { item ->
                if (!gifsMap.containsKey(item.id)) {
                    val url = viewModel.giphyRepository.getGifForAction(item.id)
                    if (url != null) gifsMap[item.id] = url
                }
            }
        }
    }

    val categoryColor = Color(category.colorHex)

    val iconSharedModifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
        with(sharedTransitionScope) {
            Modifier.sharedElement(
                state = rememberSharedContentState(key = "category_icon_${category.id}"),
                animatedVisibilityScope = animatedVisibilityScope
            )
        }
    } else {
        Modifier
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(SleekBackground)
    ) {
        val isMobilePortrait = maxWidth < 600.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(
                    start = if (isMobilePortrait) 14.dp else 24.dp,
                    end = if (isMobilePortrait) 14.dp else 24.dp,
                    top = if (isMobilePortrait) 4.dp else 8.dp,
                    bottom = if (isMobilePortrait) 14.dp else 24.dp
                )
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FocusableCard(
                        onClick = onBack,
                        shape = RoundedCornerShape(16.dp),
                        backgroundColor = SleekSurface,
                        unfocusedBorderColor = SleekSurfaceBorder,
                        focusedBorderColor = categoryColor,
                        testTag = "category_back_button"
                    ) {
                        Box(
                            modifier = Modifier.padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
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
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = categoryColor.copy(alpha = 0.15f),
                                modifier = Modifier
                                    .size(if (isMobilePortrait) 36.dp else 44.dp)
                                    .then(iconSharedModifier)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = category.emoji, fontSize = if (isMobilePortrait) 20.sp else 26.sp)
                                }
                            }
                            Text(
                                text = category.title,
                                style = if (isMobilePortrait) MaterialTheme.typography.titleMedium else MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = SleekTextDark
                            )
                        }
                        Text(
                            text = "Learning in ${targetLanguage.displayName}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = SleekEmerald
                        )
                    }
                }

                // Media Visual Mode Selector Tabs (Emoji / Pixabay Photo / Giphy GIF)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (visualMode == CardVisualMode.EMOJI) categoryColor else SleekSurface,
                        border = BorderStroke(1.dp, if (visualMode == CardVisualMode.EMOJI) categoryColor else SleekSurfaceBorder),
                        modifier = Modifier.clickable { visualMode = CardVisualMode.EMOJI }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mood,
                                contentDescription = null,
                                tint = if (visualMode == CardVisualMode.EMOJI) Color.White else SleekTextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            if (!isMobilePortrait) {
                                Text(
                                    text = "Emoji",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (visualMode == CardVisualMode.EMOJI) Color.White else SleekTextDark
                                )
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (visualMode == CardVisualMode.REAL_PHOTO) SleekOcean else SleekSurface,
                        border = BorderStroke(1.dp, if (visualMode == CardVisualMode.REAL_PHOTO) SleekOceanDark else SleekSurfaceBorder),
                        modifier = Modifier.clickable { visualMode = CardVisualMode.REAL_PHOTO }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                tint = if (visualMode == CardVisualMode.REAL_PHOTO) Color.White else SleekTextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (isMobilePortrait) "Photo" else "Pixabay Photo",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (visualMode == CardVisualMode.REAL_PHOTO) Color.White else SleekTextDark
                            )
                        }
                    }

                    if (category == LearningCategory.ACTIONS || !isMobilePortrait) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (visualMode == CardVisualMode.ACTION_GIF) Color(0xFFE91E63) else SleekSurface,
                            border = BorderStroke(1.dp, if (visualMode == CardVisualMode.ACTION_GIF) Color(0xFFC2185B) else SleekSurfaceBorder),
                            modifier = Modifier.clickable { visualMode = CardVisualMode.ACTION_GIF }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Animation,
                                    contentDescription = null,
                                    tint = if (visualMode == CardVisualMode.ACTION_GIF) Color.White else SleekTextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (isMobilePortrait) "GIF" else "Giphy Motion",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (visualMode == CardVisualMode.ACTION_GIF) Color.White else SleekTextDark
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Cards Display (Adaptive: 2-column grid on mobile portrait vs Lean-back Row on TV/Tablet)
            if (isMobilePortrait) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(bottom = 70.dp, top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(words) { item ->
                        val progress = wordProgressList.find { it.wordId == item.id }
                        val isMastered = progress?.isMastered == true
                        val targetWord = item.translations[targetLanguage.code] ?: item.englishWord

                        FocusableVocabularyCard(
                            item = item,
                            targetWord = targetWord,
                            isBilingual = bilingualMode,
                            isMastered = isMastered,
                            categoryColor = categoryColor,
                            visualMode = visualMode,
                            realPhotoUrl = photosMap[item.id],
                            gifUrl = gifsMap[item.id],
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(210.dp),
                            onClick = {
                                selectedWord = item
                                viewModel.speakWord(item)
                                confettiTrigger = true
                            },
                            onPlayRealSound = {
                                selectedWord = item
                                viewModel.playRealSoundEffect(item.id)
                            }
                        )
                    }
                }
            } else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(words) { item ->
                        val progress = wordProgressList.find { it.wordId == item.id }
                        val isMastered = progress?.isMastered == true
                        val targetWord = item.translations[targetLanguage.code] ?: item.englishWord

                        FocusableVocabularyCard(
                            item = item,
                            targetWord = targetWord,
                            isBilingual = bilingualMode,
                            isMastered = isMastered,
                            categoryColor = categoryColor,
                            visualMode = visualMode,
                            realPhotoUrl = photosMap[item.id],
                            gifUrl = gifsMap[item.id],
                            modifier = Modifier
                                .width(220.dp)
                                .height(280.dp),
                            onClick = {
                                selectedWord = item
                                viewModel.speakWord(item)
                                confettiTrigger = true
                            },
                            onPlayRealSound = {
                                selectedWord = item
                                viewModel.playRealSoundEffect(item.id)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = SleekSurface,
                border = BorderStroke(1.5.dp, SleekSurfaceBorder),
                shadowElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(
                        start = 14.dp,
                        end = if (isMobilePortrait) 105.dp else 14.dp,
                        top = 10.dp,
                        bottom = 10.dp
                    ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isMobilePortrait) "💡 Tap cards to hear & learn!" else "💡 Tip: Select any card to hear Lumi pronounce it out loud!",
                        color = SleekTextMuted,
                        fontWeight = FontWeight.Medium,
                        fontSize = if (isMobilePortrait) 12.sp else 14.sp,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Freesound FX button
                        FocusableCard(
                            onClick = {
                                selectedWord?.let { viewModel.playRealSoundEffect(it.id) }
                            },
                            shape = RoundedCornerShape(14.dp),
                            backgroundColor = SleekOcean,
                            unfocusedBorderColor = SleekOceanDark,
                            focusedBorderColor = SleekGold,
                            testTag = "freesound_fx_button"
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = "Real Sound",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Real Sound",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        if (!isMobilePortrait) {
                            // Listen Again Pronunciation
                            FocusableCard(
                                onClick = {
                                    selectedWord?.let { viewModel.speakWord(it) }
                                },
                                shape = RoundedCornerShape(14.dp),
                                backgroundColor = SleekGold,
                                unfocusedBorderColor = SleekGoldDark,
                                focusedBorderColor = SleekEmerald,
                                testTag = "repeat_audio_button"
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = "Listen",
                                        tint = SleekTextDark,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Pronounce",
                                        color = SleekTextDark,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Animated Lumi in Corner (scaled neatly on mobile)
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 10.dp)
        ) {
            LumiMascot(
                mood = mascotMood,
                isSpeaking = isSpeaking,
                thoughtBubbleEmoji = mascotBubble,
                size = if (isMobilePortrait) 85.dp else 110.dp,
                onClick = {
                    selectedWord?.let {
                        viewModel.speakWord(it)
                    } ?: viewModel.speakLumi("Pick a card to hear its sound!")
                }
            )
        }

        ConfettiCanvas(trigger = confettiTrigger)
    }
}
