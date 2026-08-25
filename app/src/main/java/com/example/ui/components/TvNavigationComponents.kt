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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
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

/**
 * TvFocusableCard
 *
 * Dedicated Android TV D-Pad focusable card with high-contrast focus rings,
 * scale elevation, and sound feedback for lean-back TV experiences.
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
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    // Play subtle audio sound when D-pad lands on this item
    LaunchedEffect(isFocused) {
        if (isFocused) {
            SoundFxHelper.playHoverBoop()
        }
    }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) focusedScale else 1.0f,
        animationSpec = tween(150),
        label = "tv_card_scale"
    )

    Card(
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = if (isFocused) backgroundColor else backgroundColor
        ),
        border = BorderStroke(
            width = if (isFocused) 3.5.dp else 1.5.dp,
            color = if (isFocused) focusedBorderColor else unfocusedBorderColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isFocused) elevation + 8.dp else elevation
        ),
        modifier = modifier
            .testTag(testTag)
            .scale(scale)
            .focusable(interactionSource = interactionSource)
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
        content()
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
        modifier = modifier.fillMaxWidth(),
        contentPadding = contentPadding,
        horizontalArrangement = horizontalArrangement
    ) {
        items(items) { item ->
            itemContent(item)
        }
    }
}
