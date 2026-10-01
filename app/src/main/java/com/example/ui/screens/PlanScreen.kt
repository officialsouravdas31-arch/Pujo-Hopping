package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GeneratedRoute
import com.example.service.RoutePlannerService
import com.example.ui.ScreenTab
import com.example.ui.components.CrowdBadge
import com.example.ui.theme.BengalRed
import com.example.ui.theme.FestiveGold
import com.example.ui.theme.FestiveGoldSoft

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlanScreen(
    currentStep: Int,
    onStepChange: (Int) -> Unit,
    startLocation: String,
    onStartLocationChange: (String) -> Unit,
    durationHours: Int,
    onDurationChange: (Int) -> Unit,
    transportMode: String,
    onTransportChange: (String) -> Unit,
    preferences: Set<String>,
    onTogglePreference: (String) -> Unit,
    walkingTolerance: String,
    onWalkingToleranceChange: (String) -> Unit,
    isGenerating: Boolean,
    generatedRoute: GeneratedRoute?,
    onGenerateRoute: () -> Unit,
    onSaveRoute: () -> Unit,
    onOpenPandalDetail: (String) -> Unit,
    onNavigateTab: (ScreenTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(if (currentStep < 6) Color(0xFF11130F) else MaterialTheme.colorScheme.background)
            .testTag("plan_screen")
    ) {
        // Wizard Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BengalRed)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (currentStep > 1 && currentStep < 6) {
                            IconButton(
                                onClick = { onStepChange(currentStep - 1) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text(
                            text = if (currentStep == 6) "Your Puja Itinerary" else "Plan My Puja",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    if (currentStep < 6) {
                        Text(
                            text = "Step $currentStep of 5",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = FestiveGoldSoft
                        )
                    } else {
                        IconButton(onClick = { onStepChange(1) }) {
                            Icon(Icons.Default.Refresh, contentDescription = "New Plan", tint = Color.White)
                        }
                    }
                }

                if (currentStep < 6) {
                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { currentStep / 5f },
                        modifier = Modifier.fillMaxWidth(),
                        color = FestiveGold,
                        trackColor = Color(0x33FFFFFF),
                    )
                }
            }
        }

        // Animated step content
        AnimatedContent(targetState = currentStep, label = "wizard_step") { step ->
            when (step) {
                1 -> StepStartLocation(
                    selected = startLocation,
                    onSelect = onStartLocationChange,
                    onNext = { onStepChange(2) }
                )
                2 -> StepDuration(
                    selected = durationHours,
                    onSelect = onDurationChange,
                    onNext = { onStepChange(3) }
                )
                3 -> StepTransport(
                    selected = transportMode,
                    onSelect = onTransportChange,
                    onNext = { onStepChange(4) }
                )
                4 -> StepPreferences(
                    selected = preferences,
                    onToggle = onTogglePreference,
                    onNext = { onStepChange(5) }
                )
                5 -> StepWalking(
                    selected = walkingTolerance,
                    onSelect = onWalkingToleranceChange,
                    isGenerating = isGenerating,
                    onGenerate = onGenerateRoute
                )
                6 -> GeneratedRouteView(
                    route = generatedRoute,
                    onSaveRoute = onSaveRoute,
                    onOpenPandal = onOpenPandalDetail,
                    onViewOnMap = { onNavigateTab(ScreenTab.MAP) },
                    onShareWhatsApp = {
                        val shareText = buildString {
                            append("🏮 My PujoPlan 2026 Itinerary!\n")
                            append("📍 Starting: ${generatedRoute?.startLocation} • ${generatedRoute?.totalTimeLabel}\n")
                            append("🚶 Total ${generatedRoute?.totalPandals} Pandals • ~${generatedRoute?.estimatedWalkingKm} km walking\n\n")
                            generatedRoute?.stops?.forEachIndexed { i, stop ->
                                append("${stop.timeLabel} - ${stop.pandal.name} (${stop.pandal.area})\n")
                            }
                            if (generatedRoute?.foodBreakStop != null) {
                                append("\n🍴 Food break: ${generatedRoute.foodBreakStop}\n")
                            }
                            append("\nCreated with PujoPlan Kolkata 2026.")
                        }
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share My Puja Plan"))
                    }
                )
            }
        }
    }
}

// Theme Tokens for Plan Flow
private val PlanFlowBg = Color(0xFF11130F)
private val PlanCardUnselectedBg = Color(0xFF151713)
private val PlanCardUnselectedBorder = Color(0x1AFFFFFF)
private val PlanCardSelectedBg = Color(0xFFB5123A)
private val PlanCardSelectedBorder = Color(0xFFF5C451)
private val PlanButtonBg = Color(0xFFB5123A)
private val PlanGoldAccent = Color(0xFFF5C451)
private val PlanTextPrimary = Color(0xFFFFFFFF)
private val PlanTextSecondary = Color(0xFFF5F5F5)
private val PlanTextMuted = Color(0xFFB8B8B8)

