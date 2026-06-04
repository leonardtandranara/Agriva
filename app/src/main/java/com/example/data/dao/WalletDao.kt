package com.example.data.dao

import androidx.room.*
import com.example.data.model.WalletTransaction
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {
    @Query("SELECT * FROM wallet_transactions WHERE deleted = 0 ORDER BY createdAt DESC")
    fun getAllActive(): Flow<List<WalletTransaction>>

    @Query("SELECT * FROM wallet_transactions WHERE deleted = 1 ORDER BY deletedAt DESC")
    fun getDeleted(): Flow<List<WalletTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: WalletTransaction): Long

    @Update
    suspend fun update(transaction: WalletTransaction)

    @Query("DELETE FROM wallet_transactions WHERE id = :id")
    suspend fun deletePermanent(id: Long)

    @Query("DELETE FROM wallet_transactions WHERE deleted = 1 AND deletedAt < :timestamp")
    suspend fun deleteOlderThan(timestamp: Long): Int
}
