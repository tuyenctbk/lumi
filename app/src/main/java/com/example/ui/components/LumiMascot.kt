package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.model.MascotMood
import com.example.ui.theme.SleekGold
import com.example.ui.theme.SleekGoldDark
import com.example.ui.theme.SleekSurfaceBorder
import kotlin.math.cos
import kotlin.math.sin

/**
 * Lumi Lottie Animation Specifications
 * Lightweight vector animation definitions for idle, happy, and thinking states.
 */
private val LOTTIE_IDLE_JSON = """
{
  "v": "5.5.7",
  "fr": 60,
  "ip": 0,
  "op": 120,
  "w": 100,
  "h": 100,
  "nm": "lumi_idle",
  "ddd": 0,
  "assets": [],
  "layers": [
    {
      "ddd": 0,
      "ind": 1,
      "ty": 4,
      "nm": "star_glow",
      "sr": 1,
      "ks": {
        "o": {"a": 1, "k": [{"t": 0, "s": [50]}, {"t": 60, "s": [90]}, {"t": 120, "s": [50]}]},
        "r": {"a": 1, "k": [{"t": 0, "s": [0]}, {"t": 120, "s": [360]}]},
        "p": {"a": 0, "k": [50, 50, 0]},
        "a": {"a": 0, "k": [0, 0, 0]},
        "s": {"a": 1, "k": [{"t": 0, "s": [95, 95, 100]}, {"t": 60, "s": [108, 108, 100]}, {"t": 120, "s": [95, 95, 100]}]}
      },
      "ao": 0,
      "shapes": [
        {
          "ty": "gr",
          "it": [
            {
              "ty": "el",
              "p": {"a": 0, "k": [0, 0]},
              "s": {"a": 0, "k": [80, 80]}
            },
            {
              "ty": "fl",
              "c": {"a": 0, "k": [1, 0.82, 0.16, 0.4]},
              "o": {"a": 0, "k": 100}
            },
            {
              "ty": "tr",
              "p": {"a": 0, "k": [0, 0]},
              "a": {"a": 0, "k": [0, 0]},
              "s": {"a": 0, "k": [100, 100]},
              "r": {"a": 0, "k": 0},
              "o": {"a": 0, "k": 100}
            }
          ]
        }
      ],
      "ip": 0,
      "op": 120,
      "st": 0,
      "bm": 0
    }
  ]
}
""".trimIndent()

private val LOTTIE_HAPPY_JSON = """
{
  "v": "5.5.7",
  "fr": 60,
  "ip": 0,
  "op": 60,
  "w": 100,
  "h": 100,
  "nm": "lumi_happy",
  "ddd": 0,
  "assets": [],
  "layers": [
    {
      "ddd": 0,
      "ind": 1,
      "ty": 4,
      "nm": "happy_burst",
      "sr": 1,
      "ks": {
        "o": {"a": 1, "k": [{"t": 0, "s": [80]}, {"t": 30, "s": [100]}, {"t": 60, "s": [80]}]},
        "r": {"a": 1, "k": [{"t": 0, "s": [-15]}, {"t": 30, "s": [15]}, {"t": 60, "s": [-15]}]},
        "p": {"a": 1, "k": [{"t": 0, "s": [50, 55, 0]}, {"t": 30, "s": [50, 42, 0]}, {"t": 60, "s": [50, 55, 0]}]},
        "a": {"a": 0, "k": [0, 0, 0]},
        "s": {"a": 1, "k": [{"t": 0, "s": [100, 100, 100]}, {"t": 30, "s": [120, 120, 100]}, {"t": 60, "s": [100, 100, 100]}]}
      },
      "ao": 0,
      "shapes": [
        {
          "ty": "sr",
          "sy": 1,
          "pt": {"a": 0, "k": 5},
          "p": {"a": 0, "k": [0, 0]},
          "r": {"a": 0, "k": 0},
          "or": {"a": 0, "k": 38},
          "os": {"a": 0, "k": 0},
          "ir": {"a": 0, "k": 18},
          "is": {"a": 0, "k": 0}
        },
        {
          "ty": "fl",
          "c": {"a": 0, "k": [1, 0.75, 0.05, 0.5]},
          "o": {"a": 0, "k": 100}
        },
        {
          "ty": "tr",
          "p": {"a": 0, "k": [0, 0]},
          "a": {"a": 0, "k": [0, 0]},
          "s": {"a": 0, "k": [100, 100]},
          "r": {"a": 0, "k": 0},
          "o": {"a": 0, "k": 100}
        }
      ]
    }
  ],
  "ip": 0,
  "op": 60,
  "st": 0,
  "bm": 0
}
""".trimIndent()