// STEP 1: Where are you starting from?
@Composable
private fun StepStartLocation(
    selected: String,
    onSelect: (String) -> Unit,
    onNext: () -> Unit
) {
    val locations = RoutePlannerService.getAvailableStartLocations()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PlanFlowBg)
            .padding(18.dp)
            .testTag("step_1_start_location")
    ) {
        Text(
            text = "STEP 1 OF 5",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = PlanGoldAccent,
            letterSpacing = 1.2.sp
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = "Where are you starting from?",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = PlanTextPrimary
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = "Select your starting hub or neighborhood in Kolkata.",
            fontSize = 13.sp,
            color = PlanTextMuted
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(locations) { _, loc ->
                val isSelected = loc == selected
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = if (isSelected) 8.dp else 0.dp,
                            shape = RoundedCornerShape(14.dp),
                            spotColor = Color(0xFFB5123A),
                            ambientColor = Color(0xFFB5123A)
                        )
                        .clickable { onSelect(loc) }
                        .testTag("start_loc_$loc"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) PlanCardSelectedBg else PlanCardUnselectedBg
                    ),
                    border = BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) PlanCardSelectedBorder else PlanCardUnselectedBorder
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 15.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        if (isSelected) Color(0x33000000) else Color(0x1AFFFFFF),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = if (isSelected) PlanGoldAccent else Color(0xFFE05370),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = loc,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 15.sp,
                                    color = if (isSelected) PlanTextPrimary else PlanTextSecondary
                                )
                                if (isSelected) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Selected starting hub",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = PlanGoldAccent
                                    )
                                }
                            }
                        }
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .background(PlanGoldAccent, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color(0xFF4C0519),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .shadow(4.dp, RoundedCornerShape(14.dp), spotColor = Color(0xFFB5123A))
                .testTag("step_1_next_btn"),
            colors = ButtonDefaults.buttonColors(
                containerColor = PlanButtonBg,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = "Next: Time Available →",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.White,
                letterSpacing = 0.5.sp
            )
        }
    }
}

// STEP 2: How much time do you have?
@Composable
private fun StepDuration(
    selected: Int,
    onSelect: (Int) -> Unit,
    onNext: () -> Unit
) {
    val options = listOf(
        Pair(2, "2 Hours • Quick hopping (2–3 pandals)"),
        Pair(4, "4 Hours • Balanced evening route (4–5 pandals)"),
        Pair(6, "6 Hours • Comprehensive circuit (6–7 pandals)"),
        Pair(8, "8 Hours • Grand night tour (7–8 pandals)"),
        Pair(12, "Full Night (All Night) • Dusk to dawn marathon")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PlanFlowBg)
            .padding(18.dp)
            .testTag("step_2_duration")
    ) {
        Text("STEP 2 OF 5", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PlanGoldAccent, letterSpacing = 1.2.sp)
        Spacer(modifier = Modifier.height(3.dp))
        Text("How much time do you have?", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = PlanTextPrimary)
        Spacer(modifier = Modifier.height(3.dp))
        Text("PujoPlan will adjust queue wait times and travel pacing accordingly.", fontSize = 13.sp, color = PlanTextMuted)

        Spacer(modifier = Modifier.height(20.dp))

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            options.forEach { (hours, desc) ->
                val isSelected = selected == hours
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = if (isSelected) 8.dp else 0.dp,
                            shape = RoundedCornerShape(14.dp),
                            spotColor = Color(0xFFB5123A),
                            ambientColor = Color(0xFFB5123A)
                        )
                        .clickable { onSelect(hours) }
                        .testTag("duration_$hours"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) PlanCardSelectedBg else PlanCardUnselectedBg
                    ),
                    border = BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) PlanCardSelectedBorder else PlanCardUnselectedBorder
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (hours == 12) "Full Night" else "$hours Hours",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = if (isSelected) PlanTextPrimary else PlanTextSecondary
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = desc,
                                fontSize = 12.sp,
                                color = if (isSelected) PlanGoldAccent else PlanTextMuted
                            )
                        }
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .background(PlanGoldAccent, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color(0xFF4C0519),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .shadow(4.dp, RoundedCornerShape(14.dp), spotColor = Color(0xFFB5123A))
                .testTag("step_2_next_btn"),
            colors = ButtonDefaults.buttonColors(containerColor = PlanButtonBg),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Next: Travel Mode →", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
        }
    }
}

