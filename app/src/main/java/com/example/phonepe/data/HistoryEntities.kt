package com.example.phonepe.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    val route: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: Event)

    @Query("SELECT * FROM events ORDER BY timestamp DESC")
    fun getAll(): Flow<List<Event>>

    @Query("DELETE FROM events")
    suspend fun clear()
}
