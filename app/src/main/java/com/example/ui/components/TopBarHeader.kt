package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TargetLanguage
import com.example.ui.theme.SleekCoral
import com.example.ui.theme.SleekEmerald
import com.example.ui.theme.SleekGold
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekSurfaceBorder
import com.example.ui.theme.SleekTextDark
import com.example.ui.theme.SleekTextSubtle

@Composable
fun TopBarHeader(
    targetLanguage: TargetLanguage,
    points: Int,
    streakDays: Int,
    onOpenLanguagePicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 14.dp, end = 14.dp, top = 2.dp, bottom = 10.dp)
    ) {
        val isCompact = maxWidth < 600.dp

        if (isCompact) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FocusableCard(
                    onClick = onOpenLanguagePicker,
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = SleekSurface,
                    unfocusedBorderColor = SleekSurfaceBorder,
                    focusedBorderColor = SleekEmerald,
                    testTag = "language_picker_button"
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = targetLanguage.flagEmoji, fontSize = 16.sp)
                        Text(
                            text = targetLanguage.displayName.substringBefore(" "),
                            fontWeight = FontWeight.Bold,
                            color = SleekTextDark,
                            fontSize = 13.sp
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = SleekSurface,
                        border = BorderStroke(1.dp, SleekSurfaceBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Whatshot,
                                contentDescription = "Streak",
                                tint = SleekCoral,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "$streakDays d",
                                fontWeight = FontWeight.Bold,
                                color = SleekTextDark,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = SleekSurface,
                        border = BorderStroke(1.dp, SleekSurfaceBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Stars",
                                tint = SleekGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "$points",
                                fontWeight = FontWeight.Bold,
                                color = SleekTextDark,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FocusableCard(
                    onClick = onOpenLanguagePicker,
                    shape = RoundedCornerShape(20.dp),
                    backgroundColor = SleekSurface,
                    unfocusedBorderColor = SleekSurfaceBorder,
                    focusedBorderColor = SleekEmerald,
                    testTag = "language_picker_button"
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = targetLanguage.flagEmoji, fontSize = 20.sp)
                        Column {
                            Text(
                                text = targetLanguage.displayName.substringBefore(" "),
                                fontWeight = FontWeight.Bold,
                                color = SleekTextDark,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "LANGUAGE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = SleekTextSubtle,
                                letterSpacing = 0.8.sp
                            )
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = SleekSurface,
                        border = BorderStroke(1.5.dp, SleekSurfaceBorder),
                        shadowElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Whatshot,
                                contentDescription = "Streak",
                                tint = SleekCoral,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "$streakDays Days",
                                fontWeight = FontWeight.Bold,
                                color = SleekTextDark,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = SleekSurface,
                        border = BorderStroke(1.5.dp, SleekSurfaceBorder),
                        shadowElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
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
                                text = "$points",
                                fontWeight = FontWeight.Bold,
                                color = SleekTextDark,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
