package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = PynovaCyan,
    onPrimary = PynovaObsidian,
    primaryContainer = PynovaCardBackground,
    onPrimaryContainer = PynovaCyan,
    secondary = PynovaPurple,
    onSecondary = PynovaTextPrimary,
    secondaryContainer = PynovaCardBorder,
    onSecondaryContainer = PynovaTextPrimary,
    tertiary = PynovaCreatorGold,
    onTertiary = PynovaObsidian,
    background = PynovaObsidian,
    onBackground = PynovaTextPrimary,
    surface = PynovaDarkSurface,
    onSurface = PynovaTextPrimary,
    surfaceVariant = PynovaCardBackground,
    onSurfaceVariant = PynovaTextSecondary,
    outline = PynovaCardBorder,
    error = PynovaErrorRed
  )

private val LightColorScheme = DarkColorScheme // Pynova AI is a dedicated luxury dark-mode experience

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = DarkColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
