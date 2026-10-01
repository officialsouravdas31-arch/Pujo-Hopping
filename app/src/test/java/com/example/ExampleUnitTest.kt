package com.example

import com.example.data.dataset.KolkataPandalDataset
import com.example.service.PlanCriteria
import com.example.service.RoutePlannerService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testPandalDatasetIntegrity() {
        val pandals = KolkataPandalDataset.pandals
        assertTrue("Dataset should contain at least 50 pandals", pandals.size >= 50)

        // Verify zones
        val south = pandals.filter { it.zone.contains("South") }
        val north = pandals.filter { it.zone.contains("North") }
        val central = pandals.filter { it.zone.contains("Central") }
        val east = pandals.filter { it.zone.contains("East") }

        assertTrue("South Kolkata must have pandals", south.isNotEmpty())
        assertTrue("North Kolkata must have pandals", north.isNotEmpty())
        assertTrue("Central Kolkata must have pandals", central.isNotEmpty())
        assertTrue("East Kolkata must have pandals", east.isNotEmpty())

        // Verify coordinates and metadata
        pandals.forEach { p ->
            assertTrue("Latitude within Kolkata bounds", p.latitude in 22.40..22.70)
            assertTrue("Longitude within Kolkata bounds", p.longitude in 88.25..88.55)
            assertTrue("Nearest metro present", p.nearestMetro.isNotBlank())
            assertTrue("Category set", p.category.label.isNotBlank())
        }
    }

    @Test
    fun testRoutePlannerGeneration() = runBlocking {
        val criteria = PlanCriteria(
            startLocation = "Gariahat",
            durationHours = 4,
            transportMode = "Metro + Walking",
            preferences = setOf("Famous Pandals", "Theme Pandals"),
            walkingTolerance = "Moderate"
        )

        val route = RoutePlannerService.generateRoute(criteria)
        assertNotNull(route)
        assertTrue("Stops should be non-empty", route.stops.isNotEmpty())
        assertNotNull("Food break scheduled", route.foodBreakStop)
        assertTrue("Walking distance calculated", route.estimatedWalkingKm > 0)
        assertTrue("AI Explanation present", route.aiExplanation.isNotBlank())
    }
}
