package com.example.data.model

enum class CrowdLevel(val label: String, val queueEstimate: String, val hexColor: Long) {
    LOW("Low Crowd", "5–15 min queue", 0xFF16A34A),
    MODERATE("Moderate Crowd", "20–35 min queue", 0xFFCA8A04),
    HIGH("High Crowd", "40–60 min queue", 0xFFEA580C),
    EXTREME("Extreme Crowd", "75+ min queue", 0xFFDC2626)
}

enum class PandalCategory(val label: String, val badgeColor: Long, val emoji: String) {
    POPULAR("Popular", 0xFFDC2626, "🔥"),
    THEME("Theme", 0xFFD97706, "🎨"),
    TRADITIONAL("Traditional", 0xFF7C3AED, "🛕"),
    FAMILY_FRIENDLY("Family Friendly", 0xFF059669, "👨‍👩‍👧"),
    HIDDEN_GEM("Hidden Gem", 0xFF2563EB, "💎")
}

data class TransportOptions(
    val metro: String,
    val bus: String,
    val cab: String,
    val walking: String
)

data class Facilities(
    val parking: Boolean,
    val toilet: Boolean,
    val foodNearby: Boolean,
    val familyFriendly: Boolean,
    val waterAvailable: Boolean = true,
    val firstAid: Boolean = true,
    val atmNearby: Boolean = true
)

data class Pandal(
    val id: String,
    val name: String,
    val area: String,
    val zone: String, // "South", "North", "Central", "East / Salt Lake"
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val category: PandalCategory,
    val description: String,
    val theme: String,
    val rating: Double,
    val reviewCount: Int,
    val crowdLevel: CrowdLevel,
    val estimatedQueueMinutes: Int,
    val nearestMetro: String,
    val metroDistance: String,
    val walkingDistance: String,
    val transportOptions: TransportOptions,
    val facilities: Facilities,
    val bestTimeToVisit: String,
    val openingTime: String,
    val closingTime: String,
    val imageUrl: String,
    val verified: Boolean = true,
    val dataStatus: String = "demo"
)
