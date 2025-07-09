
package com.devvaultpro.ui

import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

data class NFTMetadata(
    val name: String,
    val description: String,
    val image: String,
    val attributes: List<NFTAttribute>,
    val rarity: String = "Common"
)

data class NFTAttribute(
    val trait_type: String,
    val value: String,
    val rarity: Double = 0.0
)

data class MintedNFT(
    val tokenId: String,
    val contractAddress: String,
    val metadata: NFTMetadata,
    val network: String,
    val status: MintStatus,
    val transactionHash: String = "",
    val gasUsed: String = "",
    val mintCost: String = ""
)

enum class MintStatus {
    CREATING, UPLOADING, MINTING, COMPLETED, FAILED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NFTMintingScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    var mintedNFTs by remember { mutableStateOf(listOf<MintedNFT>()) }
    var isGenerating by remember { mutableStateOf(false) }
    var generatedArtwork by remember { mutableStateOf<NFTMetadata?>(null) }

    val tabs = listOf("AI Studio", "Mint NFT", "My NFTs", "Collections")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "AI-Powered NFT Studio",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

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
            0 -> AIStudioTab(
                isGenerating = isGenerating,
                generatedArtwork = generatedArtwork,
                onGenerate = { prompt, style ->
                    isGenerating = true
                    // Simulate AI generation
                    generatedArtwork = NFTMetadata(
                        name = "AI Generated Art #${System.currentTimeMillis() % 1000}",
                        description = "Created with AI using prompt: '$prompt' in $style style",
                        image = "https://ai-generated-art.com/image-${System.currentTimeMillis()}.png",
                        attributes = listOf(
                            NFTAttribute("Style", style, 0.15),
                            NFTAttribute("AI Model", "DALL-E 3", 0.05),
                            NFTAttribute("Rarity", "Rare", 0.08)
                        ),
                        rarity = "Rare"
                    )
                    isGenerating = false
                }
            )
            1 -> MintNFTTab(
                generatedArtwork = generatedArtwork,
                onMint = { metadata, network ->
                    val newNFT = MintedNFT(
                        tokenId = "${System.currentTimeMillis()}",
                        contractAddress = "0x${(1..40).map { "0123456789abcdef"[kotlin.random.Random.nextInt(16)] }.joinToString("")}",
                        metadata = metadata,
                        network = network,
                        status = MintStatus.CREATING
                    )
                    mintedNFTs = mintedNFTs + newNFT
                }
            )
            2 -> MyNFTsTab(mintedNFTs)
            3 -> CollectionsTab()
        }
    }
}

