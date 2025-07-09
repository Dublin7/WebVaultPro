
package com.devvaultpro.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import kotlinx.coroutines.delay

data class ApiRequest(
    val id: String,
    val name: String,
    val method: String,
    val url: String,
    val headers: Map<String, String>,
    val body: String,
    val response: ApiResponse? = null
)

data class ApiResponse(
    val statusCode: Int,
    val headers: Map<String, String>,
    val body: String,
    val responseTime: Long
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiTestingScreen() {
    var requests by remember { 
        mutableStateOf(listOf(
            ApiRequest(
                "1", "Get Users", "GET", 
                "https://jsonplaceholder.typicode.com/users",
                mapOf("Accept" to "application/json"),
                ""
            ),
            ApiRequest(
                "2", "Create Post", "POST",
                "https://jsonplaceholder.typicode.com/posts",
                mapOf("Content-Type" to "application/json"),
                "{\n  \"title\": \"New Post\",\n  \"body\": \"Post content\",\n  \"userId\": 1\n}"
            )
        ))
    }
    var selectedRequest by remember { mutableStateOf<ApiRequest?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showNewRequestDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        // Requests sidebar
        Card(
            modifier = Modifier
                .width(300.dp)
                .fillMaxHeight()
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
                        text = "Requests",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    IconButton(onClick = { showNewRequestDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Request")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(requests) { request ->
                        RequestItem(
                            request = request,
                            isSelected = selectedRequest?.id == request.id,
                            onClick = { selectedRequest = request }
                        )
                    }
                }
            }
        }

        // Request details
        selectedRequest?.let { request ->
            RequestDetailsPanel(
                request = request,
                isLoading = isLoading,
                onSendRequest = {
                    isLoading = true
                    // Simulate API call
                    requests = requests.map { 
                        if (it.id == request.id) {
                            it.copy(
                                response = ApiResponse(
                                    statusCode = 200,
                                    headers = mapOf("Content-Type" to "application/json"),
                                    body = "{\n  \"success\": true,\n  \"data\": \"Mock response\"\n}",
                                    responseTime = 245
                                )
                            )
                        } else it
                    }
                    selectedRequest = requests.find { it.id == request.id }
                    isLoading = false
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            )
        } ?: run {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Select a request to test",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    if (showNewRequestDialog) {
        NewRequestDialog(
            onDismiss = { showNewRequestDialog = false },
            onCreate = { newRequest ->
                requests = requests + newRequest.copy(id = (requests.size + 1).toString())
                showNewRequestDialog = false
            }
        )
    }
}

@Composable
fun RequestItem(
    request: ApiRequest,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer
            else 
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = request.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                
                AssistChip(
                    onClick = { },
                    label = { Text(request.method) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = when (request.method) {
                            "GET" -> Color(0xFF4CAF50)
                            "POST" -> Color(0xFF2196F3)
                            "PUT" -> Color(0xFFFF9800)
                            "DELETE" -> Color(0xFFF44336)
                            else -> MaterialTheme.colorScheme.primary
                        }
                    )
                )
            }
            
            Text(
                text = request.url,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            request.response?.let { response ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Status: ${response.statusCode} (${response.responseTime}ms)",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (response.statusCode in 200..299) Color(0xFF4CAF50) else Color(0xFFF44336)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestDetailsPanel(
    request: ApiRequest,
    isLoading: Boolean,
    onSendRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Request", "Response")

    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = request.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Button(
                    onClick = onSendRequest,
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(Icons.Default.Send, contentDescription = null)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Send")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Method and URL
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AssistChip(
                    onClick = { },
                    label = { Text(request.method) }
                )
                
                Text(
                    text = request.url,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tabs
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                0 -> RequestTab(request)
                1 -> ResponseTab(request.response)
            }
        }
    }
}

@Composable
fun RequestTab(request: ApiRequest) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Headers
        item {
            Text(
                text = "Headers",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    request.headers.forEach { (key, value) ->
                        Text(
                            text = "$key: $value",
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFFD4D4D4)
                        )
                    }
                }
            }
        }

        // Body
        if (request.body.isNotEmpty()) {
            item {
                Text(
                    text = "Body",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
                ) {
                    SelectionContainer {
                        Text(
                            text = request.body,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFFD4D4D4),
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ResponseTab(response: ApiResponse?) {
    if (response == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No response yet. Send a request to see the response.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Status
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AssistChip(
                    onClick = { },
                    label = { Text("${response.statusCode}") },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (response.statusCode in 200..299) 
                            Color(0xFF4CAF50) else Color(0xFFF44336)
                    )
                )
                
                Text("${response.responseTime}ms")
            }
        }

        // Response body
        item {
            Text(
                text = "Response Body",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
            ) {
                SelectionContainer {
                    Text(
                        text = response.body,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFFD4D4D4),
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewRequestDialog(
    onDismiss: () -> Unit,
    onCreate: (ApiRequest) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var method by remember { mutableStateOf("GET") }
    var url by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Request") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Request Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = method,
                    onValueChange = { method = it },
                    label = { Text("Method") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("URL") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && url.isNotBlank()) {
                        onCreate(
                            ApiRequest(
                                id = "",
                                name = name,
                                method = method,
                                url = url,
                                headers = mapOf("Accept" to "application/json"),
                                body = ""
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