private val LOTTIE_THINKING_JSON = """
{
  "v": "5.5.7",
  "fr": 60,
  "ip": 0,
  "op": 90,
  "w": 100,
  "h": 100,
  "nm": "lumi_thinking",
  "ddd": 0,
  "assets": [],
  "layers": [
    {
      "ddd": 0,
      "ind": 1,
      "ty": 4,
      "nm": "thinking_spin",
      "sr": 1,
      "ks": {
        "o": {"a": 1, "k": [{"t": 0, "s": [70]}, {"t": 45, "s": [95]}, {"t": 90, "s": [70]}]},
        "r": {"a": 1, "k": [{"t": 0, "s": [-20]}, {"t": 45, "s": [20]}, {"t": 90, "s": [-20]}]},
        "p": {"a": 0, "k": [50, 50, 0]},
        "a": {"a": 0, "k": [0, 0, 0]},
        "s": {"a": 1, "k": [{"t": 0, "s": [95, 95, 100]}, {"t": 45, "s": [105, 105, 100]}, {"t": 90, "s": [95, 95, 100]}]}
      },
      "ao": 0,
      "shapes": [
        {
          "ty": "gr",
          "it": [
            {
              "ty": "el",
              "p": {"a": 0, "k": [0, 0]},
              "s": {"a": 0, "k": [75, 75]}
            },
            {
              "ty": "st",
              "c": {"a": 0, "k": [0.51, 0.22, 0.93, 0.6]},
              "o": {"a": 0, "k": 100},
              "w": {"a": 0, "k": 6}
            },
            {
              "ty": "tr",
              "p": {"a": 0, "k": [0, 0]},
              "a": {"a": 0, "k": [0, 0]},
              "s": {"a": 0, "k": [100, 100]},
              "r": {"a": 0, "k": 0},
              "o": {"a": 0, "k": 100}
            }
          ]
        }
      ],
      "ip": 0,
      "op": 90,
      "st": 0,
      "bm": 0
    }
  ]
}
""".trimIndent()

private val LOTTIE_CELEBRATING_JSON = """
{
  "v": "5.5.7",
  "fr": 60,
  "ip": 0,
  "op": 60,
  "w": 100,
  "h": 100,
  "nm": "lumi_celebrating",
  "ddd": 0,
  "assets": [],
  "layers": [
    {
      "ddd": 0,
      "ind": 1,
      "ty": 4,
      "nm": "celebrating_burst",
      "sr": 1,
      "ks": {
        "o": {"a": 1, "k": [{"t": 0, "s": [85]}, {"t": 30, "s": [100]}, {"t": 60, "s": [85]}]},
        "r": {"a": 1, "k": [{"t": 0, "s": [-25]}, {"t": 30, "s": [25]}, {"t": 60, "s": [-25]}]},
        "p": {"a": 1, "k": [{"t": 0, "s": [50, 50, 0]}, {"t": 30, "s": [35, 35, 0]}, {"t": 60, "s": [50, 50, 0]}]},
        "a": {"a": 0, "k": [0, 0, 0]},
        "s": {"a": 1, "k": [{"t": 0, "s": [100, 100, 100]}, {"t": 30, "s": [130, 130, 100]}, {"t": 60, "s": [100, 100, 100]}]}
      },
      "ao": 0,
      "shapes": [
        {
          "ty": "sr",
          "sy": 1,
          "pt": {"a": 0, "k": 5},
          "p": {"a": 0, "k": [0, 0]},
          "r": {"a": 0, "k": 0},
          "or": {"a": 0, "k": 44},
          "os": {"a": 0, "k": 0},
          "ir": {"a": 0, "k": 22},
          "is": {"a": 0, "k": 0}
        },
        {
          "ty": "fl",
          "c": {"a": 0, "k": [1, 0.84, 0.0, 0.6]},
          "o": {"a": 0, "k": 100}
        }
      ]
    }
  ]
}
""".trimIndent()

