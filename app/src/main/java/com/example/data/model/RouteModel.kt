package com.example.data.model

data class RouteStop(
    val timeLabel: String,
    val pandal: Pandal,
    val visitMinutes: Int,
    val travelFromPrevious: String,
    val travelMinutes: Int,
    val transportHint: String
)

data class GeneratedRoute(
    val title: String,
    val startLocation: String,
    val totalTimeLabel: String,
    val totalPandals: Int,
    val estimatedWalkingKm: Double,
    val suggestedTransport: String,
    val stops: List<RouteStop>,
    val foodBreakStop: String?,
    val foodBreakTime: String?,
    val aiExplanation: String
)
