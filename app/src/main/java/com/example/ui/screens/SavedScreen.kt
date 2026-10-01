package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.dataset.KolkataPandalDataset
import com.example.data.local.SavedRouteEntity
import com.example.data.model.Pandal
import com.example.ui.components.PandalCard
import com.example.ui.theme.BengalRed
import com.example.ui.theme.FestiveGold

@Composable
fun SavedScreen(
    favoriteIds: Set<String>,
    visitedIds: Set<String>,
    savedRoutes: List<SavedRouteEntity>,
    onToggleFavorite: (String) -> Unit,
    onOpenPandal: (String) -> Unit,
    onDeleteRoute: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSection by remember { mutableIntStateOf(0) }
    val totalAvailable = KolkataPandalDataset.pandals.size
    val visitedCount = visitedIds.size

    val favoritePandals = KolkataPandalDataset.pandals.filter { favoriteIds.contains(it.id) }
    val visitedPandals = KolkataPandalDataset.pandals.filter { visitedIds.contains(it.id) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("saved_screen")
    ) {
        // Top Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BengalRed)
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Column {
                Text(
                    text = "My Puja 2026",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Track your favorite pandals, visited check-ins and hopping milestones",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Progress Bar: "7 / 20 Pandals Visited"
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0x33000000))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Puja Hopping Progress",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color.White
                            )
                            Text(
                                text = "$visitedCount / $totalAvailable Visited",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp,
                                color = FestiveGold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { if (totalAvailable > 0) visitedCount.toFloat() / totalAvailable else 0f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp),
                            color = FestiveGold,
                            trackColor = Color(0x33FFFFFF)
                        )
                    }
                }
            }
        }

        // Gamification Achievement Badges Bar
        Column(modifier = Modifier.padding(vertical = 12.dp)) {
            Text(
                text = "ACHIEVEMENTS & BADGES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = BengalRed,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BadgeCard(emoji = "🏆", title = "Puja Explorer", desc = "Visit 5 pandals", unlocked = visitedCount >= 5)
                BadgeCard(emoji = "📍", title = "South Kolkata", desc = "Visit 3 South", unlocked = visitedPandals.count { it.zone.contains("South") } >= 3)
                BadgeCard(emoji = "📸", title = "Photo Hunter", desc = "Visit 3 Themes", unlocked = visitedPandals.count { it.category.name == "THEME" } >= 3)
                BadgeCard(emoji = "🔥", title = "10 Pandal Challenge", desc = "Visit 10 pandals", unlocked = visitedCount >= 10)
                BadgeCard(emoji = "🌙", title = "Night Explorer", desc = "Saved Night Route", unlocked = savedRoutes.isNotEmpty())
                BadgeCard(emoji = "👨‍👩‍👧", title = "Family Puja", desc = "Visit 2 Family Gems", unlocked = visitedPandals.count { it.facilities.familyFriendly } >= 2)
            }
        }

        // Section Tabs: Saved Pandals, My Routes, Visited Pandals
        TabRow(
            selectedTabIndex = selectedSection,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = BengalRed,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedSection]),
                    color = BengalRed
                )
            }
        ) {
            Tab(
                selected = selectedSection == 0,
                onClick = { selectedSection = 0 },
                text = { Text("Saved (${favoritePandals.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedSection == 1,
                onClick = { selectedSection = 1 },
                text = { Text("My Routes (${savedRoutes.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedSection == 2,
                onClick = { selectedSection = 2 },
                text = { Text("Visited ($visitedCount)", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
        }

        // Tab Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 14.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            when (selectedSection) {
                0 -> {
                    if (favoritePandals.isEmpty()) {
                        item {
                            EmptyState(
                                icon = Icons.Default.Favorite,
                                title = "No Saved Pandals",
                                subtitle = "Browse the map or list and tap the heart icon to save pandals to your Puja wishlist!"
                            )
                        }
                    } else {
                        items(favoritePandals) { pandal ->
                            PandalCard(
                                pandal = pandal,
                                isFavorite = true,
                                isVisited = visitedIds.contains(pandal.id),
                                onCardClick = { onOpenPandal(pandal.id) },
                                onToggleFavorite = { onToggleFavorite(pandal.id) }
                            )
                        }
                    }
                }

                1 -> {
                    if (savedRoutes.isEmpty()) {
                        item {
                            EmptyState(
                                icon = Icons.Default.Map,
                                title = "No Saved Routes",
                                subtitle = "Go to 'Plan My Puja' to generate your customized Puja route and tap 'Save Plan'!"
                            )
                        }
                    } else {
                        items(savedRoutes) { route ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = route.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        IconButton(onClick = { onDeleteRoute(route.id) }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                        }
                                    }

                                    Text(
                                        text = "Start: ${route.startLocation} • ${route.durationHours} • ${route.transportMode}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = "📍 Total ${route.totalPandals} Pandals • ~${route.estimatedWalkingKm} km walking",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp,
                                        color = BengalRed
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = route.explanation,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }
                }

                2 -> {
                    if (visitedPandals.isEmpty()) {
                        item {
                            EmptyState(
                                icon = Icons.Default.CheckCircle,
                                title = "No Check-ins Yet",
                                subtitle = "Open any pandal detail screen and tap 'Check In / Mark Visited' as you hop across Kolkata!"
                            )
                        }
                    } else {
                        items(visitedPandals) { pandal ->
                            PandalCard(
                                pandal = pandal,
                                isFavorite = favoriteIds.contains(pandal.id),
                                isVisited = true,
                                onCardClick = { onOpenPandal(pandal.id) },
                                onToggleFavorite = { onToggleFavorite(pandal.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BadgeCard(emoji: String, title: String, desc: String, unlocked: Boolean) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (unlocked) Color(0xFFFEF3C7) else Color(0xFFF3F4F6)
        ),
        modifier = Modifier.width(130.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = emoji, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = if (unlocked) Color(0xFF78350F) else Color(0xFF6B7280)
            )
            Text(
                text = if (unlocked) "Unlocked! ✓" else desc,
                fontSize = 9.sp,
                color = if (unlocked) Color(0xFF92400E) else Color(0xFF9CA3AF)
            )
        }
    }
}

@Composable
private fun EmptyState(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(Color(0xFFFEE2E2), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = BengalRed, modifier = Modifier.size(32.dp))
        }
        Spacer(modifier = Modifier.height(14.dp))
        Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subtitle,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp),
            lineHeight = 16.sp
        )
    }
}