private val LOTTIE_ENCOURAGING_JSON = """
{
  "v": "5.5.7",
  "fr": 60,
  "ip": 0,
  "op": 90,
  "w": 100,
  "h": 100,
  "nm": "lumi_encouraging",
  "ddd": 0,
  "assets": [],
  "layers": [
    {
      "ddd": 0,
      "ind": 1,
      "ty": 4,
      "nm": "encouraging_pulse",
      "sr": 1,
      "ks": {
        "o": {"a": 1, "k": [{"t": 0, "s": [60]}, {"t": 45, "s": [90]}, {"t": 90, "s": [60]}]},
        "r": {"a": 1, "k": [{"t": 0, "s": [-5]}, {"t": 45, "s": [5]}, {"t": 90, "s": [-5]}]},
        "p": {"a": 0, "k": [50, 50, 0]},
        "a": {"a": 0, "k": [0, 0, 0]},
        "s": {"a": 1, "k": [{"t": 0, "s": [98, 98, 100]}, {"t": 45, "s": [108, 108, 100]}, {"t": 90, "s": [98, 98, 100]}]}
      },
      "ao": 0,
      "shapes": [
        {
          "ty": "gr",
          "it": [
            {
              "ty": "el",
              "p": {"a": 0, "k": [0, 0]},
              "s": {"a": 0, "k": [82, 82]}
            },
            {
              "ty": "fl",
              "c": {"a": 0, "k": [0.38, 0.65, 0.98, 0.35]},
              "o": {"a": 0, "k": 100}
            }
          ]
        }
      ]
    }
  ]
}
""".trimIndent()

/**
 * LumiMascot
 *
 * An animated Mascot component that renders Lottie animations with state-based
 * triggers to seamlessly switch between idle, happy, thinking, superstar, celebrating, and encouraging states.
 */
