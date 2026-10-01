package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val pandalId: String,
    val savedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "visited_pandals")
data class VisitedEntity(
    @PrimaryKey val pandalId: String,
    val visitedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "crowd_reports")
data class CrowdReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val pandalId: String,
    val reportedLevel: String, // "Low", "Moderate", "High", "Extreme"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_routes")
data class SavedRouteEntity(
    @PrimaryKey val id: String,
    val title: String,
    val startLocation: String,
    val durationHours: String,
    val transportMode: String,
    val pandalIdsCsv: String,
    val totalPandals: Int,
    val estimatedWalkingKm: Double,
    val explanation: String,
    val createdAt: Long = System.currentTimeMillis()
)
