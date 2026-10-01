package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.dataset.KolkataPandalDataset
import com.example.data.model.Pandal
import com.example.ui.ScreenTab
import com.example.ui.components.CrowdBadge
import com.example.ui.components.PandalCard
import com.example.ui.theme.BengalCrimson
import com.example.ui.theme.BengalRed
import com.example.ui.theme.BengalRedDark
import com.example.ui.theme.FestiveGold
import com.example.ui.theme.FestiveGoldSoft

@Composable
fun HomeScreen(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onNavigateTab: (ScreenTab) -> Unit,
    onOpenPandal: (String) -> Unit,
    onOpenArea: (String) -> Unit,
    onPresetSelect: (String) -> Unit,
    favoriteIds: Set<String>,
    visitedIds: Set<String>,
    onToggleFavorite: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val popularPandals = KolkataPandalDataset.pandals.take(8)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // 1. Festive Header & Branding
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(BengalRedDark, BengalRed, BengalCrimson)
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "PujoPlan",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = FestiveGoldSoft,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "2026",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier
                                        .background(Color(0x33FFFFFF), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = "Kolkata Durga Puja • Plan. Explore. Puja.",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.85f),
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color(0x22FFFFFF), CircleShape)
                                .clickable { onNavigateTab(ScreenTab.AI_CHAT) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Pujo AI",
                                tint = FestiveGoldSoft,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Hero Headline & Copy
                    Text(
                        text = "Plan Your Puja.\nExplore Kolkata.",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        lineHeight = 32.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Find pandals, plan routes, avoid unnecessary travel and discover Kolkata's Puja season your way.",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Primary and Secondary CTAs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onNavigateTab(ScreenTab.PLAN) },
                            modifier = Modifier
                                .weight(1.2f)
                                .height(50.dp)
                                .testTag("home_plan_my_puja_btn"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = FestiveGold,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "PLAN MY PUJA",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                letterSpacing = 0.5.sp
                            )
                        }

                        OutlinedButton(
                            onClick = { onNavigateTab(ScreenTab.MAP) },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("home_explore_map_btn"),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.White
                            ),
                            border = BorderStroke(
                                1.dp,
                                Brush.linearGradient(listOf(Color.White, Color.White.copy(alpha = 0.5f)))
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "EXPLORE MAP",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // 2. Global Search Box
        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_search_input"),
                    placeholder = { Text("Search pandal, area (e.g. Gariahat) or theme...", fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = BengalRed)
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = BengalRed,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    singleLine = true
                )
            }
        }

        // 3. Explore Kolkata Areas
        item {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                PaddingHeader(
                    title = "Explore Kolkata",
                    subtitle = "Discover iconic Puja clusters across the city"
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val areas = listOf(
                        AreaItem("South Kolkata", "20 Pandals", "Gariahat, Ballygunge, Chetla", 0xFF991B1B),
                        AreaItem("North Kolkata", "15 Pandals", "Bagbazar, Shobhabazar, Hatibagan", 0xFFD97706),
                        AreaItem("Central Kolkata", "8 Pandals", "College Sq, Mohammad Ali Park", 0xFF7C3AED),
                        AreaItem("East Kolkata", "5 Pandals", "Lake Town, Sreebhumi, VIP Road", 0xFF059669),
                        AreaItem("Salt Lake", "6 Pandals", "FD Block, BJ Block, Central Park", 0xFF2563EB),
                        AreaItem("New Town", "3 Pandals", "Mela Ground, Rajarhat", 0xFFDB2777)
                    )

                    items(areas) { area ->
                        AreaCard(
                            area = area,
                            onClick = { onOpenArea(area.name) }
                        )
                    }
                }
            }
        }

        // 4. Plan Your Puja Your Way (4 Presets)
        item {
            Column(modifier = Modifier.padding(top = 22.dp)) {
                PaddingHeader(
                    title = "Plan Your Puja Your Way",
                    subtitle = "Curated experiences matched to your style"
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PresetCard(
                        icon = Icons.Default.LocalFireDepartment,
                        title = "Famous Pandals",
                        desc = "Headline crowd pullers",
                        accent = Color(0xFFDC2626),
                        onClick = { onPresetSelect("Famous Pandals") },
                        modifier = Modifier.weight(1f)
                    )
                    PresetCard(
                        icon = Icons.Default.PhotoCamera,
                        title = "Photography Route",
                        desc = "Aesthetic architecture & light",
                        accent = Color(0xFFD97706),
                        onClick = { onPresetSelect("Photography") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PresetCard(
                        icon = Icons.Default.FamilyRestroom,
                        title = "Family Friendly",
                        desc = "Open spaces & easy transit",
                        accent = Color(0xFF059669),
                        onClick = { onPresetSelect("Family Friendly") },
                        modifier = Modifier.weight(1f)
                    )
                    PresetCard(
                        icon = Icons.AutoMirrored.Filled.DirectionsWalk,
                        title = "Less Walking",
                        desc = "Metro connected clusters",
                        accent = Color(0xFF2563EB),
                        onClick = { onPresetSelect("Less Walking") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 5. PujoPlan AI Highlight
        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 22.dp)) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_pujo_ai_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFEF3C7)
                    )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(FestiveGold, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "PujoPlan AI Assistant",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF78350F)
                                )
                                Text(
                                    text = "Powered by Gemini & Kolkata Transit Intelligence",
                                    fontSize = 11.sp,
                                    color = Color(0xFF92400E)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "“Tell us where you are, how much time you have and what kind of Puja experience you want — PujoPlan creates your route.”",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF78350F),
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { onNavigateTab(ScreenTab.AI_CHAT) },
                            colors = ButtonDefaults.buttonColors(containerColor = BengalRed),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Ask Pujo AI • Create My Route", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 6. Popular Right Now
        item {
            Column(modifier = Modifier.padding(top = 4.dp)) {
                PaddingHeader(
                    title = "Popular Right Now",
                    subtitle = "Most visited pandals with live crowd status"
                )
            }
        }

        items(popularPandals) { pandal ->
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                PandalCard(
                    pandal = pandal,
                    isFavorite = favoriteIds.contains(pandal.id),
                    isVisited = visitedIds.contains(pandal.id),
                    onCardClick = { onOpenPandal(pandal.id) },
                    onToggleFavorite = { onToggleFavorite(pandal.id) }
                )
            }
        }

        // Demo data disclaimer
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "ℹ️ Note: Some information is based on demo preview data and should be verified before travel. Kolkata Police traffic diversions apply during Puja days.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
private fun PaddingHeader(title: String, subtitle: String) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = subtitle,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private data class AreaItem(val name: String, val count: String, val highlights: String, val colorHex: Long)

@Composable
private fun AreaCard(area: AreaItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable(onClick = onClick)
            .testTag("area_card_${area.name}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(Color(area.colorHex).copy(alpha = 0.15f), RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("📍", fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = area.name,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = area.count,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = BengalRed
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = area.highlights,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun PresetCard(
    icon: ImageVector,
    title: String,
    desc: String,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick)
            .testTag("preset_card_$title"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(accent.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = desc,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 14.sp
            )
        }
    }
}
