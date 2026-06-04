package com.example.data.dao

import androidx.room.*
import com.example.data.model.WeatherData
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Query("SELECT * FROM weather_data ORDER BY region ASC")
    fun getAll(): Flow<List<WeatherData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weather: WeatherData): Long

    @Query("SELECT * FROM weather_data WHERE region = :region LIMIT 1")
    suspend fun getByRegion(region: String): WeatherData?
}
