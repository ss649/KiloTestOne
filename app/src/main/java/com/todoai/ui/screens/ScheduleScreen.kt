package com.todoai.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.todoai.ui.components.ScheduledTaskItem
import com.todoai.ui.viewmodel.ScheduleViewModel

@Composable
fun ScheduleScreen() {
    val viewModel: ScheduleViewModel = hiltViewModel()
    val suggestion by viewModel.suggestion.collectAsState()

    Column(Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Suggested Schedule", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        if (suggestion.isBlank()) {
            CircularProgressIndicator()
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(suggestion.lines()) { line ->
                    ScheduledTaskItem(line)
                }
            }
        }
    }
}
