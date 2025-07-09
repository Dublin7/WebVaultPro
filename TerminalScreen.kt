
package com.devvaultpro.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class TerminalSession(
    val id: String,
    val name: String,
    val outputLines: MutableList<TerminalLine>,
    val currentDirectory: String = "~",
    val environment: Map<String, String> = emptyMap()
)

data class TerminalLine(
    val text: String,
    val type: LineType,
    val timestamp: Long = System.currentTimeMillis()
)

enum class LineType {
    COMMAND, OUTPUT, ERROR, SYSTEM
}

data class CommandHistory(
    val command: String,
    val timestamp: Long,
    val directory: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TerminalScreen() {
    var sessions by remember { mutableStateOf(listOf(
        TerminalSession(
            id = "main",
            name = "Main Terminal",
            outputLines = mutableListOf(
                TerminalLine("Welcome to DevVault Pro Terminal v2.0", LineType.SYSTEM),
                TerminalLine("Enhanced developer environment with AI integration", LineType.SYSTEM),
                TerminalLine("Type 'help' for available commands", LineType.SYSTEM)
            )
        )
    )) }
    
    var activeSessionId by remember { mutableStateOf("main") }
    var currentCommand by remember { mutableStateOf("") }
    var commandHistory by remember { mutableStateOf(listOf<CommandHistory>()) }
    var favoriteCommands by remember { mutableStateOf(listOf(
        "ls -la",
        "git status",
        "npm install",
        "docker ps",
        "tail -f logs/app.log"
    )) }
    var historyIndex by remember { mutableStateOf(-1) }
    var showCommandPalette by remember { mutableStateOf(false) }
    var showFavorites by remember { mutableStateOf(false) }
    var showSystemInfo by remember { mutableStateOf(false) }
    
    val focusRequester = remember { FocusRequester() }
    val scope = rememberCoroutineScope()
    val clipboard = LocalClipboardManager.current
    val listState = rememberLazyListState()
    
    val activeSession = sessions.find { it.id == activeSessionId } ?: sessions.first()
    
    LaunchedEffect(activeSession.outputLines.size) {
        if (activeSession.outputLines.isNotEmpty()) {
            listState.animateScrollToItem(activeSession.outputLines.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
            .padding(16.dp)
    ) {
        // Terminal Header with tabs and controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Session tabs
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                sessions.forEach { session ->
                    Card(
                        modifier = Modifier
                            .clickable { activeSessionId = session.id }
                            .padding(2.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (session.id == activeSessionId) 
                                Color(0xFF0D7377) else Color(0xFF2D2D2D)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Terminal,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                session.name,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
                
                // Add new session button
                IconButton(
                    onClick = {
                        val newId = "session_${System.currentTimeMillis()}"
                        sessions = sessions + TerminalSession(
                            id = newId,
                            name = "Terminal ${sessions.size + 1}",
                            outputLines = mutableListOf(
                                TerminalLine("New terminal session started", LineType.SYSTEM)
                            )
                        )
                        activeSessionId = newId
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "New Session", tint = Color.White)
                }
            }
            
            // Control buttons
            Row {
                IconButton(onClick = { showSystemInfo = !showSystemInfo }) {
                    Icon(Icons.Default.Info, contentDescription = "System Info", tint = Color.White)
                }
                IconButton(onClick = { showFavorites = !showFavorites }) {
                    Icon(Icons.Default.Star, contentDescription = "Favorites", tint = Color.White)
                }
                IconButton(onClick = { showCommandPalette = !showCommandPalette }) {
                    Icon(Icons.Default.Search, contentDescription = "Command Palette", tint = Color.White)
                }
                IconButton(
                    onClick = {
                        // Clear terminal
                        sessions = sessions.map { session ->
                            if (session.id == activeSessionId) {
                                session.copy(outputLines = mutableListOf(
                                    TerminalLine("Terminal cleared", LineType.SYSTEM)
                                ))
                            } else session
                        }
                    }
                ) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.White)
                }
            }
        }
        
        // System info panel
        if (showSystemInfo) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D2D))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("System Information", color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("OS: Android (Linux)", color = Color.Gray, fontSize = 12.sp)
                    Text("Shell: DevVault Shell v2.0", color = Color.Gray, fontSize = 12.sp)
                    Text("Current Dir: ${activeSession.currentDirectory}", color = Color.Gray, fontSize = 12.sp)
                    Text("Session: ${activeSession.name}", color = Color.Gray, fontSize = 12.sp)
                }
            }
        }
        
        // Favorites panel
        if (showFavorites) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D2D))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Favorite Commands", color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    favoriteCommands.forEach { command ->
                        Text(
                            text = command,
                            color = Color(0xFF4CAF50),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier
                                .clickable {
                                    currentCommand = command
                                    showFavorites = false
                                }
                                .padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }
        
        // Terminal output
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0D0D0D)),
            shape = RoundedCornerShape(8.dp)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(activeSession.outputLines) { line ->
                    TerminalLineItem(line = line, onCopy = { text ->
                        clipboard.setText(AnnotatedString(text))
                    })
                }
                
                // Current input line
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "devvault@pro:${activeSession.currentDirectory}$ ",
                            color = Color(0xFF00FF00),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp
                        )
                        Text(
                            text = currentCommand,
                            color = Color.White,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp
                        )
                        
                        // Cursor
                        var showCursor by remember { mutableStateOf(true) }
                        LaunchedEffect(Unit) {
                            while (true) {
                                delay(500)
                                showCursor = !showCursor
                            }
                        }
                        
                        if (showCursor) {
                            Text(
                                text = "█",
                                color = Color.White,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Command input
        OutlinedTextField(
            value = currentCommand,
            onValueChange = { 
                currentCommand = it
                historyIndex = -1
            },
            placeholder = { 
                Text(
                    "Enter command...", 
                    color = Color.Gray,
                    fontFamily = FontFamily.Monospace
                ) 
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onKeyEvent { event ->
                    when {
                        event.key == Key.Enter && event.type == KeyEventType.KeyDown -> {
                            executeCommand(
                                command = currentCommand,
                                sessions = sessions,
                                activeSessionId = activeSessionId,
                                onSessionsUpdate = { sessions = it },
                                onHistoryUpdate = { history ->
                                    commandHistory = history + CommandHistory(
                                        command = currentCommand,
                                        timestamp = System.currentTimeMillis(),
                                        directory = activeSession.currentDirectory
                                    )
                                }
                            )
                            currentCommand = ""
                            historyIndex = -1
                            true
                        }
                        event.key == Key.DirectionUp && event.type == KeyEventType.KeyDown -> {
                            if (commandHistory.isNotEmpty()) {
                                if (historyIndex < commandHistory.size - 1) {
                                    historyIndex++
                                    currentCommand = commandHistory[commandHistory.size - 1 - historyIndex].command
                                }
                            }
                            true
                        }
                        event.key == Key.DirectionDown && event.type == KeyEventType.KeyDown -> {
                            if (historyIndex > 0) {
                                historyIndex--
                                currentCommand = commandHistory[commandHistory.size - 1 - historyIndex].command
                            } else if (historyIndex == 0) {
                                historyIndex = -1
                                currentCommand = ""
                            }
                            true
                        }
                        event.key == Key.Tab && event.type == KeyEventType.KeyDown -> {
                            showCommandPalette = true
                            true
                        }
                        else -> false
                    }
                },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFF0D7377),
                unfocusedBorderColor = Color.Gray,
                cursorColor = Color.White
            ),
            textStyle = androidx.compose.ui.text.TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp
            ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Text
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    executeCommand(
                        command = currentCommand,
                        sessions = sessions,
                        activeSessionId = activeSessionId,
                        onSessionsUpdate = { sessions = it },
                        onHistoryUpdate = { history ->
                            commandHistory = history + CommandHistory(
                                command = currentCommand,
                                timestamp = System.currentTimeMillis(),
                                directory = activeSession.currentDirectory
                            )
                        }
                    )
                    currentCommand = ""
                    historyIndex = -1
                }
            )
        )
        
        // Command history panel
        if (showCommandPalette) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D2D))
            ) {
                LazyColumn(
                    modifier = Modifier.padding(8.dp)
                ) {
                    item {
                        Text("Recent Commands", color = Color.White, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(commandHistory.takeLast(10).reversed()) { historyItem ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    currentCommand = historyItem.command
                                    showCommandPalette = false
                                }
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = historyItem.command,
                                color = Color(0xFF4CAF50),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp
                            )
                            Text(
                                text = SimpleDateFormat("HH:mm", Locale.getDefault())
                                    .format(Date(historyItem.timestamp)),
                                color = Color.Gray,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
    
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
fun TerminalLineItem(
    line: TerminalLine,
    onCopy: (String) -> Unit
) {
    val annotatedText = buildAnnotatedString {
        when (line.type) {
            LineType.COMMAND -> {
                withStyle(SpanStyle(color = Color(0xFF00FF00))) {
                    append("$ ")
                }
                withStyle(SpanStyle(color = Color.White)) {
                    append(line.text)
                }
            }
            LineType.OUTPUT -> {
                withStyle(SpanStyle(color = Color(0xFFE0E0E0))) {
                    append(line.text)
                }
            }
            LineType.ERROR -> {
                withStyle(SpanStyle(color = Color(0xFFFF5252))) {
                    append(line.text)
                }
            }
            LineType.SYSTEM -> {
                withStyle(SpanStyle(color = Color(0xFF2196F3))) {
                    append(line.text)
                }
            }
        }
    }
    
    Text(
        text = annotatedText,
        fontFamily = FontFamily.Monospace,
        fontSize = 14.sp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCopy(line.text) }
    )
}

fun executeCommand(
    command: String,
    sessions: List<TerminalSession>,
    activeSessionId: String,
    onSessionsUpdate: (List<TerminalSession>) -> Unit,
    onHistoryUpdate: (List<CommandHistory>) -> Unit
) {
    val trimmedCommand = command.trim()
    if (trimmedCommand.isEmpty()) return
    
    val updatedSessions = sessions.map { session ->
        if (session.id == activeSessionId) {
            val newOutputLines = session.outputLines.toMutableList()
            newOutputLines.add(TerminalLine(trimmedCommand, LineType.COMMAND))
            
            // Simulate command execution
            val output = when {
                trimmedCommand == "help" -> listOf(
                    "DevVault Pro Terminal Commands:",
                    "  help          - Show this help message",
                    "  ls            - List directory contents",
                    "  pwd           - Show current directory",
                    "  clear         - Clear terminal",
                    "  git status    - Show git repository status",
                    "  npm install   - Install npm dependencies",
                    "  code .        - Open current directory in editor",
                    "  system info   - Show system information"
                )
                trimmedCommand == "ls" || trimmedCommand == "ls -la" -> listOf(
                    "total 24",
                    "drwxr-xr-x  8 devvault devvault 4096 Jul  9 19:30 .",
                    "drwxr-xr-x  3 devvault devvault 4096 Jul  9 19:00 ..",
                    "-rw-r--r--  1 devvault devvault  123 Jul  9 19:30 README.md",
                    "drwxr-xr-x  2 devvault devvault 4096 Jul  9 19:25 src",
                    "-rw-r--r--  1 devvault devvault  456 Jul  9 19:28 package.json"
                )
                trimmedCommand == "pwd" -> listOf("/home/devvault/workspace")
                trimmedCommand == "git status" -> listOf(
                    "On branch main",
                    "Your branch is up to date with 'origin/main'.",
                    "",
                    "Changes not staged for commit:",
                    "  modified:   src/main.kt",
                    "  modified:   README.md",
                    "",
                    "no changes added to commit"
                )
                trimmedCommand.startsWith("echo ") -> listOf(trimmedCommand.substring(5))
                trimmedCommand == "date" -> listOf(Date().toString())
                trimmedCommand == "whoami" -> listOf("devvault")
                trimmedCommand == "system info" -> listOf(
                    "DevVault Pro System Information",
                    "OS: Android (Linux Kernel)",
                    "Shell: DevVault Enhanced Terminal v2.0",
                    "Architecture: ARM64",
                    "Memory: 8GB RAM",
                    "Storage: 256GB"
                )
                else -> listOf("Command not found: $trimmedCommand")
            }
            
            output.forEach { line ->
                newOutputLines.add(
                    TerminalLine(
                        line, 
                        if (line.contains("not found") || line.contains("error")) 
                            LineType.ERROR else LineType.OUTPUT
                    )
                )
            }
            
            session.copy(outputLines = newOutputLines)
        } else session
    }
    
    onSessionsUpdate(updatedSessions)
}
