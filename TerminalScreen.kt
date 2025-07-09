package com.devvaultpro.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

data class TerminalLine(
    val text: String,
    val isCommand: Boolean = false,
    val isError: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TerminalScreen() {
    var terminalLines by remember { 
        mutableStateOf(
            listOf(
                TerminalLine("DevVault Pro Terminal v1.0", false),
                TerminalLine("Type 'help' for available commands", false),
                TerminalLine("user@devvault:~$ ", true)
            )
        )
    }
    var currentInput by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }
    var commandHistory by remember { mutableStateOf(listOf<String>()) }
    var historyIndex by remember { mutableStateOf(-1) }

    val listState = rememberLazyListState()

    // Auto-scroll to bottom when new lines are added
    LaunchedEffect(terminalLines.size) {
        if (terminalLines.isNotEmpty()) {
            listState.animateScrollToItem(terminalLines.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Terminal",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Green
            )

            Row {
                IconButton(
                    onClick = {
                        terminalLines = listOf(
                            TerminalLine("Terminal cleared", false),
                            TerminalLine("user@devvault:~$ ", true)
                        )
                    }
                ) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.White)
                }

                IconButton(
                    onClick = {
                        terminalLines = terminalLines + TerminalLine("", false) +
                                TerminalLine("Command history:", false) +
                                commandHistory.mapIndexed { index, cmd -> 
                                    TerminalLine("${index + 1}. $cmd", false)
                                } +
                                TerminalLine("user@devvault:~$ ", true)
                    }
                ) {
                    Icon(Icons.Default.History, contentDescription = "History", tint = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Terminal Output
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(terminalLines) { line ->
                Text(
                    text = line.text,
                    fontFamily = FontFamily.Monospace,
                    color = when {
                        line.isError -> Color.Red
                        line.isCommand -> Color.Green
                        else -> Color.White
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Input Section
        if (!isProcessing) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "user@devvault:~$ ",
                    fontFamily = FontFamily.Monospace,
                    color = Color.Green,
                    style = MaterialTheme.typography.bodyMedium
                )

                OutlinedTextField(
                    value = currentInput,
                    onValueChange = { currentInput = it },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedBorderColor = Color.Green,
                        unfocusedBorderColor = Color.Gray
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Monospace
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        if (currentInput.isNotBlank()) {
                            executeCommand(
                                command = currentInput,
                                onResult = { output, isError ->
                                    terminalLines = terminalLines.dropLast(1) + // Remove current prompt
                                            TerminalLine("user@devvault:~$ $currentInput", true) +
                                            output.map { TerminalLine(it, isError = isError) } +
                                            TerminalLine("user@devvault:~$ ", true)

                                    commandHistory = commandHistory + currentInput
                                    currentInput = ""
                                    isProcessing = false
                                }
                            )
                            isProcessing = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Green,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Execute")
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = Color.Green
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Executing command...",
                    color = Color.White,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

suspend fun executeCommand(
    command: String,
    onResult: (List<String>, Boolean) -> Unit
) {
    delay(500) // Simulate processing time

    val output = when (command.trim().lowercase()) {
        "help" -> listOf(
            "Available commands:",
            "  help          - Show this help message",
            "  ls            - List directory contents",
            "  pwd           - Print working directory",
            "  whoami        - Display current user",
            "  date          - Show current date and time",
            "  clear         - Clear terminal",
            "  history       - Show command history",
            "  ps            - Show running processes",
            "  mkdir <name>  - Create directory",
            "  touch <name>  - Create file",
            "  cat <file>    - Display file contents",
            "  git status    - Show git status",
            "  npm install   - Install npm packages",
            "  python --version - Show Python version"
        )

        "ls" -> listOf(
            "MainActivity.kt",
            "DevVaultProApp.kt",
            "AiScreen.kt",
            "NotesScreen.kt",
            "FileScreen.kt",
            "TerminalScreen.kt",
            "build.gradle",
            "README.md",
            "src/",
            "assets/"
        )

        "pwd" -> listOf("/home/user/devvault-pro")

        "whoami" -> listOf("developer")

        "date" -> listOf(java.util.Date().toString())

        "ps" -> listOf(
            "PID   COMMAND",
            "1234  DevVault Pro",
            "1235  Android Studio",
            "1236  Chrome",
            "1237  Terminal"
        )

        "git status" -> listOf(
            "On branch main",
            "Your branch is up to date with 'origin/main'.",
            "",
            "Changes not staged for commit:",
            "  modified:   AiScreen.kt",
            "  modified:   NotesScreen.kt",
            "",
            "Untracked files:",
            "  TerminalScreen.kt",
            "",
            "no changes added to commit (use \"git add\" and/or \"git commit -a\")"
        )

        "npm install" -> listOf(
            "Installing npm packages...",
            "✓ react@18.2.0",
            "✓ typescript@4.9.5",
            "✓ @types/react@18.0.28",
            "Installation complete!"
        )

        "python --version" -> listOf("Python 3.11.0")

        else -> when {
            command.startsWith("mkdir ") -> {
                val dirName = command.substringAfter("mkdir ").trim()
                listOf("Directory '$dirName' created")
            }

            command.startsWith("touch ") -> {
                val fileName = command.substringAfter("touch ").trim()
                listOf("File '$fileName' created")
            }

            command.startsWith("cat ") -> {
                val fileName = command.substringAfter("cat ").trim()
                listOf(
                    "Content of $fileName:",
                    "package com.devvaultpro",
                    "",
                    "class MainActivity {",
                    "    // Sample file content",
                    "}"
                )
            }

            else -> listOf("Command not found: $command", "Type 'help' for available commands")
        }
    }

    val isError = command.trim().let { cmd ->
        !listOf("help", "ls", "pwd", "whoami", "date", "ps", "git status", "npm install", "python --version").contains(cmd) &&
        !cmd.startsWith("mkdir ") && !cmd.startsWith("touch ") && !cmd.startsWith("cat ")
    }

    onResult(output, isError)
}