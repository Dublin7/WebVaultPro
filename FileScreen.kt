
package com.devvaultpro.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

data class FileItem(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val size: Long = 0,
    val lastModified: Long = System.currentTimeMillis(),
    val extension: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileScreen() {
    var files by remember { mutableStateOf(getSampleFiles()) }
    var currentPath by remember { mutableStateOf("/") }
    var selectedFile by remember { mutableStateOf<FileItem?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredFiles = files.filter { file ->
        searchQuery.isEmpty() || file.name.contains(searchQuery, ignoreCase = true)
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
                text = "File Manager",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row {
                IconButton(onClick = { showCreateDialog = true }) {
                    Icon(Icons.Default.CreateNewFolder, contentDescription = "Create Folder")
                }
                IconButton(onClick = { /* Add file functionality */ }) {
                    Icon(Icons.Default.NoteAdd, contentDescription = "Create File")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Current Path
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Folder,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = currentPath,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search files...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // File List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredFiles) { file ->
                FileItemCard(
                    file = file,
                    onClick = {
                        if (file.isDirectory) {
                            currentPath = file.path
                        } else {
                            selectedFile = file
                        }
                    }
                )
            }
        }
    }

    // Create Dialog
    if (showCreateDialog) {
        CreateFileDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name, isDirectory ->
                val newFile = FileItem(
                    name = name,
                    path = "$currentPath/$name",
                    isDirectory = isDirectory,
                    size = if (isDirectory) 0 else 1024,
                    extension = if (!isDirectory) name.substringAfterLast('.', "") else null
                )
                files = files + newFile
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun FileItemCard(
    file: FileItem,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when {
                    file.isDirectory -> Icons.Default.Folder
                    file.extension == "kt" -> Icons.Default.Code
                    file.extension == "java" -> Icons.Default.Code
                    file.extension == "xml" -> Icons.Default.Code
                    file.extension == "json" -> Icons.Default.DataObject
                    file.extension == "md" -> Icons.Default.Description
                    file.extension == "txt" -> Icons.Default.TextSnippet
                    file.extension == "png" || file.extension == "jpg" -> Icons.Default.Image
                    else -> Icons.Default.InsertDriveFile
                },
                contentDescription = null,
                tint = when {
                    file.isDirectory -> MaterialTheme.colorScheme.primary
                    file.extension == "kt" -> MaterialTheme.colorScheme.secondary
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = file.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                
                Row {
                    if (!file.isDirectory) {
                        Text(
                            text = formatFileSize(file.size),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = " • ",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = dateFormat.format(Date(file.lastModified)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            IconButton(onClick = { /* More options */ }) {
                Icon(Icons.Default.MoreVert, contentDescription = "More options")
            }
        }
    }
}

@Composable
fun CreateFileDialog(
    onDismiss: () -> Unit,
    onCreate: (String, Boolean) -> Unit
) {
    var fileName by remember { mutableStateOf("") }
    var isDirectory by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create ${if (isDirectory) "Folder" else "File"}") },
        text = {
            Column {
                OutlinedTextField(
                    value = fileName,
                    onValueChange = { fileName = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isDirectory,
                        onCheckedChange = { isDirectory = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Create as folder")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (fileName.isNotBlank()) {
                        onCreate(fileName, isDirectory)
                    }
                }
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

fun formatFileSize(bytes: Long): String {
    val kb = bytes / 1024.0
    val mb = kb / 1024.0
    val gb = mb / 1024.0
    
    return when {
        gb >= 1 -> "%.1f GB".format(gb)
        mb >= 1 -> "%.1f MB".format(mb)
        kb >= 1 -> "%.1f KB".format(kb)
        else -> "$bytes B"
    }
}

fun getSampleFiles(): List<FileItem> {
    return listOf(
        FileItem("src", "/src", true),
        FileItem("MainActivity.kt", "/src/MainActivity.kt", false, 2048, extension = "kt"),
        FileItem("DevVaultProApp.kt", "/src/DevVaultProApp.kt", false, 3072, extension = "kt"),
        FileItem("AiScreen.kt", "/src/AiScreen.kt", false, 4096, extension = "kt"),
        FileItem("NotesScreen.kt", "/src/NotesScreen.kt", false, 5120, extension = "kt"),
        FileItem("FileScreen.kt", "/src/FileScreen.kt", false, 3584, extension = "kt"),
        FileItem("build.gradle", "/build.gradle", false, 1024, extension = "gradle"),
        FileItem("README.md", "/README.md", false, 512, extension = "md"),
        FileItem("assets", "/assets", true),
        FileItem("app-icon.png", "/assets/app-icon.png", false, 8192, extension = "png"),
        FileItem("config.json", "/config.json", false, 256, extension = "json"),
        FileItem("docs", "/docs", true),
        FileItem("api-documentation.md", "/docs/api-documentation.md", false, 1536, extension = "md"),
        FileItem("AndroidManifest.xml", "/AndroidManifest.xml", false, 768, extension = "xml"),
        FileItem("styles.xml", "/res/values/styles.xml", false, 512, extension = "xml"),
        FileItem("strings.xml", "/res/values/strings.xml", false, 384, extension = "xml")
    )
}
