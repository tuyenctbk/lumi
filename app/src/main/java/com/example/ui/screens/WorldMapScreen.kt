package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LearningCategory
import com.example.model.MascotMood
import com.example.ui.components.FocusableCard
import com.example.ui.components.LumiMascot
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
import com.example.ui.theme.SleekTextSubtle
import com.example.ui.viewmodel.LumiViewModel

data class WorldIslandData(
    val category: LearningCategory,
    val islandName: String,
    val subtitle: String,
    val description: String,
    val normalizedX: Float, // 0.0f to 1.0f on map canvas
    val normalizedY: Float,
    val mainColor: Color,
    val accentColor: Color,
    val badge: String
)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun WorldMapScreen(
    viewModel: LumiViewModel,
    onSelectCategory: (LearningCategory) -> Unit,
    onBack: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
    sharedTransitionScope: SharedTransitionScope? = null
) {
    val targetLanguage by viewModel.targetLanguage.collectAsState()
    val points by viewModel.points.collectAsState()
    val mascotMood by viewModel.mascotMood.collectAsState()
    val mascotBubble by viewModel.mascotSpeechBubble.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()

    val islands = remember {
        listOf(
            WorldIslandData(
                category = LearningCategory.ANIMALS,
                islandName = "Emerald Jungle",
                subtitle = "Animals & Wild Nature",
                description = "Meet friendly lions, curious cats, birds, and playful frogs in the green canopy!",
                normalizedX = 0.20f,
                normalizedY = 0.32f,
                mainColor = SleekEmerald,
                accentColor = SleekEmeraldDark,
                badge = "Nature 🦁"
            ),
            WorldIslandData(
                category = LearningCategory.SPACE,
                islandName = "Cosmic Galaxy",
                subtitle = "Space, Stars & Rockets",
                description = "Blast off in a glowing rocket across sunlit constellations and moons!",
                normalizedX = 0.50f,
                normalizedY = 0.18f,
                mainColor = SleekPurple,
                accentColor = Color(0xFF4A148C),
                badge = "Space 🚀"
            ),
            WorldIslandData(
                category = LearningCategory.FOOD,
                islandName = "Sunny Orchard",
                subtitle = "Yummy Fruits & Treats",
                description = "Pick crunchy apples, sweet bananas, and tasty bakery treats in the sun!",
                normalizedX = 0.80f,
                normalizedY = 0.30f,
                mainColor = SleekGold,
                accentColor = SleekGoldDark,
                badge = "Food 🍎"
            ),
            WorldIslandData(
                category = LearningCategory.HOME,
                islandName = "Cozy Town",
                subtitle = "Everyday Objects",
                description = "Explore cozy houses, books, clocks, chairs, and friendly town streets!",
                normalizedX = 0.25f,
                normalizedY = 0.72f,
                mainColor = SleekOcean,
                accentColor = SleekOceanDark,
                badge = "Home 🏠"
            ),
            WorldIslandData(
                category = LearningCategory.COLORS,
                islandName = "Prism Haven",
                subtitle = "Colors & Magical Shapes",
                description = "Paint shimmering rainbow arcs, glowing circles, and colorful gemstone crystals!",
                normalizedX = 0.52f,
                normalizedY = 0.82f,
                mainColor = Color(0xFFAB47BC),
                accentColor = Color(0xFF7B1FA2),
                badge = "Colors 🎨"
            ),
            WorldIslandData(
                category = LearningCategory.ACTIONS,
                islandName = "Action Arena",
                subtitle = "Movement & Action Verbs",
                description = "Jump, run, dance, and swim with lively animated movements!",
                normalizedX = 0.78f,
                normalizedY = 0.68f,
                mainColor = SleekCoral,
                accentColor = Color(0xFFC2185B),
                badge = "Actions 🏃"
            )
        )
    }

    var selectedIsland by remember { mutableStateOf(islands.first()) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE8F7FD),
                        Color(0xFFD3EEF9),
                        Color(0xFFBCE3F5)
                    )
                )
            )
    ) {
        val isCompactScreen = maxWidth < 700.dp

        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            // World Map Top Header Bar (Adaptive for TV and Mobile)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = if (isCompactScreen) 14.dp else 24.dp, vertical = 12.dp),
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
                        focusedBorderColor = SleekGold,
                        testTag = "world_map_back_button"
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
                            Text(text = "🗺️", fontSize = 22.sp)
                            Text(
                                text = "Lumi's World Map",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = SleekTextDark
                            )
                        }
                        Text(
                            text = "SELECT AN ISLAND TO START YOUR LESSON IN ${targetLanguage.displayName.uppercase()}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SleekTextSubtle,
                            letterSpacing = 0.8.sp
                        )
                    }
                }

                // Stars Badge
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = SleekSurface,
                    border = BorderStroke(1.5.dp, SleekGoldDark.copy(alpha = 0.3f)),
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Stars",
                            tint = SleekGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "$points Stars",
                            fontWeight = FontWeight.Bold,
                            color = SleekTextDark,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            if (!isCompactScreen) {
                // Wide / Android TV Layout: 2D Interactive Archipelago Canvas + Side Detail Card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Interactive Map Canvas Area (70% width)
                    Box(
                        modifier = Modifier
                            .weight(0.68f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(32.dp))
                            .background(Color(0xFFC7EBF9))
                            .border(2.dp, Color(0xFF90CAF9), RoundedCornerShape(32.dp))
                    ) {
                        InteractiveMapCanvas(
                            islands = islands,
                            selectedIsland = selectedIsland,
                            onIslandSelected = { island ->
                                selectedIsland = island
                                viewModel.speakLumi("Welcome to ${island.islandName}! ${island.subtitle}")
                            },
                            animatedVisibilityScope = animatedVisibilityScope,
                            sharedTransitionScope = sharedTransitionScope
                        )
                    }

                    // Island Inspection & Launch Lesson Card (32% width)
                    IslandDetailPanel(
                        island = selectedIsland,
                        modifier = Modifier
                            .weight(0.32f)
                            .fillMaxHeight(),
                        onLaunch = {
                            viewModel.setActiveCategory(selectedIsland.category)
                            onSelectCategory(selectedIsland.category)
                        },
                        animatedVisibilityScope = animatedVisibilityScope,
                        sharedTransitionScope = sharedTransitionScope
                    )
                }
            } else {
                // Mobile Portrait Layout: Interactive Visual Archipelago Map with Bottom Carousel Card
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    // Archipelago Visual Map
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color(0xFFC7EBF9))
                            .border(1.5.dp, Color(0xFF90CAF9), RoundedCornerShape(24.dp))
                    ) {
                        InteractiveMapCanvas(
                            islands = islands,
                            selectedIsland = selectedIsland,
                            onIslandSelected = { island ->
                                selectedIsland = island
                                viewModel.speakLumi("Welcome to ${island.islandName}!")
                            },
                            animatedVisibilityScope = animatedVisibilityScope,
                            sharedTransitionScope = sharedTransitionScope
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Island Quick Selection Row
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(islands) { island ->
                            val isSelected = island.category == selectedIsland.category
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) island.mainColor else SleekSurface,
                                border = BorderStroke(
                                    1.5.dp,
                                    if (isSelected) island.accentColor else SleekSurfaceBorder
                                ),
                                modifier = Modifier
                                    .clickable { selectedIsland = island }
                                    .testTag("island_chip_${island.category.id}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(text = island.category.emoji, fontSize = 16.sp)
                                    Text(
                                        text = island.islandName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else SleekTextDark
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Detail Launch Card
                    IslandDetailPanel(
                        island = selectedIsland,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        onLaunch = {
                            viewModel.setActiveCategory(selectedIsland.category)
                            onSelectCategory(selectedIsland.category)
                        },
                        animatedVisibilityScope = animatedVisibilityScope,
                        sharedTransitionScope = sharedTransitionScope
                    )
                }
            }
        }

        // Mascot in Bottom Corner
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 12.dp)
        ) {
            LumiMascot(
                mood = mascotMood,
                isSpeaking = isSpeaking,
                thoughtBubbleEmoji = mascotBubble ?: selectedIsland.category.emoji,
                size = if (isCompactScreen) 90.dp else 115.dp,
                onClick = {
                    viewModel.speakLumi("Tap any island on the map to explore lessons in ${targetLanguage.displayName}!")
                }
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun InteractiveMapCanvas(
    islands: List<WorldIslandData>,
    selectedIsland: WorldIslandData,
    onIslandSelected: (WorldIslandData) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
    sharedTransitionScope: SharedTransitionScope? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 30f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave_anim"
    )

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val widthPx = constraints.maxWidth.toFloat()
        val heightPx = constraints.maxHeight.toFloat()

        // Background Sea Current Trails Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw gentle ocean routes connecting islands
            for (i in 0 until islands.size - 1) {
                val start = islands[i]
                val end = islands[i + 1]
                val p1 = Offset(start.normalizedX * widthPx, start.normalizedY * heightPx)
                val p2 = Offset(end.normalizedX * widthPx, end.normalizedY * heightPx)

                drawLine(
                    color = Color(0x604FC3F7),
                    start = p1,
                    end = p2,
                    strokeWidth = 4.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 18f), waveOffset)
                )
            }
        }

        // Render Islands as Focusable, D-pad accessible & Tappable nodes
        islands.forEach { island ->
            val isSelected = island.category == selectedIsland.category

            val islandOffsetXDp = (island.normalizedX * maxWidth.value).dp - 44.dp
            val islandOffsetYDp = (island.normalizedY * maxHeight.value).dp - 44.dp

            Box(
                modifier = Modifier
                    .offset(x = islandOffsetXDp, y = islandOffsetYDp)
                    .size(88.dp)
            ) {
                FocusableIslandNode(
                    island = island,
                    isSelected = isSelected,
                    onClick = { onIslandSelected(island) },
                    animatedVisibilityScope = animatedVisibilityScope,
                    sharedTransitionScope = sharedTransitionScope
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun FocusableIslandNode(
    island: WorldIslandData,
    isSelected: Boolean,
    onClick: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
    sharedTransitionScope: SharedTransitionScope? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val pulseScale = if (isSelected || isFocused) 1.15f else 1.0f

    val discSharedModifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
        with(sharedTransitionScope) {
            Modifier.sharedElement(
                state = rememberSharedContentState(key = "category_icon_${island.category.id}"),
                animatedVisibilityScope = animatedVisibilityScope
            )
        }
    } else {
        Modifier
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .scale(pulseScale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .focusable(interactionSource = interactionSource)
            .testTag("island_node_${island.category.id}")
    ) {
        // Island Island Disc with Emoji
        Surface(
            shape = CircleShape,
            color = if (isSelected || isFocused) island.mainColor else SleekSurface,
            border = BorderStroke(
                if (isSelected || isFocused) 3.dp else 2.dp,
                if (isSelected || isFocused) island.accentColor else island.mainColor.copy(alpha = 0.5f)
            ),
            shadowElevation = if (isSelected || isFocused) 8.dp else 3.dp,
            modifier = Modifier
                .size(62.dp)
                .then(discSharedModifier)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = island.category.emoji, fontSize = 32.sp)
            }
        }

        // Island Label Pill
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = if (isSelected || isFocused) island.accentColor else Color(0xDDFFFFFF),
            modifier = Modifier
                .padding(top = 4.dp)
                .shadow(2.dp, RoundedCornerShape(10.dp))
        ) {
            Text(
                text = island.islandName,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected || isFocused) Color.White else SleekTextDark,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                maxLines = 1
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun IslandDetailPanel(
    island: WorldIslandData,
    onLaunch: () -> Unit,
    modifier: Modifier = Modifier,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
    sharedTransitionScope: SharedTransitionScope? = null
) {
    val containerSharedModifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
        with(sharedTransitionScope) {
            Modifier.sharedElement(
                state = rememberSharedContentState(key = "category_card_${island.category.id}"),
                animatedVisibilityScope = animatedVisibilityScope
            )
        }
    } else {
        Modifier
    }

    Surface(
        shape = RoundedCornerShape(28.dp),
        color = SleekSurface,
        border = BorderStroke(2.dp, island.mainColor.copy(alpha = 0.35f)),
        shadowElevation = 4.dp,
        modifier = modifier.then(containerSharedModifier)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Top Tag & Category
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = island.mainColor.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, island.accentColor.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = island.badge,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = island.accentColor,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Text(
                        text = "⭐ 100% Kid Safe",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekEmerald
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = island.mainColor.copy(alpha = 0.12f),
                        modifier = Modifier.size(60.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = island.category.emoji, fontSize = 36.sp)
                        }
                    }

                    Column {
                        Text(
                            text = island.islandName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = SleekTextDark
                        )
                        Text(
                            text = island.subtitle,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = island.accentColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = island.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SleekTextMuted,
                    lineHeight = 18.sp
                )
            }

            // Launch Island Exploration Button
            FocusableCard(
                onClick = onLaunch,
                shape = RoundedCornerShape(20.dp),
                backgroundColor = island.mainColor,
                unfocusedBorderColor = island.accentColor,
                focusedBorderColor = SleekGold,
                focusedScale = 1.05f,
                elevation = 4.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("launch_island_button")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Start Island Lessons 🚀",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }
        }
    }
}
