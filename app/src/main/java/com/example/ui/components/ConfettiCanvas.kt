package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

data class Particle(
    val x: Float,
    val y: Float,
    val speedX: Float,
    val speedY: Float,
    val color: Color,
    val size: Float,
    val isCircle: Boolean
)

@Composable
fun ConfettiCanvas(
    trigger: Boolean,
    modifier: Modifier = Modifier
) {
    if (!trigger) return

    val progress = remember { Animatable(0f) }
    val particles = remember {
        val colors = listOf(
            Color(0xFFFFC107),
            Color(0xFFFF4081),
            Color(0xFF00E676),
            Color(0xFF00E5FF),
            Color(0xFFAA00FF),
            Color(0xFFFF9100)
        )
        List(70) {
            Particle(
                x = Random.nextFloat() * 1000f,
                y = Random.nextFloat() * -300f,
                speedX = (Random.nextFloat() - 0.5f) * 600f,
                speedY = Random.nextFloat() * 800f + 600f,
                color = colors.random(),
                size = Random.nextFloat() * 18f + 12f,
                isCircle = Random.nextBoolean()
            )
        }
    }

    LaunchedEffect(trigger) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2200, easing = LinearEasing)
        )
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val t = progress.value
        particles.forEach { p ->
            val curX = (p.x + p.speedX * t) % size.width
            val curY = p.y + p.speedY * t
            if (curY in 0f..size.height) {
                val alpha = (1f - t).coerceIn(0f, 1f)
                if (p.isCircle) {
                    drawCircle(
                        color = p.color.copy(alpha = alpha),
                        radius = p.size / 2f,
                        center = Offset(curX, curY)
                    )
                } else {
                    drawRect(
                        color = p.color.copy(alpha = alpha),
                        topLeft = Offset(curX, curY),
                        size = Size(p.size, p.size * 0.6f)
                    )
                }
            }
        }
    }
}
