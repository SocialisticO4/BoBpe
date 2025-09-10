package com.example.phonepe.ui.screens

import android.util.Log // Added for logging
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // Updated import
import androidx.compose.material.icons.automirrored.filled.Send // Updated import
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.phonepe.R // For drawables
import com.example.phonepe.viewmodel.HistoryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun formatTimestampDetail(timestamp: Long): String {
    val date = Date(timestamp)
    val sdf = SimpleDateFormat("hh:mm a 'on' dd MMM yyyy", Locale.getDefault())
    return sdf.format(date)
}

@OptIn(ExperimentalMaterial3Api::class) // Opt-in for experimental Material 3 APIs
@Composable
fun TransactionDetailScreen(
    navController: NavController,
    historyViewModel: HistoryViewModel,
    transactionId: Int
) {
    Log.d("TransactionDetailScreen", "Received transactionId: $transactionId") // Logging received ID

    val transactionState by historyViewModel.transactionById(transactionId).collectAsState()
    val transaction = transactionState

    Log.d("TransactionDetailScreen", "Collected transaction state: $transaction") // Logging collected state

    Scaffold(
        topBar = {
            if (transaction != null) {
                TopAppBar(
                    title = {
                        Column {
                            Text(text = transaction.transactionType.name.lowercase().replaceFirstChar { it.titlecase() } + " Successful", fontSize = 16.sp, color = Color.White)
                            Text(
                                formatTimestampDetail(transaction.timestamp),
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White) // Updated icon
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF108759)
                    )
                )
            }
        },
    ) { paddingValues ->
        if (transaction == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
                Text("Loading transaction details... for ID: $transactionId", modifier = Modifier.padding(top = 70.dp)) // Displaying ID in loading text
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFF1A1A1A))
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_profile),
                        contentDescription = "Recipient Image",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.DarkGray),
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(transaction.recipientName, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        Text(transaction.upiId, color = Color.Gray, fontSize = 13.sp)
                    }
                    Text("₹${String.format("%.2f", transaction.amount)}", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(16.dp))

                Row {
                    Text("Banking Name: ", color = Color.Gray, fontSize = 13.sp)
                    Text(transaction.recipientName, color = Color.White, fontSize = 13.sp)
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Verified",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(16.dp).padding(start = 4.dp)
                    )
                }

                HorizontalDivider(color = Color.DarkGray, thickness = 1.dp, modifier = Modifier.padding(vertical = 20.dp)) // Updated Divider

                Row(verticalAlignment = Alignment.CenterVertically) {
                     Image(
                        painter = painterResource(id = R.drawable.ic_transfer_details),
                        contentDescription = "Transfer Details",
                        modifier = Modifier.size(24.dp),
                        colorFilter = ColorFilter.tint(Color.Gray)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Transfer Details", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.weight(1f))
                    Icon(Icons.Filled.MoreVert, contentDescription = "More options", tint = Color.Gray)
                }
                Spacer(Modifier.height(16.dp))

                DetailRowDetailScreen("Transaction ID", transaction.transactionId)
                DetailRowDetailScreen("Debited from", "${transaction.bankAccountLastFour}   ₹${String.format("%.2f", transaction.amount)}")
                DetailRowDetailScreen("UTR", transaction.utrNumber)

                Spacer(Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    ActionButtonDetailScreen(icon = Icons.AutoMirrored.Filled.Send, text = "Send Again") {} // Updated icon
                    ActionButtonDetailScreen(icon = Icons.Filled.History, text = "View History") {}
                    ActionButtonDetailScreen(icon = painterResource(id = R.drawable.ic_split), text = "Split Expense") {}
                    ActionButtonDetailScreen(icon = Icons.Filled.IosShare, text = "Share Receipt") {}
                }

                Spacer(Modifier.height(24.dp))
                HorizontalDivider(color = Color.DarkGray, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp)) // Updated Divider

                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("Contact PhonePe Support", color = Color.White, fontSize = 14.sp)
                    Spacer(Modifier.weight(1f))
                    Image(painterResource(id = R.drawable.ic_chevron_right), contentDescription = "Go", colorFilter = ColorFilter.tint(Color.Gray), modifier = Modifier.size(20.dp))
                }
                HorizontalDivider(color = Color.DarkGray, thickness = 1.dp, modifier = Modifier.padding(bottom = 16.dp)) // Updated Divider

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Text("Powered by ", color = Color.Gray, fontSize = 12.sp)
                    Icon(Icons.Filled.VerifiedUser, contentDescription = "Bank Logo", tint = Color.Gray, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun DetailRowDetailScreen(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color.Gray, fontSize = 14.sp, modifier = Modifier.weight(0.4f))
        Text(value, color = Color.White, fontSize = 14.sp, modifier = Modifier.weight(0.6f), textAlign = TextAlign.End)
        IconButton(onClick = { /* TODO: Copy to clipboard */ }, modifier = Modifier.size(32.dp).padding(start = 8.dp)) {
            Icon(Icons.Filled.ContentCopy, contentDescription = "Copy", tint = Color.Gray, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun ActionButtonDetailScreen(icon: Any, text: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 4.dp)) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color.DarkGray.copy(alpha = 0.3f))
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            when (icon) {
                is androidx.compose.ui.graphics.vector.ImageVector -> {
                    Icon(icon, contentDescription = text, tint = Color(0xFF8A2BE2), modifier = Modifier.size(28.dp))
                }
                is androidx.compose.ui.graphics.painter.Painter -> {
                    Image(painter = icon, contentDescription = text, modifier = Modifier.size(28.dp), colorFilter = ColorFilter.tint(Color(0xFF8A2BE2)))
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(text, color = Color.White, fontSize = 12.sp, textAlign = TextAlign.Center)
    }
}