@Composable
fun LumiMascot(
    mood: MascotMood,
    modifier: Modifier = Modifier,
    isSpeaking: Boolean = false,
    thoughtBubbleEmoji: String? = null,
    speechBubble: String? = null,
    size: Dp = 140.dp,
    onClick: () -> Unit = {}
) {
    val effectiveThoughtBubble = speechBubble ?: thoughtBubbleEmoji
    // Select appropriate Lottie composition based on mascot mood
    val selectedLottieJson = when (mood) {
        MascotMood.CELEBRATING, MascotMood.SUPERSTAR -> LOTTIE_CELEBRATING_JSON
        MascotMood.HAPPY -> LOTTIE_HAPPY_JSON
        MascotMood.ENCOURAGING -> LOTTIE_ENCOURAGING_JSON
        MascotMood.THINKING -> LOTTIE_THINKING_JSON
        else -> LOTTIE_IDLE_JSON
    }

    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.JsonString(selectedLottieJson)
    )

    val lottieProgress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true
    )

    val infiniteTransition = rememberInfiniteTransition(label = "lumi_mascot_anim")

    // State 1: IDLE Animation - Gentle breathing, pulsing aura & floating
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "idle_float"
    )

    val auraPulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_pulse"
    )

    // State 2: HAPPY Animation - Joyful bounce & excited wobble
    val happyBounce by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -16f,
        animationSpec = infiniteRepeatable(
            animation = tween(380, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "happy_bounce"
    )

    // State 3: THINKING Animation - Curious tilt & slow rotation
    val thinkingTilt by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "thinking_tilt"
    )

    // Talking mouth wobble
    val talkWobble by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(120, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "talk_wobble"
    )

    // Eye blinking trigger
    val blinkProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "blink_cycle"
    )

    // Dynamic rotation & scale according to state trigger
    val activeRotation = when (mood) {
        MascotMood.THINKING -> thinkingTilt
        MascotMood.HAPPY -> sin(happyBounce * 0.2f) * 8f
        else -> 0f
    }

    val activeYOffset = when (mood) {
        MascotMood.HAPPY -> happyBounce
        else -> floatOffset
    }

    val currentScale = when (mood) {
        MascotMood.HAPPY -> 1.20f
        MascotMood.SUPERSTAR -> 1.25f
        MascotMood.THINKING -> 1.08f
        MascotMood.ENCOURAGING -> 1.05f
        else -> 1.0f
    }

    // Effective thought/speech bubble emoji based on state
    val effectiveBubble = effectiveThoughtBubble ?: when (mood) {
        MascotMood.THINKING -> "🤔"
        MascotMood.HAPPY -> "✨"
        MascotMood.SUPERSTAR -> "⭐"
        else -> null
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    LaunchedEffect(isFocused) {
        if (isFocused) {
            com.example.audio.SoundFxHelper.playHoverBoop()
        }
    }

    val focusScale by animateFloatAsState(
        targetValue = if (isFocused) 1.12f else 1.0f,
        animationSpec = tween(150),
        label = "lumi_mascot_focus_scale"
    )

    val isFullTextSpeech = !speechBubble.isNullOrBlank()

    androidx.compose.foundation.layout.Column(
        modifier = modifier
            .testTag("lumi_mascot")
            .scale(focusScale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Speech Bubble Banner (Above Mascot)
        if (isFullTextSpeech && speechBubble != null) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.5.dp, SleekGold),
                shadowElevation = 8.dp,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = speechBubble,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E1B4B),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }
        }

        Box(
            modifier = Modifier.size(size + 20.dp),
            contentAlignment = Alignment.Center
        ) {
            // Lottie Background Particle Aura
            if (composition != null) {
                LottieAnimation(
                    composition = composition,
                    progress = { lottieProgress },
                    modifier = Modifier
                        .size(size * 1.35f)
                        .align(Alignment.Center)
                )
            }

            // Emoji Badge (Top corner of mascot head)
            if (!isFullTextSpeech && !effectiveBubble.isNullOrBlank()) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, SleekSurfaceBorder),
                    shadowElevation = 6.dp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 10.dp, y = (-4).dp)
                        .padding(2.dp)
                ) {
                    Text(
                        text = effectiveBubble,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                    )
                }
            }

            // Animated Character Art Canvas (Rendered at 60fps)
            Canvas(
                modifier = Modifier
                    .size(size)
                    .offset(y = activeYOffset.dp)
                    .rotate(activeRotation)
                    .scale(currentScale)
            ) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val radius = this.size.width * 0.38f

            // Outer Soft Aura / Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        SleekGold.copy(alpha = 0.5f),
                        SleekGoldDark.copy(alpha = 0.2f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = radius * 1.5f * auraPulse
                ),
                radius = radius * 1.5f * auraPulse,
                center = center
            )

            // Main Body Star/Blob
            drawLumiBody(center, radius, mood, isSpeaking, talkWobble)

            // Crown / Trophy for Superstar state
            if (mood == MascotMood.SUPERSTAR) {
                drawCrown(center, radius)
            }

            // Rosy Cheeks
            val cheekColor = Color(0xFFFF8A80)
            drawCircle(
                color = cheekColor,
                radius = radius * 0.18f,
                center = Offset(center.x - radius * 0.55f, center.y + radius * 0.22f)
            )
            drawCircle(
                color = cheekColor,
                radius = radius * 0.18f,
                center = Offset(center.x + radius * 0.55f, center.y + radius * 0.22f)
            )

            // Expressive Eyes (Idle, Happy, Thinking, Superstar)
            drawEyes(center, radius, mood, blinkProgress)

            // Mouth (Idle Smile, Talking, Thinking)
            drawMouth(center, radius, mood, isSpeaking)
        }
    }
}
}

