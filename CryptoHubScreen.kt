
package com.devvaultpro.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CryptoHub(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Crypto & Blockchain Hub",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = "Your complete Web3 development toolkit",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Quick Stats Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickStatCard(
                title = "Portfolio Value",
                value = "$12,665.00",
                change = "+2.34%",
                isPositive = true,
                modifier = Modifier.weight(1f)
            )
            
            QuickStatCard(
                title = "Gas Price",
                value = "15 gwei",
                change = "ETH",
                isPositive = null,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Feature Cards
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                FeatureCard(
                    title = "Portfolio Tracker",
                    description = "Track your crypto assets, DeFi positions, and yield farming rewards",
                    icon = Icons.Default.CurrencyBitcoin,
                    color = Color(0xFFFF9800),
                    onClick = { navController.navigate("crypto/portfolio") }
                )
            }
            
            item {
                FeatureCard(
                    title = "Blockchain Explorer",
                    description = "Explore transactions, addresses, and smart contracts across multiple chains",
                    icon = Icons.Default.Search,
                    color = Color(0xFF2196F3),
                    onClick = { navController.navigate("crypto/explorer") }
                )
            }
            
            item {
                FeatureCard(
                    title = "Web3 Dev Tools",
                    description = "Smart contract testing, ABI tools, and gas optimization helpers",
                    icon = Icons.Default.Code,
                    color = Color(0xFF9C27B0),
                    onClick = { navController.navigate("crypto/web3") }
                )
            }
            
            item {
                FeatureCard(
                    title = "DeFi Analytics",
                    description = "Track yield farming, liquidity pools, and protocol analytics",
                    icon = Icons.Default.Analytics,
                    color = Color(0xFF4CAF50),
                    onClick = { /* Navigate to DeFi screen */ }
                )
            }
            
            item {
                FeatureCard(
                    title = "NFT Collection",
                    description = "View and manage your NFT collection across multiple marketplaces",
                    icon = Icons.Default.Image,
                    color = Color(0xFFE91E63),
                    onClick = { /* Navigate to NFT screen */ }
                )
            }
        }
    }
}

@Composable
fun QuickStatCard(
    title: String,
    value: String,
    change: String,
    isPositive: Boolean?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = change,
                style = MaterialTheme.typography.bodySmall,
                color = when (isPositive) {
                    true -> Color(0xFF4CAF50)
                    false -> Color(0xFFF44336)
                    null -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}

@Composable
fun FeatureCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
                modifier = Modifier.size(48.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
