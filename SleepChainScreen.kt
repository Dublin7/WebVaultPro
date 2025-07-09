
package com.devvaultpro.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.*

data class BreathingState(
    val phase: BreathingPhase,
    val progress: Float,
    val cycleCount: Int
)

enum class BreathingPhase {
    INHALE, HOLD_IN, EXHALE, HOLD_OUT
}

data class SleepSession(
    val duration: Int, // in minutes
    val musicType: String,
    val breathingPattern: String,
    val startTime: Long = System.currentTimeMillis()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepChainScreen() {
    var isSessionActive by remember { mutableStateOf(false) }
    var selectedDuration by remember { mutableStateOf(10) }
    var selectedMusicType by remember { mutableStateOf("528Hz Healing") }
    var selectedBreathingPattern by remember { mutableStateOf("4-4-4-4") }
    var breathingState by remember { 
        mutableStateOf(BreathingState(BreathingPhase.INHALE, 0f, 0))
    }
    var sessionTimeRemaining by remember { mutableStateOf(0) }
    var showSettings by remember { mutableStateOf(false) }
    var volumeLevel by remember { mutableStateOf(0.7f) }

    // Animated values for visual effects
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing_scale"
    )

    val waveAnimation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing)
        ),
        label = "wave_animation"
    )

    // Session timer
    LaunchedEffect(isSessionActive, selectedDuration) {
        if (isSessionActive) {
            sessionTimeRemaining = selectedDuration * 60
            while (sessionTimeRemaining > 0 && isSessionActive) {
                delay(1000)
                sessionTimeRemaining--
            }
            if (sessionTimeRemaining <= 0) {
                isSessionActive = false
            }
        }
    }

    // Breathing cycle management
    LaunchedEffect(isSessionActive) {
        if (isSessionActive) {
            val cycleDuration = when (selectedBreathingPattern) {
                "4-4-4-4" -> 16000L // 4s each phase
                "4-7-8" -> 19000L   // inhale 4, hold 7, exhale 8
                "6-6-6" -> 18000L   // 6s each phase
                else -> 16000L
            }
            
            while (isSessionActive) {
                // Inhale
                breathingState = breathingState.copy(phase = BreathingPhase.INHALE)
                for (i in 0..100) {
                    if (!isSessionActive) break
                    breathingState = breathingState.copy(progress = i / 100f)
                    delay(cycleDuration / 400) // 1/4 of cycle for inhale
                }
                
                // Hold
                breathingState = breathingState.copy(phase = BreathingPhase.HOLD_IN, progress = 1f)
                delay(cycleDuration / 4)
                
                // Exhale
                breathingState = breathingState.copy(phase = BreathingPhase.EXHALE)
                for (i in 100 downTo 0) {
                    if (!isSessionActive) break
                    breathingState = breathingState.copy(progress = i / 100f)
                    delay(cycleDuration / 400) // 1/4 of cycle for exhale
                }
                
                // Hold
                breathingState = breathingState.copy(phase = BreathingPhase.HOLD_OUT, progress = 0f)
                delay(cycleDuration / 4)
                
                breathingState = breathingState.copy(cycleCount = breathingState.cycleCount + 1)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (isSessionActive) 
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF1A1B3A),
                            Color(0xFF0F0F23),
                            Color(0xFF000000)
                        )
                    )
                else MaterialTheme.colorScheme.background
            )
    ) {
        if (!isSessionActive) {
            // Setup Screen
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Text(
                    text = "Sleep Chain",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6366F1)
                )
                
                Text(
                    text = "Relaxation mode with healing frequencies",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Session Duration
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Session Duration",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(5, 10, 15, 20, 30).forEach { duration ->
                                FilterChip(
                                    onClick = { selectedDuration = duration },
                                    label = { Text("${duration}m") },
                                    selected = selectedDuration == duration,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Music Selection
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Healing Frequencies",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                "528Hz Healing" to "DNA repair & love frequency",
                                "432Hz Natural" to "Earth's natural frequency",
                                "741Hz Cleansing" to "Remove negative energy",
                                "Nature Sounds" to "Rain, ocean & forest"
                            ).forEach { (frequency, description) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = selectedMusicType == frequency,
                                        onClick = { selectedMusicType = frequency }
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = frequency,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        Text(
                                            text = description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Breathing Pattern
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Breathing Pattern",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("4-4-4-4", "4-7-8", "6-6-6").forEach { pattern ->
                                FilterChip(
                                    onClick = { selectedBreathingPattern = pattern },
                                    label = { Text(pattern) },
                                    selected = selectedBreathingPattern == pattern,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                        
                        Text(
                            text = when (selectedBreathingPattern) {
                                "4-4-4-4" -> "Inhale 4s, Hold 4s, Exhale 4s, Hold 4s"
                                "4-7-8" -> "Inhale 4s, Hold 7s, Exhale 8s (Sleep inducing)"
                                "6-6-6" -> "Inhale 6s, Hold 6s, Exhale 6s (Deep relaxation)"
                                else -> ""
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Start Button
                Button(
                    onClick = { isSessionActive = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6366F1)
                    )
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Begin Sleep Chain Session",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        } else {
            // Active Session Screen
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Session Info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { showSettings = !showSettings }
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    }
                    
                    Text(
                        text = "${sessionTimeRemaining / 60}:${(sessionTimeRemaining % 60).toString().padStart(2, '0')}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Light
                    )
                    
                    IconButton(
                        onClick = { isSessionActive = false }
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Stop",
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Animated Visual Guide
                Box(
                    modifier = Modifier.size(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Background waves
                    Canvas(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        drawSleepChainVisuals(
                            waveAnimation = waveAnimation,
                            breathingScale = breathingScale,
                            breathingState = breathingState
                        )
                    }
                    
                    // Breathing Circle
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .scale(if (isSessionActive) breathingScale else 1f)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF6366F1).copy(alpha = 0.6f),
                                        Color(0xFF8B5CF6).copy(alpha = 0.3f),
                                        Color.Transparent
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (breathingState.phase) {
                                BreathingPhase.INHALE -> "Breathe In"
                                BreathingPhase.HOLD_IN -> "Hold"
                                BreathingPhase.EXHALE -> "Breathe Out"
                                BreathingPhase.HOLD_OUT -> "Rest"
                            },
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Light
                        )
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Session Stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${breathingState.cycleCount}",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Cycles",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = selectedMusicType.substringBefore(" "),
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Frequency",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = selectedBreathingPattern,
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Pattern",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                // Volume Control (when settings shown)
                if (showSettings) {
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Black.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Volume",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium
                            )
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.VolumeDown,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.7f)
                                )
                                
                                Slider(
                                    value = volumeLevel,
                                    onValueChange = { volumeLevel = it },
                                    modifier = Modifier.weight(1f),
                                    colors = SliderDefaults.colors(
                                        thumbColor = Color(0xFF6366F1),
                                        activeTrackColor = Color(0xFF6366F1)
                                    )
                                )
                                
                                Icon(
                                    Icons.Default.VolumeUp,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun DrawScope.drawSleepChainVisuals(
    waveAnimation: Float,
    breathingScale: Float,
    breathingState: BreathingState
) {
    val centerX = size.width / 2
    val centerY = size.height / 2
    
    // Draw concentric waves
    for (i in 1..5) {
        val radius = (i * 40f + waveAnimation * 10) * breathingScale
        val alpha = (1f - i * 0.15f) * 0.3f
        
        drawCircle(
            color = Color(0xFF6366F1).copy(alpha = alpha),
            radius = radius,
            center = androidx.compose.ui.geometry.Offset(centerX, centerY),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
        )
    }
    
    // Draw energy particles
    for (i in 0..8) {
        val angle = waveAnimation + i * (2 * PI / 8).toFloat()
        val particleRadius = 80f * breathingScale
        val x = centerX + cos(angle) * particleRadius
        val y = centerY + sin(angle) * particleRadius
        
        drawCircle(
            color = Color(0xFF8B5CF6).copy(alpha = 0.6f),
            radius = 3.dp.toPx(),
            center = androidx.compose.ui.geometry.Offset(x, y)
        )
    }
}
