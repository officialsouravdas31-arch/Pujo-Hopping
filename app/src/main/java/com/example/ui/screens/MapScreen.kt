package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CrowdLevel
import com.example.data.model.Pandal
import com.example.data.model.PandalCategory
import com.example.ui.components.KolkataMapCanvas
import com.example.ui.theme.BengalRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    pandals: List<Pandal>,
    selectedPandal: Pandal?,
    onPandalSelected: (Pandal) -> Unit,
    onViewDetails: (Pandal) -> Unit,
    onAddToPlan: (Pandal) -> Unit,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    categoryFilter: PandalCategory?,
    onCategoryFilterChange: (PandalCategory?) -> Unit,
    crowdFilter: CrowdLevel?,
    onCrowdFilterChange: (CrowdLevel?) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("map_screen")
    ) {
        // 1. Full-screen interactive Kolkata Canvas map
        KolkataMapCanvas(
            pandals = pandals,
            selectedPandal = selectedPandal,
            onPandalSelected = onPandalSelected,
            onViewDetails = onViewDetails,
            onAddToPlan = onAddToPlan,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Floating Search & Filter Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("map_search_input"),
                placeholder = { Text("Search pandal, area or landmark...", fontSize = 13.sp) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = BengalRed)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(alpha = 0.95f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.92f),
                    focusedBorderColor = BengalRed,
                    unfocusedBorderColor = Color(0xFFD6C8B5)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Filter Chips Bar (All, Popular, Theme, Traditional, Family, Low Crowd)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // All Chip
                FilterChip(
                    selected = categoryFilter == null && crowdFilter == null,
                    onClick = {
                        onCategoryFilterChange(null)
                        onCrowdFilterChange(null)
                    },
                    label = { Text("All (${pandals.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BengalRed,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("filter_chip_all")
                )

                // Popular
                FilterChip(
                    selected = categoryFilter == PandalCategory.POPULAR,
                    onClick = { onCategoryFilterChange(if (categoryFilter == PandalCategory.POPULAR) null else PandalCategory.POPULAR) },
                    label = { Text("🔥 Popular", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFDC2626),
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("filter_chip_popular")
                )

                // Theme
                FilterChip(
                    selected = categoryFilter == PandalCategory.THEME,
                    onClick = { onCategoryFilterChange(if (categoryFilter == PandalCategory.THEME) null else PandalCategory.THEME) },
                    label = { Text("🎨 Theme", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFD97706),
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("filter_chip_theme")
                )

                // Traditional
                FilterChip(
                    selected = categoryFilter == PandalCategory.TRADITIONAL,
                    onClick = { onCategoryFilterChange(if (categoryFilter == PandalCategory.TRADITIONAL) null else PandalCategory.TRADITIONAL) },
                    label = { Text("🛕 Traditional", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF7C3AED),
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("filter_chip_traditional")
                )

                // Family Friendly
                FilterChip(
                    selected = categoryFilter == PandalCategory.FAMILY_FRIENDLY,
                    onClick = { onCategoryFilterChange(if (categoryFilter == PandalCategory.FAMILY_FRIENDLY) null else PandalCategory.FAMILY_FRIENDLY) },
                    label = { Text("👨‍👩‍👧 Family", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF059669),
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("filter_chip_family")
                )

                // Low Crowd
                FilterChip(
                    selected = crowdFilter == CrowdLevel.LOW,
                    onClick = { onCrowdFilterChange(if (crowdFilter == CrowdLevel.LOW) null else CrowdLevel.LOW) },
                    label = { Text("🟢 Low Crowd", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF16A34A),
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("filter_chip_low_crowd")
                )
            }
        }
    }
}
