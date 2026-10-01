package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PujoDao {
    // Favorites
    @Query("SELECT pandalId FROM favorites ORDER BY savedAt DESC")
    fun getAllFavoriteIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE pandalId = :pandalId")
    suspend fun removeFavorite(pandalId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE pandalId = :pandalId)")
    fun isFavorite(pandalId: String): Flow<Boolean>

    // Visited
    @Query("SELECT pandalId FROM visited_pandals ORDER BY visitedAt DESC")
    fun getAllVisitedIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun markVisited(visited: VisitedEntity)

    @Query("DELETE FROM visited_pandals WHERE pandalId = :pandalId")
    suspend fun unmarkVisited(pandalId: String)

    // Crowd reports
    @Query("SELECT * FROM crowd_reports WHERE pandalId = :pandalId ORDER BY timestamp DESC LIMIT 1")
    fun getLatestCrowdReport(pandalId: String): Flow<CrowdReportEntity?>

    @Query("SELECT * FROM crowd_reports ORDER BY timestamp DESC")
    fun getAllCrowdReports(): Flow<List<CrowdReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrowdReport(report: CrowdReportEntity)

    // Saved Routes
    @Query("SELECT * FROM saved_routes ORDER BY createdAt DESC")
    fun getAllSavedRoutes(): Flow<List<SavedRouteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveRoute(route: SavedRouteEntity)

    @Query("DELETE FROM saved_routes WHERE id = :id")
    suspend fun deleteRoute(id: String)
}
