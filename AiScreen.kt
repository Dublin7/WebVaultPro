
package com.devvaultpro.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class ChatMessage(
    val message: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiScreen() {
    var chatMessages by remember { mutableStateOf(listOf<ChatMessage>()) }
    var inputText by remember { mutableStateOf("") }
    var isAnalyzing by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "AI Assistant",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Quick Actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    isAnalyzing = true
                    chatMessages = chatMessages + ChatMessage(
                        "Analyze my code for potential issues",
                        true
                    )
                    // Simulate AI response
                    chatMessages = chatMessages + ChatMessage(
                        "🔍 Code Analysis Complete:\n\n• No unused imports found\n• 3 functions could benefit from documentation\n• Consider adding error handling to API calls\n• Overall code quality: Good ✅",
                        false
                    )
                    isAnalyzing = false
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Analyze Code")
            }
            
            Button(
                onClick = {
                    chatMessages = chatMessages + ChatMessage(
                        "Generate documentation for my functions",
                        true
                    )
                    chatMessages = chatMessages + ChatMessage(
                        "📚 Documentation generated! Here's what I found:\n\n```kotlin\n/**\n * Handles OAuth redirect from GitHub\n * @param activity The calling activity\n * @param uri The redirect URI containing auth code\n */\nfun handleOAuthRedirect(activity: ComponentActivity, uri: Uri)\n```",
                        false
                    )
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Gen Docs")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Blockchain-specific actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    chatMessages = chatMessages + ChatMessage(
                        "Help me optimize gas usage in my smart contract",
                        true
                    )
                    chatMessages = chatMessages + ChatMessage(
                        "⛽ Gas Optimization Tips:\n\n• Use `uint256` instead of smaller uints\n• Pack struct variables efficiently\n• Use `view`/`pure` functions when possible\n• Avoid unnecessary storage operations\n• Consider using events instead of storage for logs",
                        false
                    )
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Gas Tips")
            }
            
            Button(
                onClick = {
                    chatMessages = chatMessages + ChatMessage(
                        "Show me Web3 security best practices",
                        true
                    )
                    chatMessages = chatMessages + ChatMessage(
                        "🔒 Web3 Security Checklist:\n\n• Always validate user inputs\n• Use reentrancy guards\n• Implement proper access controls\n• Test with multiple scenarios\n• Audit smart contracts before mainnet\n• Use established libraries (OpenZeppelin)",
                        false
                    )
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Security")
            }
        }

        // Chat Messages
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (chatMessages.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                "👋 Hello! I'm your AI development assistant.",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "I can help you with:\n• Code analysis and review\n• Bug detection\n• Documentation generation\n• Refactoring suggestions\n• Best practices",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
            
            items(chatMessages) { message ->
                ChatBubble(message = message)
            }
            
            if (isAnalyzing) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AI is analyzing...")
                    }
                }
            }
        }

        // Input Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Ask me anything about your code...") },
                modifier = Modifier.weight(1f)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Button(
                onClick = {
                    if (inputText.isNotBlank()) {
                        chatMessages = chatMessages + ChatMessage(inputText, true)
                        // Simulate AI response
                        val response = when {
                            inputText.contains("bug", ignoreCase = true) -> 
                                "🐛 I can help you debug! Please share the specific error or unexpected behavior you're seeing."
                            inputText.contains("optimize", ignoreCase = true) -> 
                                "⚡ For optimization, I'd recommend: 1) Profile your code first 2) Focus on bottlenecks 3) Consider caching strategies"
                            inputText.contains("test", ignoreCase = true) -> 
                                "🧪 Testing is crucial! I can help you write unit tests, integration tests, or suggest testing strategies for your code."
                            inputText.contains("smart contract", ignoreCase = true) || inputText.contains("blockchain", ignoreCase = true) -> 
                                "⛓️ I can help with blockchain development! Need assistance with smart contracts, Web3 integration, or DeFi protocols?"
                            inputText.contains("crypto", ignoreCase = true) || inputText.contains("web3", ignoreCase = true) -> 
                                "🚀 Web3 development is exciting! I can help with wallet integration, transaction handling, and blockchain interactions."
                            inputText.contains("gas", ignoreCase = true) && inputText.contains("ethereum", ignoreCase = true) -> 
                                "⛽ Gas optimization is crucial! Consider using: 1) Efficient data structures 2) Batch operations 3) Gas-efficient patterns"
                            else -> 
                                "💡 I understand you're asking about: \"$inputText\". Could you provide more context about your specific development challenge?"
                        }
                        chatMessages = chatMessages + ChatMessage(response, false)
                        inputText = ""
                    }
                }
            ) {
                Text("Send")
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            modifier = Modifier.widthIn(max = 280.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (message.isUser) 
                    MaterialTheme.colorScheme.primary
                else 
                    MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Text(
                text = message.message,
                modifier = Modifier.padding(12.dp),
                color = if (message.isUser) 
                    MaterialTheme.colorScheme.onPrimary
                else 
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
