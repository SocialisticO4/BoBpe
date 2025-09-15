package com.example.phonepe.ui.screens

import androidx.compose.foundation.Image // Import Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale // Import ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource // Import painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.verticalScroll
import androidx.navigation.NavController
import com.example.phonepe.R // Import R for resources
import com.example.phonepe.data.Transaction
import com.example.phonepe.data.TransactionStatus
import com.example.phonepe.data.TransactionType
import com.example.phonepe.ui.theme.Gray500
import com.example.phonepe.ui.theme.Gray900
import com.example.phonepe.viewmodel.HistoryViewModel
import com.example.phonepe.ui.components.NeoPopButton

val PaymentScreenLightGrayBackground = Color(0xFFF0F0F0)

@Composable
fun PaymentScreen(
    name: String,
    upiId: String,
    qrData: String? = null,
    navController: NavController,
    historyViewModel: HistoryViewModel
) {
    var amount by remember { mutableStateOf("") }
    val cleanedName = remember(name) { name.replace("+", " ") }
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val haptic = LocalHapticFeedback.current

    val paymentAmount = amount.toDoubleOrNull()
    val isButtonEnabled = paymentAmount != null && paymentAmount > 0

    Box(modifier = Modifier
        .fillMaxSize()
        .background(PaymentScreenLightGrayBackground)) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(start = 16.dp, end = 16.dp, top = 16.dp) // Content padding
                .padding(bottom = 120.dp) // Increased space for the button at the bottom, adjust if needed
        ) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Paying to", fontSize = 16.sp, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(cleanedName, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)
                    Text(upiId, fontSize = 14.sp, color = Gray500)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            TextField(
                value = amount,
                onValueChange = { newAmountString ->
                    if (newAmountString.matches(Regex("^\\d*\\.?\\d*$"))) {
                        amount = newAmountString
                    }
                },
                placeholder = {
                    Text(
                        text = "₹0",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = Gray500
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = TextStyle(
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = Gray900
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
        }

        NeoPopButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)      // Align the whole button assembly to bottom center of the Box
                .navigationBarsPadding()           // Respect system navigation bars
                .imePadding()                      // Respect keyboard
                .padding(vertical = 16.dp)           // Vertical spacing from safe areas/screen bottom
                // .padding(horizontal = 24.dp) // Optional: if you want guaranteed space from screen edges
                                                 // even if the fractional width makes the button wide.
                                                 // For now, let fillMaxWidth on Image control total width relative to screen.
                .wrapContentSize(Alignment.Center), // NeoPopButton shrinks to its content & is centered
            onClick = {
                println("[PaymentScreen] Pay button clicked. Amount string: '$amount'")
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                focusManager.clearFocus(true)

                val currentPaymentAmount = amount.toDoubleOrNull()
                println("[PaymentScreen] currentPaymentAmount: $currentPaymentAmount, isButtonEnabled from state: $isButtonEnabled")

                if (currentPaymentAmount != null && currentPaymentAmount > 0) {
                    println("[PaymentScreen] Condition for navigation met. Attempting to insert transaction and navigate...")
                    val transaction = Transaction(
                        recipientName = cleanedName,
                        upiId = upiId,
                        amount = currentPaymentAmount,
                        transactionType = TransactionType.PAYMENT,
                        status = TransactionStatus.SUCCESS,
                        description = "Payment to $cleanedName",
                        category = "Personal",
                        paymentMethod = "UPI",
                        isSuccessful = true,
                        qrCodeData = qrData, 
                        merchantName = if (qrData != null) cleanedName else null,
                        paymentSource = if (qrData != null) "QR_SCAN" else "Manual"
                    )
                    historyViewModel.insert(transaction) 
                    navController.navigate("success") {
                        popUpTo("home") { inclusive = false }
                    }
                    println("[PaymentScreen] Navigation to 'success' called.")
                } else {
                    println("[PaymentScreen] Condition for navigation NOT met. currentPaymentAmount: $currentPaymentAmount")
                }
            },
            enabled = isButtonEnabled,
            surfaceColor = Color.Transparent, 
            buttonDepth = 6.dp,             
            content = {                      
                Image(
                    painter = painterResource(id = R.drawable.pay_button_vector),
                    contentDescription = "Pay",
                    modifier = Modifier
                        .fillMaxWidth(0.85f), // Image takes 85% of screen width. Adjust fraction as needed.
                    contentScale = ContentScale.Fit 
                )
            },
            label = null, 
            defaultTextViewBackgroundColor = Color.Transparent, 
            defaultTextViewContentColor = Color.Black 
        )
    }
}
