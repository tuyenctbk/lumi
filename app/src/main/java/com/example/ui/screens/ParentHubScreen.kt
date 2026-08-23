package com.example.ui.screens

import androidx.compose.runtime.Composable
import com.example.ui.viewmodel.LumiViewModel

/**
 * ParentHubScreen (Alias for ParentDashboardScreen)
 */
@Composable
fun ParentHubScreen(
    viewModel: LumiViewModel,
    onBack: () -> Unit
) {
    ParentDashboardScreen(
        viewModel = viewModel,
        onBack = onBack
    )
}
