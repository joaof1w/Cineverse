package com.equipe.cineverse.userinterface

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val VerdePortal = Color(0xFF97CE4C)
private val VerdeEscuro = Color(0xFF3F6B12)
private val Ciano = Color(0xFF00B5CC)
private val CianoEscuro = Color(0xFF006876)

private val EsquemaEscuro = darkColorScheme(
    primary = VerdePortal,
    onPrimary = Color(0xFF13290A),
    secondary = Ciano,
    onSecondary = Color(0xFF00363D),
    background = Color(0xFF0F1418),
    onBackground = Color(0xFFE1E3DE),
    surface = Color(0xFF0F1418),
    onSurface = Color(0xFFE1E3DE),
    surfaceVariant = Color(0xFF1C252B),
    onSurfaceVariant = Color(0xFFBEC8C9),
    surfaceContainer = Color(0xFF172026)
)

private val EsquemaClaro = lightColorScheme(
    primary = VerdeEscuro,
    onPrimary = Color.White,
    secondary = CianoEscuro,
    onSecondary = Color.White,
    background = Color(0xFFF7F9F2),
    surface = Color(0xFFF7F9F2),
    surfaceVariant = Color(0xFFE1E6D8)
)

@Composable
fun CineVerseTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) EsquemaEscuro else EsquemaClaro,
        content = content
    )
}
