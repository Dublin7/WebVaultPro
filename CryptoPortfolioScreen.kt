
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

data class CryptoAsset(
    val symbol: String,
    val name: String,
    val price: Double,
    val change24h: Double,
    val holdings: Double,
    val value: Double,
    val stakingYield: Double = 0.0,
    val priceAlert: Double? = null,
    val alertType: AlertType = AlertType.NONE
)

enum class AlertType {
    NONE, ABOVE, BELOW
}

data class DeFiPosition(
    val protocol: String,
    val asset: String,
    val deposited: Double,
    val apy: Double,
    val earned: Double
)

data class Portfolio(
    val totalValue: Double,
    val totalChange: Double,
    val assets: List<CryptoAsset>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CryptoPortfolioScreen() {
    var portfolio by remember { 
        mutableStateOf(Portfolio(
            totalValue = 0.0,
            totalChange = 0.0,
            assets = listOf(
                CryptoAsset("BTC", "Bitcoin", 43250.0, 2.5, 0.1, 4325.0),
                CryptoAsset("ETH", "Ethereum", 2580.0, -1.2, 2.5, 6450.0),
                CryptoAsset("ADA", "Cardano", 0.48, 5.7, 1000.0, 480.0),
                CryptoAsset("SOL", "Solana", 98.5, 8.3, 10.0, 985.0),
                CryptoAsset("MATIC", "Polygon", 0.85, -3.1, 500.0, 425.0)
            )
        ))
    }
    var showAddAssetDialog by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }

    // Calculate portfolio totals
    val totalValue = portfolio.assets.sumOf { it.value }
    val weightedChange = portfolio.assets.sumOf { it.change24h * (it.value / totalValue) }

    // Simulate price updates
    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            portfolio = portfolio.copy(
                assets = portfolio.assets.map { asset ->
                    val priceChange = (Math.random() - 0.5) * 0.02
                    val newPrice = asset.price * (1 + priceChange)
                    asset.copy(
                        price = newPrice,
                        value = newPrice * asset.holdings,
                        change24h = asset.change24h + priceChange * 100
                    )
                }
            )
        }
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
                text = "Crypto Portfolio",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row {
                IconButton(
                    onClick = {
                        isRefreshing = true
                        // Simulate refresh
                        isRefreshing = false
                    }
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                }
                
                Button(onClick = { showAddAssetDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Asset")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Portfolio Summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Total Portfolio Value",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                Text(
                    text = "$${String.format("%.2f", totalValue)}",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (weightedChange >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                        contentDescription = null,
                        tint = if (weightedChange >= 0) Color(0xFF4CAF50) else Color(0xFFF44336),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${if (weightedChange >= 0) "+" else ""}${String.format("%.2f", weightedChange)}% (24h)",
                        color = if (weightedChange >= 0) Color(0xFF4CAF50) else Color(0xFFF44336),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Assets List
        Text(
            text = "Assets",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(portfolio.assets) { asset ->
                CryptoAssetCard(asset = asset)
            }
        }
    }

    if (showAddAssetDialog) {
        AddAssetDialog(
            onDismiss = { showAddAssetDialog = false },
            onAdd = { symbol, amount ->
                // Add new asset logic
                showAddAssetDialog = false
            }
        )
    }
}

@Composable
fun CryptoAssetCard(asset: CryptoAsset) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = asset.symbol,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = asset.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$${String.format("%.2f", asset.price)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (asset.change24h >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                        contentDescription = null,
                        tint = if (asset.change24h >= 0) Color(0xFF4CAF50) else Color(0xFFF44336),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${if (asset.change24h >= 0) "+" else ""}${String.format("%.2f", asset.change24h)}%",
                        color = if (asset.change24h >= 0) Color(0xFF4CAF50) else Color(0xFFF44336),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${asset.holdings} ${asset.symbol}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "$${String.format("%.2f", asset.value)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAssetDialog(
    onDismiss: () -> Unit,
    onAdd: (String, Double) -> Unit
) {
    var symbol by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Crypto Asset") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = symbol,
                    onValueChange = { symbol = it.uppercase() },
                    label = { Text("Symbol (e.g., BTC)") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (symbol.isNotBlank() && amount.isNotBlank()) {
                        onAdd(symbol, amount.toDoubleOrNull() ?: 0.0)
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
