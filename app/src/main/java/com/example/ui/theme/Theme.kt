package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Custom Semantic Color Container for Lumi Theme
 * Ensures adaptive backgrounds, surfaces, text, and borders across Light and Dark modes.
 */
data class LumiExtendedColors(
    val bgCanvas: Color,
    val cardSurface: Color,
    val cardSurfaceVariant: Color,
    val cardBorder: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val emeraldAccent: Color,
    val oceanAccent: Color,
    val coralAccent: Color,
    val goldAccent: Color,
    val purpleAccent: Color,
    val shadowColor: Color
)

/**
 * Android TV Leanback Requirement Palette & Focus Configurations
 */
data class TvFocusColors(
    val focusRingColor: Color = SleekEmerald,
    val focusGlowColor: Color = SleekGold,
    val focusedSurface: Color = Color(0xFF2D2926),
    val unfocusedBorderWidth: Dp = 1.5.dp,
    val focusedBorderWidth: Dp = 3.5.dp,
    val focusedScale: Float = 1.08f
)

val LocalLumiExtendedColors = staticCompositionLocalOf {
    LumiExtendedColors(
        bgCanvas = SleekBackground,
        cardSurface = SleekSurface,
        cardSurfaceVariant = SleekSurfaceVariant,
        cardBorder = SleekSurfaceBorder,
        textPrimary = SleekTextDark,
        textSecondary = SleekTextBody,
        textMuted = SleekTextMuted,
        emeraldAccent = SleekEmerald,
        oceanAccent = SleekOcean,
        coralAccent = SleekCoral,
        goldAccent = SleekGold,
        purpleAccent = SleekPurple,
        shadowColor = Color(0x1A000000)
    )
}

val LocalTvFocusColors = staticCompositionLocalOf {
    TvFocusColors()
}

val LightExtendedColors = LumiExtendedColors(
    bgCanvas = Color(0xFFFFF9F2),
    cardSurface = Color(0xFFFFFFFF),
    cardSurfaceVariant = Color(0xFFF8F5F0),
    cardBorder = Color(0xFFF2EDE4),
    textPrimary = Color(0xFF2D2926),
    textSecondary = Color(0xFF403B36),
    textMuted = Color(0xFF8D8070),
    emeraldAccent = SleekEmerald,
    oceanAccent = SleekOcean,
    coralAccent = SleekCoral,
    goldAccent = SleekGold,
    purpleAccent = SleekPurple,
    shadowColor = Color(0x14000000)
)

val DarkExtendedColors = LumiExtendedColors(
    bgCanvas = Color(0xFF141210),
    cardSurface = Color(0xFF201D1A),
    cardSurfaceVariant = Color(0xFF2B2724),
    cardBorder = Color(0xFF3B3631),
    textPrimary = Color(0xFFF7F4F0),
    textSecondary = Color(0xFFE2DDD7),
    textMuted = Color(0xFFA8A096),
    emeraldAccent = SleekEmerald,
    oceanAccent = Color(0xFF38BDF8),
    coralAccent = Color(0xFFFB7185),
    goldAccent = Color(0xFFFFD166),
    purpleAccent = Color(0xFFA78BFA),
    shadowColor = Color(0x40000000)
)

/**
 * Custom Material 3 Light Color Scheme
 */
val LumiLightColorScheme: ColorScheme = lightColorScheme(
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
    tertiaryContainer = Color(0xFFFFE8EE),
    onTertiaryContainer = SleekCoralDark,
    background = Color(0xFFFFF9F2),
    onBackground = SleekTextBody,
    surface = Color(0xFFFFFFFF),
    onSurface = SleekTextDark,
    surfaceVariant = Color(0xFFF8F5F0),
    onSurfaceVariant = SleekTextMuted,
    outline = SleekSurfaceBorder,
    outlineVariant = Color(0xFFE5DFD5)
)

/**
 * Custom Material 3 Dark Color Scheme
 */
val LumiDarkColorScheme: ColorScheme = darkColorScheme(
    primary = SleekEmerald,
    onPrimary = Color(0xFF00382B),
    primaryContainer = Color(0xFF004D3B),
    onPrimaryContainer = Color(0xFF6EE7B7),
    secondary = Color(0xFF38BDF8),
    onSecondary = Color(0xFF082F49),
    secondaryContainer = Color(0xFF0C4A6E),
    onSecondaryContainer = Color(0xFFBAE6FD),
    tertiary = Color(0xFFFB7185),
    onTertiary = Color(0xFF4C0519),
    tertiaryContainer = Color(0xFF881337),
    onTertiaryContainer = Color(0xFFFECDD3),
    background = Color(0xFF141210),
    onBackground = Color(0xFFF7F4F0),
    surface = Color(0xFF201D1A),
    onSurface = Color(0xFFF7F4F0),
    surfaceVariant = Color(0xFF2B2724),
    onSurfaceVariant = Color(0xFFA8A096),
    outline = Color(0xFF3B3631),
    outlineVariant = Color(0xFF4E4741)
)

val LumiColorScheme = LumiLightColorScheme
val SleekColorScheme = LumiLightColorScheme
val SleekDarkColorScheme = LumiDarkColorScheme

/**
 * Lumi Rounded Shapes Definition
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
 * Supports custom Material 3 color schemes, mobile typography, and Android TV Leanback focus requirements.
 */
@Composable
fun LumiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    isTvLeanbackMode: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) LumiDarkColorScheme else LumiLightColorScheme
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors
    val typographySystem = if (isTvLeanbackMode) TvLeanbackTypography else Typography
    val tvFocusColors = TvFocusColors(
        focusRingColor = SleekEmerald,
        focusGlowColor = SleekGold,
        focusedSurface = if (darkTheme) Color(0xFF2E2A26) else Color(0xFFFFFFFF)
    )

    CompositionLocalProvider(
        LocalLumiExtendedColors provides extendedColors,
        LocalTvFocusColors provides tvFocusColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            shapes = LumiShapes,
            typography = typographySystem,
            content = content
        )
    }
}

/**
 * Easy accessor for Lumi semantic theme colors and TV Leanback focus settings
 */
object LumiAppTheme {
    val colors: LumiExtendedColors
        @Composable
        @ReadOnlyComposable
        get() = LocalLumiExtendedColors.current

    val tvFocus: TvFocusColors
        @Composable
        @ReadOnlyComposable
        get() = LocalTvFocusColors.current
}

/**
 * Compatibility Theme Aliases
 */
@Composable
fun SleekTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    LumiTheme(
        darkTheme = darkTheme,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    LumiTheme(
        darkTheme = darkTheme,
        content = content
    )
}
