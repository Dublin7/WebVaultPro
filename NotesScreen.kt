
package com.devvaultpro.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

data class Note(
    val id: String,
    val title: String,
    val content: String,
    val tags: List<String> = emptyList(),
    val language: String = "text",
    val createdAt: Long = System.currentTimeMillis()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen() {
    var notes by remember { mutableStateOf(getSampleNotes()) }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedNote by remember { mutableStateOf<Note?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedTag by remember { mutableStateOf("All") }

    val filteredNotes = notes.filter { note ->
        val matchesSearch = searchQuery.isEmpty() || 
                           note.title.contains(searchQuery, ignoreCase = true) ||
                           note.content.contains(searchQuery, ignoreCase = true)
        val matchesTag = selectedTag == "All" || note.tags.contains(selectedTag)
        matchesSearch && matchesTag
    }

    val allTags = listOf("All") + notes.flatMap { it.tags }.distinct()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Developer Notes",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Note")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search notes...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Tag Filter
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(allTags) { tag ->
                FilterChip(
                    onClick = { selectedTag = tag },
                    label = { Text(tag) },
                    selected = selectedTag == tag
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Notes List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredNotes) { note ->
                NoteCard(
                    note = note,
                    onClick = { selectedNote = note },
                    onDelete = { notes = notes.filter { it.id != note.id } }
                )
            }
        }
    }

    // Add Note Dialog
    if (showAddDialog) {
        CreateNoteDialog(
            onDismiss = { showAddDialog = false },
            onSave = { newNote ->
                notes = notes + newNote.copy(id = System.currentTimeMillis().toString())
                showAddDialog = false
            }
        )
    }

    // View Note Dialog
    selectedNote?.let { note ->
        ViewNoteDialog(
            note = note,
            onDismiss = { selectedNote = null },
            onEdit = { editedNote ->
                notes = notes.map { if (it.id == editedNote.id) editedNote else it }
                selectedNote = null
            }
        )
    }
}

@Composable
fun NoteCard(
    note: Note,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = note.content.take(100) + if (note.content.length > 100) "..." else "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(note.tags) { tag ->
                    AssistChip(
                        onClick = { },
                        label = { Text(tag, style = MaterialTheme.typography.bodySmall) }
                    )
                }
            }
        }
    }
}

@Composable
fun CreateNoteDialog(
    onDismiss: () -> Unit,
    onSave: (Note) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var language by remember { mutableStateOf("kotlin") }
    var tags by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Create New Note",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
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
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 5,
                    maxLines = 10
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
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = {
                            if (title.isNotBlank() && content.isNotBlank()) {
                                val note = Note(
                                    id = "",
                                    title = title,
                                    content = content,
                                    language = language,
                                    tags = tags.split(",").map { it.trim() }.filter { it.isNotBlank() }
                                )
                                onSave(note)
                            }
                        }
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@Composable
fun ViewNoteDialog(
    note: Note,
    onDismiss: () -> Unit,
    onEdit: (Note) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf(note.title) }
    var content by remember { mutableStateOf(note.content) }
    var language by remember { mutableStateOf(note.language) }
    var tags by remember { mutableStateOf(note.tags.joinToString(", ")) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEditing) "Edit Note" else note.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    IconButton(
                        onClick = { isEditing = !isEditing }
                    ) {
                        Icon(
                            if (isEditing) Icons.Default.Close else Icons.Default.Edit,
                            contentDescription = if (isEditing) "Cancel Edit" else "Edit Note"
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                if (isEditing) {
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
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 5,
                        maxLines = 10
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
                } else {
                    SelectionContainer {
                        Text(
                            text = note.content,
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = if (note.language != "text") FontFamily.Monospace else FontFamily.Default,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(note.tags) { tag ->
                            AssistChip(
                                onClick = { },
                                label = { Text(tag, style = MaterialTheme.typography.bodySmall) }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Close")
                    }
                    
                    if (isEditing) {
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Button(
                            onClick = {
                                if (title.isNotBlank() && content.isNotBlank()) {
                                    val editedNote = note.copy(
                                        title = title,
                                        content = content,
                                        language = language,
                                        tags = tags.split(",").map { it.trim() }.filter { it.isNotBlank() }
                                    )
                                    onEdit(editedNote)
                                }
                            }
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }
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
                
                // Suspend function
                suspend fun fetchUser(id: String): User {
                    return withContext(Dispatchers.IO) {
                        apiService.getUser(id)
                    }
                }
            """.trimIndent(),
            tags = listOf("kotlin", "coroutines", "async"),
            language = "kotlin"
        ),
        Note(
            id = "2",
            title = "Git Commands Reference",
            content = """
                # Basic Git Commands
                git status
                git add .
                git commit -m "message"
                git push origin main
                git pull origin main
                
                # Branch Operations
                git branch feature-name
                git checkout feature-name
                git merge feature-name
                git branch -d feature-name
                
                # Remote Operations
                git remote add origin <url>
                git fetch origin
                git reset --hard origin/main
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
                
                // Usage
                suspend fun fetchData(): NetworkResult<ApiResponse<User>> {
                    return try {
                        val response = apiService.getUser()
                        NetworkResult.Success(response)
                    } catch (e: Exception) {
                        NetworkResult.Error(e.message ?: "Unknown error")
                    }
                }
            """.trimIndent(),
            tags = listOf("kotlin", "api", "model", "networking"),
            language = "kotlin"
        )
    )
}
