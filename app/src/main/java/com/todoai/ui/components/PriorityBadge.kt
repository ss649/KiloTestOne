package com.todoai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.todoai.domain.model.Priority

@Composable
fun PriorityBadge(priority: Priority) {
    val (bg, fg) = when (priority) {
        Priority.HIGH   -> Color(0xFFB00020) to Color.White
        Priority.MEDIUM -> Color(0xFFFF6D00) to Color.White
        Priority.LOW    -> Color(0xFF1B5E20) to Color.White
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(99.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(priority.name, color = fg, style = MaterialTheme.typography.labelSmall)
    }
}
