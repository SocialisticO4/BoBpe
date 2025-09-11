package com.example.phonepe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.phonepe.data.Transaction
import com.example.phonepe.data.TransactionDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(private val transactionDao: TransactionDao) : ViewModel() {

    val allTransactions: StateFlow<List<Transaction>> = transactionDao.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val latestTransaction: StateFlow<Transaction?> = transactionDao.getLatestTransaction()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun insert(transaction: Transaction) = viewModelScope.launch {
        transactionDao.insert(transaction)
    }

    fun transactionById(id: Int): StateFlow<Transaction?> =
        transactionDao.getTransactionById(id)
            .stateIn(viewModelScope, SharingStarted.Eagerly, null) // Changed to Eagerly
}

class HistoryViewModelFactory(private val transactionDao: TransactionDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HistoryViewModel(transactionDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
