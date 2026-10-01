package com.example.service

import com.example.data.dataset.KolkataPandalDataset
import com.example.data.model.CrowdLevel
import com.example.data.model.GeneratedRoute
import com.example.data.model.Pandal
import com.example.data.model.PandalCategory
import com.example.data.model.RouteStop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class PlanCriteria(
    val startLocation: String,
    val durationHours: Int, // 2, 4, 6, 8, 12 (Full Night)
    val transportMode: String,
    val preferences: Set<String>,
    val walkingTolerance: String // "Low", "Moderate", "High"
)

object RoutePlannerService {

    // Approximate Kolkata landmarks anchor coordinates
    private val START_LOCATIONS = mapOf(
        "Gariahat" to Pair(22.5190, 88.3665),
        "Kalighat" to Pair(22.5180, 88.3470),
        "Ballygunge" to Pair(22.5280, 88.3650),
        "Dhakuria" to Pair(22.5070, 88.3680),
        "Shyambazar" to Pair(22.6020, 88.3720),
        "Bagbazar" to Pair(22.6030, 88.3680),
        "Hatibagan" to Pair(22.5970, 88.3710),
        "College Street" to Pair(22.5740, 88.3640),
        "Esplanade" to Pair(22.5640, 88.3520),
        "Sealdah" to Pair(22.5680, 88.3710),
        "Salt Lake (Karunamoyee)" to Pair(22.5860, 88.4140),
        "New Town" to Pair(22.5830, 88.4600),
        "Dum Dum" to Pair(22.6200, 88.4000),
        "Current Location (South Kolkata)" to Pair(22.5220, 88.3580)
    )

    fun getAvailableStartLocations(): List<String> = START_LOCATIONS.keys.toList()

