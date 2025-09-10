package com.example.phonepe.ui.screens

import android.util.Log // Added for logging
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.phonepe.data.Transaction
import com.example.phonepe.data.TransactionStatus
import com.example.phonepe.data.TransactionType
import com.example.phonepe.viewmodel.HistoryViewModel // Correct ViewModel import
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(
    navController: NavController, 
    historyViewModel: HistoryViewModel // Use the passed ViewModel
) {
    val transactions by historyViewModel.allTransactions.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Header with filters
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Transaction History",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Row {
                FilterChip(
                    onClick = { /* TODO: Implement filter logic */ },
                    label = { Text("All", fontSize = 12.sp) },
                    selected = true, // This should be dynamic based on selected filter
                    modifier = Modifier.padding(end = 8.dp)
                )
                FilterChip(
                    onClick = { /* TODO: Implement filter logic */ },
                    label = { Text("Sent", fontSize = 12.sp) },
                    selected = false // This should be dynamic
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (transactions.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.HourglassEmpty,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No transactions yet",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Your payment history will appear here",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                val groupedTransactions = transactions.groupBy {
                    SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(it.timestamp))
                }
                
                groupedTransactions.entries.forEach { (date, transactionList) ->
                    item {
                        Text(
                            text = date,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    
                    items(transactionList) { transaction ->
                        TransactionItem(
                            transaction = transaction,
                            onClick = {
                                Log.d("HistoryScreen", "Navigating to detail for transaction ID: ${transaction.id}") // Logging transaction ID
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
private fun TransactionItem(
    transaction: Transaction,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(getTransactionIconBackground(transaction.transactionType)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getTransactionIcon(transaction.transactionType),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = getTransactionTitle(transaction),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (transaction.paymentSource == "QR_SCAN") 
                        "${transaction.upiId} • QR Payment" 
                    else transaction.upiId,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(transaction.timestamp)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "₹${formatAmount(transaction.amount)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (transaction.transactionType == TransactionType.RECEIVED) 
                        Color(0xFF10B981) else MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = getStatusIcon(transaction.status),
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = getStatusColor(transaction.status)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = transaction.status.name,
                        style = MaterialTheme.typography.labelSmall,
                        color = getStatusColor(transaction.status)
                    )
                }
            }
        }
    }
}

// Helper functions (getTransactionTitle, getTransactionIcon, etc.) remain the same
private fun getTransactionTitle(transaction: Transaction): String {
    return when (transaction.transactionType) {
        TransactionType.PAYMENT -> {
            val prefix = if (transaction.paymentSource == "QR_SCAN") "QR Payment to" else "Paid to"
            val name = transaction.merchantName ?: transaction.recipientName
            "$prefix $name"
        }
        TransactionType.RECEIVED -> "Received from ${transaction.recipientName}"
        TransactionType.RECHARGE -> "Mobile Recharge"
        TransactionType.BILL_PAYMENT -> "Bill Payment"
        TransactionType.BANK_TRANSFER -> "Bank Transfer"
    }
}

private fun getTransactionIcon(type: TransactionType): ImageVector {
    return when (type) {
        TransactionType.PAYMENT -> Icons.Default.ArrowUpward
        TransactionType.RECEIVED -> Icons.Default.ArrowDownward
        else -> Icons.Default.ArrowUpward // Default or specific icons for others
    }
}

private fun getTransactionIconBackground(type: TransactionType): Color {
    return when (type) {
        TransactionType.PAYMENT -> Color(0xFFEF4444) // Red
        TransactionType.RECEIVED -> Color(0xFF10B981) // Green
        TransactionType.RECHARGE -> Color(0xFF3B82F6) // Blue
        TransactionType.BILL_PAYMENT -> Color(0xFFF59E0B) // Orange
        TransactionType.BANK_TRANSFER -> Color(0xFF8B5CF6) // Purple
    }
}

private fun getStatusIcon(status: TransactionStatus): ImageVector {
    return when (status) {
        TransactionStatus.SUCCESS -> Icons.Default.CheckCircle
        TransactionStatus.FAILED -> Icons.Default.Error
        // Consider PENDING, CANCELLED as well if they are used
        else -> Icons.Default.HourglassEmpty 
    }
}

private fun getStatusColor(status: TransactionStatus): Color {
    return when (status) {
        TransactionStatus.SUCCESS -> Color(0xFF10B981) // Green
        TransactionStatus.FAILED -> Color(0xFFEF4444) // Red
        TransactionStatus.PENDING -> Color(0xFFF59E0B) // Orange
        TransactionStatus.CANCELLED -> Color(0xFF6B7280) // Gray
    }
}

private fun formatAmount(amount: Double): String {
    // Ensure this Locale is what you intend for currency formatting, e.g., for India
    return NumberFormat.getNumberInstance(Locale("en", "IN")).format(amount)
}
