package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.util.DeviceFormFactor
import com.example.ui.util.rememberAdaptiveLayoutProfile

/**
 * NavigationManager
 *
 * Adaptive Navigation component that utilizes WindowSizeClass and DeviceFormFactor
 * to detect device types (Android TV, Tablet, Mobile) and switch between:
 * 1. TvLazyRow: Horizontal lean-back D-pad optimized scrolling for Android TV displays.
 * 2. LazyVerticalGrid: Responsive multi-column grid layout for Mobile and Tablet screens.
 */
@Composable
fun <T> NavigationManager(
    items: List<T>,
    modifier: Modifier = Modifier,
    horizontalSpacing: Dp = 16.dp,
    verticalSpacing: Dp = 16.dp,
    testTag: String = "navigation_manager",
    itemContent: @Composable (item: T, isTv: Boolean) -> Unit
) {
    val layoutProfile = rememberAdaptiveLayoutProfile()
    val isTv = layoutProfile.isTv || layoutProfile.formFactor == DeviceFormFactor.ANDROID_TV

    Box(modifier = modifier.fillMaxWidth().testTag(testTag)) {
        if (isTv) {
            // Android TV Lean-Back Navigation Row
            LazyRow(
                contentPadding = PaddingValues(horizontal = layoutProfile.horizontalPadding),
                horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tv_lazy_row_navigation")
            ) {
                items(items) { item ->
                    itemContent(item, true)
                }
            }
        } else {
            // Mobile and Tablet Responsive Multi-Column Grid Layout
            val columns = when (layoutProfile.widthSizeClass) {
                WindowWidthSizeClass.Expanded -> 4
                WindowWidthSizeClass.Medium -> 3
                else -> 2
            }
            val rows = (items.size + columns - 1) / columns
            val minGridHeight = (rows * 200).dp

            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                contentPadding = PaddingValues(horizontal = layoutProfile.horizontalPadding, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
                verticalArrangement = Arrangement.spacedBy(verticalSpacing),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(minGridHeight.coerceAtLeast(200.dp))
                    .testTag("lazy_vertical_grid_navigation")
            ) {
                items(items) { item ->
                    itemContent(item, false)
                }
            }
        }
    }
}
