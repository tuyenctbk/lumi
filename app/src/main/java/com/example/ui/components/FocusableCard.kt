package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.audio.SoundFxHelper
import com.example.ui.theme.SleekEmerald
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekSurfaceBorder

@Composable
fun FocusableCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "focusable_card",
    shape: Shape = RoundedCornerShape(26.dp),
    backgroundColor: Color = SleekSurface,
    focusedBorderColor: Color = SleekEmerald,
    focusedScale: Float = 1.10f,
    elevation: Dp = 4.dp,
    unfocusedBorderColor: Color = SleekSurfaceBorder,
    borderWidth: Dp = 1.5.dp,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    // Play subtle audio boop when TV D-pad focuses onto this card
    LaunchedEffect(isFocused) {
        if (isFocused) {
            SoundFxHelper.playHoverBoop()
        }
    }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) focusedScale else 1.0f,
        animationSpec = tween(180),
        label = "card_scale"
    )

    val currentBorder = if (isFocused) {
        BorderStroke(3.5.dp, focusedBorderColor)
    } else {
        BorderStroke(borderWidth, unfocusedBorderColor)
    }

    Card(
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = currentBorder,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isFocused) elevation + 8.dp else elevation
        ),
        modifier = modifier
            .testTag(testTag)
            .scale(scale)
            .focusable(interactionSource = interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.material3.ripple(),
                onClick = onClick
            )
    ) {
        content()
    }
}

