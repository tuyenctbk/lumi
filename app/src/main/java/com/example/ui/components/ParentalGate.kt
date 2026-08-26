package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.audio.SoundFxHelper
import com.example.ui.theme.SleekCoral
import com.example.ui.theme.SleekEmerald
import com.example.ui.theme.SleekEmeraldDark
import com.example.ui.theme.SleekGold
import com.example.ui.theme.SleekGoldDark
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekSurfaceBorder
import com.example.ui.theme.SleekTextDark
import com.example.ui.theme.SleekTextMuted
import kotlin.random.Random

/**
 * ParentalGate
 *
 * A COPPA/GDPR-K compliant parental verification gate component. Requires a simple
 * math challenge (e.g. 5 + 7 = ?) to be solved before granting access to parent
 * dashboards, external links, or sensitive settings.
 */
@Composable
fun ParentalGate(
    onDismiss: () -> Unit,
    onSuccess: () -> Unit,
    title: String = "Grown-Ups Only",
    subtitle: String = "Please solve this quick math challenge to access settings and progress reports:"
) {
    // Generate fresh random numbers for the math challenge
    var challengeSeed by remember { mutableIntStateOf(0) }
    val num1 = remember(challengeSeed) { Random.nextInt(4, 9) }
    val num2 = remember(challengeSeed) { Random.nextInt(3, 8) }
    val correctAnswer = num1 + num2

    val options = remember(challengeSeed) {
        val wrong1 = correctAnswer + if (Random.nextBoolean()) 2 else -2
        val wrong2 = correctAnswer + if (Random.nextBoolean()) 1 else -1
        listOf(correctAnswer, wrong1.coerceAtLeast(1), wrong2.coerceAtLeast(1))
            .distinct()
            .let { list ->
                if (list.size < 3) list + (correctAnswer + 3) else list
            }
            .take(3)
            .shuffled()
    }

    var hasError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = SleekSurface),
            border = BorderStroke(1.5.dp, SleekSurfaceBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(10.dp)
                .testTag("parental_gate")
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Shield / Lock Badge
                Surface(
                    shape = CircleShape,
                    color = SleekGold.copy(alpha = 0.18f),
                    border = BorderStroke(1.5.dp, SleekGoldDark.copy(alpha = 0.4f)),
                    modifier = Modifier.size(50.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Parental Verification",
                            tint = SleekGoldDark,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = SleekTextDark
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = SleekTextMuted,
                    modifier = Modifier.padding(vertical = 6.dp)
                )

                // Math Equation Box (Single-line guaranteed, bold, high-contrast)
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFFFF9F2),
                    border = BorderStroke(2.dp, SleekSurfaceBorder),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "$num1 + $num2 = ?",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = SleekTextDark,
                            maxLines = 1,
                            softWrap = false,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                if (hasError) {
                    Text(
                        text = "Incorrect answer, please try again with a new question!",
                        color = SleekCoral,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // D-Pad Focusable Options Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    options.forEach { opt ->
                        FocusableCard(
                            onClick = {
                                if (opt == correctAnswer) {
                                    SoundFxHelper.playCorrectChime()
                                    onSuccess()
                                } else {
                                    SoundFxHelper.playWrongOops()
                                    hasError = true
                                    challengeSeed++ // Regenerate challenge
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(18.dp),
                            testTag = "parental_gate_option_$opt",
                            backgroundColor = SleekSurface,
                            unfocusedBorderColor = SleekSurfaceBorder,
                            focusedBorderColor = SleekEmerald,
                            focusedScale = 1.06f
                        ) {
                            Text(
                                text = "$opt",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = SleekTextDark,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Safety / Compliance Footnote (High contrast and clear)
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SleekEmerald.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, SleekEmerald.copy(alpha = 0.3f)),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Text(
                        text = "🔒 Safe Kids Protection • COPPA Compliant",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekEmeraldDark,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }

                FocusableCard(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = SleekSurface,
                    unfocusedBorderColor = SleekSurfaceBorder,
                    focusedBorderColor = SleekCoral,
                    focusedScale = 1.04f,
                    testTag = "parental_gate_cancel_button",
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Back to Learning",
                        color = SleekTextDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    )
                }
            }
        }
    }
}
