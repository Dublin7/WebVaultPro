
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
    val extension: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileScreen() {
    var currentPath by remember { mutableStateOf("/") }
    var files by remember { mutableStateOf(getSampleFiles()) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFiles by remember { mutableStateOf(setOf<String>()) }
    var showBulkActions by remember { mutableStateOf(false) }

    val filteredFiles = files.filter { file ->
        searchQuery.isEmpty() || 
        file.name.contains(searchQuery, ignoreCase = true) ||
        file.extension.contains(searchQuery, ignoreCase = true)
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
                IconButton(onClick = { /* TODO: Create new file */ }) {
                    Icon(Icons.Default.Add, contentDescription = "New File")
                }
                IconButton(onClick = { showBulkActions = !showBulkActions }) {
                    Icon(Icons.Default.Settings, contentDescription = "Bulk Actions")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Path breadcrumb
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "📁 $currentPath",
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Search and filter
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search files, extensions...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Bulk actions
        if (showBulkActions && selectedFiles.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${selectedFiles.size} files selected")
                    Row {
                        TextButton(onClick = { /* TODO: Bulk rename */ }) {
                            Text("Rename")
                        }
                        TextButton(onClick = { /* TODO: Bulk move */ }) {
                            Text("Move")
                        }
                        TextButton(onClick = { /* TODO: Bulk delete */ }) {
                            Text("Delete")
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // File list
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(filteredFiles) { file ->
                FileItemCard(
                    file = file,
                    isSelected = selectedFiles.contains(file.path),
                    onSelectionChange = { isSelected ->
                        selectedFiles = if (isSelected) {
                            selectedFiles + file.path
                        } else {
                            selectedFiles - file.path
                        }
                    },
                    onClick = {
                        if (file.isDirectory) {
                            currentPath = file.path
                            // TODO: Load directory contents
                        } else {
                            // TODO: Open file
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun FileItemCard(
    file: FileItem,
    isSelected: Boolean,
    onSelectionChange: (Boolean) -> Unit,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = onSelectionChange
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Icon(
                imageVector = when {
                    file.isDirectory -> Icons.Default.Folder
                    file.extension == "kt" -> Icons.Default.Code
                    file.extension == "json" -> Icons.Default.DataObject
                    file.extension == "md" -> Icons.Default.Description
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
            
            IconButton(onClick = { /* TODO: File actions menu */ }) {
                Icon(Icons.Default.MoreVert, contentDescription = "More actions")
            }
        }
    }
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
        FileItem("build.gradle", "/build.gradle", false, 1024, extension = "gradle"),
        FileItem("README.md", "/README.md", false, 512, extension = "md"),
        FileItem("assets", "/assets", true),
        FileItem("app-icon.png", "/assets/app-icon.png", false, 8192, extension = "png"),
        FileItem("config.json", "/config.json", false, 256, extension = "json"),
        FileItem("docs", "/docs", true),
        FileItem("api-documentation.md", "/docs/api-documentation.md", false, 1536, extension = "md")
    )
}
