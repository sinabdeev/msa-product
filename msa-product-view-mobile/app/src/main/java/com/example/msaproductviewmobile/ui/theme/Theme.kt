package com.example.msaproductviewmobile.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

/**
 * Dark color palette for dark theme.
 */
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryVariant,
    onPrimary = OnPrimary,
    primaryContainer = Primary,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = SecondaryVariant,
    onSecondary = OnSecondary,
    secondaryContainer = Secondary,
    onSecondaryContainer = OnSecondaryContainer,
    tertiary = Tertiary,
    onTertiary = OnTertiary,
    tertiaryContainer = Tertiary,
    onTertiaryContainer = OnTertiary,
    error = Error,
    onError = OnError,
    errorContainer = Error,
    onErrorContainer = OnErrorContainer,
    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    surfaceDim = Color(0xFF1C1B1F),
    surfaceBright = Color(0xFF44444B),
    surfaceContainerLowest = Color(0xFF16151A),
    surfaceContainerLow = Color(0xFF242329),
    surfaceContainer = Color(0xFF2D2C32),
    surfaceContainerHigh = Color(0xFF38373E),
    surfaceContainerHighest = Color(0xFF434149),
)

/**
 * Light color palette for light theme.
 */
private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    tertiary = Tertiary,
    onTertiary = OnTertiary,
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = OnTertiaryContainer,
    error = Error,
    onError = OnError,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    surfaceDim = SurfaceDim,
    surfaceBright = SurfaceBright,
    surfaceContainerLowest = SurfaceContainerLowest,
    surfaceContainerLow = SurfaceContainerLow,
    surfaceContainer = SurfaceContainer,
    surfaceContainerHigh = SurfaceContainerHigh,
    surfaceContainerHighest = SurfaceContainerHighest,
)

/**
 * Typography for the app.
 */
private val Typography = androidx.compose.material3.Typography(
    displayLarge = androidx.compose.material3.Typography.displayLarge.copy(fontSize = 57.sp),
    displayMedium = androidx.compose.material3.Typography.displayMedium.copy(fontSize = 45.sp),
    displaySmall = androidx.compose.material3.Typography.displaySmall.copy(fontSize = 36.sp),
    headlineLarge = androidx.compose.material3.Typography.headlineLarge.copy(fontSize = 32.sp),
    headlineMedium = androidx.compose.material3.Typography.headlineMedium.copy(fontSize = 28.sp),
    headlineSmall = androidx.compose.material3.Typography.headlineSmall.copy(fontSize = 24.sp),
    titleLarge = androidx.compose.material3.Typography.titleLarge.copy(fontSize = 22.sp),
    titleMedium = androidx.compose.material3.Typography.titleMedium.copy(fontSize = 16.sp),
    titleSmall = androidx.compose.material3.Typography.titleSmall.copy(fontSize = 14.sp),
    bodyLarge = androidx.compose.material3.Typography.bodyLarge.copy(fontSize = 16.sp),
    bodyMedium = androidx.compose.material3.Typography.bodyMedium.copy(fontSize = 14.sp),
    bodySmall = androidx.compose.material3.Typography.bodySmall.copy(fontSize = 12.sp),
    labelLarge = androidx.compose.material3.Typography.labelLarge.copy(fontSize = 14.sp),
    labelMedium = androidx.compose.material3.Typography.labelMedium.copy(fontSize = 12.sp),
    labelSmall = androidx.compose.material3.Typography.labelSmall.copy(fontSize = 11.sp),
)

/**
 * Composable to apply the MSA Product View Mobile theme.
 */
@Composable
fun MSAProductViewMobileTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
