package com.example.data.dao

import androidx.room.*
import com.example.data.model.MarketItem
import kotlinx.coroutines.flow.Flow

@Dao
interface MarketDao {
    @Query("SELECT * FROM market_items WHERE deleted = 0 ORDER BY updatedAt DESC")
    fun getAllActive(): Flow<List<MarketItem>>

    @Query("SELECT * FROM market_items WHERE deleted = 1 ORDER BY deletedAt DESC")
    fun getDeleted(): Flow<List<MarketItem>>

    @Query("SELECT * FROM market_items WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): MarketItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: MarketItem): Long

    @Update
    suspend fun update(item: MarketItem)

    @Query("DELETE FROM market_items WHERE id = :id")
    suspend fun deletePermanent(id: Long)

    @Query("DELETE FROM market_items WHERE deleted = 1 AND deletedAt < :timestamp")
    suspend fun deleteOlderThan(timestamp: Long): Int
}
