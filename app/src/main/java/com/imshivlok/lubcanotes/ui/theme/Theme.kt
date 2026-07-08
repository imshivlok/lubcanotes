package com.imshivlok.lubcanotes.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ClaudeLightScheme = lightColorScheme(
    primary = ClaudeAccent,
    background = ClaudeBackground,
    surface = ClaudeSurface,
    onBackground = ClaudeTextMain,
    onSurface = ClaudeTextMain
)

@Composable
fun LUBCANotesTheme(
    darkTheme: Boolean = false, // Enforce clean light mode
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ClaudeLightScheme,
        typography = Typography,
        content = content
    )
}