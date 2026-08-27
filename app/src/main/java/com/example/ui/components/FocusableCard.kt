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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.audio.SoundFxHelper
import com.example.ui.theme.SleekEmerald
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekSurfaceBorder

import androidx.compose.ui.input.InputMode
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInputModeManager

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

    // Play subtle audio boop when TV D-pad focuses onto this card
    LaunchedEffect(showFocus) {
        if (showFocus) {
            SoundFxHelper.playHoverBoop()
        }
    }

    val scale by animateFloatAsState(
        targetValue = if (showFocus) focusedScale else 1.0f,
        animationSpec = tween(180),
        label = "card_scale"
    )

    val currentBorder = if (showFocus) {
        BorderStroke(4.dp, focusedBorderColor)
    } else {
        BorderStroke(borderWidth, unfocusedBorderColor)
    }

    Card(
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = if (showFocus) Color.White else backgroundColor),
        border = currentBorder,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (showFocus) elevation + 12.dp else elevation
        ),
        modifier = modifier
            .testTag(testTag)
            .zIndex(if (showFocus) 20f else 1f)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                shadowElevation = if (showFocus) 24f else 4f
                spotShadowColor = if (showFocus) focusedBorderColor else Color.Black.copy(alpha = 0.1f)
                ambientShadowColor = if (showFocus) focusedBorderColor.copy(alpha = 0.45f) else Color.Black.copy(alpha = 0.05f)
            }
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.material3.ripple(),
                onClick = onClick
            )
    ) {
        content()
    }
}

