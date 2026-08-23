package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * ResponsiveContentSection
 *
 * Adaptive architecture switching between:
 * - TvLazyRow: Horizontal lean-back scrolling for Android TV / Large landscape displays.
 * - LazyVerticalGrid / Multi-Column Grid: Structured vertical scrolling for mobile phones and tablets.
 */
@Composable
fun <T> ResponsiveContentSection(
    items: List<T>,
    modifier: Modifier = Modifier,
    horizontalSpacing: Dp = 12.dp,
    verticalSpacing: Dp = 12.dp,
    mobileColumns: Int = 2,
    tabletColumns: Int = 3,
    itemContent: @Composable (item: T, isTvOrLandscape: Boolean) -> Unit
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val width = maxWidth
        val isTvOrLandscape = width > 720.dp

        if (isTvOrLandscape) {
            // Lean-back horizontal row for Android TV and wide tablets
            LazyRow(
                contentPadding = PaddingValues(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(items) { item ->
                    itemContent(item, true)
                }
            }
        } else {
            // Touch-friendly vertical 2-column (or 3-column) grid for mobile & portrait tablets
            val columns = if (width > 480.dp) tabletColumns else mobileColumns
            val rows = (items.size + columns - 1) / columns
            val gridHeight = (rows * 190).dp
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                contentPadding = PaddingValues(horizontal = 2.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
                verticalArrangement = Arrangement.spacedBy(verticalSpacing),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(gridHeight.coerceAtLeast(190.dp))
            ) {
                items(items) { item ->
                    itemContent(item, false)
                }
            }
        }
    }
}
