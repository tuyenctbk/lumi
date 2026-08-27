package com.example.ui.components

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.model.MascotMood
import com.example.ui.theme.SleekCoral
import com.example.ui.theme.SleekEmerald
import com.example.ui.theme.SleekOcean
import com.example.ui.theme.SleekTextDark
import com.example.ui.theme.SleekTextMuted
import com.example.util.SmartSuggestionType

@Composable
fun SmartEngagementDialog(
    suggestionType: SmartSuggestionType,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 12.dp
            ),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .widthIn(max = 480.dp)
                .padding(12.dp)
                .testTag("smart_engagement_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Large visual Lumi Mascot
                LumiMascot(
                    mood = when (suggestionType) {
                        SmartSuggestionType.RATE_APP -> MascotMood.SUPERSTAR
                        SmartSuggestionType.SHARE_APP -> MascotMood.HAPPY
                        SmartSuggestionType.UPDATE_APP -> MascotMood.THINKING
                    },
                    size = 130.dp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Dialog Header Text
                Text(
                    text = when (suggestionType) {
                        SmartSuggestionType.RATE_APP -> stringResource(R.string.dialog_rate_title)
                        SmartSuggestionType.SHARE_APP -> stringResource(R.string.dialog_share_title)
                        SmartSuggestionType.UPDATE_APP -> stringResource(R.string.dialog_update_title)
                    },
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = SleekTextDark,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Dialog Description Text
                Text(
                    text = when (suggestionType) {
                        SmartSuggestionType.RATE_APP -> stringResource(R.string.dialog_rate_desc)
                        SmartSuggestionType.SHARE_APP -> stringResource(R.string.dialog_share_desc)
                        SmartSuggestionType.UPDATE_APP -> stringResource(R.string.dialog_update_desc)
                    },
                    fontSize = 14.sp,
                    color = SleekTextMuted,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Action buttons row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FocusableCard(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(16.dp),
                        backgroundColor = Color(0xFFF1F5F9),
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = SleekOcean,
                        testTag = "engagement_dismiss_button"
                    ) {
                        Text(
                            text = stringResource(R.string.button_later),
                            color = SleekTextMuted,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    val confirmColor = when (suggestionType) {
                        SmartSuggestionType.RATE_APP -> SleekEmerald
                        SmartSuggestionType.SHARE_APP -> SleekOcean
                        SmartSuggestionType.UPDATE_APP -> SleekCoral
                    }

                    FocusableCard(
                        onClick = onConfirm,
                        shape = RoundedCornerShape(16.dp),
                        backgroundColor = confirmColor,
                        unfocusedBorderColor = confirmColor,
                        focusedBorderColor = Color(0xFFFFD54F),
                        testTag = "engagement_confirm_button"
                    ) {
                        Text(
                            text = when (suggestionType) {
                                SmartSuggestionType.RATE_APP -> stringResource(R.string.button_rate)
                                SmartSuggestionType.SHARE_APP -> stringResource(R.string.button_share)
                                SmartSuggestionType.UPDATE_APP -> stringResource(R.string.button_update)
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                        )
                    }
                }
            }
        }
    }
}
