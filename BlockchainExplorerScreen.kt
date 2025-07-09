
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

data class BlockchainNetwork(
    val name: String,
    val symbol: String,
    val rpcUrl: String,
    val explorerUrl: String
)

data class Transaction(
    val hash: String,
    val from: String,
    val to: String,
    val value: String,
    val gasUsed: String,
    val status: String,
    val timestamp: Long
)

data class AddressInfo(
    val address: String,
    val balance: String,
    val transactionCount: Int,
    val transactions: List<Transaction>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockchainExplorerScreen() {
    var selectedNetwork by remember { 
        mutableStateOf(BlockchainNetwork(
            "Ethereum", "ETH", 
            "https://eth-mainnet.g.alchemy.com/v2/",
            "https://etherscan.io"
        ))
    }
    var searchQuery by remember { mutableStateOf("") }
    var searchResult by remember { mutableStateOf<Any?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }

    val networks = listOf(
        BlockchainNetwork("Ethereum", "ETH", "https://eth-mainnet.g.alchemy.com/v2/", "https://etherscan.io"),
        BlockchainNetwork("Polygon", "MATIC", "https://polygon-rpc.com/", "https://polygonscan.com"),
        BlockchainNetwork("BSC", "BNB", "https://bsc-dataseed.binance.org/", "https://bscscan.com"),
        BlockchainNetwork("Arbitrum", "ARB", "https://arb1.arbitrum.io/rpc", "https://arbiscan.io")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Blockchain Explorer",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Network Selection
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            networks.forEach { network ->
                FilterChip(
                    onClick = { selectedNetwork = network },
                    label = { Text(network.name) },
                    selected = selectedNetwork.name == network.name
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Section
        Card {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Search ${selectedNetwork.name}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Enter address, transaction hash, or block number") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = {
                            isLoading = true
                            // Simulate search
                            searchResult = when {
                                searchQuery.startsWith("0x") && searchQuery.length == 42 -> {
                                    // Address search
                                    AddressInfo(
                                        address = searchQuery,
                                        balance = "1.234 ${selectedNetwork.symbol}",
                                        transactionCount = 156,
                                        transactions = listOf(
                                            Transaction(
                                                "0x1234...abcd", searchQuery, "0x5678...efgh",
                                                "0.5 ${selectedNetwork.symbol}", "21000", "Success", System.currentTimeMillis()
                                            )
                                        )
                                    )
                                }
                                searchQuery.startsWith("0x") && searchQuery.length == 66 -> {
                                    // Transaction search
                                    Transaction(
                                        searchQuery, "0x1234...5678", "0xabcd...efgh",
                                        "1.0 ${selectedNetwork.symbol}", "21000", "Success", System.currentTimeMillis()
                                    )
                                }
                                else -> null
                            }
                            isLoading = false
                        },
                        enabled = !isLoading && searchQuery.isNotBlank()
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Search")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Results Section
        searchResult?.let { result ->
            when (result) {
                is AddressInfo -> AddressResultView(result)
                is Transaction -> TransactionResultView(result)
            }
        } ?: run {
            // Sample Data when no search
            SampleDataView(selectedNetwork)
        }
    }
}

@Composable
fun AddressResultView(addressInfo: AddressInfo) {
    Column {
        Text(
            text = "Address Information",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Card {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Address",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                SelectionContainer {
                    Text(
                        text = addressInfo.address,
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Balance",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = addressInfo.balance,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Column {
                        Text(
                            text = "Transactions",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = addressInfo.transactionCount.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Recent Transactions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(addressInfo.transactions) { transaction ->
                TransactionCard(transaction)
            }
        }
    }
}

@Composable
fun TransactionResultView(transaction: Transaction) {
    Column {
        Text(
            text = "Transaction Details",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        TransactionCard(transaction, expanded = true)
    }
}

@Composable
fun TransactionCard(transaction: Transaction, expanded: Boolean = false) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SelectionContainer {
                    Text(
                        text = if (expanded) transaction.hash else "${transaction.hash.take(10)}...${transaction.hash.takeLast(10)}",
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                AssistChip(
                    onClick = { },
                    label = { Text(transaction.status) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = when (transaction.status) {
                            "Success" -> Color(0xFF4CAF50)
                            "Failed" -> Color(0xFFF44336)
                            "Pending" -> Color(0xFFFF9800)
                            else -> MaterialTheme.colorScheme.primary
                        }
                    )
                )
            }
            
            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TransactionDetail("From", transaction.from)
                    TransactionDetail("To", transaction.to)
                    TransactionDetail("Value", transaction.value)
                    TransactionDetail("Gas Used", transaction.gasUsed)
                }
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Value: ${transaction.value}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun TransactionDetail(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        SelectionContainer {
            Text(
                text = value,
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun SampleDataView(network: BlockchainNetwork) {
    Column {
        Text(
            text = "Latest Blocks on ${network.name}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(5) { index ->
                Card {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Block #${18500000 + index}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Text(
                            text = "Hash: 0x${(1..8).map { "abcdef0123456789"[kotlin.random.Random.nextInt(16)] }.joinToString("")}...",
                            fontFamily = FontFamily.Monospace,
                            style = MaterialTheme.typography.bodySmall
                        )
                        
                        Text(
                            text = "Transactions: ${kotlin.random.Random.nextInt(100, 300)}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}
