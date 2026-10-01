package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.service.GeminiChatModel
import com.example.service.GeneratedCreativeResult
import com.example.service.PujoAiRole
import com.example.service.UserProfile
import com.example.ui.AiStudioSection
import com.example.ui.ChatMessage
import com.example.ui.theme.BengalRed
import com.example.ui.theme.FestiveGold

@Composable
fun AiChatScreen(
    messages: List<ChatMessage>,
    isGenerating: Boolean,
    onSendMessage: (String) -> Unit,
    activeSection: AiStudioSection,
    onSectionChange: (AiStudioSection) -> Unit,
    selectedModel: GeminiChatModel,
    onModelChange: (GeminiChatModel) -> Unit,
    selectedRole: PujoAiRole,
    onRoleChange: (PujoAiRole) -> Unit,
    useSearchGrounding: Boolean,
    onToggleSearchGrounding: () -> Unit,
    useMapsGrounding: Boolean,
    onToggleMapsGrounding: () -> Unit,
    isTranscribing: Boolean,
    onTriggerTranscribe: () -> Unit,
    isLiveVoiceActive: Boolean,
    liveVoiceTranscript: String,
    onToggleLiveVoice: () -> Unit,
    creativeResults: List<GeneratedCreativeResult>,
    isGeneratingCreative: Boolean,
    onGenerateImage: (prompt: String, inputImage: String?, aspectRatio: String) -> Unit,
    onGenerateVideo: (prompt: String, inputImage: String?, aspectRatio: String) -> Unit,
    onGenerateMusic: (prompt: String, isPro: Boolean) -> Unit,
    currentUser: UserProfile?,
    syncStatus: String,
    onGoogleSignIn: (String, String) -> Unit,
    onSyncFirestore: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("ai_studio_screen")
    ) {
        // App Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BengalRed)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0x33FFFFFF), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Pujo AI Studio",
                            tint = FestiveGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Pujo AI Studio",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Gemini 3.5 & 3.1 • Veo 3 • Lyria Audio",
                            fontSize = 11.sp,
                            color = FestiveGold
                        )
                    }
                }

                // Live Voice Button
                OutlinedButton(
                    onClick = onToggleLiveVoice,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (isLiveVoiceActive) Color.White else FestiveGold,
                        containerColor = if (isLiveVoiceActive) Color(0xFFDC2626) else Color.Transparent
                    ),
                    border = BorderStroke(1.dp, FestiveGold),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("live_voice_mode_btn")
                ) {
                    Icon(
                        imageVector = if (isLiveVoiceActive) Icons.Default.RecordVoiceOver else Icons.Default.GraphicEq,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (isLiveVoiceActive) Color.White else FestiveGold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isLiveVoiceActive) "LIVE ON" else "Live Voice",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Section Tabs
        TabRow(
            selectedTabIndex = activeSection.ordinal,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = BengalRed,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeSection.ordinal]),
                    color = BengalRed
                )
            }
        ) {
            Tab(
                selected = activeSection == AiStudioSection.CHATBOT,
                onClick = { onSectionChange(AiStudioSection.CHATBOT) },
                text = { Text("💬 Chatbot", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = activeSection == AiStudioSection.CREATIVE_STUDIO,
                onClick = { onSectionChange(AiStudioSection.CREATIVE_STUDIO) },
                text = { Text("✨ Creative Studio", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = activeSection == AiStudioSection.CLOUD_SYNC,
                onClick = { onSectionChange(AiStudioSection.CLOUD_SYNC) },
                text = { Text("☁️ Cloud Sync", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
        }

        // Live Voice Banner when Active
        AnimatedVisibility(visible = isLiveVoiceActive) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                border = BorderStroke(1.dp, Color(0xFFFCA5A5))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = null,
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Gemini 3.8 Live API Active",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF991B1B)
                        )
                        Text(
                            text = liveVoiceTranscript.ifEmpty { "Speak to Kolkata Pujo AI in real-time..." },
                            fontSize = 11.sp,
                            color = Color(0xFF7F1D1D),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        when (activeSection) {
            AiStudioSection.CHATBOT -> {
                ChatbotView(
                    messages = messages,
                    isGenerating = isGenerating,
                    onSendMessage = onSendMessage,
                    selectedModel = selectedModel,
                    onModelChange = onModelChange,
                    selectedRole = selectedRole,
                    onRoleChange = onRoleChange,
                    useSearchGrounding = useSearchGrounding,
                    onToggleSearchGrounding = onToggleSearchGrounding,
                    useMapsGrounding = useMapsGrounding,
                    onToggleMapsGrounding = onToggleMapsGrounding,
                    isTranscribing = isTranscribing,
                    onTriggerTranscribe = onTriggerTranscribe
                )
            }
            AiStudioSection.CREATIVE_STUDIO -> {
                CreativeStudioView(
                    results = creativeResults,
                    isGenerating = isGeneratingCreative,
                    onGenerateImage = onGenerateImage,
                    onGenerateVideo = onGenerateVideo,
                    onGenerateMusic = onGenerateMusic
                )
            }
            AiStudioSection.CLOUD_SYNC -> {
                CloudSyncView(
                    currentUser = currentUser,
                    syncStatus = syncStatus,
                    onGoogleSignIn = onGoogleSignIn,
                    onSyncFirestore = onSyncFirestore
                )
            }
        }
    }
}

// ----------------------------------------------------------------------------
// 1. CHATBOT VIEW (Multi-turn chat, Model Switcher, System Roles, Grounding)
// ----------------------------------------------------------------------------
@Composable
private fun ChatbotView(
    messages: List<ChatMessage>,
    isGenerating: Boolean,
    onSendMessage: (String) -> Unit,
    selectedModel: GeminiChatModel,
    onModelChange: (GeminiChatModel) -> Unit,
    selectedRole: PujoAiRole,
    onRoleChange: (PujoAiRole) -> Unit,
    useSearchGrounding: Boolean,
    onToggleSearchGrounding: () -> Unit,
    useMapsGrounding: Boolean,
    onToggleMapsGrounding: () -> Unit,
    isTranscribing: Boolean,
    onTriggerTranscribe: () -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    var showModelMenu by remember { mutableStateOf(false) }
    var showRoleMenu by remember { mutableStateOf(false) }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Model & Tool Controls Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Model Selector Dropdown Chip
            Box {
                SuggestionChip(
                    onClick = { showModelMenu = true },
                    label = { Text("⚡ ${selectedModel.displayName}", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.testTag("model_picker_btn")
                )
                DropdownMenu(
                    expanded = showModelMenu,
                    onDismissRequest = { showModelMenu = false }
                ) {
                    GeminiChatModel.values().forEach { model ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text("${model.displayName} (${model.badge})", fontWeight = FontWeight.Bold)
                                    Text(model.description, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            },
                            onClick = {
                                onModelChange(model)
                                showModelMenu = false
                            }
                        )
                    }
                }
            }

            // Role Selector Dropdown Chip
            Box {
                SuggestionChip(
                    onClick = { showRoleMenu = true },
                    label = { Text("${selectedRole.emoji} ${selectedRole.title}", fontSize = 11.sp) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.testTag("role_picker_btn")
                )
                DropdownMenu(
                    expanded = showRoleMenu,
                    onDismissRequest = { showRoleMenu = false }
                ) {
                    PujoAiRole.values().forEach { role ->
                        DropdownMenuItem(
                            text = {
                                Text("${role.emoji} ${role.title}", fontWeight = FontWeight.SemiBold)
                            },
                            onClick = {
                                onRoleChange(role)
                                showRoleMenu = false
                            }
                        )
                    }
                }
            }

            // Google Search Grounding Chip
            FilterChip(
                selected = useSearchGrounding,
                onClick = onToggleSearchGrounding,
                label = { Text("🌐 Search Grounding", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BengalRed,
                    selectedLabelColor = Color.White
                ),
                modifier = Modifier.testTag("search_grounding_chip")
            )

            // Google Maps Grounding Chip
            FilterChip(
                selected = useMapsGrounding,
                onClick = onToggleMapsGrounding,
                label = { Text("📍 Maps Grounding", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BengalRed,
                    selectedLabelColor = Color.White
                ),
                modifier = Modifier.testTag("maps_grounding_chip")
            )
        }

        // Multi-Turn Message Thread
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { msg ->
                val isUser = msg.sender == "user"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    if (!isUser) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .background(Color(0xFFFEF3C7), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(selectedRole.emoji, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(0.85f),
                        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
                    ) {
                        Card(
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (isUser) 16.dp else 4.dp,
                                bottomEnd = if (isUser) 4.dp else 16.dp
                            ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isUser) BengalRed else MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = msg.text,
                                    color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )

                                if (!isUser && (msg.modelTag != null || msg.groundingSource != null)) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        if (msg.modelTag != null) {
                                            Text(
                                                text = "⚡ ${msg.modelTag}",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = BengalRed
                                            )
                                        }
                                        if (msg.groundingSource != null) {
                                            Text(
                                                text = msg.groundingSource,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF059669)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (isGenerating) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 38.dp, top = 4.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = BengalRed
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${selectedModel.displayName} is formulating your itinerary...",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Suggestions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(
                "3-hour South Kolkata route",
                "Crowd status at Sree Bhumi",
                "North Kolkata heritage tour",
                "Metro midnight timings"
            ).forEach { prompt ->
                SuggestionChip(
                    onClick = { onSendMessage(prompt) },
                    label = { Text(prompt, fontSize = 11.sp) }
                )
            }
        }

        // Input Field, Mic Transcription & Send
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Audio Transcription Button (gemini-3.5-transcribe)
            IconButton(
                onClick = onTriggerTranscribe,
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        if (isTranscribing) Color(0xFFDC2626) else MaterialTheme.colorScheme.surfaceVariant,
                        CircleShape
                    )
                    .testTag("audio_transcribe_btn")
            ) {
                if (isTranscribing) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.White)
                } else {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Transcribe with gemini-3.5-transcribe",
                        tint = BengalRed,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_field"),
                placeholder = { Text("Ask ${selectedRole.title}...", fontSize = 12.sp) },
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BengalRed,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                maxLines = 3
            )

            Spacer(modifier = Modifier.width(6.dp))

            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        onSendMessage(inputText)
                        inputText = ""
                    }
                },
                enabled = inputText.isNotBlank() && !isGenerating,
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        if (inputText.isNotBlank() && !isGenerating) BengalRed else Color.LightGray,
                        CircleShape
                    )
                    .testTag("chat_send_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ----------------------------------------------------------------------------
// 2. CREATIVE STUDIO (Veo Video 3.1, Gemini Image 3.1, Lyria Audio)
// ----------------------------------------------------------------------------
@Composable
private fun CreativeStudioView(
    results: List<GeneratedCreativeResult>,
    isGenerating: Boolean,
    onGenerateImage: (prompt: String, inputImage: String?, aspectRatio: String) -> Unit,
    onGenerateVideo: (prompt: String, inputImage: String?, aspectRatio: String) -> Unit,
    onGenerateMusic: (prompt: String, isPro: Boolean) -> Unit
) {
    var subTab by remember { mutableStateOf(0) } // 0: Image, 1: Veo Video, 2: Lyria Music
    var promptText by remember { mutableStateOf("") }
    var selectedAspectRatio by remember { mutableStateOf("16:9") } // "16:9" or "9:16" for Veo
    var isImageToVideo by remember { mutableStateOf(false) }
    var isProLyriaTrack by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(14.dp)
    ) {
        // Studio Sub-tab bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = subTab == 0,
                onClick = {
                    subTab = 0
                    if (promptText.isBlank()) promptText = "Majestic golden Durga idol decorated with traditional flowers in Kumartuli art style"
                },
                label = { Text("🖼️ Images", fontSize = 12.sp) },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = subTab == 1,
                onClick = {
                    subTab = 1
                    if (promptText.isBlank()) promptText = "Cinematic slow motion drone shot soaring across illuminated Chandannagar lighting gates in Kolkata"
                },
                label = { Text("🎬 Veo Video", fontSize = 12.sp) },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = subTab == 2,
                onClick = {
                    subTab = 2
                    if (promptText.isBlank()) promptText = "Traditional rhythmic Kolkata Dhak beats with brass Kanshi bells during Maha Ashtami Aarti"
                },
                label = { Text("🎵 Lyria Music", fontSize = 12.sp) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Generator Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                val headerTitle = when (subTab) {
                    0 -> "Gemini 3.1 Flash Image Studio"
                    1 -> "Veo 3.1 Fast Video Studio"
                    else -> "Lyria Audio & Dhak Studio"
                }
                val modelId = when (subTab) {
                    0 -> "gemini-3.1-flash-image-preview"
                    1 -> "veo-3.1-fast-generate-preview"
                    else -> if (isProLyriaTrack) "lyria-3-pro-preview" else "lyria-3-clip-preview"
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = headerTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = BengalRed
                    )
                    Text(
                        text = modelId,
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = promptText,
                    onValueChange = { promptText = it },
                    label = { Text("Enter prompt / instructions") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 3,
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Veo Aspect Ratio (Mandatory 16:9 landscape or 9:16 portrait)
                if (subTab == 1) {
                    Text("Select Aspect Ratio:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = selectedAspectRatio == "16:9",
                            onClick = { selectedAspectRatio = "16:9" },
                            label = { Text("16:9 Landscape") }
                        )
                        FilterChip(
                            selected = selectedAspectRatio == "9:16",
                            onClick = { selectedAspectRatio = "9:16" },
                            label = { Text("9:16 Portrait (Reels)") }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    // Animate photo toggle
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FilterChip(
                            selected = isImageToVideo,
                            onClick = { isImageToVideo = !isImageToVideo },
                            label = { Text(if (isImageToVideo) "✓ Animate Uploaded Photo" else "+ Upload Photo to Animate") }
                        )
                    }
                }

                // Lyria duration toggle
                if (subTab == 2) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = !isProLyriaTrack,
                            onClick = { isProLyriaTrack = false },
                            label = { Text("30s Clip (Lyria Clip)") }
                        )
                        FilterChip(
                            selected = isProLyriaTrack,
                            onClick = { isProLyriaTrack = true },
                            label = { Text("Full Track (Lyria Pro)") }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        when (subTab) {
                            0 -> onGenerateImage(promptText, null, "1:1")
                            1 -> onGenerateVideo(promptText, if (isImageToVideo) "sample_photo" else null, selectedAspectRatio)
                            2 -> onGenerateMusic(promptText, isProLyriaTrack)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BengalRed),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isGenerating && promptText.isNotBlank()
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Synthesizing Media...", fontWeight = FontWeight.Bold)
                    } else {
                        val btnLabel = when (subTab) {
                            0 -> "Generate Image"
                            1 -> if (isImageToVideo) "Animate Image into Video (Veo)" else "Generate Video (Veo 3.1)"
                            else -> "Generate Puja Music (Lyria)"
                        }
                        Text(btnLabel, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "GENERATED CREATIONS (${results.size})",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = BengalRed,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        results.forEach { res ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val icon = when (res.type) {
                                "image" -> Icons.Default.Image
                                "video" -> Icons.Default.Movie
                                else -> Icons.Default.MusicNote
                            }
                            Icon(icon, contentDescription = null, tint = BengalRed, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(res.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(res.prompt, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(6.dp))

                    if (res.mediaUrl != null && res.type == "image") {
                        AsyncImage(
                            model = res.mediaUrl,
                            contentDescription = res.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(10.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(res.details, fontSize = 10.sp, color = Color(0xFF059669), fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// ----------------------------------------------------------------------------
// 3. CLOUD SYNC & FIREBASE AUTH (Google Sign-In, Firestore Data Persistence)
// ----------------------------------------------------------------------------
@Composable
private fun CloudSyncView(
    currentUser: UserProfile?,
    syncStatus: String,
    onGoogleSignIn: (String, String) -> Unit,
    onSyncFirestore: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(0xFFFEF3C7), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            tint = BengalRed,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = currentUser?.displayName ?: "Guest Puja Hopper",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = currentUser?.email ?: "Offline local mode",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Firebase Auth & Firestore Integration",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = BengalRed
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Sync your favorite pandals, visited check-ins, and saved multi-hour itineraries securely across devices with Firestore.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = onSyncFirestore,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BengalRed),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sync to Firestore", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            onGoogleSignIn("Sourav Das", "official.souravdas31@gmail.com")
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Google Sign-In", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CloudDone, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Status: $syncStatus",
                        fontSize = 11.sp,
                        color = Color(0xFF059669),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
