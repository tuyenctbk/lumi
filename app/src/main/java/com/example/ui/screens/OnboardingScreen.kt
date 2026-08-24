package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.R
import com.example.model.MascotMood
import com.example.ui.components.LumiMascot
import com.example.ui.theme.SleekBackground
import com.example.ui.theme.SleekCoral
import com.example.ui.theme.SleekEmerald
import com.example.ui.theme.SleekGold
import com.example.ui.theme.SleekOcean
import com.example.ui.theme.SleekTextDark
import com.example.ui.theme.SleekTextMuted
import com.example.util.SmartEngagementManager

@Composable
fun OnboardingScreen(
    onOnboardingFinished: () -> Unit
) {
    val context = LocalContext.current
    val engagementManager = remember { SmartEngagementManager(context) }
    var currentPage by remember { mutableStateOf(0) }

    // Permission States
    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    // Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasMicPermission = permissions[Manifest.permission.RECORD_AUDIO] ?: hasMicPermission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            hasNotificationPermission = permissions[Manifest.permission.POST_NOTIFICATIONS] ?: hasNotificationPermission
        }
    }

    val totalPages = 3

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFFDFC),
                        SleekBackground
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("onboarding_screen")
    ) {
        // Skip Button (Top Right)
        if (currentPage < totalPages - 1) {
            TextButton(
                onClick = {
                    engagementManager.isOnboardingCompleted = true
                    onOnboardingFinished()
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .testTag("onboarding_skip_button")
            ) {
                Text(
                    text = stringResource(R.string.button_skip),
                    color = SleekTextMuted,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Main Illustration Card with Lumi Mascot
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    when (currentPage) {
                        0 -> {
                            LumiMascot(
                                mood = MascotMood.HAPPY,
                                size = 180.dp,
                                modifier = Modifier.padding(16.dp)
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = stringResource(R.string.onboarding_title_welcome),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = SleekTextDark,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = stringResource(R.string.onboarding_desc_welcome),
                                fontSize = 16.sp,
                                color = SleekTextMuted,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                        1 -> {
                            LumiMascot(
                                mood = MascotMood.THINKING,
                                size = 180.dp,
                                modifier = Modifier.padding(16.dp)
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = stringResource(R.string.onboarding_title_games),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = SleekTextDark,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = stringResource(R.string.onboarding_desc_games),
                                fontSize = 16.sp,
                                color = SleekTextMuted,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                        2 -> {
                            LumiMascot(
                                mood = MascotMood.HAPPY,
                                speechBubble = if (hasMicPermission && hasNotificationPermission) stringResource(R.string.permission_granted) else null,
                                size = 140.dp,
                                modifier = Modifier.padding(8.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = stringResource(R.string.onboarding_title_permissions),
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Black,
                                color = SleekTextDark,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.onboarding_desc_permissions),
                                fontSize = 14.sp,
                                color = SleekTextMuted,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            // Custom Permission Status Cards
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                PermissionStatusCard(
                                    title = stringResource(R.string.permission_mic),
                                    isGranted = hasMicPermission,
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.Mic,
                                            contentDescription = null,
                                            tint = if (hasMicPermission) SleekEmerald else SleekOcean
                                        )
                                    }
                                )

                                PermissionStatusCard(
                                    title = stringResource(R.string.permission_notif),
                                    isGranted = hasNotificationPermission,
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.Notifications,
                                            contentDescription = null,
                                            tint = if (hasNotificationPermission) SleekEmerald else SleekCoral
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Navigation Panel
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button
                if (currentPage > 0) {
                    IconButton(
                        onClick = { currentPage -= 1 },
                        modifier = Modifier.testTag("onboarding_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.button_back),
                            tint = SleekOcean
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(48.dp))
                }

                // Page Indicator Dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(totalPages) { index ->
                        Box(
                            modifier = Modifier
                                .size(if (currentPage == index) 12.dp else 8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (currentPage == index) SleekOcean else SleekTextMuted.copy(alpha = 0.4f)
                                )
                        )
                    }
                }

                // Next or Get Started Button
                if (currentPage < totalPages - 1) {
                    IconButton(
                        onClick = { currentPage += 1 },
                        modifier = Modifier
                            .background(SleekOcean, CircleShape)
                            .size(48.dp)
                            .testTag("onboarding_next_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = stringResource(R.string.button_next),
                            tint = Color.White
                        )
                    }
                } else {
                    // Final page - Grant permissions & Get Started
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (!hasMicPermission || !hasNotificationPermission) {
                            Button(
                                onClick = {
                                    val permissionsToRequest = mutableListOf<String>()
                                    if (!hasMicPermission) {
                                        permissionsToRequest.add(Manifest.permission.RECORD_AUDIO)
                                    }
                                    if (!hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                    if (permissionsToRequest.isNotEmpty()) {
                                        permissionLauncher.launch(permissionsToRequest.toTypedArray())
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SleekCoral
                                ),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.testTag("onboarding_grant_button")
                            ) {
                                Text(
                                    text = stringResource(R.string.button_allow),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        Button(
                            onClick = {
                                engagementManager.isOnboardingCompleted = true
                                onOnboardingFinished()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SleekEmerald
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.testTag("onboarding_start_button")
                        ) {
                            Text(
                                text = stringResource(R.string.button_get_started),
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PermissionStatusCard(
    title: String,
    isGranted: Boolean,
    icon: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                icon()
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = SleekTextDark
                )
            }

            if (isGranted) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = SleekEmerald,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SleekTextMuted.copy(alpha = 0.12f),
                    modifier = Modifier.size(24.dp)
                ) {}
            }
        }
    }
}
