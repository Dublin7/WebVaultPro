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

data class NFTArtwork(
    val id: String,
    val title: String,
    val description: String,
    val style: String,
    val prompt: String,
    val price: String,
    val chain: String,
    val status: String = "Draft"
)

enum class MintingStep {
    DESIGN, METADATA, DEPLOY, COMPLETE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NFTMintingScreen() {
    var currentStep by remember { mutableStateOf(MintingStep.DESIGN) }
    var artworks by remember { mutableStateOf(listOf<NFTArtwork>()) }
    var isGenerating by remember { mutableStateOf(false) }
    var showCreateDialog by remember { mutableStateOf(false) }

    // AI Generation fields
    var artPrompt by remember { mutableStateOf("") }
    var selectedStyle by remember { mutableStateOf("Digital Art") }
    var selectedChain by remember { mutableStateOf("Ethereum") }
    var nftTitle by remember { mutableStateOf("") }
    var nftDescription by remember { mutableStateOf("") }
    var mintPrice by remember { mutableStateOf("0.1") }
    var royaltyPercent by remember { mutableStateOf("5") }

    val artStyles = listOf("Digital Art", "Oil Painting", "Watercolor", "Pixel Art", "Abstract", "Realistic")
    val blockchains = listOf("Ethereum", "Polygon", "Binance Smart Chain", "Solana", "Avalanche")

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
                text = "AI NFT Studio",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            FloatingActionButton(
                onClick = { showCreateDialog = true },
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create NFT")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Step Indicator
        StepIndicator(currentStep = currentStep)

        Spacer(modifier = Modifier.height(24.dp))

        when (currentStep) {
            MintingStep.DESIGN -> {
                DesignStep(
                    artPrompt = artPrompt,
                    onPromptChange = { artPrompt = it },
                    selectedStyle = selectedStyle,
                    onStyleChange = { selectedStyle = it },
                    artStyles = artStyles,
                    isGenerating = isGenerating,
                    onGenerate = {
                        isGenerating = true
                        // Simulate AI generation
                        LaunchedEffect(Unit) {
                            delay(3000)
                            isGenerating = false
                            currentStep = MintingStep.METADATA
                        }
                    }
                )
            }

            MintingStep.METADATA -> {
                MetadataStep(
                    nftTitle = nftTitle,
                    onTitleChange = { nftTitle = it },
                    nftDescription = nftDescription,
                    onDescriptionChange = { nftDescription = it },
                    mintPrice = mintPrice,
                    onPriceChange = { mintPrice = it },
                    royaltyPercent = royaltyPercent,
                    onRoyaltyChange = { royaltyPercent = it },
                    selectedChain = selectedChain,
                    onChainChange = { selectedChain = it },
                    blockchains = blockchains,
                    onNext = { currentStep = MintingStep.DEPLOY }
                )
            }

            MintingStep.DEPLOY -> {
                DeployStep(
                    nftTitle = nftTitle,
                    selectedChain = selectedChain,
                    mintPrice = mintPrice,
                    onDeploy = {
                        val newArtwork = NFTArtwork(
                            id = System.currentTimeMillis().toString(),
                            title = nftTitle,
                            description = nftDescription,
                            style = selectedStyle,
                            prompt = artPrompt,
                            price = mintPrice,
                            chain = selectedChain,
                            status = "Minted"
                        )
                        artworks = artworks + newArtwork
                        currentStep = MintingStep.COMPLETE
                    }
                )
            }

            MintingStep.COMPLETE -> {
                CompleteStep(
                    onCreateAnother = {
                        currentStep = MintingStep.DESIGN
                        artPrompt = ""
                        nftTitle = ""
                        nftDescription = ""
                        mintPrice = "0.1"
                        royaltyPercent = "5"
                    }
                )
            }
        }

        // My NFTs Section
        if (artworks.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "My NFT Collection",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(artworks) { artwork ->
                    NFTCard(artwork = artwork)
                }
            }
        }
    }
}

