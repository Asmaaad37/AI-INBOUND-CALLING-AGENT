package com.uos.uosassistance.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.uos.uosassistance.viewmodel.MainViewModel

val UOS_Navy = Color(0xFF223468)
val UOS_Gold = Color(0xFFD2BC71)
val Orange = Color(0xFFFFA500)

private val LightColorScheme = lightColorScheme(
    primary = UOS_Navy,
    onPrimary = Color.White,
    primaryContainer = UOS_Navy.copy(alpha = 0.1f),
    onPrimaryContainer = UOS_Navy,
    secondary = UOS_Gold,
    onSecondary = Color.Black,
    background = Color(0xFFF8F9FA),
    surface = Color.White,
    onSurface = UOS_Navy,
    secondaryContainer = Color(0xFFF1F4F8)
)

private val DarkColorScheme = darkColorScheme(
    primary = UOS_Gold,
    onPrimary = Color.Black,
    primaryContainer = UOS_Navy,
    onPrimaryContainer = Color.White,
    secondary = UOS_Gold,
    onSecondary = Color.Black,
    background = Color(0xFF10141D),
    surface = Color(0xFF1B222C),
    onSurface = Color.White,
    secondaryContainer = Color(0xFF223468)
)

@Composable
fun UOSAssistanceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as android.app.Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val uiState by viewModel.uiState

    UOSAssistanceTheme {
        // Urdu is RTL
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Header
                    Text(
                        text = "UOS Admission Assistant",
                        style = MaterialTheme.typography.headlineMedium,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        softWrap = false,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Visible
                    )
                    // Content Area
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (uiState.userText.isNotEmpty()) {
                            Text(
                                text = uiState.userText,
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(bottom = 16.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        if (uiState.isProcessing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        if (uiState.assistantText.isNotEmpty()) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, UOS_Gold.copy(alpha = 0.3f))
                            ) {
                                Text(
                                    text = uiState.assistantText,
                                    style = MaterialTheme.typography.headlineSmall,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        uiState.error?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 16.dp)
                            )
                        }
                    }

                    // Microphone Button
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(bottom = 48.dp)
                    ) {
                        if (uiState.isListening) {
                            RippleAnimation()
                        }

                        FloatingActionButton(
                            onClick = { viewModel.onMicClicked() },
                            shape = CircleShape,
                            containerColor = if (uiState.isListening) Color.Red else if (uiState.isSpeaking) Orange else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(80.dp),
                            elevation = FloatingActionButtonDefaults.elevation(8.dp)
                        ) {
                            Icon(
                                imageVector = if (uiState.isListening || uiState.isSpeaking) Icons.Filled.Clear else Icons.Filled.Mic,
                                contentDescription = "Mic",
                                tint = if (uiState.isListening || uiState.isSpeaking) Color.White else MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RippleAnimation() {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .size(80.dp)
            .scale(scale)
            .background(Color.Red.copy(alpha = alpha), CircleShape)
    )
}
