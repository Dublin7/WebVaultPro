package com.devvaultpro.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevVaultProApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("DevVault Pro") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.SmartToy, contentDescription = "AI") },
                        label = { Text("AI") },
                        selected = currentRoute == "ai",
                        onClick = { navController.navigate("ai") }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Note, contentDescription = "Notes") },
                        label = { Text("Notes") },
                        selected = currentRoute == "notes",
                        onClick = { navController.navigate("notes") }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.FilePresent, contentDescription = "Files") },
                        label = { Text("Files") },
                        selected = currentRoute == "files",
                        onClick = { navController.navigate("files") }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Computer, contentDescription = "Terminal") },
                        label = { Text("Terminal") },
                        selected = currentRoute == "terminal",
                        onClick = { navController.navigate("terminal") }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.CurrencyBitcoin, contentDescription = "Crypto") },
                        label = { Text("Crypto") },
                        selected = currentRoute?.startsWith("crypto") == true,
                        onClick = { navController.navigate("crypto") }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Nightlight, contentDescription = "Sleep") },
                        label = { Text("Sleep") },
                        selected = currentRoute == "sleep",
                        onClick = { navController.navigate("sleep") }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.CloudUpload, contentDescription = "GitHub") },
                        label = { Text("GitHub") },
                        selected = currentRoute == "github",
                        onClick = { navController.navigate("github") }
                    )
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = "ai",
                modifier = Modifier.padding(paddingValues)
            ) {
                composable("ai") { AiScreen() }
                composable("notes") { NotesScreen() }
                composable("files") { FileScreen() }
                composable("terminal") { TerminalScreen() }
                composable("snippets") { CodeSnippetScreen() }
                composable("templates") { ProjectTemplatesScreen() }
                composable("api-testing") { ApiTestingScreen() }
                composable("performance") { PerformanceMonitorScreen() }
                composable("crypto") { CryptoHubScreen() }
                composable("crypto/portfolio") { CryptoPortfolioScreen() }
                composable("crypto/explorer") { BlockchainExplorerScreen() }
                composable("crypto/web3") { Web3DevToolsScreen() }
                composable("crypto/nft") { NFTMintingScreen() }
                composable("sleep") { SleepChainScreen() }
                composable("github") { GitHubScreen() }
            }
        }
    }
}

@Composable
fun AiScreen() {
    Text("AI Screen")
}

@Composable
fun NotesScreen() {
    Text("Notes Screen")
}

@Composable
fun FileScreen() {
    Text("Files Screen")
}

@Composable
fun TerminalScreen() {
    Text("Terminal Screen")
}

@Composable
fun CodeSnippetScreen() {
    Text("Code Snippets Screen")
}

@Composable
fun ProjectTemplatesScreen() {
    Text("Project Templates Screen")
}

@Composable
fun ApiTestingScreen() {
    Text("API Testing Screen")
}

@Composable
fun PerformanceMonitorScreen() {
    Text("Performance Monitor Screen")
}

@Composable
fun CryptoHubScreen() {
    Text("Crypto Hub Screen")
}

@Composable
fun CryptoPortfolioScreen() {
    Text("Crypto Portfolio Screen")
}

@Composable
fun BlockchainExplorerScreen() {
    Text("Blockchain Explorer Screen")
}

@Composable
fun Web3DevToolsScreen() {
    Text("Web3 Dev Tools Screen")
}

@Composable
fun SleepChainScreen() {
    Text("Sleep Chain Screen")
}

@Composable
fun NFTMintingScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("NFT Minting Screen", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text("AI Powered NFT Creation Studio Coming Soon!", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun GitHubScreen() {
    Text("GitHub Screen")
}

@Preview(showBackground = true)
@Composable
fun DevVaultProAppPreview() {
    DevVaultProApp()
}