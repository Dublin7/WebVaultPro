
package com.devvaultpro.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class ProjectTemplate(
    val id: String,
    val name: String,
    val description: String,
    val icon: ImageVector,
    val category: String,
    val files: List<TemplateFile>,
    val dependencies: List<String>,
    val isCustom: Boolean = false
)

data class TemplateFile(
    val path: String,
    val content: String,
    val isExecutable: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectTemplatesScreen() {
    var templates by remember { 
        mutableStateOf(listOf(
            ProjectTemplate(
                "android-mvvm", "Android MVVM",
                "Modern Android app with MVVM architecture, Room, and Retrofit",
                Icons.Default.PhoneAndroid, "Mobile",
                listOf(
                    TemplateFile("MainActivity.kt", "class MainActivity : ComponentActivity() { /* MVVM setup */ }"),
                    TemplateFile("build.gradle", "dependencies {\n  implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2'\n}"),
                    TemplateFile("UserViewModel.kt", "class UserViewModel : ViewModel() { /* ViewModel logic */ }")
                ),
                listOf("androidx.lifecycle", "androidx.room", "retrofit2")
            ),
            ProjectTemplate(
                "react-typescript", "React TypeScript",
                "React app with TypeScript, ESLint, and modern tooling",
                Icons.Default.Web, "Web",
                listOf(
                    TemplateFile("App.tsx", "import React from 'react';\n\nfunction App() {\n  return <div>Hello World</div>;\n}"),
                    TemplateFile("package.json", "{\n  \"name\": \"react-typescript-app\",\n  \"dependencies\": {\n    \"react\": \"^18.0.0\"\n  }\n}"),
                    TemplateFile("tsconfig.json", "{\n  \"compilerOptions\": {\n    \"target\": \"es5\",\n    \"strict\": true\n  }\n}")
                ),
                listOf("react", "typescript", "@types/react")
            ),
            ProjectTemplate(
                "node-express-api", "Node.js Express API",
                "RESTful API with Express, middleware, and JWT authentication",
                Icons.Default.Api, "Backend",
                listOf(
                    TemplateFile("server.js", "const express = require('express');\nconst app = express();\n\napp.get('/', (req, res) => {\n  res.json({ message: 'API is running' });\n});"),
                    TemplateFile("package.json", "{\n  \"name\": \"express-api\",\n  \"dependencies\": {\n    \"express\": \"^4.18.0\",\n    \"jsonwebtoken\": \"^9.0.0\"\n  }\n}"),
                    TemplateFile("middleware/auth.js", "const jwt = require('jsonwebtoken');\n\nmodule.exports = (req, res, next) => {\n  // JWT validation logic\n};")
                ),
                listOf("express", "jsonwebtoken", "bcryptjs", "cors")
            )
        ))
    }
    var selectedCategory by remember { mutableStateOf("All") }
    var showCreateDialog by remember { mutableStateOf(false) }

    val categories = listOf("All", "Mobile", "Web", "Backend", "Desktop", "Custom")
    val filteredTemplates = templates.filter { 
        selectedCategory == "All" || it.category == selectedCategory 
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
                text = "Project Templates",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Button(
                onClick = { showCreateDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Create")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Category filter
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                FilterChip(
                    onClick = { selectedCategory = category },
                    label = { Text(category) },
                    selected = selectedCategory == category
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Templates grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredTemplates) { template ->
                TemplateCard(
                    template = template,
                    onClick = { /* Handle template selection */ }
                )
            }
        }
    }

    if (showCreateDialog) {
        CreateTemplateDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { newTemplate ->
                templates = templates + newTemplate.copy(id = (templates.size + 1).toString())
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun TemplateCard(
    template: ProjectTemplate,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = template.icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = template.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = template.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            AssistChip(
                onClick = { },
                label = { Text(template.category) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTemplateDialog(
    onDismiss: () -> Unit,
    onCreate: (ProjectTemplate) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Custom") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Template") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Template Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onCreate(
                            ProjectTemplate(
                                id = "",
                                name = name,
                                description = description,
                                icon = Icons.Default.Folder,
                                category = category,
                                files = emptyList(),
                                dependencies = emptyList(),
                                isCustom = true
                            )
                        )
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
