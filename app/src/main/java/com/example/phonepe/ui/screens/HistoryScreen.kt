package com.example.phonepe.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items // Ensure this is the correct items import
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.phonepe.R
import com.example.phonepe.data.Transaction
import com.example.phonepe.data.TransactionType
import com.example.phonepe.viewmodel.HistoryViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Assuming these colors from your theme or define them if not present
val LightPurpleBackground = Color(0xFFF3E5F5) // A light purple, adjust as needed
val PurpleAccent = Color(0xFF673AB7) // A purple for icons/text, adjust as needed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavController,
    historyViewModel: HistoryViewModel
) {
    val transactions by historyViewModel.allTransactions.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    // Optimize by remembering the filtered list
    val filteredTransactions = remember(transactions, searchQuery) {
        if (searchQuery.isEmpty()) {
            transactions
        } else {
            transactions.filter { 
                it.recipientName.contains(searchQuery, ignoreCase = true) || 
                it.upiId.contains(searchQuery, ignoreCase = true) ||
                (it.merchantName?.contains(searchQuery, ignoreCase = true) == true) 
            }
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "History",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Button(
                    onClick = { /* TODO: Navigate to My Statements */ },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LightPurpleBackground)
                ) {
                    Icon(
                        Icons.Outlined.Description,
                        contentDescription = "My Statements",
                        modifier = Modifier.size(18.dp),
                        tint = PurpleAccent
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("My Statements", color = PurpleAccent, fontSize = 13.sp)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background) // Use theme background
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search transactions", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    Icon(
                        Icons.Default.Tune, // Filter icon
                        contentDescription = "Filter",
                        tint = PurpleAccent
                    )
                },
                shape = RoundedCornerShape(28.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PurpleAccent,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedContainerColor = LightPurpleBackground.copy(alpha = 0.5f),
                    unfocusedContainerColor = LightPurpleBackground.copy(alpha = 0.5f)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredTransactions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (searchQuery.isNotEmpty()) "No matching transactions found." else "No transactions yet.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = filteredTransactions, 
                        key = { transaction -> transaction.id } // Add key for performance
                    ) { transaction ->
                        TransactionHistoryItem(
                            transaction = transaction,
                            onClick = {
                                Log.d("HistoryScreen", "Navigating to detail for transaction ID: ${transaction.id}")
                                navController.navigate("transaction_detail/${transaction.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionHistoryItem(
    transaction: Transaction,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = if (transaction.transactionType == TransactionType.RECEIVED) {
                painterResource(id = R.drawable.android_transaction_received_icon_selected)
            } else {
                painterResource(id = R.drawable.android_transaction_paid_icon_selected)
            },
            contentDescription = "Transaction type",
            modifier = Modifier.size(36.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (transaction.transactionType == TransactionType.RECEIVED) 
                        "Received from ${transaction.recipientName}" 
                    else 
                        "Paid to ${transaction.merchantName ?: transaction.recipientName}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(transaction.timestamp)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = NumberFormat.getCurrencyInstance(Locale("en", "IN")).format(transaction.amount),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface // Black as per image
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (transaction.transactionType == TransactionType.RECEIVED) "Credited to" else "Debited from",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    Icons.Outlined.AccountBalance, // Placeholder bank/card icon
                    contentDescription = "Account",
                    modifier = Modifier.size(12.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), thickness = 0.5.dp)
}
