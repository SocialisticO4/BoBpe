package com.example.phonepe.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

enum class TransactionType {
    PAYMENT, RECEIVED, RECHARGE, BILL_PAYMENT, BANK_TRANSFER
}

enum class TransactionStatus {
    SUCCESS, PENDING, FAILED, CANCELLED
}

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val recipientName: String,
    val upiId: String, // Recipient's UPI ID
    val amount: Double,
    val transactionType: TransactionType = TransactionType.PAYMENT,
    val status: TransactionStatus = TransactionStatus.SUCCESS,
    val description: String = "",
    val transactionId: String = generateTransactionId(), // For "Transaction ID" display
    val utrNumber: String = generateUtrNumber(), // For "UTR" display
    val bankAccountLastFour: String = generateBankAccountLastFour(), // For "Debited from"
    val timestamp: Long = System.currentTimeMillis(),
    val category: String = "Personal", // Personal, Business, Bills, etc.
    val paymentMethod: String = "UPI", // UPI, Wallet, Card, etc.
    val isSuccessful: Boolean = true, // Consolidate with status? For now, keep if used elsewhere
    val qrCodeData: String? = null, // Raw QR code data if scanned
    val merchantName: String? = null, // Merchant name from QR if different from recipient
    val paymentSource: String = "Manual" // QR_SCAN, MANUAL, CONTACT, etc.
)

// Generates ID like T<date><time><random>
private fun generateTransactionId(): String {
    val timestamp = System.currentTimeMillis()
    val dateTimePart = SimpleDateFormat("yyMMddHHmmssSSS", Locale.getDefault()).format(Date(timestamp))
    val randomSuffix = (100..999).random()
    return "T${dateTimePart}${randomSuffix}"
}

// Generates a 12-digit UTR number
private fun generateUtrNumber(): String {
    return (1..12).map { Random.nextInt(0, 10) }.joinToString("")
}

// Generates a masked bank account like XXXXXX1234
private fun generateBankAccountLastFour(): String {
    val lastFour = (1000..9999).random()
    return "XXXXXX${lastFour}"
}
