package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.DirectionsTransit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Remove
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Pandal
import com.example.ui.theme.BengalRed
import com.example.ui.theme.FestiveGold
import kotlin.math.hypot

// Geographic bounds for Kolkata urban metropolitan corridor
private const val MIN_LAT = 22.45
private const val MAX_LAT = 22.64
private const val MIN_LNG = 88.29
private const val MAX_LNG = 88.48

@Composable
fun KolkataMapCanvas(
    pandals: List<Pandal>,
    selectedPandal: Pandal?,
    onPandalSelected: (Pandal) -> Unit,
    onViewDetails: (Pandal) -> Unit,
    onAddToPlan: (Pandal) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Zoom and pan state
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val transformState = rememberTransformableState { zoomChange, offsetChange, _ ->
        scale = (scale * zoomChange).coerceIn(0.7f, 4.0f)
        offset += offsetChange
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF1EDE6))
    ) {
        // Interactive Canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .transformable(state = transformState)
                .pointerInput(pandals, scale, offset) {
                    detectTapGestures { tapOffset ->
                        val w = size.width
                        val h = size.height

                        // Find closest pandal within touch radius
                        var closest: Pandal? = null
                        var minDistance = Float.MAX_VALUE

                        pandals.forEach { p ->
                            val pt = projectCoord(p.latitude, p.longitude, w.toFloat(), h.toFloat(), scale, offset)
                            val dist = hypot(pt.x - tapOffset.x, pt.y - tapOffset.y)
                            if (dist < 36.dp.toPx() && dist < minDistance) {
                                minDistance = dist
                                closest = p
                            }
                        }

                        if (closest != null) {
                            onPandalSelected(closest!!)
                        }
                    }
                }
        ) {
            val w = size.width
            val h = size.height

            // 1. Draw Hooghly River
            drawHooghlyRiver(w, h, scale, offset)

            // 2. Draw Kolkata Metro Lines
            drawMetroLines(w, h, scale, offset)

            // 3. Draw Kolkata major bridges (Howrah Bridge, Vidyasagar Setu)
            drawBridges(w, h, scale, offset)

            // 4. Draw Pandal Markers
            pandals.forEach { pandal ->
                val pt = projectCoord(pandal.latitude, pandal.longitude, w, h, scale, offset)
                val isSelected = pandal.id == selectedPandal?.id
                val markerColor = Color(pandal.category.badgeColor)

                if (isSelected) {
                    // Outer pulse ring
                    drawCircle(
                        color = markerColor.copy(alpha = 0.35f),
                        radius = 22.dp.toPx(),
                        center = pt
                    )
                    drawCircle(
                        color = markerColor,
                        radius = 14.dp.toPx(),
                        center = pt
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 6.dp.toPx(),
                        center = pt
                    )
                } else {
                    // Normal pin
                    drawCircle(
                        color = Color.White,
                        radius = 10.dp.toPx(),
                        center = pt
                    )
                    drawCircle(
                        color = markerColor,
                        radius = 8.dp.toPx(),
                        center = pt
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 3.dp.toPx(),
                        center = pt
                    )
                }
            }
        }

        // Map Category Legend (Top right overlay)
        Card(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 12.dp, end = 12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
            shape = RoundedCornerShape(10.dp),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
                LegendItem(color = Color(0xFFDC2626), label = "Popular")
                LegendItem(color = Color(0xFFD97706), label = "Theme")
                LegendItem(color = Color(0xFF7C3AED), label = "Traditional")
                LegendItem(color = Color(0xFF059669), label = "Family")
                LegendItem(color = Color(0xFF2563EB), label = "Hidden Gem")
            }
        }

        // Zoom & Reset Controls (Right side floating)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = { scale = (scale * 1.3f).coerceAtMost(4.0f) },
                modifier = Modifier
                    .size(42.dp)
                    .background(Color.White, CircleShape)
                    .testTag("zoom_in_btn")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Zoom In", tint = BengalRed)
            }

            IconButton(
                onClick = { scale = (scale / 1.3f).coerceAtLeast(0.7f) },
                modifier = Modifier
                    .size(42.dp)
                    .background(Color.White, CircleShape)
                    .testTag("zoom_out_btn")
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Zoom Out", tint = BengalRed)
            }

            IconButton(
                onClick = {
                    scale = 1.0f
                    offset = Offset.Zero
                },
                modifier = Modifier
                    .size(42.dp)
                    .background(Color.White, CircleShape)
                    .testTag("recenter_map_btn")
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "Reset View", tint = BengalRed)
            }
        }

        // Selected Pandal Bottom Sheet / Quick Card
        AnimatedVisibility(
            visible = selectedPandal != null,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(14.dp),
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            selectedPandal?.let { pandal ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("map_pandal_bottom_sheet"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = pandal.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "📍 ${pandal.area} • ${pandal.zone}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = FestiveGold, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(text = "${pandal.rating}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CrowdBadge(level = pandal.crowdLevel)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.DirectionsTransit,
                                    contentDescription = "Metro",
                                    modifier = Modifier.size(14.dp),
                                    tint = BengalRed
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${pandal.nearestMetro.substringBefore(" (")} (${pandal.metroDistance})",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Action Buttons: VIEW DETAILS, ADD TO PLAN, NAVIGATE
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onViewDetails(pandal) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("view_details_btn"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Details", fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = { onAddToPlan(pandal) },
                                modifier = Modifier
                                    .weight(1.1f)
                                    .testTag("add_to_plan_btn"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.BookmarkAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Plan", fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    val geoUri = Uri.parse("geo:${pandal.latitude},${pandal.longitude}?q=${pandal.latitude},${pandal.longitude}(${Uri.encode(pandal.name)})")
                                    val intent = Intent(Intent.ACTION_VIEW, geoUri)
                                    context.startActivity(intent)
                                },
                                modifier = Modifier
                                    .weight(1.1f)
                                    .testTag("navigate_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = BengalRed),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Navigate", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 1.dp)
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(text = label, fontSize = 9.sp, fontWeight = FontWeight.Medium, color = Color(0xFF333333))
    }
}

// Convert Lat/Lng to Canvas coordinates with zoom & pan
private fun projectCoord(lat: Double, lng: Double, width: Float, height: Float, scale: Float, offset: Offset): Offset {
    val normX = (lng - MIN_LNG) / (MAX_LNG - MIN_LNG)
    val normY = (MAX_LAT - lat) / (MAX_LAT - MIN_LAT) // Invert Y because lat increases upward

    val centerX = width / 2f
    val centerY = height / 2f

    val basePx = normX.toFloat() * width
    val basePy = normY.toFloat() * height

    val finalX = (basePx - centerX) * scale + centerX + offset.x
    val finalY = (basePy - centerY) * scale + centerY + offset.y

    return Offset(finalX, finalY)
}

// Renders the natural curve of Hooghly River flowing along Western Kolkata
private fun DrawScope.drawHooghlyRiver(width: Float, height: Float, scale: Float, offset: Offset) {
    val riverPath = Path()
    val riverCoords = listOf(
        Pair(22.65, 88.35),
        Pair(22.62, 88.355),
        Pair(22.59, 88.358),
        Pair(22.58, 88.348), // Howrah Bridge
        Pair(22.55, 88.332), // Vidyasagar Setu
        Pair(22.52, 88.315),
        Pair(22.48, 88.295),
        Pair(22.44, 88.280)
    )

    riverCoords.forEachIndexed { i, (lat, lng) ->
        val pt = projectCoord(lat, lng, width, height, scale, offset)
        if (i == 0) riverPath.moveTo(pt.x, pt.y) else riverPath.lineTo(pt.x, pt.y)
    }

    drawPath(
        path = riverPath,
        color = Color(0xFFBAE6FD),
        style = Stroke(width = 16.dp.toPx() * scale)
    )
}

// Renders Kolkata Blue Line (North-South) and Green Line (East-West)
private fun DrawScope.drawMetroLines(width: Float, height: Float, scale: Float, offset: Offset) {
    // 1. Blue Line (Dakshineswar to Kavi Subhash via Shyambazar, Central, Park St, Kalighat)
    val blueLineCoords = listOf(
        Pair(22.63, 88.375), // Dum Dum
        Pair(22.602, 88.372), // Shyambazar
        Pair(22.587, 88.368), // Girish Park
        Pair(22.581, 88.361), // MG Road
        Pair(22.574, 88.363), // Central
        Pair(22.564, 88.352), // Esplanade
        Pair(22.551, 88.351), // Park Street
        Pair(22.531, 88.346), // Netaji Bhavan
        Pair(22.518, 88.347), // Kalighat
        Pair(22.512, 88.351), // Rabindra Sarobar
        Pair(22.490, 88.358), // Mahanayak Uttam Kumar
        Pair(22.472, 88.362), // Gitanjali (Naktala)
        Pair(22.450, 88.370)  // Kavi Subhash
    )

    val bluePath = Path()
    blueLineCoords.forEachIndexed { i, (lat, lng) ->
        val pt = projectCoord(lat, lng, width, height, scale, offset)
        if (i == 0) bluePath.moveTo(pt.x, pt.y) else bluePath.lineTo(pt.x, pt.y)
    }

    drawPath(
        path = bluePath,
        color = Color(0xFF2563EB).copy(alpha = 0.7f),
        style = Stroke(width = 3.5.dp.toPx() * scale)
    )

    // 2. Green Line (East-West: Howrah -> Esplanade -> Sealdah -> Salt Lake Stadium -> Karunamoyee -> Sector V)
    val greenLineCoords = listOf(
        Pair(22.583, 88.342), // Howrah Station
        Pair(22.564, 88.352), // Esplanade interchange
        Pair(22.568, 88.371), // Sealdah
        Pair(22.578, 88.398), // Phoolbagan
        Pair(22.582, 88.406), // Salt Lake Stadium
        Pair(22.586, 88.414), // Karunamoyee
        Pair(22.580, 88.432)  // Sector V
    )

    val greenPath = Path()
    greenLineCoords.forEachIndexed { i, (lat, lng) ->
        val pt = projectCoord(lat, lng, width, height, scale, offset)
        if (i == 0) greenPath.moveTo(pt.x, pt.y) else greenPath.lineTo(pt.x, pt.y)
    }

    drawPath(
        path = greenPath,
        color = Color(0xFF16A34A).copy(alpha = 0.7f),
        style = Stroke(width = 3.5.dp.toPx() * scale)
    )
}

private fun DrawScope.drawBridges(width: Float, height: Float, scale: Float, offset: Offset) {
    // Howrah Bridge
    val hb1 = projectCoord(22.585, 88.343, width, height, scale, offset)
    val hb2 = projectCoord(22.586, 88.354, width, height, scale, offset)
    drawLine(
        color = Color(0xFF475569),
        start = hb1,
        end = hb2,
        strokeWidth = 3.dp.toPx() * scale
    )

    // Vidyasagar Setu (2nd Hooghly Bridge)
    val vs1 = projectCoord(22.553, 88.324, width, height, scale, offset)
    val vs2 = projectCoord(22.556, 88.338, width, height, scale, offset)
    drawLine(
        color = Color(0xFF475569),
        start = vs1,
        end = vs2,
        strokeWidth = 3.dp.toPx() * scale
    )
}
