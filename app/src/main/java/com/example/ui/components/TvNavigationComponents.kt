package com.example.ui.components

import android.view.KeyEvent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.foundation.focusGroup
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.zIndex
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import com.example.audio.SoundFxHelper
import com.example.ui.theme.SleekCoral
import com.example.ui.theme.SleekEmerald
import com.example.ui.theme.SleekGold
import com.example.ui.theme.SleekOcean
import com.example.ui.theme.SleekOceanDark
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekSurfaceBorder

import androidx.compose.ui.input.InputMode
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInputModeManager

/**
 * TvFocusableCard
 *
 * Dedicated Android TV D-Pad focusable card with high-contrast focus rings,
 * scale elevation, and sound feedback for lean-back TV experiences.
 * On mobile/tablet touch screens, focus indication is disabled for clean touch UI.
 */
@Composable
fun TvFocusableCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "tv_focusable_card",
    shape: Shape = RoundedCornerShape(24.dp),
    backgroundColor: Color = SleekSurface,
    focusedBorderColor: Color = SleekEmerald,
    unfocusedBorderColor: Color = SleekSurfaceBorder,
    focusedScale: Float = 1.08f,
    elevation: Dp = 4.dp,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val inputModeManager = LocalInputModeManager.current
    val isTvDevice = remember {
        context.packageManager.hasSystemFeature(android.content.pm.PackageManager.FEATURE_LEANBACK) ||
        context.packageManager.hasSystemFeature(android.content.pm.PackageManager.FEATURE_TELEVISION) ||
        (context.resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_TYPE_MASK) == android.content.res.Configuration.UI_MODE_TYPE_TELEVISION
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val showFocus = isFocused && (isTvDevice || inputModeManager.inputMode == InputMode.Keyboard)

    // Play subtle audio sound when D-pad lands on this item on TV
    LaunchedEffect(showFocus) {
        if (showFocus) {
            SoundFxHelper.playHoverBoop()
        }
    }

    val scale by animateFloatAsState(
        targetValue = if (showFocus) focusedScale else 1.0f,
        animationSpec = tween(150),
        label = "tv_card_scale"
    )

    Card(
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = if (showFocus) Color.White else backgroundColor
        ),
        border = BorderStroke(
            width = if (showFocus) 4.dp else 1.5.dp,
            color = if (showFocus) focusedBorderColor else unfocusedBorderColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (showFocus) elevation + 12.dp else elevation
        ),
        modifier = modifier
            .testTag(testTag)
            .zIndex(if (showFocus) 20f else 1f)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                shadowElevation = if (showFocus) 28f else 4f
                spotShadowColor = if (showFocus) focusedBorderColor else Color.Black.copy(alpha = 0.1f)
                ambientShadowColor = if (showFocus) focusedBorderColor.copy(alpha = 0.45f) else Color.Black.copy(alpha = 0.05f)
            }
            .onKeyEvent { keyEvent ->
                if (keyEvent.nativeKeyEvent.action == KeyEvent.ACTION_DOWN &&
                    (keyEvent.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_DPAD_CENTER ||
                     keyEvent.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER ||
                     keyEvent.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER)
                ) {
                    SoundFxHelper.playPop()
                    onClick()
                    true
                } else {
                    false
                }
            }
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.material3.ripple(),
                onClick = {
                    SoundFxHelper.playPop()
                    onClick()
                }
            )
    ) {
        Box {
            content()
            if (showFocus) {
                // High-visibility focus indicator dot/glow in top corner on TV
                Surface(
                    shape = RoundedCornerShape(topStart = 0.dp, topEnd = 24.dp, bottomStart = 12.dp, bottomEnd = 0.dp),
                    color = focusedBorderColor,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(
                        text = "●",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

/**
 * TvShelfRow
 *
 * TV-optimized horizontal shelf wrapping androidx.tv.foundation.lazy.list.TvLazyRow
 * with smooth D-pad horizontal traversal, pivot offsets, and spacious margins.
 */
@Composable
fun <T> TvShelfRow(
    items: List<T>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(16.dp),
    itemContent: @Composable (T) -> Unit
) {
    val listState = rememberLazyListState()
    LazyRow(
        state = listState,
        modifier = modifier
            .fillMaxWidth()
            .focusGroup(),
        contentPadding = contentPadding,
        horizontalArrangement = horizontalArrangement
    ) {
        items(items) { item ->
            itemContent(item)
        }
    }
}