    suspend fun generateRoute(criteria: PlanCriteria): GeneratedRoute = withContext(Dispatchers.Default) {
        val startCoord = START_LOCATIONS[criteria.startLocation] ?: Pair(22.5220, 88.3580)
        val allPandals = KolkataPandalDataset.pandals

        // 1. Determine target number of pandals based on available time & walking tolerance
        val targetCount = when (criteria.durationHours) {
            2 -> if (criteria.walkingTolerance == "Low") 2 else 3
            4 -> if (criteria.walkingTolerance == "Low") 3 else 4
            6 -> if (criteria.walkingTolerance == "Low") 5 else 6
            8 -> if (criteria.walkingTolerance == "Low") 6 else 7
            else -> 8 // Full night
        }

        // 2. Score pandals based on criteria
        val scored = allPandals.map { pandal ->
            val dist = calculateDistanceKm(startCoord.first, startCoord.second, pandal.latitude, pandal.longitude)
            var score = 100.0 - (dist * 12.0) // Proximity advantage

            if (criteria.preferences.contains("Famous Pandals") && pandal.category == PandalCategory.POPULAR) {
                score += 35.0
            }
            if (criteria.preferences.contains("Theme Pandals") && pandal.category == PandalCategory.THEME) {
                score += 35.0
            }
            if (criteria.preferences.contains("Traditional Puja") && pandal.category == PandalCategory.TRADITIONAL) {
                score += 35.0
            }
            if (criteria.preferences.contains("Family Friendly") && pandal.facilities.familyFriendly) {
                score += 25.0
            }
            if (criteria.preferences.contains("Less Crowd")) {
                when (pandal.crowdLevel) {
                    CrowdLevel.LOW -> score += 40.0
                    CrowdLevel.MODERATE -> score += 20.0
                    CrowdLevel.HIGH -> score -= 15.0
                    CrowdLevel.EXTREME -> score -= 35.0
                }
            }
            if (criteria.walkingTolerance == "Low" && pandal.crowdLevel == CrowdLevel.EXTREME) {
                score -= 20.0 // Avoid crushing long standing queues
            }

            Pair(pandal, score)
        }.sortedByDescending { it.second }

        // Take top candidate pool
        val candidatePool = scored.take(16).map { it.first }.toMutableList()

        // 3. Greedy path solver starting from startCoord to minimize jumping
        val routePandals = mutableListOf<Pandal>()
        var currentLat = startCoord.first
        var currentLng = startCoord.second

        while (routePandals.size < targetCount && candidatePool.isNotEmpty()) {
            val nextBest = candidatePool.minByOrNull {
                calculateDistanceKm(currentLat, currentLng, it.latitude, it.longitude)
            } ?: break

            routePandals.add(nextBest)
            candidatePool.remove(nextBest)
            currentLat = nextBest.latitude
            currentLng = nextBest.longitude
        }

        // 4. Build timed schedule
        var startHour = 18 // 6:00 PM default start
        var startMinute = 0
        var totalWalkKm = 0.0

        val stops = mutableListOf<RouteStop>()
        var prevLat = startCoord.first
        var prevLng = startCoord.second

        routePandals.forEachIndexed { index, pandal ->
            val legDist = calculateDistanceKm(prevLat, prevLng, pandal.latitude, pandal.longitude)
            totalWalkKm += if (criteria.transportMode == "Walking") legDist else (legDist * 0.35)

            val transitMinutes = when (criteria.transportMode) {
                "Walking" -> (legDist * 13).toInt().coerceIn(6, 25)
                "Metro" -> 12
                "Cab", "Car" -> 15
                else -> 10
            }

            // Advance time for travel
            startMinute += transitMinutes
            if (startMinute >= 60) {
                startHour += startMinute / 60
                startMinute %= 60
            }

            val timeLabel = formatTime(startHour, startMinute)
            val visitDuration = when (pandal.crowdLevel) {
                CrowdLevel.LOW -> 18
                CrowdLevel.MODERATE -> 24
                CrowdLevel.HIGH -> 32
                CrowdLevel.EXTREME -> 42
            }

            val transportHint = if (index == 0) {
                "Head from ${criteria.startLocation} to ${pandal.name} via ${criteria.transportMode} (~$transitMinutes min)"
            } else {
                "Transit from previous pandal (~$transitMinutes min, nearest ${pandal.nearestMetro})"
            }

            stops.add(
                RouteStop(
                    timeLabel = timeLabel,
                    pandal = pandal,
                    visitMinutes = visitDuration,
                    travelFromPrevious = if (index == 0) "Start from ${criteria.startLocation}" else "Leg $index (~${String.format(Locale.US, "%.1f", legDist)} km)",
                    travelMinutes = transitMinutes,
                    transportHint = transportHint
                )
            )

            // Advance time for pandal exploration
            startMinute += visitDuration
            if (startMinute >= 60) {
                startHour += startMinute / 60
                startMinute %= 60
            }

            prevLat = pandal.latitude
            prevLng = pandal.longitude
        }

        // Insert food break near midpoint
        val midIdx = (stops.size / 2).coerceAtLeast(1)
        val foodPandal = stops.getOrNull(midIdx)?.pandal
        val foodSpot = when {
            foodPandal?.area?.contains("Gariahat", true) == true -> "Gariahat Crossing (Kathi Rolls, Singara & Mishti)"
            foodPandal?.area?.contains("Kalighat", true) == true -> "Rashbehari Avenue (Phuchka & Telebhaja Stalls)"
            foodPandal?.area?.contains("College", true) == true -> "Indian Coffee House & College Square Dhabas"
            foodPandal?.area?.contains("Hatibagan", true) == true -> "Hatibagan Fariapukur Telebhaja & Cha Adda"
            foodPandal?.area?.contains("Salt Lake", true) == true -> "Karunamoyee Food Hub & Mishti Hub"
            else -> "${foodPandal?.area ?: criteria.startLocation} Puja Street Food Zone"
        }

        val totalTimeStr = "${criteria.durationHours} Hours"
        val suggestedTransport = criteria.transportMode

        // Explanation text
        val whyThisRoute = buildString {
            append("Optimized for ${criteria.startLocation} with minimal travel backtracking. ")
            append("We arranged ${stops.size} exceptional pandals within ${criteria.durationHours} hours, matching your preferences for ")
            append(criteria.preferences.joinToString(", ").ifEmpty { "iconic Puja experiences" })
            append(". Walking load is calibrated to ${criteria.walkingTolerance} (~${String.format(Locale.US, "%.1f", totalWalkKm)} km total). ")
            append("A refreshing street-food break is scheduled at $foodSpot.")
        }

        GeneratedRoute(
            title = "PujoPlan: ${criteria.startLocation} Circuit",
            startLocation = criteria.startLocation,
            totalTimeLabel = totalTimeStr,
            totalPandals = stops.size,
            estimatedWalkingKm = (totalWalkKm * 10).toInt() / 10.0,
            suggestedTransport = suggestedTransport,
            stops = stops,
            foodBreakStop = foodSpot,
            foodBreakTime = "Mid-circuit break (30 min)",
            aiExplanation = whyThisRoute
        )
    }

    private fun formatTime(hour24: Int, min: Int): String {
        val h = hour24 % 24
        val displayH = if (h == 0) 12 else if (h > 12) h - 12 else h
        val ampm = if (h < 12) "AM" else "PM"
        return String.format(Locale.US, "%d:%02d %s", displayH, min, ampm)
    }

    private fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Earth radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}
