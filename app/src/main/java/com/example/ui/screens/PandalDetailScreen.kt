package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.CrowdLevel
import com.example.data.model.Pandal
import com.example.ui.components.ConnectivityCard
import com.example.ui.components.CrowdBadge
import com.example.ui.components.ReportCrowdDialog
import com.example.ui.theme.BengalRed
import com.example.ui.theme.FestiveGold
import com.example.ui.util.PandalImageHelper

@Composable
fun PandalDetailScreen(
    pandal: Pandal,
    isFavorite: Boolean,
    isVisited: Boolean,
    onBack: () -> Unit,
    onToggleFavorite: () -> Unit,
    onToggleVisited: () -> Unit,
    onReportCrowd: (CrowdLevel) -> Unit,
    onAddToPlan: (Pandal) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showReportDialog by remember { mutableStateOf(false) }

    if (showReportDialog) {
        ReportCrowdDialog(
            pandal = pandal,
            onDismiss = { showReportDialog = false },
            onSubmit = { level ->
                onReportCrowd(level)
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("pandal_detail_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // 1. Hero Image with Overlay, Back Button, and Action Buttons
        item {
            val imageRequest = remember(pandal.id, pandal.imageUrl) {
                PandalImageHelper.buildImageRequest(context, pandal)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            ) {
                AsyncImage(
                    model = imageRequest,
                    contentDescription = pandal.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Gradient scrim
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0x99000000), Color.Transparent, Color(0xCC000000))
                            )
                        )
                )

                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0x88000000), CircleShape)
                            .testTag("detail_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Favorite
                        IconButton(
                            onClick = onToggleFavorite,
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0x88000000), CircleShape)
                                .testTag("detail_fav_btn")
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFavorite) BengalRed else Color.White
                            )
                        }

                        // Share
                        IconButton(
                            onClick = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "Explore ${pandal.name} (${pandal.area}, Kolkata)! 2026 Theme: ${pandal.theme}. Nearest Metro: ${pandal.nearestMetro}. Planned via PujoPlan Kolkata 2026."
                                    )
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share Pandal"))
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0x88000000), CircleShape)
                                .testTag("detail_share_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = Color.White
                            )
                        }
                    }
                }

                // Bottom Overlay: Category and Name
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color(pandal.category.badgeColor), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${pandal.category.emoji} ${pandal.category.label}",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = pandal.name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "📍 ${pandal.address}",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }

        // 2. Crowd Status & Live Report Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                        val isTabletOrDesktop = maxWidth >= 540.dp
                        if (isTabletOrDesktop) {
                            // Desktop / Tablet layout: Left header, Middle badge, Right button
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "LIVE CROWD & QUEUE",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BengalRed,
                                    letterSpacing = 1.sp,
                                    maxLines = 1,
                                    softWrap = false
                                )
                                CrowdBadge(level = pandal.crowdLevel, isCommunityReport = true)
                                OutlinedButton(
                                    onClick = { showReportDialog = true },
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.5.dp, BengalRed),
                                    modifier = Modifier
                                        .widthIn(min = 140.dp, max = 175.dp)
                                        .height(52.dp)
                                        .testTag("report_crowd_btn")
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            Icons.Default.People,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp),
                                            tint = BengalRed
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Report Crowd",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BengalRed,
                                            maxLines = 1,
                                            softWrap = false
                                        )
                                    }
                                }
                            }
                        } else {
                            // Mobile clean two-row layout:
                            // Row 1: LIVE CROWD & QUEUE + Crowd Status Pill
                            // Row 2: Full-width generous Report Crowd button
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "LIVE CROWD & QUEUE",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BengalRed,
                                        letterSpacing = 1.sp,
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    CrowdBadge(level = pandal.crowdLevel, isCommunityReport = true)
                                }

                                OutlinedButton(
                                    onClick = { showReportDialog = true },
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.5.dp, BengalRed),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("report_crowd_btn")
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            Icons.Default.People,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp),
                                            tint = BengalRed
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Report Crowd",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BengalRed,
                                            maxLines = 1,
                                            softWrap = false
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        InfoMetric(title = "Estimated Queue", value = "${pandal.estimatedQueueMinutes} mins", icon = "⏱️", modifier = Modifier.weight(1f))
                        InfoMetric(title = "Rating", value = "${pandal.rating} ★ (${pandal.reviewCount})", icon = "🌟", modifier = Modifier.weight(1f))
                        InfoMetric(title = "Verified", value = if (pandal.verified) "Police Verified" else "Demo Data", icon = "🛡️", modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // 3. Mark as Visited Quick Toggle
        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("toggle_visited_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isVisited) Color(0xFFDCFCE7) else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        val isTabletOrDesktop = maxWidth >= 500.dp
                        if (isTabletOrDesktop) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isVisited) Icons.Default.CheckCircle else Icons.Default.CheckCircleOutline,
                                        contentDescription = null,
                                        tint = if (isVisited) Color(0xFF16A34A) else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(26.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = if (isVisited) "Marked as Visited! ✓" else "Have you visited this pandal?",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = if (isVisited) Color(0xFF14532D) else MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = if (isVisited) "Added to your Puja 2026 hoppers diary" else "Tap to track your hopping progress",
                                            fontSize = 12.sp,
                                            color = if (isVisited) Color(0xFF15803D) else MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Button(
                                    onClick = onToggleVisited,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isVisited) Color(0xFF16A34A) else BengalRed
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 20.dp),
                                    modifier = Modifier
                                        .defaultMinSize(minWidth = 120.dp)
                                        .height(52.dp)
                                        .testTag("detail_visited_btn")
                                ) {
                                    Text(
                                        text = if (isVisited) "✓ Visited" else "✓ Check In",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }
                            }
                        } else {
                            // Mobile layout: Icon & description on top, full comfortable Check In button below
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isVisited) Icons.Default.CheckCircle else Icons.Default.CheckCircleOutline,
                                        contentDescription = null,
                                        tint = if (isVisited) Color(0xFF16A34A) else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(26.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (isVisited) "Marked as Visited! ✓" else "Have you visited this pandal?",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (isVisited) Color(0xFF14532D) else MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = if (isVisited) "Added to your Puja 2026 hoppers diary" else "Tap to track your hopping progress",
                                            fontSize = 11.sp,
                                            color = if (isVisited) Color(0xFF15803D) else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = onToggleVisited,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isVisited) Color(0xFF16A34A) else BengalRed
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 20.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .testTag("detail_visited_btn")
                                ) {
                                    Text(
                                        text = if (isVisited) "✓ Visited" else "✓ Check In",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Description and Theme Story
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "2026 PUJA THEME",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BengalRed,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = pandal.theme,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = pandal.description,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 19.sp
                    )
                }
            }
        }

        // 5. Best Time to Visit & Timings
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "TIMINGS & RECOMMENDATION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BengalRed,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, tint = FestiveGold, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = "Best Time to Visit", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = pandal.bestTimeToVisit, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Timings: ${pandal.openingTime} – ${pandal.closingTime}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 6. Connectivity Guide
        item {
            Box(modifier = Modifier.padding(16.dp)) {
                ConnectivityCard(
                    nearestMetro = pandal.nearestMetro,
                    metroDistance = pandal.metroDistance,
                    walkingDistance = pandal.walkingDistance,
                    transportOptions = pandal.transportOptions,
                    facilities = pandal.facilities
                )
            }
        }

        // 7. Nearby Services & Facilities
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "NEARBY FACILITIES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BengalRed,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        FacilityItem("🍴", "Food Nearby", pandal.facilities.foodNearby)
                        FacilityItem("🚻", "Public Toilet", pandal.facilities.toilet)
                        FacilityItem("🅿️", "Parking", pandal.facilities.parking)
                        FacilityItem("🏧", "ATM", pandal.facilities.atmNearby)
                        FacilityItem("💊", "First Aid", pandal.facilities.firstAid)
                    }
                }
            }
        }

        // 8. Bottom Sticky-Style Action Bar (Add to Plan & Get Directions)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { onAddToPlan(pandal) },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("detail_add_to_plan_btn"),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.BookmarkAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ADD TO PLAN",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        maxLines = 1,
                        softWrap = false
                    )
                }

                Button(
                    onClick = {
                        val geoUri = Uri.parse("geo:${pandal.latitude},${pandal.longitude}?q=${pandal.latitude},${pandal.longitude}(${Uri.encode(pandal.name)})")
                        val intent = Intent(Intent.ACTION_VIEW, geoUri)
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .weight(1.2f)
                        .height(52.dp)
                        .testTag("detail_get_directions_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = BengalRed),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "GET DIRECTIONS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoMetric(title: String, value: String, icon: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(icon, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun FacilityItem(icon: String, name: String, available: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(
                    if (available) Color(0xFFECFDF5) else Color(0xFFF3F4F6),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(icon, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = name,
            fontSize = 10.sp,
            color = if (available) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            fontWeight = if (available) FontWeight.SemiBold else FontWeight.Normal,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis
        )
    }
}
