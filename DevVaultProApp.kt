package com.devvaultpro.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
                    title = { Text("DevVault Pro") }
                )
            },
            bottomBar = {
                NavigationBar {
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
                        icon = { Icon(Icons.Default.Code, contentDescription = "Snippets") },
                        label = { Text("Snippets") },
                        selected = currentRoute == "snippets",
                        onClick = { navController.navigate("snippets") }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Folder, contentDescription = "Templates") },
                        label = { Text("Templates") },
                        selected = currentRoute == "templates",
                        onClick = { navController.navigate("templates") }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Api, contentDescription = "API Testing") },
                        label = { Text("API") },
                        selected = currentRoute == "api-testing",
                        onClick = { navController.navigate("api-testing") }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Monitoring, contentDescription = "Performance") },
                        label = { Text("Monitor") },
                        selected = currentRoute == "performance",
                        onClick = { navController.navigate("performance") }
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
                        icon = { Icon(Icons.Default.Image, contentDescription = "NFT Minting") },
                        label = { Text("NFT Minting") },
                        selected = currentRoute == "crypto/nft",
                        onClick = { navController.navigate("crypto/nft") }
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
                startDestination = "notes",
                modifier = Modifier.padding(paddingValues)
            ) {
                composable("notes") { NotesScreen() }
                composable("files") { FileScreen() }
                composable("terminal") { TerminalScreen() }
                composable("github") { GitHubScreen() }
                composable("snippets") { CodeSnippetScreen() }
                composable("templates") { ProjectTemplatesScreen() }
                composable("api-testing") { ApiTestingScreen() }
                composable("performance") { PerformanceMonitorScreen() }
                composable("crypto") { CryptoPortfolioScreen() }
                composable("blockchain") { BlockchainExplorerScreen() }
                composable("web3") { Web3DevToolsScreen() }
                 composable("crypto/web3") {
                        Web3DevToolsScreen()
                    }
                    composable("crypto/nft") {
                        NFTMintingScreen()
                    }
                composable("sleep") { SleepChainScreen() }
            }
        }
    }
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