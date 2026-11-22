package org.example.pagoqr.theme

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

private val Background = Color(0xFF0F1115)
private val SurfaceBase = Color(0xFF171A21)
private val SurfaceVariant = Color(0xFF20242D)
private val Primary = Color(0xFF8B6CF6)
private val PrimaryDark = Color(0xFF6E4FD8)
private val PrimaryContainer = Color(0xFF2A2147)
private val TextPrimary = Color(0xFFF5F3FA)
private val TextSecondary = Color(0xFFA9A6B2)
private val Outline = Color(0xFF343945)

internal data class StatusColors(val accent: Color, val background: Color)

internal object PagoQrStatusColors {
    val success = StatusColors(Color(0xFF56C271), Color(0xFF173523))
    val failed = StatusColors(Color(0xFFFF6B6B), Color(0xFF3A1C20))
    val refunded = StatusColors(Color(0xFFA995FF), Color(0xFF2B2446))
    val warning = StatusColors(Color(0xFFF2B84B), Color(0xFF3A2D14))
}

private val PagoQrColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = Background,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = TextPrimary,
    secondary = TextSecondary,
    onSecondary = Background,
    secondaryContainer = SurfaceVariant,
    onSecondaryContainer = TextPrimary,
    tertiary = TextSecondary,
    onTertiary = Background,
    tertiaryContainer = SurfaceVariant,
    onTertiaryContainer = TextPrimary,
    background = Background,
    onBackground = TextPrimary,
    surface = SurfaceBase,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSecondary,
    surfaceTint = Primary,
    surfaceBright = SurfaceVariant,
    surfaceDim = Background,
    surfaceContainerLowest = Background,
    surfaceContainerLow = SurfaceBase,
    surfaceContainer = SurfaceBase,
    surfaceContainerHigh = SurfaceBase,
    surfaceContainerHighest = SurfaceVariant,
    outline = Outline,
    outlineVariant = Outline,
    inverseSurface = TextPrimary,
    inverseOnSurface = Background,
    inversePrimary = PrimaryDark,
    error = PagoQrStatusColors.failed.accent,
    onError = Background,
    errorContainer = PagoQrStatusColors.failed.background,
    onErrorContainer = PagoQrStatusColors.failed.accent
)

@Composable
fun PagoQrTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = PagoQrColorScheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = PagoQrColorScheme.background,
            contentColor = PagoQrColorScheme.onBackground,
            content = content
        )
    }
}
