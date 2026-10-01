package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DirectionsTransit
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Facilities
import com.example.data.model.TransportOptions
import com.example.ui.theme.BengalRed
import com.example.ui.theme.FestiveGold

@Composable
fun ConnectivityCard(
    nearestMetro: String,
    metroDistance: String,
    walkingDistance: String,
    transportOptions: TransportOptions,
    facilities: Facilities,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "HOW TO REACH",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = BengalRed
            )
            Text(
                text = "Transit & Connectivity Guide",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Metro
            TransitRow(
                icon = Icons.Default.DirectionsTransit,
                iconBg = Color(0xFFEFF6FF),
                iconTint = Color(0xFF2563EB),
                title = "Metro Transit",
                subtitle = "$nearestMetro • $metroDistance ($walkingDistance)",
                details = transportOptions.metro
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

            // Walking
            TransitRow(
                icon = Icons.AutoMirrored.Filled.DirectionsWalk,
                iconBg = Color(0xFFECFDF5),
                iconTint = Color(0xFF059669),
                title = "Walking Corridor",
                subtitle = walkingDistance,
                details = transportOptions.walking
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

            // Cab
            TransitRow(
                icon = Icons.Default.DirectionsCar,
                iconBg = Color(0xFFFEF3C7),
                iconTint = FestiveGold,
                title = "Cab / Rideshare Drop-off",
                subtitle = "Avoid police barricade diversions",
                details = transportOptions.cab
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

            // Bus
            TransitRow(
                icon = Icons.Default.DirectionsBus,
                iconBg = Color(0xFFF3E8FF),
                iconTint = Color(0xFF7C3AED),
                title = "Bus Connectivity",
                subtitle = "Regular and AC special Puja buses",
                details = transportOptions.bus
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

            // Parking
            TransitRow(
                icon = Icons.Default.LocalParking,
                iconBg = if (facilities.parking) Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                iconTint = if (facilities.parking) Color(0xFF16A34A) else Color(0xFFDC2626),
                title = "Vehicle Parking",
                subtitle = if (facilities.parking) "Designated parking space available nearby" else "No official parking; public transit strongly recommended",
                details = if (facilities.parking) "Paid park-and-walk ground within 400m" else "Streets strictly barricaded for pedestrian movement"
            )
        }
    }
}

@Composable
private fun TransitRow(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    details: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Row(
            modifier = Modifier
                .size(36.dp)
                .background(iconBg, CircleShape),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier
                    .size(20.dp)
                    .fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = details,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )
        }
    }
}
