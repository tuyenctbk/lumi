package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SleekEmerald
import com.example.ui.theme.SleekGold
import com.example.ui.theme.SleekGoldDark
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekSurfaceBorder
import com.example.ui.theme.SleekTextDark

@Composable
fun GameOverView(
    correctCount: Int,
    totalRounds: Int,
    onPlayAgain: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = CircleShape,
            color = SleekGold.copy(alpha = 0.2f),
            border = BorderStroke(2.dp, SleekGoldDark),
            modifier = Modifier.size(120.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Star",
                    tint = SleekGold,
                    modifier = Modifier.size(72.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Show Complete!",
            fontSize = 36.sp,
            fontWeight = FontWeight.Black,
            color = SleekTextDark
        )

        Text(
            text = "You scored $correctCount out of $totalRounds stars!",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = SleekEmerald,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            FocusableCard(
                onClick = onPlayAgain,
                shape = RoundedCornerShape(20.dp),
                backgroundColor = SleekGold,
                unfocusedBorderColor = SleekGoldDark,
                focusedBorderColor = SleekEmerald,
                testTag = "play_again_button"
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Replay,
                        contentDescription = null,
                        tint = SleekTextDark
                    )
                    Text(
                        text = "Play Again",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekTextDark
                    )
                }
            }

            FocusableCard(
                onClick = onBack,
                shape = RoundedCornerShape(20.dp),
                backgroundColor = SleekSurface,
                unfocusedBorderColor = SleekSurfaceBorder,
                focusedBorderColor = SleekEmerald,
                testTag = "finish_game_button"
            ) {
                Text(
                    text = "Back to Islands",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = SleekTextDark,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 14.dp)
                )
            }
        }
    }
}
