package com.novadarts.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object NovaPalette {
    val ink = Color(0xFF080B10)
    val panel = Color(0xFF111720)
    val panelRaised = Color(0xFF171F29)
    val line = Color(0xFF273340)
    val lime = Color(0xFFD7FF59)
    val mint = Color(0xFF69E2CB)
    val violet = Color(0xFF9C8CFF)
    val coral = Color(0xFFFF8B67)
    val text = Color(0xFFF5F7F2)
    val muted = Color(0xFF8D99A7)
    val soft = Color(0xFFB8C1CB)
    val boardBlack = Color(0xFF20262D)
    val boardCream = Color(0xFFD4D1C8)
    val boardRed = Color(0xFFE95D53)
    val boardGreen = Color(0xFF69B88F)
}

@Composable
fun NovaDartsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = NovaPalette.lime,
            secondary = NovaPalette.mint,
            background = NovaPalette.ink,
            surface = NovaPalette.panel,
            onPrimary = NovaPalette.ink,
            onBackground = NovaPalette.text,
            onSurface = NovaPalette.text,
        ),
        content = content,
    )
}
