
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class CodeSnippet(
    val id: String,
    val title: String,
    val code: String,
    val language: String,
    val tags: List<String>,
    val description: String,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeSnippetScreen() {
    var snippets by remember { 
        mutableStateOf(listOf(
            CodeSnippet(
                "1", "Retrofit Setup", 
                "val retrofit = Retrofit.Builder()\n    .baseUrl(\"https://api.github.com/\")\n    .addConverterFactory(GsonConverterFactory.create())\n    .build()",
                "kotlin", listOf("android", "networking"), "Basic Retrofit configuration"
            ),
            CodeSnippet(
                "2", "Room Database Entity",
                "@Entity(tableName = \"users\")\ndata class User(\n    @PrimaryKey val id: Int,\n    val name: String,\n    val email: String\n)",
                "kotlin", listOf("android", "database"), "Room entity example"
            ),
            CodeSnippet(
                "3", "JWT Token Validation",
                "function validateToken(token) {\n  try {\n    const decoded = jwt.verify(token, process.env.JWT_SECRET);\n    return decoded;\n  } catch (error) {\n    return null;\n  }\n}",
                "javascript", listOf("auth", "backend"), "JWT validation function"
            )
        ))
    }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }

    val languages = listOf("All", "kotlin", "javascript", "python", "java", "swift", "dart")
    
    val filteredSnippets = snippets.filter { snippet ->
        (selectedLanguage == "All" || snippet.language == selectedLanguage) &&
        (searchQuery.isEmpty() || snippet.title.contains(searchQuery, ignoreCase = true) ||
         snippet.code.contains(searchQuery, ignoreCase = true) ||
         snippet.tags.any { it.contains(searchQuery, ignoreCase = true) })
    }

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
                text = "Code Snippets",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Button(
                onClick = { showAddDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search snippets...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Language filter
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(languages) { language ->
                FilterChip(
                    onClick = { selectedLanguage = language },
                    label = { Text(language) },
                    selected = selectedLanguage == language
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Snippets list
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredSnippets) { snippet ->
                SnippetCard(
                    snippet = snippet,
                    onFavoriteToggle = { id ->
                        snippets = snippets.map { 
                            if (it.id == id) it.copy(isFavorite = !it.isFavorite) else it 
                        }
                    },
                    onDelete = { id ->
                        snippets = snippets.filter { it.id != id }
                    }
                )
            }
        }
    }

    if (showAddDialog) {
        AddSnippetDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { newSnippet ->
                snippets = snippets + newSnippet.copy(id = (snippets.size + 1).toString())
                showAddDialog = false
            }
        )
    }
}

@Composable
fun SnippetCard(
    snippet: CodeSnippet,
    onFavoriteToggle: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
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
                        text = snippet.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = snippet.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Row {
                    IconButton(onClick = { onFavoriteToggle(snippet.id) }) {
                        Icon(
                            if (snippet.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (snippet.isFavorite) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { onDelete(snippet.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Language badge
            AssistChip(
                onClick = { },
                label = { Text(snippet.language.uppercase()) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Code with syntax highlighting simulation
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
            ) {
                SelectionContainer {
                    Text(
                        text = snippet.code,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFFD4D4D4),
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tags
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(snippet.tags) { tag ->
                    AssistChip(
                        onClick = { },
                        label = { Text("#$tag") }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSnippetDialog(
    onDismiss: () -> Unit,
    onAdd: (CodeSnippet) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var language by remember { mutableStateOf("kotlin") }
    var description by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Code Snippet") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = language,
                    onValueChange = { language = it },
                    label = { Text("Language") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Code") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 8
                )
                
                OutlinedTextField(
                    value = tags,
                    onValueChange = { tags = it },
                    label = { Text("Tags (comma separated)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && code.isNotBlank()) {
                        onAdd(
                            CodeSnippet(
                                id = "",
                                title = title,
                                code = code,
                                language = language,
                                description = description,
                                tags = tags.split(",").map { it.trim() }.filter { it.isNotBlank() }
                            )
                        )
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
