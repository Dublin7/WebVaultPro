
package com.devvaultpro.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.random.Random

data class SystemMetrics(
    val cpuUsage: Float,
    val memoryUsage: Float,
    val diskUsage: Float,
    val networkIn: Float,
    val networkOut: Float,
    val timestamp: Long = System.currentTimeMillis()
)

data class ProcessInfo(
    val name: String,
    val pid: Int,
    val cpuUsage: Float,
    val memoryUsage: Float,
    val status: String
)

@Composable
fun PerformanceMonitorScreen() {
    var metrics by remember { mutableStateOf<List<SystemMetrics>>(emptyList()) }
    var currentMetrics by remember { mutableStateOf(SystemMetrics(0f, 0f, 0f, 0f, 0f)) }
    var processes by remember { 
        mutableStateOf(listOf(
            ProcessInfo("Android Studio", 1234, 45.2f, 1024f, "Running"),
            ProcessInfo("Chrome", 5678, 23.1f, 512f, "Running"),
            ProcessInfo("VS Code", 9012, 15.7f, 256f, "Running"),
            ProcessInfo("Spotify", 3456, 8.3f, 128f, "Running"),
            ProcessInfo("Terminal", 7890, 2.1f, 64f, "Running")
        ))
    }
    var selectedTab by remember { mutableStateOf(0) }

    // Simulate real-time data updates
    LaunchedEffect(Unit) {
        while (true) {
            val newMetrics = SystemMetrics(
                cpuUsage = Random.nextFloat() * 100,
                memoryUsage = 60f + Random.nextFloat() * 30,
                diskUsage = 45f + Random.nextFloat() * 10,
                networkIn = Random.nextFloat() * 50,
                networkOut = Random.nextFloat() * 30
            )
            
            currentMetrics = newMetrics
            metrics = (metrics + newMetrics).takeLast(50)
            
            // Update processes
            processes = processes.map { process ->
                process.copy(
                    cpuUsage = maxOf(0f, process.cpuUsage + Random.nextFloat() * 10 - 5),
                    memoryUsage = maxOf(32f, process.memoryUsage + Random.nextFloat() * 50 - 25)
                )
            }
            
            delay(2000)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "System Performance",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Real-time metrics cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricCard(
                title = "CPU",
                value = "${currentMetrics.cpuUsage.toInt()}%",
                icon = Icons.Default.Memory,
                color = Color(0xFF2196F3),
                modifier = Modifier.weight(1f)
            )
            
            MetricCard(
                title = "Memory",
                value = "${currentMetrics.memoryUsage.toInt()}%",
                icon = Icons.Default.Storage,
                color = Color(0xFF4CAF50),
                modifier = Modifier.weight(1f)
            )
            
            MetricCard(
                title = "Disk",
                value = "${currentMetrics.diskUsage.toInt()}%",
                icon = Icons.Default.FolderOpen,
                color = Color(0xFFFF9800),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Network metrics
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricCard(
                title = "Network In",
                value = "${currentMetrics.networkIn.toInt()} MB/s",
                icon = Icons.Default.CloudDownload,
                color = Color(0xFF9C27B0),
                modifier = Modifier.weight(1f)
            )
            
            MetricCard(
                title = "Network Out",
                value = "${currentMetrics.networkOut.toInt()} MB/s",
                icon = Icons.Default.CloudUpload,
                color = Color(0xFFF44336),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Tabs
        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                text = { Text("Charts") },
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 }
            )
            Tab(
                text = { Text("Processes") },
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTab) {
            0 -> ChartsTab(metrics)
            1 -> ProcessesTab(processes)
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
fun ChartsTab(metrics: List<SystemMetrics>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ChartCard(
                title = "CPU Usage",
                data = metrics.map { it.cpuUsage },
                color = Color(0xFF2196F3),
                unit = "%"
            )
        }
        
        item {
            ChartCard(
                title = "Memory Usage",
                data = metrics.map { it.memoryUsage },
                color = Color(0xFF4CAF50),
                unit = "%"
            )
        }
        
        item {
            ChartCard(
                title = "Network Activity",
                data = metrics.map { it.networkIn + it.networkOut },
                color = Color(0xFF9C27B0),
                unit = "MB/s"
            )
        }
    }
}

@Composable
fun ChartCard(
    title: String,
    data: List<Float>,
    color: Color,
    unit: String
) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (data.isNotEmpty()) {
                val currentValue = data.lastOrNull() ?: 0f
                Text(
                    text = "${currentValue.toInt()} $unit",
                    style = MaterialTheme.typography.headlineSmall,
                    color = color
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                ) {
                    drawChart(data, color)
                }
            }
        }
    }
}

fun DrawScope.drawChart(data: List<Float>, color: Color) {
    if (data.size < 2) return
    
    val maxValue = data.maxOrNull() ?: 100f
    val minValue = data.minOrNull() ?: 0f
    val range = maxValue - minValue
    
    val stepX = size.width / (data.size - 1)
    val path = Path()
    
    data.forEachIndexed { index, value ->
        val x = index * stepX
        val y = size.height - ((value - minValue) / range) * size.height
        
        if (index == 0) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
    }
    
    drawPath(
        path = path,
        color = color,
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
    )
}

@Composable
fun ProcessesTab(processes: List<ProcessInfo>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(processes) { process ->
            ProcessCard(process = process)
        }
    }
}

@Composable
fun ProcessCard(process: ProcessInfo) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = process.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "PID: ${process.pid}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                AssistChip(
                    onClick = { },
                    label = { Text(process.status) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = when (process.status) {
                            "Running" -> Color(0xFF4CAF50)
                            "Sleeping" -> Color(0xFFFF9800)
                            "Stopped" -> Color(0xFFF44336)
                            else -> MaterialTheme.colorScheme.primary
                        }
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "CPU: ${process.cpuUsage.toInt()}%",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    LinearProgressIndicator(
                        progress = process.cpuUsage / 100f,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Memory: ${process.memoryUsage.toInt()} MB",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    LinearProgressIndicator(
                        progress = process.memoryUsage / 2048f,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
