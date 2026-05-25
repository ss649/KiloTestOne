package com.todoai.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatDueDate(millis: Long): String {
    val now = System.currentTimeMillis()
    val diff = millis - now
    return when {
        diff < 0 -> "Overdue"
        diff < 86_400_000 -> SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(millis))
        diff < 604_800_000 -> SimpleDateFormat("EEE h:mm a", Locale.getDefault()).format(Date(millis))
        else -> SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(millis))
    }
}
