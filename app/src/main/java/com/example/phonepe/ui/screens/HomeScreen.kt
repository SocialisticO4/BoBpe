package com.example.phonepe.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue // Existing import
import androidx.compose.runtime.setValue // Added import
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.phonepe.ui.components.NeoPopButton

data class ServiceItem(
    val title: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun HomeScreen(onShowHistory: () -> Unit) {
    val services = listOf(
        ServiceItem("Scan QR", Icons.Default.QrCodeScanner, Color(0xFF6A4C93)),
        ServiceItem("Pay Bills", Icons.Default.Receipt, Color(0xFF1B85DB)),
        ServiceItem("Recharge", Icons.Default.PhoneAndroid, Color(0xFF00D4AA)),
        ServiceItem("Bank Transfer", Icons.Default.AccountBalance, Color(0xFFF72585)),
        ServiceItem("Credit Card", Icons.Default.CreditCard, Color(0xFFFF7F00)),
        ServiceItem("Loans", Icons.Default.LocalAtm, Color(0xFF2ECC71)),
        ServiceItem("Insurance", Icons.Default.Widgets, Color(0xFF8E44AD)),
        ServiceItem("View History", Icons.Default.Receipt, Color(0xFF3498DB))
    )

    var walletBalance by rememberSaveable { mutableStateOf(0.0) }
    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF5B2C87),
                        Color(0xFF6B46C1)
                    )
                )
            )
            .padding(16.dp)
    ) {
        // Header Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Hi, User",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Welcome to PhonePe",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 16.sp
                )
            }
            
            // Profile Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "U",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        // Balance Card — starts at 0.0 and updates only via Add Money
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "PhonePe Wallet",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "₹${"%,.0f".format(walletBalance)}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    TextButton(onClick = { showAddDialog = true }) {
                        Text(
                            text = "Add Money",
                            color = Color(0xFF6B46C1)
                        )
                    }
                }
            }
        }
        
        // Services Grid
        Text(
            text = "Pay or Send Money",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(services) { service ->
                ServiceCard(
                    service = service,
                    onClick = {
                        if (service.title == "View History") {
                            onShowHistory()
                        }
                    }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Quick Actions
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickActionItem("Check Balance", "₹1,23,456")
                QuickActionItem("Last Transaction", "₹500 to John")
                QuickActionItem("Rewards", "₹25 earned")
            }
        }
    }

    if (showAddDialog) {
        var input by remember { mutableStateOf(TextFieldValue("")) }
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    val amt = input.text.toDoubleOrNull()
                    if (amt != null && amt > 0) {
                        walletBalance += amt
                    }
                    showAddDialog = false
                }) { Text("Add") }
            },
            dismissButton = { TextButton(onClick = { showAddDialog = false }) { Text("Cancel") } },
            title = { Text("Add Money") },
            text = {
                Column {
                    Text("Enter amount to add", textAlign = TextAlign.Start)
                    Spacer(Modifier.height(8.dp))
                    TextField(value = input, onValueChange = { input = it }, placeholder = { Text("0") })
                }
            }
        )
    }
}

@Composable
private fun ServiceCard(
    service: ServiceItem,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.size(80.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = service.icon,
                contentDescription = service.title,
                tint = service.color,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = service.title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                maxLines = 2,
                lineHeight = 12.sp
            )
        }
    }
}

@Composable
private fun QuickActionItem(
    title: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 10.sp
        )
    }
}
