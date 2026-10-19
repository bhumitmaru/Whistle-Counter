package com.whistlecounter.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
    primary = Terracotta,
    onPrimary = WarmWhite,
    primaryContainer = TerracottaPale,
    onPrimaryContainer = Terracotta,
    secondary = InkStrong,
    onSecondary = WarmWhite,
    secondaryContainer = SageSoft,
    onSecondaryContainer = SageDeep,
    tertiary = Sage,
    onTertiary = WarmWhite,
    background = Cream,
    onBackground = Ink,
    surface = Cream,
    onSurface = Ink,
    surfaceVariant = CardCream,
    onSurfaceVariant = Muted,
    surfaceTint = Terracotta,
    outline = Border,
    outlineVariant = BorderSoft,
    error = AlarmRed,
    onError = WarmWhite,
    errorContainer = AlarmSoft,
    onErrorContainer = AlarmRed,
)

private val WhistleShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

@Composable
fun WhistleTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = WhistleTypography,
        shapes = WhistleShapes,
        content = content,
    )
}