@Composable
fun StepIndicator(currentStep: MintingStep) {
    val steps = listOf("Design", "Metadata", "Deploy", "Complete")
    val currentIndex = when (currentStep) {
        MintingStep.DESIGN -> 0
        MintingStep.METADATA -> 1
        MintingStep.DEPLOY -> 2
        MintingStep.COMPLETE -> 3
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        steps.forEachIndexed { index, step ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (index <= currentIndex) {
                        Card(
                            modifier = Modifier.size(32.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                if (index < currentIndex) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                } else {
                                    Text(
                                        text = "${index + 1}",
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    } else {
                        Card(
                            modifier = Modifier.size(32.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = step,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (index <= currentIndex) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun DesignStep(
    artPrompt: String,
    onPromptChange: (String) -> Unit,
    selectedStyle: String,
    onStyleChange: (String) -> Unit,
    artStyles: List<String>,
    isGenerating: Boolean,
    onGenerate: () -> Unit
) {
    Column {
        Text(
            text = "Create Your NFT Artwork",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = artPrompt,
            onValueChange = onPromptChange,
            label = { Text("Describe your artwork") },
            placeholder = { Text("A futuristic cityscape with neon lights and flying cars") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Art Style",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.height(200.dp)
        ) {
            items(artStyles) { style ->
                FilterChip(
                    onClick = { onStyleChange(style) },
                    label = { Text(style) },
                    selected = selectedStyle == style,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onGenerate,
            modifier = Modifier.fillMaxWidth(),
            enabled = artPrompt.isNotBlank() && !isGenerating
        ) {
            if (isGenerating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generating Artwork...")
            } else {
                Icon(Icons.Default.AutoFixHigh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generate with AI")
            }
        }
    }
}

@Composable
fun MetadataStep(
    nftTitle: String,
    onTitleChange: (String) -> Unit,
    nftDescription: String,
    onDescriptionChange: (String) -> Unit,
    mintPrice: String,
    onPriceChange: (String) -> Unit,
    royaltyPercent: String,
    onRoyaltyChange: (String) -> Unit,
    selectedChain: String,
    onChainChange: (String) -> Unit,
    blockchains: List<String>,
    onNext: () -> Unit
) {
    Column {
        Text(
            text = "NFT Metadata",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = nftTitle,
            onValueChange = onTitleChange,
            label = { Text("NFT Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nftDescription,
            onValueChange = onDescriptionChange,
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = mintPrice,
                onValueChange = onPriceChange,
                label = { Text("Price (ETH)") },
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = royaltyPercent,
                onValueChange = onRoyaltyChange,
                label = { Text("Royalty %") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Blockchain",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.height(150.dp)
        ) {
            items(blockchains) { chain ->
                FilterChip(
                    onClick = { onChainChange(chain) },
                    label = { Text(chain) },
                    selected = selectedChain == chain,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth(),
            enabled = nftTitle.isNotBlank() && nftDescription.isNotBlank()
        ) {
            Text("Continue to Deploy")
        }
    }
}

@Composable
fun DeployStep(
    nftTitle: String,
    selectedChain: String,
    mintPrice: String,
    onDeploy: () -> Unit
) {
    var isDeploying by remember { mutableStateOf(false) }

    Column {
        Text(
            text = "Deploy NFT",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

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
                    text = "Deployment Summary",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("Title: $nftTitle")
                Text("Chain: $selectedChain")
                Text("Price: $mintPrice ETH")
                Text("Estimated Gas: 0.008 ETH")

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        isDeploying = true
                        LaunchedEffect(Unit) {
                            delay(2000)
                            isDeploying = false
                            onDeploy()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isDeploying
                ) {
                    if (isDeploying) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Deploying...")
                    } else {
                        Icon(Icons.Default.Rocket, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Deploy NFT")
                    }
                }
            }
        }
    }
}

@Composable
fun CompleteStep(
    onCreateAnother: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "NFT Successfully Minted!",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Your NFT has been deployed to the blockchain",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onCreateAnother,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Create Another NFT")
        }
    }
}

@Composable
fun NFTCard(artwork: NFTArtwork) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = artwork.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Badge(
                    containerColor = if (artwork.status == "Minted") 
                        Color.Green else Color.Orange
                ) {
                    Text(artwork.status)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = artwork.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${artwork.price} ETH",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = artwork.chain,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}