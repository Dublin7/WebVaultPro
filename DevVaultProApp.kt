
package com.devvaultpro.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevVaultProApp() {
    val navController = rememberNavController()
    
    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("DevVault Pro") }
                )
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = "notes",
                modifier = Modifier.padding(paddingValues)
            ) {
                composable("notes") { NotesScreen() }
                composable("files") { FileScreen() }
                composable("github") { GitHubScreen() }
                composable("ai") { AiScreen() }
                composable("terminal") { TerminalScreen() }
            }
        }
    }
}
