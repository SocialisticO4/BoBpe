package com.example.phonepe.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.phonepe.data.Transaction
import com.example.phonepe.viewmodel.HistoryViewModel
import com.example.phonepe.ui.theme.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.input.TextFieldValue

@Composable
fun PaymentScreen(
    name: String,
    upiId: String,
    qrData: String? = null,
    navController: NavController,
    historyViewModel: HistoryViewModel
) {
    var amount by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)) {

        Column(modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(16.dp)) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Paying to", fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(name, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text(upiId, fontSize = 14.sp, color = Gray500)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            TextField(
                value = amount,
                onValueChange = { new ->
                    // allow only digits and optional decimal point
                    if (new.matches(Regex("\\d*\\.?\\d*"))) amount = new
                },
                placeholder = {
                    Text(
                        text = "₹0",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = Gray500
                    )
                },
                textStyle = TextStyle(
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = Gray900
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(200.dp)) // allow content to scroll above button on small screens
        }

        // Pay button anchored to bottom and respects IME
        Button(
            onClick = {
                focusManager.clearFocus(true)
                val paymentAmount = amount.toDoubleOrNull()
                if (paymentAmount != null && paymentAmount > 0) {
                    val transaction = Transaction(
                        recipientName = name,
                        upiId = upiId,
                        amount = paymentAmount,
                        transactionType = com.example.phonepe.data.TransactionType.PAYMENT,
                        status = com.example.phonepe.data.TransactionStatus.SUCCESS,
                        description = if (qrData != null) "QR Payment to $name" else "Payment to $name",
                        category = "Personal",
                        paymentMethod = "UPI",
                        isSuccessful = true,
                        qrCodeData = qrData,
                        merchantName = if (qrData != null) name else null,
                        paymentSource = if (qrData != null) "QR_SCAN" else "Manual"
                    )
                    historyViewModel.insert(transaction)
                    navController.navigate("success") {
                        popUpTo("home") { inclusive = false }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .imePadding()
        ) {
            Text("Pay")
        }
    }
}
