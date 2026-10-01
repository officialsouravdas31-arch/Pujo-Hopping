package com.example.data.repository

import com.example.data.dataset.KolkataPandalDataset
import com.example.data.local.CrowdReportEntity
import com.example.data.local.FavoriteEntity
import com.example.data.local.PujoDao
import com.example.data.local.SavedRouteEntity
import com.example.data.local.VisitedEntity
import com.example.data.model.CrowdLevel
import com.example.data.model.Pandal
import com.example.data.model.PandalCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PujoRepository(private val dao: PujoDao) {

    val allPandals: List<Pandal> = KolkataPandalDataset.pandals

    val favoriteIds: Flow<Set<String>> = dao.getAllFavoriteIds().map { it.toSet() }

    val visitedIds: Flow<Set<String>> = dao.getAllVisitedIds().map { it.toSet() }

    val savedRoutes: Flow<List<SavedRouteEntity>> = dao.getAllSavedRoutes()

    val allCrowdReports: Flow<Map<String, CrowdReportEntity>> = dao.getAllCrowdReports().map { list ->
        list.groupBy { it.pandalId }.mapValues { it.value.maxByOrNull { r -> r.timestamp }!! }
    }

    fun getPandalById(id: String): Pandal? = KolkataPandalDataset.getPandalById(id)

    suspend fun toggleFavorite(pandalId: String, isFav: Boolean) {
        if (isFav) {
            dao.removeFavorite(pandalId)
        } else {
            dao.addFavorite(FavoriteEntity(pandalId = pandalId))
        }
    }

    suspend fun toggleVisited(pandalId: String, isVisited: Boolean) {
        if (isVisited) {
            dao.unmarkVisited(pandalId)
        } else {
            dao.markVisited(VisitedEntity(pandalId = pandalId))
        }
    }

    suspend fun submitCrowdReport(pandalId: String, level: CrowdLevel) {
        dao.insertCrowdReport(
            CrowdReportEntity(
                pandalId = pandalId,
                reportedLevel = level.name
            )
        )
    }

    suspend fun saveRoute(
        title: String,
        startLocation: String,
        duration: String,
        transport: String,
        pandalIds: List<String>,
        walkingKm: Double,
        explanation: String
    ) {
        val entity = SavedRouteEntity(
            id = "route_${System.currentTimeMillis()}",
            title = title,
            startLocation = startLocation,
            durationHours = duration,
            transportMode = transport,
            pandalIdsCsv = pandalIds.joinToString(","),
            totalPandals = pandalIds.size,
            estimatedWalkingKm = walkingKm,
            explanation = explanation
        )
        dao.saveRoute(entity)
    }

    suspend fun deleteRoute(id: String) {
        dao.deleteRoute(id)
    }
}
