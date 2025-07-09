
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

data class GitHubRepo(
    val name: String,
    val fullName: String,
    val description: String,
    val private: Boolean,
    val url: String,
    val lastUpdated: String
)

data class CommitInfo(
    val hash: String,
    val message: String,
    val author: String,
    val date: String
)

enum class UploadStatus {
    IDLE, AUTHENTICATING, CREATING_REPO, UPLOADING_FILES, COMMITTING, PUSHING, COMPLETED, FAILED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GitHubScreen() {
    var githubToken by remember { mutableStateOf("") }
    var isConnected by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("") }
    var repositories by remember { mutableStateOf(listOf<GitHubRepo>()) }
    var uploadStatus by remember { mutableStateOf(UploadStatus.IDLE) }
    var selectedTab by remember { mutableStateOf(0) }
    var statusMessage by remember { mutableStateOf("") }
    
    // Project upload fields
    var projectName by remember { mutableStateOf("devvault-pro") }
    var projectDescription by remember { mutableStateOf("DevVault Pro - Comprehensive Development Toolkit") }
    var isPrivateRepo by remember { mutableStateOf(false) }
    var commitMessage by remember { mutableStateOf("Initial commit - DevVault Pro project") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "GitHub Integration",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Connection Status
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isConnected) 
                    MaterialTheme.colorScheme.primaryContainer 
                else 
                    MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isConnected) "Connected to GitHub" else "Not Connected",
                        fontWeight = FontWeight.Bold,
                        color = if (isConnected) 
                            MaterialTheme.colorScheme.onPrimaryContainer 
                        else 
                            MaterialTheme.colorScheme.onErrorContainer
                    )
                    if (isConnected) {
                        Text(
                            text = "Welcome, $userName!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                
                Icon(
                    imageVector = if (isConnected) Icons.Default.CheckCircle else Icons.Default.Error,
                    contentDescription = null,
                    tint = if (isConnected) 
                        MaterialTheme.colorScheme.onPrimaryContainer 
                    else 
                        MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        // Authentication Section
        if (!isConnected) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "GitHub Authentication",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    OutlinedTextField(
                        value = githubToken,
                        onValueChange = { githubToken = it },
                        label = { Text("Personal Access Token") },
                        placeholder = { Text("ghp_xxxxxxxxxxxxxxxxxxxx") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null)
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Get your token from GitHub Settings > Developer Settings > Personal Access Tokens",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = {
                            if (githubToken.isNotBlank()) {
                                isConnected = true
                                userName = "developer" // Simulate API call
                                statusMessage = "Successfully connected to GitHub!"
                                // Load repositories
                                repositories = listOf(
                                    GitHubRepo(
                                        "my-android-app", "developer/my-android-app",
                                        "Android application", false,
                                        "https://github.com/developer/my-android-app",
                                        "2 days ago"
                                    ),
                                    GitHubRepo(
                                        "web-portfolio", "developer/web-portfolio",
                                        "Personal portfolio website", false,
                                        "https://github.com/developer/web-portfolio",
                                        "1 week ago"
                                    )
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = githubToken.isNotBlank()
                    ) {
                        Icon(Icons.Default.Login, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Connect to GitHub")
                    }
                }
            }
        } else {
            // Tabs for different sections
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Upload Project") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("My Repositories") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                0 -> {
                    // Upload Project Tab
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Upload DevVault Pro to GitHub",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            OutlinedTextField(
                                value = projectName,
                                onValueChange = { projectName = it },
                                label = { Text("Repository Name") },
                                modifier = Modifier.fillMaxWidth(),
                                leadingIcon = {
                                    Icon(Icons.Default.Storage, contentDescription = null)
                                }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = projectDescription,
                                onValueChange = { projectDescription = it },
                                label = { Text("Description") },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 3,
                                leadingIcon = {
                                    Icon(Icons.Default.Description, contentDescription = null)
                                }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = isPrivateRepo,
                                        onCheckedChange = { isPrivateRepo = it }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Private Repository")
                                }
                                
                                Icon(
                                    imageVector = if (isPrivateRepo) Icons.Default.Lock else Icons.Default.Public,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = commitMessage,
                                onValueChange = { commitMessage = it },
                                label = { Text("Initial Commit Message") },
                                modifier = Modifier.fillMaxWidth(),
                                leadingIcon = {
                                    Icon(Icons.Default.Message, contentDescription = null)
                                }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Upload Progress
                            if (uploadStatus != UploadStatus.IDLE && uploadStatus != UploadStatus.COMPLETED) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(20.dp),
                                                strokeWidth = 2.dp
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(
                                                text = when (uploadStatus) {
                                                    UploadStatus.AUTHENTICATING -> "Authenticating with GitHub..."
                                                    UploadStatus.CREATING_REPO -> "Creating repository..."
                                                    UploadStatus.UPLOADING_FILES -> "Uploading project files..."
                                                    UploadStatus.COMMITTING -> "Committing changes..."
                                                    UploadStatus.PUSHING -> "Pushing to GitHub..."
                                                    else -> "Processing..."
                                                },
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                        
                                        Spacer(modifier = Modifier.height(8.dp))
                                        LinearProgressIndicator(
                                            modifier = Modifier.fillMaxWidth(),
                                            progress = when (uploadStatus) {
                                                UploadStatus.AUTHENTICATING -> 0.1f
                                                UploadStatus.CREATING_REPO -> 0.3f
                                                UploadStatus.UPLOADING_FILES -> 0.6f
                                                UploadStatus.COMMITTING -> 0.8f
                                                UploadStatus.PUSHING -> 0.9f
                                                else -> 0.0f
                                            }
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            // Success message
                            if (uploadStatus == UploadStatus.COMPLETED) {
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
                                            Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = "Upload Successful!",
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                            Text(
                                                text = "Repository: github.com/$userName/$projectName",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        }
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            // Upload Button
                            Button(
                                onClick = {
                                    uploadStatus = UploadStatus.AUTHENTICATING
                                    statusMessage = "Starting upload process..."
                                    
                                    // Simulate upload process
                                    LaunchedEffect(Unit) {
                                        delay(1000)
                                        uploadStatus = UploadStatus.CREATING_REPO
                                        delay(1500)
                                        uploadStatus = UploadStatus.UPLOADING_FILES
                                        delay(2000)
                                        uploadStatus = UploadStatus.COMMITTING
                                        delay(1000)
                                        uploadStatus = UploadStatus.PUSHING
                                        delay(1500)
                                        uploadStatus = UploadStatus.COMPLETED
                                        statusMessage = "Project successfully uploaded to GitHub!"
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = uploadStatus == UploadStatus.IDLE || uploadStatus == UploadStatus.COMPLETED
                            ) {
                                Icon(Icons.Default.Upload, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    if (uploadStatus == UploadStatus.COMPLETED) "Upload Again" else "Upload to GitHub"
                                )
                            }
                        }
                    }
                }

                1 -> {
                    // My Repositories Tab
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(repositories) { repo ->
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
                                        Text(
                                            text = repo.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = if (repo.private) Icons.Default.Lock else Icons.Default.Public,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (repo.private) "Private" else "Public",
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    Text(
                                        text = repo.description,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Updated ${repo.lastUpdated}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        
                                        Row {
                                            TextButton(
                                                onClick = { /* Open in browser */ }
                                            ) {
                                                Icon(Icons.Default.OpenInBrowser, contentDescription = null)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("View")
                                            }
                                            
                                            TextButton(
                                                onClick = { /* Clone repository */ }
                                            ) {
                                                Icon(Icons.Default.Download, contentDescription = null)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Clone")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Status message
        if (statusMessage.isNotBlank()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = statusMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
