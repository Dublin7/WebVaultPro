
package com.devvaultpro.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class Note(
    val id: String,
    val title: String,
    val content: String,
    val tags: List<String> = emptyList(),
    val language: String = "text",
    val timestamp: Long = System.currentTimeMillis()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen() {
    var notes by remember { mutableStateOf(getSampleNotes()) }
    var searchQuery by remember { mutableStateOf("") }
    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedNote by remember { mutableStateOf<Note?>(null) }

    val filteredNotes = notes.filter { note ->
        searchQuery.isEmpty() || 
        note.title.contains(searchQuery, ignoreCase = true) ||
        note.content.contains(searchQuery, ignoreCase = true) ||
        note.tags.any { it.contains(searchQuery, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header with search
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Smart Notes",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Note")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search notes, tags, content...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Notes List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredNotes) { note ->
                NoteCard(
                    note = note,
                    onClick = { selectedNote = note }
                )
            }
        }
    }

    // Create Note Dialog
    if (showCreateDialog) {
        CreateNoteDialog(
            onDismiss = { showCreateDialog = false },
            onSave = { newNote ->
                notes = notes + newNote
                showCreateDialog = false
            }
        )
    }

    // Note Detail Dialog
    selectedNote?.let { note ->
        NoteDetailDialog(
            note = note,
            onDismiss = { selectedNote = null },
            onUpdate = { updatedNote ->
                notes = notes.map { if (it.id == updatedNote.id) updatedNote else it }
                selectedNote = null
            }
        )
    }
}

@Composable
fun NoteCard(
    note: Note,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = note.language.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = note.content.take(100) + if (note.content.length > 100) "..." else "",
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = FontFamily.Monospace
            )
            
            if (note.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    note.tags.forEach { tag ->
                        AssistChip(
                            onClick = { },
                            label = { Text(tag, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNoteDialog(
    onDismiss: () -> Unit,
    onSave: (Note) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var language by remember { mutableStateOf("kotlin") }
    var tags by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Note") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Content") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 5
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = language,
                    onValueChange = { language = it },
                    label = { Text("Language") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = tags,
                    onValueChange = { tags = it },
                    label = { Text("Tags (comma separated)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        onSave(
                            Note(
                                id = System.currentTimeMillis().toString(),
                                title = title,
                                content = content,
                                language = language,
                                tags = tags.split(",").map { it.trim() }.filter { it.isNotBlank() }
                            )
                        )
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailDialog(
    note: Note,
    onDismiss: () -> Unit,
    onUpdate: (Note) -> Unit
) {
    var content by remember { mutableStateOf(note.content) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(note.title) },
        text = {
            Column {
                Text(
                    text = "Language: ${note.language}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    maxLines = 8
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onUpdate(note.copy(content = content))
                }
            ) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

fun getSampleNotes(): List<Note> {
    return listOf(
        Note(
            id = "1",
            title = "Kotlin Coroutines Cheatsheet",
            content = """
                // Basic coroutine launch
                GlobalScope.launch {
                    delay(1000)
                    println("Hello from coroutine!")
                }
                
                // Async/await pattern
                val deferred = async { 
                    expensiveOperation() 
                }
                val result = deferred.await()
            """.trimIndent(),
            tags = listOf("kotlin", "coroutines", "async"),
            language = "kotlin"
        ),
        Note(
            id = "2",
            title = "Git Commands Reference",
            content = """
                git status
                git add .
                git commit -m "message"
                git push origin main
                git pull origin main
                git branch feature-name
                git checkout feature-name
                git merge feature-name
            """.trimIndent(),
            tags = listOf("git", "version-control", "reference"),
            language = "bash"
        ),
        Note(
            id = "3",
            title = "API Response Model",
            content = """
                data class ApiResponse<T>(
                    val data: T?,
                    val message: String,
                    val success: Boolean,
                    val errorCode: Int? = null
                )
                
                sealed class NetworkResult<T> {
                    data class Success<T>(val data: T) : NetworkResult<T>()
                    data class Error<T>(val message: String) : NetworkResult<T>()
                    class Loading<T> : NetworkResult<T>()
                }
            """.trimIndent(),
            tags = listOf("kotlin", "api", "model", "networking"),
            language = "kotlin"
        )
    )
}