// STEP 3: How are you travelling?
@Composable
private fun StepTransport(
    selected: String,
    onSelect: (String) -> Unit,
    onNext: () -> Unit
) {
    val modes = listOf(
        Pair("Metro + Walking", "🚇 Fastest & most reliable during peak police diversions"),
        Pair("Walking", "🚶 Ideal for concentrated clusters like Gariahat or Hatibagan"),
        Pair("Cab / Rideshare", "🚕 Comfortable, but expect police drop-off barricades"),
        Pair("Two-Wheeler / Bike", "🛵 Flexible navigation through neighborhood bypass lanes"),
        Pair("Bus", "🚌 Kolkata special Puja parikrama bus services")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PlanFlowBg)
            .padding(18.dp)
            .testTag("step_3_transport")
    ) {
        Text("STEP 3 OF 5", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PlanGoldAccent, letterSpacing = 1.2.sp)
        Spacer(modifier = Modifier.height(3.dp))
        Text("How are you travelling?", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = PlanTextPrimary)
        Spacer(modifier = Modifier.height(3.dp))
        Text("We calculate realistic transit times between each pandal stop.", fontSize = 13.sp, color = PlanTextMuted)

        Spacer(modifier = Modifier.height(20.dp))

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            modes.forEach { (mode, desc) ->
                val isSelected = selected == mode
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = if (isSelected) 8.dp else 0.dp,
                            shape = RoundedCornerShape(14.dp),
                            spotColor = Color(0xFFB5123A),
                            ambientColor = Color(0xFFB5123A)
                        )
                        .clickable { onSelect(mode) }
                        .testTag("transport_$mode"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) PlanCardSelectedBg else PlanCardUnselectedBg
                    ),
                    border = BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) PlanCardSelectedBorder else PlanCardUnselectedBorder
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = mode,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = if (isSelected) PlanTextPrimary else PlanTextSecondary
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = desc,
                                fontSize = 11.sp,
                                color = if (isSelected) PlanGoldAccent else PlanTextMuted
                            )
                        }
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .background(PlanGoldAccent, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color(0xFF4C0519),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .shadow(4.dp, RoundedCornerShape(14.dp), spotColor = Color(0xFFB5123A))
                .testTag("step_3_next_btn"),
            colors = ButtonDefaults.buttonColors(containerColor = PlanButtonBg),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Next: Experiences →", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
        }
    }
}

// STEP 4: What kind of Puja experience do you want?
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StepPreferences(
    selected: Set<String>,
    onToggle: (String) -> Unit,
    onNext: () -> Unit
) {
    val options = listOf(
        "Famous Pandals" to "🔥",
        "Theme Pandals" to "🎨",
        "Traditional Puja" to "🛕",
        "Less Crowd" to "🟢",
        "Photography" to "📸",
        "Family Friendly" to "👨‍👩‍👧",
        "Food + Puja" to "🍴",
        "Maximum Pandals" to "⚡"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PlanFlowBg)
            .padding(18.dp)
            .testTag("step_4_preferences")
    ) {
        Text("STEP 4 OF 5", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PlanGoldAccent, letterSpacing = 1.2.sp)
        Spacer(modifier = Modifier.height(3.dp))
        Text("What kind of Puja experience do you want?", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = PlanTextPrimary)
        Spacer(modifier = Modifier.height(3.dp))
        Text("Select all that match your dream hopping plan.", fontSize = 13.sp, color = PlanTextMuted)

        Spacer(modifier = Modifier.height(20.dp))

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            options.forEach { (pref, emoji) ->
                val isSelected = selected.contains(pref)
                FilterChip(
                    selected = isSelected,
                    onClick = { onToggle(pref) },
                    label = {
                        Text(
                            text = "$emoji $pref",
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    border = BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) PlanCardSelectedBorder else PlanCardUnselectedBorder
                    ),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PlanCardSelectedBg,
                        selectedLabelColor = Color.White,
                        containerColor = PlanCardUnselectedBg,
                        labelColor = PlanTextSecondary
                    ),
                    modifier = Modifier.testTag("pref_chip_$pref")
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .shadow(4.dp, RoundedCornerShape(14.dp), spotColor = Color(0xFFB5123A))
                .testTag("step_4_next_btn"),
            colors = ButtonDefaults.buttonColors(containerColor = PlanButtonBg),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Next: Walking Comfort →", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
        }
    }
}

