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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

data class CryptoFeature(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val route: String,
    val isNew: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CryptoHubScreen() {
    val navController = rememberNavController()

    val cryptoFeatures = listOf(
        CryptoFeature(
            title = "Portfolio Tracker",
            description = "Track your crypto investments and performance",
            icon = Icons.Default.AccountBalance,
            route = "portfolio"
        ),
        CryptoFeature(
            title = "Blockchain Explorer",
            description = "Explore blockchain transactions and blocks",
            icon = Icons.Default.Search,
            route = "explorer"
        ),
        CryptoFeature(
            title = "Web3 Dev Tools",
            description = "Smart contract testing and deployment tools",
            icon = Icons.Default.Code,
            route = "web3"
        ),
        CryptoFeature(
            title = "NFT Minting Studio",
            description = "Create and mint NFTs with AI assistance",
            icon = Icons.Default.Palette,
            route = "nft",
            isNew = true
        )
    )

    NavHost(
        navController = navController,
        startDestination = "hub"
    ) {
        composable("hub") {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Text(
                    text = "Crypto Hub",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Your comprehensive Web3 development toolkit",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Features Grid
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(cryptoFeatures) { feature ->
                        CryptoFeatureCard(
                            feature = feature,
                            onClick = { navController.navigate(feature.route) }
                        )
                    }
                }
            }
        }

        composable("portfolio") { CryptoPortfolioScreen() }
        composable("explorer") { BlockchainExplorerScreen() }
        composable("web3") { Web3DevToolsScreen() }
        composable("nft") { NFTMintingScreen() }
    }
}

@Composable
fun CryptoFeatureCard(
    feature: CryptoFeature,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = feature.icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = feature.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    if (feature.isNew) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Badge(
                            containerColor = MaterialTheme.colorScheme.primary
                        ) {
                            Text("NEW", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = feature.description,
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