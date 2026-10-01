package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CrowdLevel
import com.example.ui.theme.CrowdGreen
import com.example.ui.theme.CrowdOrange
import com.example.ui.theme.CrowdRed
import com.example.ui.theme.CrowdYellow

@Composable
fun CrowdBadge(
    level: CrowdLevel,
    modifier: Modifier = Modifier,
    isCommunityReport: Boolean = false,
    showQueue: Boolean = true
) {
    val (dotColor, bg, textColor) = when (level) {
        CrowdLevel.LOW -> Triple(CrowdGreen, Color(0xFFDCFCE7), Color(0xFF14532D))
        CrowdLevel.MODERATE -> Triple(CrowdYellow, Color(0xFFFEF9C3), Color(0xFF713F12))
        CrowdLevel.HIGH -> Triple(CrowdOrange, Color(0xFFFFEDD5), Color(0xFF7C2D12))
        CrowdLevel.EXTREME -> Triple(CrowdRed, Color(0xFFFEE2E2), Color(0xFF7F1D1D))
    }

    Row(
        modifier = modifier
            .background(bg, RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .background(dotColor, CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = if (showQueue) "${level.label} • ${level.queueEstimate}" else level.label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis
        )
        if (isCommunityReport) {
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = "👥",
                fontSize = 10.sp,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}