// STEP 5: How much walking is comfortable?
@Composable
private fun StepWalking(
    selected: String,
    onSelect: (String) -> Unit,
    isGenerating: Boolean,
    onGenerate: () -> Unit
) {
    val levels = listOf(
        Pair("Low", "🚶 Low (< 1.5 km total) • Prioritize close metro stations and easy drop-offs"),
        Pair("Moderate", "🚶‍♂️ Moderate (2–3.5 km total) • Balanced walking between connected neighborhoods"),
        Pair("High", "🏃 High (4+ km total) • Maximize pandal count across long pedestrian streets")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PlanFlowBg)
            .padding(18.dp)
            .testTag("step_5_walking")
    ) {
        Text("STEP 5 OF 5", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PlanGoldAccent, letterSpacing = 1.2.sp)
        Spacer(modifier = Modifier.height(3.dp))
        Text("How much walking is comfortable?", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = PlanTextPrimary)
        Spacer(modifier = Modifier.height(3.dp))
        Text("We ensure your route respects your comfort level.", fontSize = 13.sp, color = PlanTextMuted)

        Spacer(modifier = Modifier.height(20.dp))

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            levels.forEach { (level, desc) ->
                val isSelected = selected == level
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = if (isSelected) 8.dp else 0.dp,
                            shape = RoundedCornerShape(14.dp),
                            spotColor = Color(0xFFB5123A),
                            ambientColor = Color(0xFFB5123A)
                        )
                        .clickable { onSelect(level) }
                        .testTag("walking_$level"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) PlanCardSelectedBg else PlanCardUnselectedBg
                    ),
                    border = BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) PlanCardSelectedBorder else PlanCardUnselectedBorder
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "$level Walking Comfort",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = if (isSelected) PlanTextPrimary else PlanTextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = desc,
                                fontSize = 12.sp,
                                color = if (isSelected) PlanGoldAccent else PlanTextMuted
                            )
                        }
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .background(PlanGoldAccent, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color(0xFF4C0519),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = onGenerate,
            enabled = !isGenerating,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .shadow(6.dp, RoundedCornerShape(14.dp), spotColor = Color(0xFFB5123A))
                .testTag("generate_route_btn"),
            colors = ButtonDefaults.buttonColors(
                containerColor = PlanButtonBg,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            if (isGenerating) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                Spacer(modifier = Modifier.width(10.dp))
                Text("Creating Smart Itinerary...", fontWeight = FontWeight.Bold, color = Color.White)
            } else {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PlanGoldAccent, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("GENERATE MY ROUTE", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
            }
        }
    }
}

// STEP 6: Generated Route Output Timeline
@Composable
private fun GeneratedRouteView(
    route: GeneratedRoute?,
    onSaveRoute: () -> Unit,
    onOpenPandal: (String) -> Unit,
    onViewOnMap: () -> Unit,
    onShareWhatsApp: () -> Unit
) {
    if (route == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No route generated yet.")
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("generated_route_view"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Route Summary Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = route.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Starting: ${route.startLocation} • ${route.totalTimeLabel}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        MetricPill("🛕 Pandals", "${route.totalPandals}")
                        MetricPill("⏱️ Total Time", route.totalTimeLabel)
                        MetricPill("🚶 Walking", "${route.estimatedWalkingKm} km")
                        MetricPill("🚇 Mode", route.suggestedTransport.substringBefore(" "))
                    }
                }
            }
        }

        // Why This Route AI Explanation
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = FestiveGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Why this route?",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF78350F)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = route.aiExplanation,
                        fontSize = 12.sp,
                        color = Color(0xFF78350F),
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Timeline stops
        itemsIndexed(route.stops) { idx, stop ->
            // Insert food break at midpoint
            if (idx == route.stops.size / 2 && route.foodBreakStop != null) {
                FoodBreakCard(name = route.foodBreakStop)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenPandal(stop.pandal.id) }
                    .testTag("timeline_stop_$idx"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(BengalRed, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("${idx + 1}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(stop.timeLabel, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stop.pandal.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "📍 ${stop.pandal.area} • Visit: ${stop.visitMinutes} min",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        CrowdBadge(level = stop.pandal.crowdLevel, showQueue = false)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Transit: ${stop.transportHint}",
                            fontSize = 11.sp,
                            color = Color(0xFF2563EB),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Action Buttons: Save, View Map, Share on WhatsApp
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onShareWhatsApp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("share_whatsapp_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share My Puja Plan on WhatsApp", fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onSaveRoute,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("save_route_btn"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Save Plan")
                    }

                    Button(
                        onClick = onViewOnMap,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("view_route_map_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = BengalRed),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("View on Map")
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricPill(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun FoodBreakCard(name: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Color(0xFF059669), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Restaurant, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text("Mid-Circuit Street Food & Adda Break", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF065F46))
                Text(name, fontSize = 11.sp, color = Color(0xFF047857))
            }
        }
    }
}
