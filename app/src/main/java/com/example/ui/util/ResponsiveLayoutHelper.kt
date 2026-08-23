package com.example.ui.util

import android.app.Activity
import android.app.UiModeManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * DeviceFormFactor
 *
 * Distinguishes between handheld mobile devices, foldable/portrait tablets,
 * expanded large-screen tablets, and 10-foot lean-back Android TV environments.
 */
enum class DeviceFormFactor {
    MOBILE_PORTRAIT,
    MOBILE_LANDSCAPE,
    TABLET_PORTRAIT,
    TABLET_EXPANDED,
    ANDROID_TV
}

/**
 * AdaptiveLayoutProfile
 *
 * Encapsulates dynamic layout metrics, typography scaling, grid column recommendations,
 * and component spacing determined by WindowSizeClass and hardware form factors.
 */
data class AdaptiveLayoutProfile(
    val formFactor: DeviceFormFactor,
    val isTv: Boolean,
    val isTablet: Boolean,
    val isCompact: Boolean,
    val widthSizeClass: WindowWidthSizeClass,
    val heightSizeClass: WindowHeightSizeClass,
    val gridColumns: Int,
    val horizontalPadding: Dp,
    val verticalPadding: Dp,
    val cardCornerRadius: Dp,
    val heroTitleSize: TextUnit,
    val sectionHeaderSize: TextUnit,
    val bodyTextSize: TextUnit,
    val buttonHeight: Dp,
    val mascotSize: Dp,
    val scaleFactor: Float
) {
    val contentPadding: PaddingValues
        get() = PaddingValues(horizontal = horizontalPadding, vertical = verticalPadding)
}

val LocalAdaptiveLayout = compositionLocalOf<AdaptiveLayoutProfile> {
    AdaptiveLayoutProfile(
        formFactor = DeviceFormFactor.MOBILE_PORTRAIT,
        isTv = false,
        isTablet = false,
        isCompact = true,
        widthSizeClass = WindowWidthSizeClass.Compact,
        heightSizeClass = WindowHeightSizeClass.Compact,
        gridColumns = 2,
        horizontalPadding = 16.dp,
        verticalPadding = 16.dp,
        cardCornerRadius = 20.dp,
        heroTitleSize = 22.sp,
        sectionHeaderSize = 18.sp,
        bodyTextSize = 14.sp,
        buttonHeight = 52.dp,
        mascotSize = 90.dp,
        scaleFactor = 1.0f
    )
}

/**
 * Detects if the current environment is an Android TV device based on
 * PackageManager Leanback feature, UiModeManager, or TV configuration flag.
 */
fun isAndroidTvDevice(context: Context): Boolean {
    val pm = context.packageManager
    val isLeanback = pm.hasSystemFeature(PackageManager.FEATURE_LEANBACK)
    val isTelevisionFeature = pm.hasSystemFeature("android.hardware.type.television")

    val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as? UiModeManager
    val isUiModeTv = uiModeManager?.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION

    val config = context.resources.configuration
    val isConfigTv = (config.uiMode and Configuration.UI_MODE_TYPE_MASK) == Configuration.UI_MODE_TYPE_TELEVISION

    return isLeanback || isTelevisionFeature || isUiModeTv || isConfigTv
}

/**
 * Calculates the AdaptiveLayoutProfile based on WindowSizeClass and hardware detection.
 */
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun rememberAdaptiveLayoutProfile(
    windowSizeClass: WindowSizeClass? = null
): AdaptiveLayoutProfile {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isTvHardware = remember(context) { isAndroidTvDevice(context) }

    val activity = context as? Activity
    val calculatedSizeClass = if (windowSizeClass != null) {
        windowSizeClass
    } else if (activity != null) {
        calculateWindowSizeClass(activity)
    } else {
        fallbackWindowSizeClass(configuration)
    }

    return remember(calculatedSizeClass, isTvHardware, configuration.screenWidthDp, configuration.screenHeightDp) {
        deriveLayoutProfile(
            widthClass = calculatedSizeClass.widthSizeClass,
            heightClass = calculatedSizeClass.heightSizeClass,
            isTvHardware = isTvHardware,
            screenWidthDp = configuration.screenWidthDp
        )
    }
}

/**
 * Fallback calculation if Activity or WindowMetrics are unavailable.
 */
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
private fun fallbackWindowSizeClass(configuration: Configuration): WindowSizeClass {
    val width = configuration.screenWidthDp
    val height = configuration.screenHeightDp

    return WindowSizeClass.calculateFromSize(
        androidx.compose.ui.unit.DpSize(width.dp, height.dp)
    )
}

/**
 * Derives the profile parameters based on size class and hardware flags.
 */
