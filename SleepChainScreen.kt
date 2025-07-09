package com.devvaultpro.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
importandroidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepChainScreen() {
    var isPlaying by remember { mutableStateOf(false) }
    var selectedDuration by remember { mutableStateOf(10) }
    var remainingTime by remember { mutableStateOf(selectedDuration * 60) }
    var currentPhase by remember { mutableStateOf("Inhale") }
    var showBreathingGuide by remember { mutableStateOf(false) }

    val animationValue by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    // Timer effect
    LaunchedEffect(isPlaying, remainingTime) {
        if (isPlaying && remainingTime > 0) {
            delay(1000)
            remainingTime--
        } else if (remainingTime <= 0) {
            isPlaying = false
            remainingTime = selectedDuration * 60
        }
    }

    // Breathing guide effect
    LaunchedEffect(animationValue) {
        if (showBreathingGuide) {
            currentPhase = when {
                animationValue < 0.25f -> "Inhale"
                animationValue < 0.5f -> "Hold"
                animationValue < 0.75f -> "Exhale"
                else -> "Hold"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = "Sleep Chain",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "528Hz Healing Frequency • Deep Relaxation",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Visualization
        Box(
            modifier = Modifier
                .size(200.dp)
                .padding(bottom = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            // Animated waves
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                drawWaves(animationValue, isPlaying)
            }

            // Breathing guide circle
            if (showBreathingGuide) {
                val breathingScale by animateFloatAsState(
                    targetValue = when (currentPhase) {
                        "Inhale" -> 1.2f
                        "Hold" -> 1.2f
                        "Exhale" -> 0.8f
                        else -> 0.8f
                    },
                    animationSpec = tween(1000)
                )

                Box(
                    modifier = Modifier
                        .size((80 * breathingScale).dp)
                        .clip(CircleShape)
                        .background(Color.Blue.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentPhase,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Timer Display
        Text(
            text = formatTime(remainingTime),
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Duration Selection
        Text(
            text = "Session Duration",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            listOf(5, 10, 15, 20, 30).forEach { duration ->
                FilterChip(
                    onClick = { 
                        selectedDuration = duration
                        remainingTime = duration * 60
                    },
                    label = { Text("${duration}m") },
                    selected = selectedDuration == duration,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color.Blue,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // Controls
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Button(
                onClick = { 
                    isPlaying = !isPlaying
                    if (isPlaying) {
                        showBreathingGuide = true
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPlaying) Color.Red else Color.Green,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isPlaying) "Pause" else "Start")
            }

            Button(
                onClick = { 
                    isPlaying = false
                    remainingTime = selectedDuration * 60
                    showBreathingGuide = false
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Gray,
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Default.Stop, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reset")
            }
        }

        // Breathing Guide Toggle
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Switch(
                checked = showBreathingGuide,
                onCheckedChange = { showBreathingGuide = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Blue,
                    checkedTrackColor = Color.Blue.copy(alpha = 0.5f)
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Breathing Guide (4-7-8 Pattern)",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Benefits Info
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.Gray.copy(alpha = 0.2f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Benefits of 528Hz Frequency",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                listOf(
                    "🧬 DNA repair and healing",
                    "🧘 Stress reduction and relaxation",
                    "💤 Improved sleep quality",
                    "🧠 Enhanced mental clarity",
                    "❤️ Emotional balance and well-being"
                ).forEach { benefit ->
                    Text(
                        text = benefit,
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }
        }
    }
}

fun DrawScope.drawWaves(animationValue: Float, isPlaying: Boolean) {
    if (!isPlaying) return

    val centerX = size.width / 2
    val centerY = size.height / 2

    repeat(3) { i ->
        val radius = (50 + i * 30) + (sin(animationValue * 2 * Math.PI + i) * 20).toFloat()
        val alpha = 1f - (i * 0.3f)

        drawCircle(
            color = Color.Blue.copy(alpha = alpha),
            radius = radius,
            center = androidx.compose.ui.geometry.Offset(centerX, centerY),
            style = Stroke(width = 2.dp.toPx())
        )
    }
}

fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return "%02d:%02d".format(minutes, remainingSeconds)
}