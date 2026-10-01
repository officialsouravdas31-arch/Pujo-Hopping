package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.dataset.KolkataPandalDataset
import com.example.ui.components.PandalCard
import com.example.ui.theme.BengalRed
import com.example.ui.theme.FestiveGold

@Composable
fun AreaScreen(
    zone: String,
    onBack: () -> Unit,
    onOpenPandal: (String) -> Unit,
    onExploreMap: () -> Unit,
    onPlanRoute: () -> Unit,
    favoriteIds: Set<String>,
    visitedIds: Set<String>,
    onToggleFavorite: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val areaPandals = KolkataPandalDataset.pandals.filter {
        it.zone.contains(zone, ignoreCase = true) || it.area.contains(zone, ignoreCase = true)
    }

    val (desc, metroHub) = when {
        zone.contains("South") -> Pair("Explore the biggest and most creative Puja clusters across South Kolkata.", "Kalighat, Rabindra Sarobar & Jatin Das Park")
        zone.contains("North") -> Pair("Experience century-old traditional heritage, Rajbari aristocratic pujas and vibrant street food.", "Shyambazar & Shobhabazar Sutanuti")
        zone.contains("Central") -> Pair("Illuminated palace replicas reflecting across lakes and historic freedom-movement venues.", "Central, MG Road & Sealdah")
        zone.contains("East") -> Pair("Global architectural spectacles along the VIP Road and Lake Town corridors.", "Ultadanga, Salt Lake Stadium & Lake Town")
        zone.contains("Salt Lake") -> Pair("Planned wide avenues with tranquil green parks and family-friendly thematic pavilions.", "Karunamoyee & Central Park")
        else -> Pair("Modern expansive venues with spacious parking, food courts and electric shuttle connectivity.", "New Town Mela Ground & Sector V")
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("area_screen_$zone"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Top Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BengalRed)
                    .padding(horizontal = 16.dp, vertical = 18.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0x33FFFFFF), CircleShape)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = zone,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = desc,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0x33000000))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🚇 Metro Hub: $metroHub",
                                fontSize = 11.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${areaPandals.size} Pandals",
                                fontSize = 11.sp,
                                color = FestiveGold,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Action bar: Explore on Map & Plan Area Route
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onExploreMap,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("View Area Map", fontSize = 12.sp)
                }

                Button(
                    onClick = onPlanRoute,
                    colors = ButtonDefaults.buttonColors(containerColor = BengalRed),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Plan This Area", fontSize = 12.sp)
                }
            }
        }

        // Pandal List
        items(areaPandals) { pandal ->
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
    }
}
