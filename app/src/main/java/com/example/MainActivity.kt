package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.Coil
import coil.ImageLoader
import com.example.data.dataset.KolkataPandalDataset
import com.example.ui.PujoViewModel
import com.example.ui.ScreenTab
import com.example.ui.screens.AiChatScreen
import com.example.ui.screens.AreaScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MapScreen
import com.example.ui.screens.PandalDetailScreen
import com.example.ui.screens.PlanScreen
import com.example.ui.screens.SavedScreen
import com.example.ui.theme.BengalRed
import com.example.ui.theme.FestiveGold
import com.example.ui.theme.MyApplicationTheme
import okhttp3.OkHttpClient

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Configure Coil ImageLoader with authentic User-Agent for Wikimedia & remote sources
        val imageLoader = ImageLoader.Builder(this)
            .okHttpClient {
                OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val request = chain.request().newBuilder()
                            .header("User-Agent", "PujoPlanKolkata/1.0 (Android; Kolkata Durga Puja Guide; contact: pujoplan@example.com)")
                            .build()
                        chain.proceed(request)
                    }
                    .build()
            }
            .crossfade(true)
            .build()
        Coil.setImageLoader(imageLoader)

        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: PujoViewModel = viewModel()
                PujoPlanApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun PujoPlanApp(viewModel: PujoViewModel) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val detailPandalId by viewModel.detailPandalId.collectAsStateWithLifecycle()
    val selectedAreaZone by viewModel.selectedAreaZone.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val categoryFilter by viewModel.categoryFilter.collectAsStateWithLifecycle()
    val crowdFilter by viewModel.crowdFilter.collectAsStateWithLifecycle()
    val filteredPandals by viewModel.filteredPandals.collectAsStateWithLifecycle()
    val mapSelectedPandal by viewModel.mapSelectedPandal.collectAsStateWithLifecycle()

    val favoriteIds by viewModel.favoriteIds.collectAsStateWithLifecycle()
    val visitedIds by viewModel.visitedIds.collectAsStateWithLifecycle()
    val savedRoutes by viewModel.savedRoutes.collectAsStateWithLifecycle()

    // Planner state
    val plannerStep by viewModel.plannerStep.collectAsStateWithLifecycle()
    val planStartLoc by viewModel.planStartLocation.collectAsStateWithLifecycle()
    val planDuration by viewModel.planDurationHours.collectAsStateWithLifecycle()
    val planTransport by viewModel.planTransportMode.collectAsStateWithLifecycle()
    val planPreferences by viewModel.planPreferences.collectAsStateWithLifecycle()
    val planWalkingTol by viewModel.planWalkingTolerance.collectAsStateWithLifecycle()
    val isGeneratingRoute by viewModel.isGeneratingRoute.collectAsStateWithLifecycle()
    val generatedRoute by viewModel.generatedRoute.collectAsStateWithLifecycle()

    // AI Studio & Chat state
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val isChatGenerating by viewModel.isChatGenerating.collectAsStateWithLifecycle()
    val aiStudioSection by viewModel.aiStudioSection.collectAsStateWithLifecycle()
    val selectedChatModel by viewModel.selectedChatModel.collectAsStateWithLifecycle()
    val selectedRole by viewModel.selectedRole.collectAsStateWithLifecycle()
    val useSearchGrounding by viewModel.useSearchGrounding.collectAsStateWithLifecycle()
    val useMapsGrounding by viewModel.useMapsGrounding.collectAsStateWithLifecycle()
    val isTranscribing by viewModel.isTranscribingAudio.collectAsStateWithLifecycle()
    val isLiveVoiceActive by viewModel.isLiveVoiceActive.collectAsStateWithLifecycle()
    val liveVoiceTranscript by viewModel.liveVoiceTranscript.collectAsStateWithLifecycle()
    val creativeResults by viewModel.creativeResults.collectAsStateWithLifecycle()
    val isGeneratingCreative by viewModel.isGeneratingCreative.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val firebaseSyncStatus by viewModel.firebaseSyncStatus.collectAsStateWithLifecycle()

    // BackHandler routing
    BackHandler(enabled = detailPandalId != null || selectedAreaZone != null || currentTab != ScreenTab.HOME) {
        when {
            detailPandalId != null -> viewModel.closePandalDetail()
            selectedAreaZone != null -> viewModel.closeAreaZone()
            currentTab == ScreenTab.PLAN && plannerStep > 1 -> viewModel.setPlannerStep(plannerStep - 1)
            currentTab != ScreenTab.HOME -> viewModel.navigateToTab(ScreenTab.HOME)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // Only display Bottom Navigation when not in full-screen detail view
            if (detailPandalId == null && selectedAreaZone == null) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = BengalRed
                ) {
                    NavigationBarItem(
                        selected = currentTab == ScreenTab.HOME,
                        onClick = { viewModel.navigateToTab(ScreenTab.HOME) },
                        icon = {
                            Icon(
                                if (currentTab == ScreenTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                                contentDescription = "Home"
                            )
                        },
                        label = { Text("Home", fontSize = 11.sp, fontWeight = if (currentTab == ScreenTab.HOME) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            indicatorColor = BengalRed
                        ),
                        modifier = Modifier.testTag("nav_item_home")
                    )

                    NavigationBarItem(
                        selected = currentTab == ScreenTab.MAP,
                        onClick = { viewModel.navigateToTab(ScreenTab.MAP) },
                        icon = {
                            Icon(
                                if (currentTab == ScreenTab.MAP) Icons.Filled.Map else Icons.Outlined.Map,
                                contentDescription = "Map"
                            )
                        },
                        label = { Text("Map", fontSize = 11.sp, fontWeight = if (currentTab == ScreenTab.MAP) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            indicatorColor = BengalRed
                        ),
                        modifier = Modifier.testTag("nav_item_map")
                    )

                    NavigationBarItem(
                        selected = currentTab == ScreenTab.PLAN,
                        onClick = { viewModel.navigateToTab(ScreenTab.PLAN) },
                        icon = {
                            Icon(
                                if (currentTab == ScreenTab.PLAN) Icons.Filled.AutoAwesome else Icons.Outlined.AutoAwesome,
                                contentDescription = "Plan"
                            )
                        },
                        label = { Text("Plan", fontSize = 11.sp, fontWeight = if (currentTab == ScreenTab.PLAN) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            indicatorColor = FestiveGold
                        ),
                        modifier = Modifier.testTag("nav_item_plan")
                    )

                    NavigationBarItem(
                        selected = currentTab == ScreenTab.SAVED,
                        onClick = { viewModel.navigateToTab(ScreenTab.SAVED) },
                        icon = {
                            Icon(
                                if (currentTab == ScreenTab.SAVED) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Saved"
                            )
                        },
                        label = { Text("Saved", fontSize = 11.sp, fontWeight = if (currentTab == ScreenTab.SAVED) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            indicatorColor = BengalRed
                        ),
                        modifier = Modifier.testTag("nav_item_saved")
                    )

                    NavigationBarItem(
                        selected = currentTab == ScreenTab.AI_CHAT,
                        onClick = { viewModel.navigateToTab(ScreenTab.AI_CHAT) },
                        icon = {
                            Icon(
                                if (currentTab == ScreenTab.AI_CHAT) Icons.Filled.ChatBubble else Icons.Outlined.ChatBubbleOutline,
                                contentDescription = "Pujo AI"
                            )
                        },
                        label = { Text("Pujo AI", fontSize = 11.sp, fontWeight = if (currentTab == ScreenTab.AI_CHAT) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            indicatorColor = BengalRed
                        ),
                        modifier = Modifier.testTag("nav_item_ai_chat")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                // 1. Pandal Detail View
                detailPandalId != null -> {
                    val pandal = KolkataPandalDataset.getPandalById(detailPandalId!!)
                    if (pandal != null) {
                        PandalDetailScreen(
                            pandal = pandal,
                            isFavorite = favoriteIds.contains(pandal.id),
                            isVisited = visitedIds.contains(pandal.id),
                            onBack = { viewModel.closePandalDetail() },
                            onToggleFavorite = { viewModel.toggleFavorite(pandal.id) },
                            onToggleVisited = { viewModel.toggleVisited(pandal.id) },
                            onReportCrowd = { level -> viewModel.submitCrowdReport(pandal.id, level) },
                            onAddToPlan = { p ->
                                viewModel.setPlanStartLocation(p.area)
                                viewModel.navigateToTab(ScreenTab.PLAN)
                                viewModel.closePandalDetail()
                            }
                        )
                    }
                }

                // 2. Area Cluster View
                selectedAreaZone != null -> {
                    AreaScreen(
                        zone = selectedAreaZone!!,
                        onBack = { viewModel.closeAreaZone() },
                        onOpenPandal = { id -> viewModel.openPandalDetail(id) },
                        onExploreMap = {
                            viewModel.closeAreaZone()
                            viewModel.navigateToTab(ScreenTab.MAP)
                        },
                        onPlanRoute = {
                            val startArea = when {
                                selectedAreaZone!!.contains("South") -> "Gariahat"
                                selectedAreaZone!!.contains("North") -> "Shyambazar"
                                selectedAreaZone!!.contains("Central") -> "College Street"
                                selectedAreaZone!!.contains("East") -> "Salt Lake (Karunamoyee)"
                                selectedAreaZone!!.contains("Salt Lake") -> "Salt Lake (Karunamoyee)"
                                else -> "New Town"
                            }
                            viewModel.setPlanStartLocation(startArea)
                            viewModel.closeAreaZone()
                            viewModel.navigateToTab(ScreenTab.PLAN)
                        },
                        favoriteIds = favoriteIds,
                        visitedIds = visitedIds,
                        onToggleFavorite = { id -> viewModel.toggleFavorite(id) }
                    )
                }

                // 3. Tab Navigation Destinations
                else -> {
                    when (currentTab) {
                        ScreenTab.HOME -> {
                            HomeScreen(
                                searchQuery = searchQuery,
                                onSearchChange = { query ->
                                    viewModel.setSearchQuery(query)
                                    if (query.isNotBlank()) {
                                        viewModel.navigateToTab(ScreenTab.MAP)
                                    }
                                },
                                onNavigateTab = { tab -> viewModel.navigateToTab(tab) },
                                onOpenPandal = { id -> viewModel.openPandalDetail(id) },
                                onOpenArea = { zone -> viewModel.openAreaZone(zone) },
                                onPresetSelect = { preset ->
                                    viewModel.togglePreference(preset)
                                    viewModel.navigateToTab(ScreenTab.PLAN)
                                },
                                favoriteIds = favoriteIds,
                                visitedIds = visitedIds,
                                onToggleFavorite = { id -> viewModel.toggleFavorite(id) }
                            )
                        }

                        ScreenTab.MAP -> {
                            MapScreen(
                                pandals = filteredPandals,
                                selectedPandal = mapSelectedPandal,
                                onPandalSelected = { p -> viewModel.selectMapPandal(p) },
                                onViewDetails = { p -> viewModel.openPandalDetail(p.id) },
                                onAddToPlan = { p ->
                                    viewModel.setPlanStartLocation(p.area)
                                    viewModel.navigateToTab(ScreenTab.PLAN)
                                },
                                searchQuery = searchQuery,
                                onSearchChange = { viewModel.setSearchQuery(it) },
                                categoryFilter = categoryFilter,
                                onCategoryFilterChange = { viewModel.setCategoryFilter(it) },
                                crowdFilter = crowdFilter,
                                onCrowdFilterChange = { viewModel.setCrowdFilter(it) }
                            )
                        }

                        ScreenTab.PLAN -> {
                            PlanScreen(
                                currentStep = plannerStep,
                                onStepChange = { step -> viewModel.setPlannerStep(step) },
                                startLocation = planStartLoc,
                                onStartLocationChange = { viewModel.setPlanStartLocation(it) },
                                durationHours = planDuration,
                                onDurationChange = { viewModel.setPlanDuration(it) },
                                transportMode = planTransport,
                                onTransportChange = { viewModel.setPlanTransportMode(it) },
                                preferences = planPreferences,
                                onTogglePreference = { viewModel.togglePreference(it) },
                                walkingTolerance = planWalkingTol,
                                onWalkingToleranceChange = { viewModel.setWalkingTolerance(it) },
                                isGenerating = isGeneratingRoute,
                                generatedRoute = generatedRoute,
                                onGenerateRoute = { viewModel.generateRouteNow() },
                                onSaveRoute = { viewModel.saveCurrentRoute() },
                                onOpenPandalDetail = { id -> viewModel.openPandalDetail(id) },
                                onNavigateTab = { tab -> viewModel.navigateToTab(tab) }
                            )
                        }

                        ScreenTab.SAVED -> {
                            SavedScreen(
                                favoriteIds = favoriteIds,
                                visitedIds = visitedIds,
                                savedRoutes = savedRoutes,
                                onToggleFavorite = { id: String -> viewModel.toggleFavorite(id) },
                                onOpenPandal = { id: String -> viewModel.openPandalDetail(id) },
                                onDeleteRoute = { id: String -> viewModel.deleteRoute(id) }
                            )
                        }

                        ScreenTab.AI_CHAT -> {
                            AiChatScreen(
                                messages = chatMessages,
                                isGenerating = isChatGenerating,
                                onSendMessage = { text -> viewModel.sendChatMessage(text) },
                                activeSection = aiStudioSection,
                                onSectionChange = { sec -> viewModel.setAiStudioSection(sec) },
                                selectedModel = selectedChatModel,
                                onModelChange = { m -> viewModel.setSelectedChatModel(m) },
                                selectedRole = selectedRole,
                                onRoleChange = { r -> viewModel.setSelectedRole(r) },
                                useSearchGrounding = useSearchGrounding,
                                onToggleSearchGrounding = { viewModel.toggleSearchGrounding() },
                                useMapsGrounding = useMapsGrounding,
                                onToggleMapsGrounding = { viewModel.toggleMapsGrounding() },
                                isTranscribing = isTranscribing,
                                onTriggerTranscribe = { viewModel.triggerAudioTranscription() },
                                isLiveVoiceActive = isLiveVoiceActive,
                                liveVoiceTranscript = liveVoiceTranscript,
                                onToggleLiveVoice = { viewModel.toggleLiveVoiceMode() },
                                creativeResults = creativeResults,
                                isGeneratingCreative = isGeneratingCreative,
                                onGenerateImage = { prompt, inputImg, ar -> viewModel.generateImage(prompt, inputImg, ar) },
                                onGenerateVideo = { prompt, inputImg, ar -> viewModel.generateVideo(prompt, inputImg, ar) },
                                onGenerateMusic = { prompt, isPro -> viewModel.generateMusic(prompt, isPro) },
                                currentUser = currentUser,
                                syncStatus = firebaseSyncStatus,
                                onGoogleSignIn = { name, email -> viewModel.signInGoogleDemo(name, email) },
                                onSyncFirestore = { viewModel.syncDataWithFirestore() }
                            )
                        }
                    }
                }
            }
        }
    }
}
