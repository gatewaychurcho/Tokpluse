package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val TokDarkColorScheme =
  darkColorScheme(
    primary = TokPink,
    secondary = TokCyan,
    tertiary = TokPurple,
    background = TokDarkBg,
    surface = TokSurface,
    surfaceVariant = TokSurfaceVariant,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.White,
    onBackground = TokTextPrimary,
    onSurface = TokTextPrimary,
    outline = TokBorder
  )

private val TokLightColorScheme =
  lightColorScheme(
    primary = TokPink,
    secondary = TokCyan,
    tertiary = TokPurple,
    background = Color(0xFFF7F7F9),
    surface = Color.White,
    surfaceVariant = Color(0xFFEDEDF2),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.White,
    onBackground = Color(0xFF16161A),
    onSurface = Color(0xFF16161A),
    outline = Color(0xFFD4D5DC)
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Default to immersive dark mode for TikTok video apps
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) TokDarkColorScheme else TokLightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

