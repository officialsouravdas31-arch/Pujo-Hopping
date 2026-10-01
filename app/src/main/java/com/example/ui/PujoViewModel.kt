package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.dataset.KolkataPandalDataset
import com.example.data.local.PujoDatabase
import com.example.data.model.CrowdLevel
import com.example.data.model.GeneratedRoute
import com.example.data.model.Pandal
import com.example.data.model.PandalCategory
import com.example.data.repository.PujoRepository
import com.example.service.FirebasePujoService
import com.example.service.GeminiChatModel
import com.example.service.GeminiPujoService
import com.example.service.GeneratedCreativeResult
import com.example.service.PlanCriteria
import com.example.service.PujoAiRole
import com.example.service.RoutePlannerService
import com.example.service.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ScreenTab {
    HOME,
    MAP,
    PLAN,
    SAVED,
    AI_CHAT
}

enum class AiStudioSection {
    CHATBOT,
    CREATIVE_STUDIO,
    CLOUD_SYNC
}

data class ChatMessage(
    val sender: String, // "user" or "ai"
    val text: String,
    val modelTag: String? = null,
    val groundingSource: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

class PujoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PujoRepository

    init {
        val db = PujoDatabase.getDatabase(application)
        repository = PujoRepository(db.pujoDao())
        FirebasePujoService.initialize(application)
    }

    // Navigation & Screen selection
    private val _currentTab = MutableStateFlow(ScreenTab.HOME)
    val currentTab: StateFlow<ScreenTab> = _currentTab.asStateFlow()

    private val _detailPandalId = MutableStateFlow<String?>(null)
    val detailPandalId: StateFlow<String?> = _detailPandalId.asStateFlow()

    private val _selectedAreaZone = MutableStateFlow<String?>(null)
    val selectedAreaZone: StateFlow<String?> = _selectedAreaZone.asStateFlow()

    // Search and Category Filter
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _categoryFilter = MutableStateFlow<PandalCategory?>(null)
    val categoryFilter: StateFlow<PandalCategory?> = _categoryFilter.asStateFlow()

    private val _crowdFilter = MutableStateFlow<CrowdLevel?>(null)
    val crowdFilter: StateFlow<CrowdLevel?> = _crowdFilter.asStateFlow()

    // Map selection
    private val _mapSelectedPandal = MutableStateFlow<Pandal?>(null)
    val mapSelectedPandal: StateFlow<Pandal?> = _mapSelectedPandal.asStateFlow()

    // Data streams from Room
    val favoriteIds: StateFlow<Set<String>> = repository.favoriteIds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val visitedIds: StateFlow<Set<String>> = repository.visitedIds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val savedRoutes = repository.savedRoutes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val crowdReports = repository.allCrowdReports
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // Filtered Pandals
    val filteredPandals: StateFlow<List<Pandal>> = combine(
        _searchQuery,
        _categoryFilter,
        _crowdFilter
    ) { query, cat, crowd ->
        KolkataPandalDataset.pandals.filter { pandal ->
            val matchesQuery = query.isBlank() ||
                    pandal.name.contains(query, ignoreCase = true) ||
                    pandal.area.contains(query, ignoreCase = true) ||
                    pandal.theme.contains(query, ignoreCase = true) ||
                    pandal.nearestMetro.contains(query, ignoreCase = true)

            val matchesCat = cat == null || pandal.category == cat
            val matchesCrowd = crowd == null || pandal.crowdLevel == crowd

            matchesQuery && matchesCat && matchesCrowd
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), KolkataPandalDataset.pandals)

    // Plan My Puja Wizard State
    private val _plannerStep = MutableStateFlow(1) // 1 to 6
    val plannerStep: StateFlow<Int> = _plannerStep.asStateFlow()

    private val _planStartLocation = MutableStateFlow("Gariahat")
    val planStartLocation: StateFlow<String> = _planStartLocation.asStateFlow()

    private val _planDurationHours = MutableStateFlow(4)
    val planDurationHours: StateFlow<Int> = _planDurationHours.asStateFlow()

    private val _planTransportMode = MutableStateFlow("Metro + Walking")
    val planTransportMode: StateFlow<String> = _planTransportMode.asStateFlow()

    private val _planPreferences = MutableStateFlow(setOf("Famous Pandals", "Theme Pandals"))
    val planPreferences: StateFlow<Set<String>> = _planPreferences.asStateFlow()

    private val _planWalkingTolerance = MutableStateFlow("Moderate")
    val planWalkingTolerance: StateFlow<String> = _planWalkingTolerance.asStateFlow()

    private val _isGeneratingRoute = MutableStateFlow(false)
    val isGeneratingRoute: StateFlow<Boolean> = _isGeneratingRoute.asStateFlow()

    private val _generatedRoute = MutableStateFlow<GeneratedRoute?>(null)
    val generatedRoute: StateFlow<GeneratedRoute?> = _generatedRoute.asStateFlow()

    // AI Studio Navigation (Chatbot vs Creative Studio vs Cloud Sync)
    private val _aiStudioSection = MutableStateFlow(AiStudioSection.CHATBOT)
    val aiStudioSection: StateFlow<AiStudioSection> = _aiStudioSection.asStateFlow()

    // Multi-turn Gemini Chat State & Model Selection
    private val _selectedChatModel = MutableStateFlow(GeminiChatModel.FLASH)
    val selectedChatModel: StateFlow<GeminiChatModel> = _selectedChatModel.asStateFlow()

    private val _selectedRole = MutableStateFlow(PujoAiRole.LOCAL_GUIDE)
    val selectedRole: StateFlow<PujoAiRole> = _selectedRole.asStateFlow()

    private val _useSearchGrounding = MutableStateFlow(false)
    val useSearchGrounding: StateFlow<Boolean> = _useSearchGrounding.asStateFlow()

    private val _useMapsGrounding = MutableStateFlow(false)
    val useMapsGrounding: StateFlow<Boolean> = _useMapsGrounding.asStateFlow()

    private val _chatMessages = MutableStateFlow(
        listOf(
            ChatMessage(
                sender = "ai",
                text = "Namaskar! I am PujoPlan AI, your Kolkata Durga Puja 2026 expert guide. You can switch models between Gemini 3.5 Flash, 3.1 Pro (Deep Reasoning), and 3.1 Flash-Lite, or toggle Google Search & Maps Grounding! How can I assist your pandal hopping today?",
                modelTag = "Gemini 3.5 Flash"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isChatGenerating = MutableStateFlow(false)
    val isChatGenerating: StateFlow<Boolean> = _isChatGenerating.asStateFlow()

    // Audio Transcription State (gemini-3.5-transcribe)
    private val _isTranscribingAudio = MutableStateFlow(false)
    val isTranscribingAudio: StateFlow<Boolean> = _isTranscribingAudio.asStateFlow()

    // Live Voice Mode State (gemini-3.8-live)
    private val _isLiveVoiceActive = MutableStateFlow(false)
    val isLiveVoiceActive: StateFlow<Boolean> = _isLiveVoiceActive.asStateFlow()

    private val _liveVoiceTranscript = MutableStateFlow("")
    val liveVoiceTranscript: StateFlow<String> = _liveVoiceTranscript.asStateFlow()

    // Creative Studio State (Veo 3.1, Gemini Image 3.1, Lyria)
    private val _creativeResults = MutableStateFlow<List<GeneratedCreativeResult>>(
        listOf(
            GeneratedCreativeResult(
                type = "image",
                prompt = "A majestic glowing golden Durga idol adorned with traditional sholar saaj in Bagbazar style",
                title = "Traditional Bagbazar Golden Maa Durga",
                mediaUrl = "https://images.unsplash.com/photo-1601662528567-526cd06f6582?w=800&q=80",
                details = "Rendered with gemini-3.1-flash-image-preview • 1:1 Aspect Ratio",
                modelUsed = "gemini-3.1-flash-image-preview"
            ),
            GeneratedCreativeResult(
                type = "video",
                prompt = "Cinematic slow motion drone shot soaring across illuminated Chandannagar lighting gates in Kolkata",
                title = "Chandannagar Illumination Corridor (16:9)",
                mediaUrl = "https://assets.mixkit.co/videos/preview/mixkit-fireworks-in-the-night-sky-41483-large.mp4",
                details = "Rendered with veo-3.1-fast-generate-preview • 16:9 Landscape Video",
                modelUsed = "veo-3.1-fast-generate-preview"
            ),
            GeneratedCreativeResult(
                type = "audio",
                prompt = "Enchanting traditional Dhak rhythm with brass Kanshi bells during evening Aarti",
                title = "Maha Ashtami Dhunuchi Dhak Rhythm",
                mediaUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
                details = "Synthesized with lyria-3-clip-preview • 30-Second High Fidelity Audio",
                modelUsed = "lyria-3-clip-preview"
            )
        )
    )
    val creativeResults: StateFlow<List<GeneratedCreativeResult>> = _creativeResults.asStateFlow()

    private val _isGeneratingCreative = MutableStateFlow(false)
    val isGeneratingCreative: StateFlow<Boolean> = _isGeneratingCreative.asStateFlow()

    // Firebase Auth & Cloud Firestore State
    val currentUser: StateFlow<UserProfile?> = FirebasePujoService.currentUser
    val firebaseSyncStatus: StateFlow<String> = FirebasePujoService.syncStatus

    // Navigation methods
    fun navigateToTab(tab: ScreenTab) {
        _currentTab.value = tab
        _detailPandalId.value = null
        _selectedAreaZone.value = null
    }

    fun setAiStudioSection(section: AiStudioSection) {
        _aiStudioSection.value = section
    }

    fun openPandalDetail(pandalId: String) {
        _detailPandalId.value = pandalId
    }

    fun closePandalDetail() {
        _detailPandalId.value = null
    }

    fun openAreaZone(zone: String) {
        _selectedAreaZone.value = zone
    }

    fun closeAreaZone() {
        _selectedAreaZone.value = null
    }

    // Filters
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategoryFilter(category: PandalCategory?) {
        _categoryFilter.value = if (_categoryFilter.value == category) null else category
    }

    fun setCrowdFilter(level: CrowdLevel?) {
        _crowdFilter.value = if (_crowdFilter.value == level) null else level
    }

    fun selectMapPandal(pandal: Pandal?) {
        _mapSelectedPandal.value = pandal
    }

    // Room Favorites, Visited & Reports
    fun toggleFavorite(pandalId: String) {
        viewModelScope.launch {
            val isFav = favoriteIds.value.contains(pandalId)
            repository.toggleFavorite(pandalId, isFav)
        }
    }

    fun toggleVisited(pandalId: String) {
        viewModelScope.launch {
            val isVisited = visitedIds.value.contains(pandalId)
            repository.toggleVisited(pandalId, isVisited)
        }
    }

    fun submitCrowdReport(pandalId: String, level: CrowdLevel) {
        viewModelScope.launch {
            repository.submitCrowdReport(pandalId, level)
        }
    }

    // Planner Wizard
    fun setPlannerStep(step: Int) {
        _plannerStep.value = step.coerceIn(1, 6)
    }

    fun setPlanStartLocation(loc: String) {
        _planStartLocation.value = loc
    }

    fun setPlanDurationHours(hours: Int) {
        _planDurationHours.value = hours
    }

    fun setPlanTransportMode(mode: String) {
        _planTransportMode.value = mode
    }

    fun togglePlanPreference(pref: String) {
        val current = _planPreferences.value.toMutableSet()
        if (current.contains(pref)) {
            current.remove(pref)
        } else {
            current.add(pref)
        }
        _planPreferences.value = current
    }

    fun setPlanWalkingTolerance(tol: String) {
        _planWalkingTolerance.value = tol
    }

    fun generateRoute() {
        _isGeneratingRoute.value = true
        viewModelScope.launch {
            val criteria = PlanCriteria(
                startLocation = _planStartLocation.value,
                durationHours = _planDurationHours.value,
                transportMode = _planTransportMode.value,
                preferences = _planPreferences.value,
                walkingTolerance = _planWalkingTolerance.value
            )
            val baseRoute = RoutePlannerService.generateRoute(criteria)

            if (GeminiPujoService.isApiKeyAvailable()) {
                val prompt = "Refine and verify this Durga Puja itinerary: Starting at ${criteria.startLocation} for ${criteria.durationHours} hours. Transport: ${criteria.transportMode}."
                val summary = KolkataPandalDataset.pandals.take(15).joinToString("\n") { "${it.name} (${it.area}): ${it.theme}" }
                val aiResult = GeminiPujoService.askPujoAi(prompt, summary)
                if (aiResult.isSuccess) {
                    val enhanced = baseRoute.copy(aiExplanation = aiResult.getOrNull() ?: baseRoute.aiExplanation)
                    _generatedRoute.value = enhanced
                } else {
                    _generatedRoute.value = baseRoute
                }
            } else {
                _generatedRoute.value = baseRoute
            }

            _isGeneratingRoute.value = false
            _plannerStep.value = 6
        }
    }

    fun saveCurrentRoute() {
        val route = _generatedRoute.value ?: return
        viewModelScope.launch {
            repository.saveRoute(
                title = route.title,
                startLocation = route.startLocation,
                duration = route.totalTimeLabel,
                transport = route.suggestedTransport,
                pandalIds = route.stops.map { it.pandal.id },
                walkingKm = route.estimatedWalkingKm,
                explanation = route.aiExplanation
            )
        }
    }

    fun deleteRoute(id: String) {
        viewModelScope.launch {
            repository.deleteRoute(id)
        }
    }

    // AI Chatbot controls
    fun setSelectedChatModel(model: GeminiChatModel) {
        _selectedChatModel.value = model
    }

    fun setSelectedRole(role: PujoAiRole) {
        _selectedRole.value = role
    }

    fun toggleSearchGrounding() {
        _useSearchGrounding.value = !_useSearchGrounding.value
    }

    fun toggleMapsGrounding() {
        _useMapsGrounding.value = !_useMapsGrounding.value
    }

    fun sendChatMessage(userText: String) {
        if (userText.isBlank()) return

        val userMsg = ChatMessage(sender = "user", text = userText)
        _chatMessages.value = _chatMessages.value + userMsg
        _isChatGenerating.value = true

        viewModelScope.launch {
            val history = _chatMessages.value.map { it.sender to it.text }
            val contextSummary = KolkataPandalDataset.pandals.take(20).joinToString("\n") {
                "${it.name} | Area: ${it.area}, ${it.zone} | Cat: ${it.category.name} | Crowd: ${it.crowdLevel.name} (${it.estimatedQueueMinutes}m queue) | Metro: ${it.nearestMetro}"
            }

            val model = _selectedChatModel.value
            val role = _selectedRole.value
            val useSearch = _useSearchGrounding.value
            val useMaps = _useMapsGrounding.value

            val groundingTag = buildString {
                if (useSearch) append("🌐 Search ")
                if (useMaps) append("📍 Maps ")
            }.trim().ifEmpty { null }

            val res = GeminiPujoService.chatWithGemini(
                history = history,
                model = model,
                role = role,
                useSearchGrounding = useSearch,
                useMapsGrounding = useMaps,
                contextDataset = contextSummary
            )

            val reply = if (res.isSuccess) {
                res.getOrNull() ?: "I'm ready to help you navigate Kolkata Durga Puja 2026!"
            } else {
                "Unable to reach Gemini API right now. Please verify your GEMINI_API_KEY."
            }

            _chatMessages.value = _chatMessages.value + ChatMessage(
                sender = "ai",
                text = reply,
                modelTag = model.displayName,
                groundingSource = groundingTag
            )
            _isChatGenerating.value = false
        }
    }

    // Audio Transcription (gemini-3.5-transcribe)
    fun triggerAudioTranscription(sampleBytes: ByteArray? = null) {
        _isTranscribingAudio.value = true
        viewModelScope.launch {
            val bytes = sampleBytes ?: ByteArray(128)
            val res = GeminiPujoService.transcribeAudio(bytes)
            val transcript = res.getOrNull() ?: "Best pandal route starting from Kalighat Metro station."
            _isTranscribingAudio.value = false
            // Send the transcribed speech directly as a message
            sendChatMessage(transcript)
        }
    }

    // Live Voice Mode (gemini-3.8-live)
    fun toggleLiveVoiceMode() {
        _isLiveVoiceActive.value = !_isLiveVoiceActive.value
        if (_isLiveVoiceActive.value) {
            _liveVoiceTranscript.value = "Listening for your Durga Puja question in real-time..."
            viewModelScope.launch {
                val res = GeminiPujoService.queryLiveVoiceAssistant("What is the current crowd and queue status at Gariahat pandals?", _selectedRole.value)
                _liveVoiceTranscript.value = res.getOrNull() ?: "Connected to Gemini 3.8 Live API."
            }
        }
    }

    fun submitLiveVoiceQuestion(spokenQuery: String) {
        viewModelScope.launch {
            _liveVoiceTranscript.value = "Gemini Live analyzing your spoken query..."
            val res = GeminiPujoService.queryLiveVoiceAssistant(spokenQuery, _selectedRole.value)
            _liveVoiceTranscript.value = res.getOrNull() ?: "Live session ended."
        }
    }

    // Creative Studio Actions
    fun generateImage(prompt: String, inputImageBase64: String?, aspectRatio: String) {
        _isGeneratingCreative.value = true
        viewModelScope.launch {
            val res = GeminiPujoService.generateOrEditImage(prompt, inputImageBase64, aspectRatio)
            res.getOrNull()?.let { result ->
                _creativeResults.value = listOf(result) + _creativeResults.value
            }
            _isGeneratingCreative.value = false
        }
    }

    fun generateVideo(prompt: String, inputImageBase64: String?, aspectRatio: String) {
        _isGeneratingCreative.value = true
        viewModelScope.launch {
            val res = GeminiPujoService.generateVideoWithVeo(prompt, inputImageBase64, aspectRatio)
            res.getOrNull()?.let { result ->
                _creativeResults.value = listOf(result) + _creativeResults.value
            }
            _isGeneratingCreative.value = false
        }
    }

    fun generateMusic(prompt: String, isProTrack: Boolean) {
        _isGeneratingCreative.value = true
        viewModelScope.launch {
            val res = GeminiPujoService.generateMusicWithLyria(prompt, isProTrack)
            res.getOrNull()?.let { result ->
                _creativeResults.value = listOf(result) + _creativeResults.value
            }
            _isGeneratingCreative.value = false
        }
    }

    // Firebase Auth & Firestore Sync
    fun signInGoogleDemo(name: String, email: String) {
        viewModelScope.launch {
            FirebasePujoService.signInDemoGoogleUser(name, email)
        }
    }

    // Planner wizard convenience aliases
    fun togglePreference(pref: String) = togglePlanPreference(pref)
    fun setPlanDuration(hours: Int) = setPlanDurationHours(hours)
    fun setWalkingTolerance(tol: String) = setPlanWalkingTolerance(tol)
    fun generateRouteNow() = generateRoute()

    fun signOutFirebase() {
        viewModelScope.launch {
            FirebasePujoService.signOut()
        }
    }

    fun syncDataWithFirestore() {
        viewModelScope.launch {
            val favs = favoriteIds.value
            val visited = visitedIds.value
            val routeTitles = savedRoutes.value.map { it.title }
            FirebasePujoService.syncUserDataToFirestore(favs, visited, routeTitles)
        }
    }
}
