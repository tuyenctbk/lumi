package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Soft Pastel Color Palette for Lumi
 * Minimalist, high contrast, warm neutral and pastel aesthetic tailored for child-friendly learning
 */
val LumiColorScheme: ColorScheme = lightColorScheme(
    primary = SleekEmerald,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE6FAF4),
    onPrimaryContainer = SleekEmeraldDark,
    secondary = SleekOcean,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE8F6FA),
    onSecondaryContainer = SleekOceanDark,
    tertiary = SleekCoral,
    onTertiary = Color.White,
    background = SleekBackground,
    onBackground = SleekTextBody,
    surface = SleekSurface,
    onSurface = SleekTextDark,
    surfaceVariant = SleekSurfaceVariant,
    onSurfaceVariant = SleekTextMuted,
    outline = SleekSurfaceBorder
)

val LumiDarkColorScheme: ColorScheme = darkColorScheme(
    primary = SleekGold,
    onPrimary = SleekTextDark,
    primaryContainer = Color(0xFF352F2B),
    onPrimaryContainer = SleekGold,
    secondary = SleekOcean,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF1E293B),
    onSecondaryContainer = Color(0xFFBAE6FD),
    tertiary = SleekCoral,
    onTertiary = Color.White,
    background = Color(0xFF1C1917),
    onBackground = Color(0xFFF5F5F4),
    surface = Color(0xFF292524),
    onSurface = Color(0xFFF5F5F4),
    surfaceVariant = Color(0xFF38332E),
    onSurfaceVariant = Color(0xFFD6D3D1),
    outline = Color(0xFF44403C)
)

val SleekColorScheme = LumiColorScheme
val SleekDarkColorScheme = LumiDarkColorScheme

/**
 * Lumi Rounded Shapes Definition
 * Friendly rounded corners for cards, dialogs, buttons, and chips
 */
val LumiShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(22.dp),
    large = RoundedCornerShape(30.dp),
    extraLarge = RoundedCornerShape(36.dp)
)

val SleekShapes = LumiShapes

/**
 * LumiTheme Definition
 * Incorporates a soft pastel color palette, rounded shapes, and typography
 * consistent with a minimalist, child-friendly design.
 */
@Composable
fun LumiTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) LumiDarkColorScheme else LumiColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        shapes = LumiShapes,
        typography = Typography,
        content = content
    )
}

/**
 * Compatibility Theme Aliases
 */
@Composable
fun SleekTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    LumiTheme(
        darkTheme = darkTheme,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    LumiTheme(
        darkTheme = darkTheme,
        content = content
    )
}
