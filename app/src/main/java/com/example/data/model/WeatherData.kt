package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_data")
data class WeatherData(
    @PrimaryKey val region: String, // e.g., "Atsimo-Andrefana", "Androy", "Anosy", "Ihorombe"
    val temperature: Double,
    val rain: Double, // in mm
    val wind: Double, // in km/h
    val humidity: Double, // in %
    val riskLevel: String, // "VERT" | "JAUNE" | "ORANGE" | "ROUGE"
    val updatedAt: Long = System.currentTimeMillis()
)