private fun DrawScope.drawLumiBody(
    center: Offset,
    radius: Float,
    mood: MascotMood,
    isSpeaking: Boolean,
    talkWobble: Float
) {
    val bodyColor = Color(0xFFFFCA28)
    val bodyLight = Color(0xFFFFF176)

    // Soft rounded organic star-blob shape
    val path = Path().apply {
        val points = 8
        val innerRadius = radius * 0.85f
        val outerRadius = radius * 1.08f
        val angleStep = (Math.PI * 2 / points).toFloat()
        val rotationOffset = if (isSpeaking) talkWobble * 0.05f else 0f

        for (i in 0 until points) {
            val r = if (i % 2 == 0) outerRadius else innerRadius
            val angle = i * angleStep + rotationOffset
            val x = center.x + cos(angle) * r
            val y = center.y + sin(angle) * r
            if (i == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }

    drawPath(
        path = path,
        brush = Brush.radialGradient(
            colors = listOf(bodyLight, bodyColor, Color(0xFFFFB300)),
            center = Offset(center.x - radius * 0.2f, center.y - radius * 0.2f),
            radius = radius * 1.2f
        )
    )

    // Top Sparkle Antenna
    val topX = center.x
    val topY = center.y - radius * 1.12f
    drawCircle(
        color = Color(0xFFFFF9C4),
        radius = radius * 0.15f,
        center = Offset(topX, topY)
    )
}

private fun DrawScope.drawEyes(
    center: Offset,
    radius: Float,
    mood: MascotMood,
    blinkProgress: Float
) {
    val eyeColor = Color(0xFF1E1B4B)
    val isBlinking = blinkProgress in 0.94f..0.98f

    val leftEyeCenter = Offset(center.x - radius * 0.32f, center.y - radius * 0.05f)
    val rightEyeCenter = Offset(center.x + radius * 0.32f, center.y - radius * 0.05f)

    if (mood == MascotMood.HAPPY || mood == MascotMood.SUPERSTAR) {
        // Joyful Arch Eyes "^ ^"
        drawArc(
            color = eyeColor,
            startAngle = 190f,
            sweepAngle = 160f,
            useCenter = false,
            topLeft = Offset(leftEyeCenter.x - radius * 0.18f, leftEyeCenter.y - radius * 0.15f),
            size = Size(radius * 0.36f, radius * 0.3f),
            style = Stroke(width = 6f)
        )
        drawArc(
            color = eyeColor,
            startAngle = 190f,
            sweepAngle = 160f,
            useCenter = false,
            topLeft = Offset(rightEyeCenter.x - radius * 0.18f, rightEyeCenter.y - radius * 0.15f),
            size = Size(radius * 0.36f, radius * 0.3f),
            style = Stroke(width = 6f)
        )
    } else if (mood == MascotMood.THINKING) {
        // Thinking Eyes (Left looking up-right, Right squinting inquisitive)
        val eyeRadius = radius * 0.17f
        drawCircle(
            color = eyeColor,
            radius = eyeRadius,
            center = Offset(leftEyeCenter.x, leftEyeCenter.y - radius * 0.06f)
        )
        drawCircle(
            color = Color.White,
            radius = eyeRadius * 0.45f,
            center = Offset(leftEyeCenter.x + eyeRadius * 0.3f, leftEyeCenter.y - radius * 0.06f - eyeRadius * 0.3f)
        )

        drawArc(
            color = eyeColor,
            startAngle = 200f,
            sweepAngle = 140f,
            useCenter = false,
            topLeft = Offset(rightEyeCenter.x - radius * 0.16f, rightEyeCenter.y - radius * 0.12f),
            size = Size(radius * 0.32f, radius * 0.24f),
            style = Stroke(width = 5.5f)
        )
    } else if (isBlinking) {
        // Blink lines
        drawLine(
            color = eyeColor,
            start = Offset(leftEyeCenter.x - radius * 0.15f, leftEyeCenter.y),
            end = Offset(leftEyeCenter.x + radius * 0.15f, leftEyeCenter.y),
            strokeWidth = 5f
        )
        drawLine(
            color = eyeColor,
            start = Offset(rightEyeCenter.x - radius * 0.15f, rightEyeCenter.y),
            end = Offset(rightEyeCenter.x + radius * 0.15f, rightEyeCenter.y),
            strokeWidth = 5f
        )
    } else {
        // Big round sparkling eyes
        val eyeRadius = radius * 0.18f
        drawCircle(color = eyeColor, radius = eyeRadius, center = leftEyeCenter)
        drawCircle(color = eyeColor, radius = eyeRadius, center = rightEyeCenter)

        // Eye highlights (sparkles)
        drawCircle(
            color = Color.White,
            radius = eyeRadius * 0.42f,
            center = Offset(leftEyeCenter.x - eyeRadius * 0.3f, leftEyeCenter.y - eyeRadius * 0.3f)
        )
        drawCircle(
            color = Color.White,
            radius = eyeRadius * 0.2f,
            center = Offset(leftEyeCenter.x + eyeRadius * 0.35f, leftEyeCenter.y + eyeRadius * 0.25f)
        )

        drawCircle(
            color = Color.White,
            radius = eyeRadius * 0.42f,
            center = Offset(rightEyeCenter.x - eyeRadius * 0.3f, rightEyeCenter.y - eyeRadius * 0.3f)
        )
        drawCircle(
            color = Color.White,
            radius = eyeRadius * 0.2f,
            center = Offset(rightEyeCenter.x + eyeRadius * 0.35f, rightEyeCenter.y + eyeRadius * 0.25f)
        )
    }
}

private fun DrawScope.drawMouth(
    center: Offset,
    radius: Float,
    mood: MascotMood,
    isSpeaking: Boolean
) {
    val mouthColor = Color(0xFF1E1B4B)
    val mouthY = center.y + radius * 0.26f

    if (isSpeaking) {
        // Open singing/talking mouth
        drawOval(
            color = Color(0xFFE53935),
            topLeft = Offset(center.x - radius * 0.16f, mouthY - radius * 0.08f),
            size = Size(radius * 0.32f, radius * 0.24f)
        )
    } else if (mood == MascotMood.THINKING) {
        // Cute sideways curious mouth "~"
        drawArc(
            color = mouthColor,
            startAngle = 10f,
            sweepAngle = 120f,
            useCenter = false,
            topLeft = Offset(center.x - radius * 0.10f, mouthY - radius * 0.08f),
            size = Size(radius * 0.24f, radius * 0.18f),
            style = Stroke(width = 5f)
        )
    } else if (mood == MascotMood.ENCOURAGING) {
        // Gentle "o" encouraging mouth
        drawCircle(
            color = mouthColor,
            radius = radius * 0.08f,
            center = Offset(center.x, mouthY)
        )
    } else {
        // Cute upward curve smile
        drawArc(
            color = mouthColor,
            startAngle = 20f,
            sweepAngle = 140f,
            useCenter = false,
            topLeft = Offset(center.x - radius * 0.16f, mouthY - radius * 0.12f),
            size = Size(radius * 0.32f, radius * 0.22f),
            style = Stroke(width = 5.5f)
        )
    }
}

private fun DrawScope.drawCrown(center: Offset, radius: Float) {
    val crownPath = Path().apply {
        val y = center.y - radius * 0.9f
        val w = radius * 0.6f
        moveTo(center.x - w, y)
        lineTo(center.x - w * 0.7f, y - radius * 0.35f)
        lineTo(center.x - w * 0.3f, y - radius * 0.15f)
        lineTo(center.x, y - radius * 0.45f)
        lineTo(center.x + w * 0.3f, y - radius * 0.15f)
        lineTo(center.x + w * 0.7f, y - radius * 0.35f)
        lineTo(center.x + w, y)
        close()
    }
    drawPath(path = crownPath, color = Color(0xFFFFD700), style = Fill)
    drawPath(path = crownPath, color = Color(0xFFFFA000), style = Stroke(width = 3f))
}