@Composable
fun AIStudioTab(
    isGenerating: Boolean,
    generatedArtwork: NFTMetadata?,
    onGenerate: (String, String) -> Unit
) {
    var prompt by remember { mutableStateOf("") }
    var selectedStyle by remember { mutableStateOf("Digital Art") }
    var selectedModel by remember { mutableStateOf("DALL-E 3") }

    val artStyles = listOf("Digital Art", "Oil Painting", "Watercolor", "Pixel Art", "3D Render", "Abstract", "Photorealistic")
    val aiModels = listOf("DALL-E 3", "Midjourney", "Stable Diffusion", "Leonardo AI")

    Column {
        Text(
            text = "AI Art Generator",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Generate Unique NFT Artwork",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    label = { Text("Describe your NFT artwork") },
                    placeholder = { Text("A futuristic cityscape with neon lights...") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Art Style",
                            style = MaterialTheme.typography.labelMedium
                        )
                        
                        var expandedStyle by remember { mutableStateOf(false) }
                        
                        ExposedDropdownMenuBox(
                            expanded = expandedStyle,
                            onExpandedChange = { expandedStyle = !expandedStyle }
                        ) {
                            OutlinedTextField(
                                value = selectedStyle,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStyle) },
                                modifier = Modifier.menuAnchor()
                            )
                            
                            ExposedDropdownMenu(
                                expanded = expandedStyle,
                                onDismissRequest = { expandedStyle = false }
                            ) {
                                artStyles.forEach { style ->
                                    DropdownMenuItem(
                                        text = { Text(style) },
                                        onClick = {
                                            selectedStyle = style
                                            expandedStyle = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "AI Model",
                            style = MaterialTheme.typography.labelMedium
                        )
                        
                        var expandedModel by remember { mutableStateOf(false) }
                        
                        ExposedDropdownMenuBox(
                            expanded = expandedModel,
                            onExpandedChange = { expandedModel = !expandedModel }
                        ) {
                            OutlinedTextField(
                                value = selectedModel,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedModel) },
                                modifier = Modifier.menuAnchor()
                            )
                            
                            ExposedDropdownMenu(
                                expanded = expandedModel,
                                onDismissRequest = { expandedModel = false }
                            ) {
                                aiModels.forEach { model ->
                                    DropdownMenuItem(
                                        text = { Text(model) },
                                        onClick = {
                                            selectedModel = model
                                            expandedModel = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onGenerate(prompt, selectedStyle) },
                    enabled = !isGenerating && prompt.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Generating...")
                    } else {
                        Icon(Icons.Default.Brush, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Generate Artwork")
                    }
                }
            }
        }

        generatedArtwork?.let { artwork ->
            Spacer(modifier = Modifier.height(16.dp))
            
            Card {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Generated Artwork",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.Image,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = Color.Gray
                                )
                                Text(
                                    text = "AI Generated Artwork",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = artwork.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = artwork.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Attributes:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )

                    artwork.attributes.forEach { attribute ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${attribute.trait_type}:",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = attribute.value,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MintNFTTab(
    generatedArtwork: NFTMetadata?,
    onMint: (NFTMetadata, String) -> Unit
) {
    var selectedNetwork by remember { mutableStateOf("Ethereum") }
    var mintPrice by remember { mutableStateOf("0.001") }
    var royaltyPercentage by remember { mutableStateOf("10") }

    val networks = listOf("Ethereum", "Polygon", "BSC", "Arbitrum", "Optimism")

    Column {
        Text(
            text = "Mint Your NFT",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (generatedArtwork == null) {
            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Brush,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No artwork generated",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Go to AI Studio to create your NFT artwork first",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            Card {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "NFT Configuration",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Blockchain Network",
                        style = MaterialTheme.typography.labelMedium
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        networks.forEach { network ->
                            FilterChip(
                                onClick = { selectedNetwork = network },
                                label = { Text(network) },
                                selected = selectedNetwork == network
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = mintPrice,
                            onValueChange = { mintPrice = it },
                            label = { Text("Mint Price (ETH)") },
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = royaltyPercentage,
                            onValueChange = { royaltyPercentage = it },
                            label = { Text("Royalty (%)") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = "Estimated Costs",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Gas Fee:", style = MaterialTheme.typography.bodySmall)
                                Text("~0.005 ETH", style = MaterialTheme.typography.bodySmall)
                            }
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Platform Fee:", style = MaterialTheme.typography.bodySmall)
                                Text("2.5%", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { onMint(generatedArtwork, selectedNetwork) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Token, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Mint NFT")
                    }
                }
            }
        }
    }
}

@Composable
fun MyNFTsTab(nfts: List<MintedNFT>) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "My NFTs",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "${nfts.size} items",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (nfts.isEmpty()) {
            Card {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.CollectionsBookmark,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No NFTs yet",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Create and mint your first NFT using the AI Studio",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(nfts) { nft ->
                    NFTCard(nft)
                }
            }
        }
    }
}

@Composable
fun NFTCard(nft: MintedNFT) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = nft.metadata.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "Token ID: ${nft.tokenId}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Text(
                        text = nft.network,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                
                AssistChip(
                    onClick = { },
                    label = { Text(nft.status.name) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = when (nft.status) {
                            MintStatus.COMPLETED -> Color(0xFF4CAF50)
                            MintStatus.FAILED -> Color(0xFFF44336)
                            MintStatus.MINTING -> Color(0xFFFF9800)
                            else -> MaterialTheme.colorScheme.primary
                        }
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Contract Address",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            SelectionContainer {
                Text(
                    text = nft.contractAddress,
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            if (nft.status == MintStatus.COMPLETED && nft.transactionHash.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Transaction Hash",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                SelectionContainer {
                    Text(
                        text = nft.transactionHash,
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun CollectionsTab() {
    Column {
        Text(
            text = "Featured Collections",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(3) { index ->
                Card {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "AI Art Collection #${index + 1}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Text(
                            text = "Created with DevVault Pro AI Studio",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Floor: ${0.01 + index * 0.005} ETH",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            
                            Text(
                                text = "${100 + index * 50} items",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
