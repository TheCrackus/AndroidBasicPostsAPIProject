package com.example.androidbasicpostsapiproject.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = MessageNodePurpleLight,
    onPrimary = MessageNodePurpleDark,
    primaryContainer = MessageNodePurple,
    onPrimaryContainer = MessageNodeWhite,
    secondary = MessageNodeAmber,
    onSecondary = MessageNodeOnAmber,
    secondaryContainer = MessageNodeAmberPressed,
    onSecondaryContainer = MessageNodeOnAmber,
    error = MessageNodeDeleteRedLight,
    onError = MessageNodeOnDeleteRedDark,
    background = MessageNodeBackgroundDark,
    onBackground = MessageNodeOnSurfaceDark,
    surface = MessageNodeSurfaceDark,
    onSurface = MessageNodeOnSurfaceDark,
    onSurfaceVariant = MessageNodeOnSurfaceVariantDark,
    outline = MessageNodeBorderDark,
)

private val LightColorScheme = lightColorScheme(
    primary = MessageNodePurple,
    onPrimary = MessageNodeWhite,
    primaryContainer = MessageNodePurple,
    onPrimaryContainer = MessageNodeWhite,
    secondary = MessageNodeAmber,
    onSecondary = MessageNodeOnAmber,
    secondaryContainer = MessageNodeAmberPressed,
    onSecondaryContainer = MessageNodeOnAmber,
    error = MessageNodeDeleteRed,
    onError = MessageNodeWhite,
    background = MessageNodeBackgroundLight,
    onBackground = MessageNodePurpleDark,
    surface = MessageNodeSurfaceLight,
    onSurface = MessageNodePurpleDark,
    onSurfaceVariant = MessageNodeTextGray,
    outline = MessageNodeBorderGray,
)

@Composable
fun AndroidBasicPostsAPIProjectTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}