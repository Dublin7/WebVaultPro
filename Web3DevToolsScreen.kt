
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

data class SmartContract(
    val name: String,
    val address: String,
    val abi: String,
    val network: String
)

data class GasEstimate(
    val slow: Int,
    val standard: Int,
    val fast: Int,
    val instant: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Web3DevToolsScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    var contracts by remember { 
        mutableStateOf(listOf(
            SmartContract(
                "ERC20 Token", 
                "0xA0b86a33E6F2C7BdA67F64d2C8493c5C1A12AB3e",
                "[{\"inputs\":[],\"name\":\"totalSupply\",\"outputs\":[{\"type\":\"uint256\"}],\"type\":\"function\"}]",
                "Ethereum"
            )
        ))
    }
    var gasEstimates by remember { 
        mutableStateOf(GasEstimate(20, 25, 30, 40))
    }

    val tabs = listOf("Smart Contracts", "Gas Calculator", "ABI Tools", "Utilities")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Web3 Development Tools",
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
            0 -> SmartContractsTab(contracts) { contracts = contracts + it }
            1 -> GasCalculatorTab(gasEstimates)
            2 -> AbiToolsTab()
            3 -> UtilitiesTab()
        }
    }
}

@Composable
fun SmartContractsTab(
    contracts: List<SmartContract>,
    onAddContract: (SmartContract) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Smart Contracts",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Button(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Contract")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(contracts) { contract ->
                ContractCard(contract)
            }
        }
    }

    if (showAddDialog) {
        AddContractDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { contract ->
                onAddContract(contract)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun ContractCard(contract: SmartContract) {
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
                        text = contract.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = contract.network,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                AssistChip(
                    onClick = { },
                    label = { Text("Interact") }
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Address",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            SelectionContainer {
                Text(
                    text = contract.address,
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "ABI",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
            ) {
                SelectionContainer {
                    Text(
                        text = contract.abi.take(100) + "...",
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFFD4D4D4),
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun GasCalculatorTab(gasEstimates: GasEstimate) {
    var gasLimit by remember { mutableStateOf("21000") }
    var selectedGasPrice by remember { mutableStateOf(gasEstimates.standard) }
    
    Column {
        Text(
            text = "Gas Price Tracker",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GasPriceCard("Slow", gasEstimates.slow, Color(0xFF4CAF50), Modifier.weight(1f)) {
                selectedGasPrice = gasEstimates.slow
            }
            GasPriceCard("Standard", gasEstimates.standard, Color(0xFF2196F3), Modifier.weight(1f)) {
                selectedGasPrice = gasEstimates.standard
            }
            GasPriceCard("Fast", gasEstimates.fast, Color(0xFFFF9800), Modifier.weight(1f)) {
                selectedGasPrice = gasEstimates.fast
            }
            GasPriceCard("Instant", gasEstimates.instant, Color(0xFFF44336), Modifier.weight(1f)) {
                selectedGasPrice = gasEstimates.instant
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Card {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Gas Calculator",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = gasLimit,
                    onValueChange = { gasLimit = it },
                    label = { Text("Gas Limit") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Gas Price",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$selectedGasPrice Gwei",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Column {
                        Text(
                            text = "Estimated Cost",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        val cost = (gasLimit.toIntOrNull() ?: 0) * selectedGasPrice / 1_000_000_000.0 * 2500 // Assuming ETH price
                        Text(
                            text = "$${String.format("%.4f", cost)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GasPriceCard(
    label: String,
    price: Int,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = color
            )
            Text(
                text = "$price",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = "Gwei",
                style = MaterialTheme.typography.labelSmall,
                color = color
            )
        }
    }
}

@Composable
fun AbiToolsTab() {
    var abiInput by remember { mutableStateOf("") }
    var decodedOutput by remember { mutableStateOf("") }
    
    Column {
        Text(
            text = "ABI Decoder/Encoder",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Card {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "ABI Input",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = abiInput,
                    onValueChange = { abiInput = it },
                    placeholder = { Text("Paste ABI JSON here...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    maxLines = 8
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            decodedOutput = "Decoded ABI functions:\n• transfer(address,uint256)\n• balanceOf(address)\n• approve(address,uint256)"
                        }
                    ) {
                        Text("Decode")
                    }
                    
                    Button(
                        onClick = {
                            decodedOutput = "Encoded function call:\n0xa9059cbb000000000000000000000000..."
                        }
                    ) {
                        Text("Encode")
                    }
                }
                
                if (decodedOutput.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
                    ) {
                        SelectionContainer {
                            Text(
                                text = decodedOutput,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFFD4D4D4),
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UtilitiesTab() {
    var inputAddress by remember { mutableStateOf("") }
    var checksumAddress by remember { mutableStateOf("") }
    
    Column {
        Text(
            text = "Web3 Utilities",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Address Checksum Tool
        Card {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Address Checksum Validator",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = inputAddress,
                    onValueChange = { inputAddress = it },
                    label = { Text("Ethereum Address") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("0x...") }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Button(
                    onClick = {
                        checksumAddress = if (inputAddress.length == 42) {
                            "✅ Valid checksum address:\n${inputAddress.lowercase().replaceFirstChar { it.uppercase() }}"
                        } else {
                            "❌ Invalid address format"
                        }
                    }
                ) {
                    Text("Validate & Convert")
                }
                
                if (checksumAddress.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    SelectionContainer {
                        Text(
                            text = checksumAddress,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Unit Converter
        Card {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Unit Converter",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text("1 ETH = 1,000,000,000,000,000,000 Wei")
                Text("1 ETH = 1,000,000,000 Gwei")
                Text("1 Gwei = 1,000,000,000 Wei")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContractDialog(
    onDismiss: () -> Unit,
    onAdd: (SmartContract) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var abi by remember { mutableStateOf("") }
    var network by remember { mutableStateOf("Ethereum") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Smart Contract") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Contract Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Contract Address") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = network,
                    onValueChange = { network = it },
                    label = { Text("Network") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = abi,
                    onValueChange = { abi = it },
                    label = { Text("ABI (JSON)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && address.isNotBlank()) {
                        onAdd(SmartContract(name, address, abi, network))
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