private fun deriveLayoutProfile(
    widthClass: WindowWidthSizeClass,
    heightClass: WindowHeightSizeClass,
    isTvHardware: Boolean,
    screenWidthDp: Int
): AdaptiveLayoutProfile {
    val formFactor = when {
        isTvHardware -> DeviceFormFactor.ANDROID_TV
        widthClass == WindowWidthSizeClass.Expanded && screenWidthDp >= 1000 -> DeviceFormFactor.TABLET_EXPANDED
        widthClass == WindowWidthSizeClass.Expanded || widthClass == WindowWidthSizeClass.Medium -> DeviceFormFactor.TABLET_PORTRAIT
        heightClass == WindowHeightSizeClass.Compact -> DeviceFormFactor.MOBILE_LANDSCAPE
        else -> DeviceFormFactor.MOBILE_PORTRAIT
    }

    val isTv = formFactor == DeviceFormFactor.ANDROID_TV
    val isTablet = formFactor == DeviceFormFactor.TABLET_PORTRAIT || formFactor == DeviceFormFactor.TABLET_EXPANDED
    val isCompact = widthClass == WindowWidthSizeClass.Compact

    return when (formFactor) {
        DeviceFormFactor.ANDROID_TV -> AdaptiveLayoutProfile(
            formFactor = DeviceFormFactor.ANDROID_TV,
            isTv = true,
            isTablet = false,
            isCompact = false,
            widthSizeClass = widthClass,
            heightSizeClass = heightClass,
            gridColumns = 4,
            horizontalPadding = 48.dp,
            verticalPadding = 32.dp,
            cardCornerRadius = 24.dp,
            heroTitleSize = 32.sp,
            sectionHeaderSize = 24.sp,
            bodyTextSize = 18.sp,
            buttonHeight = 64.dp,
            mascotSize = 160.dp,
            scaleFactor = 1.35f
        )

        DeviceFormFactor.TABLET_EXPANDED -> AdaptiveLayoutProfile(
            formFactor = DeviceFormFactor.TABLET_EXPANDED,
            isTv = false,
            isTablet = true,
            isCompact = false,
            widthSizeClass = widthClass,
            heightSizeClass = heightClass,
            gridColumns = 4,
            horizontalPadding = 32.dp,
            verticalPadding = 24.dp,
            cardCornerRadius = 24.dp,
            heroTitleSize = 28.sp,
            sectionHeaderSize = 22.sp,
            bodyTextSize = 16.sp,
            buttonHeight = 60.dp,
            mascotSize = 140.dp,
            scaleFactor = 1.2f
        )

        DeviceFormFactor.TABLET_PORTRAIT -> AdaptiveLayoutProfile(
            formFactor = DeviceFormFactor.TABLET_PORTRAIT,
            isTv = false,
            isTablet = true,
            isCompact = false,
            widthSizeClass = widthClass,
            heightSizeClass = heightClass,
            gridColumns = 3,
            horizontalPadding = 24.dp,
            verticalPadding = 20.dp,
            cardCornerRadius = 22.dp,
            heroTitleSize = 24.sp,
            sectionHeaderSize = 20.sp,
            bodyTextSize = 15.sp,
            buttonHeight = 56.dp,
            mascotSize = 120.dp,
            scaleFactor = 1.1f
        )

        DeviceFormFactor.MOBILE_LANDSCAPE -> AdaptiveLayoutProfile(
            formFactor = DeviceFormFactor.MOBILE_LANDSCAPE,
            isTv = false,
            isTablet = false,
            isCompact = false,
            widthSizeClass = widthClass,
            heightSizeClass = heightClass,
            gridColumns = 3,
            horizontalPadding = 20.dp,
            verticalPadding = 12.dp,
            cardCornerRadius = 18.dp,
            heroTitleSize = 20.sp,
            sectionHeaderSize = 17.sp,
            bodyTextSize = 13.sp,
            buttonHeight = 48.dp,
            mascotSize = 90.dp,
            scaleFactor = 0.95f
        )

        DeviceFormFactor.MOBILE_PORTRAIT -> AdaptiveLayoutProfile(
            formFactor = DeviceFormFactor.MOBILE_PORTRAIT,
            isTv = false,
            isTablet = false,
            isCompact = true,
            widthSizeClass = widthClass,
            heightSizeClass = heightClass,
            gridColumns = 2,
            horizontalPadding = 16.dp,
            verticalPadding = 14.dp,
            cardCornerRadius = 20.dp,
            heroTitleSize = 22.sp,
            sectionHeaderSize = 18.sp,
            bodyTextSize = 14.sp,
            buttonHeight = 52.dp,
            mascotSize = 95.dp,
            scaleFactor = 1.0f
        )
    }
}

/**
 * AdaptiveScaffoldContainer
 *
 * Wraps content with LocalAdaptiveLayout provider for deeply nested child components
 * to adapt sizes, padding, and layout orientation seamlessly.
 */
@Composable
fun AdaptiveScaffoldContainer(
    modifier: Modifier = Modifier,
    content: @Composable (AdaptiveLayoutProfile) -> Unit
) {
    val layoutProfile = rememberAdaptiveLayoutProfile()
    CompositionLocalProvider(LocalAdaptiveLayout provides layoutProfile) {
        Box(modifier = modifier.fillMaxSize()) {
            content(layoutProfile)
        }
    }
}
