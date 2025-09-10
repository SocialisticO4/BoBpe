package com.example.phonepe.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert
    suspend fun insert(transaction: Transaction)

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    fun getTransactionById(id: Int): Flow<Transaction?>

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC LIMIT 1")
    fun getLatestTransaction(): Flow<Transaction?> // Changed to Flow for consistency
}
