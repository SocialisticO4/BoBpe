package com.example.phonepe.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Transaction::class, Event::class], version = 5, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun eventDao(): EventDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Canonical DB filename used by the app
        private const val DB_NAME = "phonepe.db"

        // Known legacy/test filenames we may want to remove when recreating the DB
        private val LEGACY_DB_NAMES = listOf("phonepe_v5.db")

        fun get(context: Context): AppDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DB_NAME
            ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
        }

        // Backwards compatibility alias
        fun getDatabase(context: Context): AppDatabase = get(context)

        /**
         * Force-close the current DB instance (if any), remove known DB files from the
         * app's data directory, and recreate a fresh database instance. Useful during
         * development to recover from Room schema hash mismatches without using adb.
         * WARNING: This deletes all local DB data.
         */
        fun clearAndRecreate(context: Context) {
            synchronized(this) {
                try {
                    INSTANCE?.close()
                } catch (e: Exception) {
                    // ignore close errors
                }
                // Delete canonical DB and any legacy filenames
                try {
                    context.deleteDatabase(DB_NAME)
                    for (name in LEGACY_DB_NAMES) context.deleteDatabase(name)
                } catch (e: Exception) {
                    // ignore delete errors
                }
                INSTANCE = null
                // Create a fresh instance
                get(context)
            }
        }
    }
}
