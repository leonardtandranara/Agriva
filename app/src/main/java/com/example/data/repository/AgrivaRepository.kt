package com.example.data.repository

import com.example.data.dao.MarketDao
import com.example.data.dao.WalletDao
import com.example.data.dao.WeatherDao
import com.example.data.model.MarketItem
import com.example.data.model.WalletTransaction
import com.example.data.model.WeatherData
import kotlinx.coroutines.flow.Flow

class AgrivaRepository(
    private val marketDao: MarketDao,
    private val walletDao: WalletDao,
    private val weatherDao: WeatherDao
) {
    // === Market Items ===
    val activeMarketItems: Flow<List<MarketItem>> = marketDao.getAllActive()
    val deletedMarketItems: Flow<List<MarketItem>> = marketDao.getDeleted()

    suspend fun getMarketItemById(id: Long): MarketItem? = marketDao.getById(id)

    suspend fun insertMarketItem(item: MarketItem): Long = marketDao.insert(item)

    suspend fun updateMarketItem(item: MarketItem) = marketDao.update(item)

    suspend fun softDeleteMarketItem(id: Long) {
        val item = marketDao.getById(id)
        if (item != null) {
            marketDao.update(
                item.copy(
                    deleted = true,
                    deletedAt = System.currentTimeMillis(),
                    status = "inactive"
                )
            )
        }
    }

    suspend fun restoreMarketItem(id: Long) {
        val item = marketDao.getById(id)
        if (item != null) {
            marketDao.update(
                item.copy(
                    deleted = false,
                    deletedAt = null,
                    status = "active",
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun deleteMarketItemPermanent(id: Long) {
        marketDao.deletePermanent(id)
    }

    suspend fun cleanMarketTrash(threshold: Long): Int {
        return marketDao.deleteOlderThan(threshold)
    }


    // === Wallet Transactions ===
    val activeTransactions: Flow<List<WalletTransaction>> = walletDao.getAllActive()
    val deletedTransactions: Flow<List<WalletTransaction>> = walletDao.getDeleted()

    suspend fun insertTransaction(transaction: WalletTransaction): Long = walletDao.insert(transaction)

    suspend fun softDeleteTransaction(transaction: WalletTransaction) {
        walletDao.update(
            transaction.copy(
                deleted = true,
                deletedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun restoreTransaction(transaction: WalletTransaction) {
        walletDao.update(
            transaction.copy(
                deleted = false,
                deletedAt = null
            )
        )
    }

    suspend fun deleteTransactionPermanent(id: Long) {
        walletDao.deletePermanent(id)
    }

    suspend fun cleanWalletTrash(threshold: Long): Int {
        return walletDao.deleteOlderThan(threshold)
    }


    // === Weather ===
    val allWeather: Flow<List<WeatherData>> = weatherDao.getAll()

    suspend fun insertWeather(weather: WeatherData): Long = weatherDao.insert(weather)

    suspend fun getWeatherByRegion(region: String): WeatherData? = weatherDao.getByRegion(region)
}
