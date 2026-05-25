package com.todoai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun ScheduledTaskItem(line: String) {
    // Expected format: "PRIO | TITLE | suggestion"
    val parts = line.split("|").map { it.trim() }
    val priority = parts.getOrElse(0) { "" }
    val title = parts.getOrElse(1) { line }
    val suggestion = parts.getOrElse(2) { "" }

    val bg = when (priority) {
        "HIGH"   -> Color(0xFFFFEBEE)
        "MEDIUM" -> Color(0xFFFFF3E0)
        "LOW"    -> Color(0xFFE8F5E9)
        else     -> MaterialTheme.colorScheme.surface
    }
    val priorityColor = when (priority) {
        "HIGH"   -> Color(0xFFB00020)
        "MEDIUM" -> Color(0xFFE65100)
        "LOW"    -> Color(0xFF2E7D32)
        else     -> Color.Gray
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(priorityColor)
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(priority, color = Color.White, style = MaterialTheme.typography.labelSmall)
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (suggestion.isNotBlank()) Text(suggestion, style = MaterialTheme.typography.bodySmall)
        }
    }
}
