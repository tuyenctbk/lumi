package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.example.R
import com.example.model.MascotMood
import com.example.ui.theme.SleekCoral
import com.example.ui.theme.SleekEmerald
import com.example.ui.theme.SleekOcean

/**
 * Sealed class representing API Data Fetching & UI Rendering States
 */
sealed interface ScreenDataState<out T> {
    data class Loading(val message: String = "Fetching data...") : ScreenDataState<Nothing>
    data class Success<T>(val data: T) : ScreenDataState<T>
    data class Error(val message: String, val onRetry: (() -> Unit)? = null) : ScreenDataState<Nothing>
    data object Empty : ScreenDataState<Nothing>
}

/**
 * GlobalLoadingWrapper
 *
 * Reusable Compose state wrapper handling seamless transitions between API data fetching,
 * loading animations, error retries, and UI rendering.
 */
@Composable
fun <T> GlobalLoadingWrapper(
    state: ScreenDataState<T>,
    modifier: Modifier = Modifier,
    emptyMessage: String = "No learning content available right now.",
    content: @Composable (T) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("global_loading_wrapper")
    ) {
        AnimatedContent(
            targetState = state,
            transitionSpec = {
                (fadeIn() + scaleIn(initialScale = 0.96f)) togetherWith (fadeOut() + scaleOut(targetScale = 0.96f))
            },
            label = "screen_data_transition"
        ) { targetState ->
            when (targetState) {
                is ScreenDataState.Loading -> {
                    LoadingView(message = targetState.message)
                }
                is ScreenDataState.Success -> {
                    content(targetState.data)
                }
                is ScreenDataState.Error -> {
                    ErrorView(
                        message = targetState.message,
                        onRetry = targetState.onRetry
                    )
                }
                is ScreenDataState.Empty -> {
                    EmptyView(message = emptyMessage)
                }
            }
        }
    }
}

/**
 * Simplified boolean overload for quick screen state wrapping
 */
@Composable
fun GlobalLoadingWrapper(
    isLoading: Boolean,
    errorMessage: String? = null,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    loadingMessage: String = "Loading curriculum content...",
    content: @Composable () -> Unit
) {
    val state: ScreenDataState<Unit> = when {
        isLoading -> ScreenDataState.Loading(loadingMessage)
        errorMessage != null -> ScreenDataState.Error(errorMessage, onRetry)
        else -> ScreenDataState.Success(Unit)
    }

    GlobalLoadingWrapper(
        state = state,
        modifier = modifier
    ) {
        content()
    }
}

@Composable
private fun LoadingView(message: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .testTag("global_loading_view")
    ) {
        ConsistentLoadingIndicator(
            message = message,
            subMessage = "Curating vocabulary & interactive exercises"
        )
    }
}

@Composable
private fun ErrorView(
    message: String,
    onRetry: (() -> Unit)?
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .testTag("global_error_view")
    ) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.5.dp, SleekCoral),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = CircleShape,
                    color = SleekCoral.copy(alpha = 0.15f),
                    modifier = Modifier.size(64.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Error",
                            tint = SleekCoral,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Connection Issue",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )

                if (onRetry != null) {
                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onRetry,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SleekOcean),
                        modifier = Modifier.testTag("global_error_retry_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Retry",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.lumi_reaction_try_again), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyView(message: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .testTag("global_empty_view")
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LumiMascot(
                mood = MascotMood.IDLE,
                speechBubble = "Nothing here yet!",
                size = 100.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
