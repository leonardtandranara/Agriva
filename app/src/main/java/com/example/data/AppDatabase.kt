package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.MarketDao
import com.example.data.dao.WalletDao
import com.example.data.dao.WeatherDao
import com.example.data.model.MarketItem
import com.example.data.model.WalletTransaction
import com.example.data.model.WeatherData

@Database(
    entities = [
        MarketItem::class,
        WalletTransaction::class,
        WeatherData::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun marketDao(): MarketDao
    abstract fun walletDao(): WalletDao
    abstract fun weatherDao(): WeatherDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "agriva_mada_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
