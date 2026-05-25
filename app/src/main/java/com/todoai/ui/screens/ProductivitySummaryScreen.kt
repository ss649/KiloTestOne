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
import com.todoai.ui.viewmodel.SummaryViewModel

@Composable
fun ProductivitySummaryScreen() {
    val viewModel: SummaryViewModel = hiltViewModel()
    val summary by viewModel.summary.collectAsState()

    Column(Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Productivity Summary", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        if (summary.isBlank()) {
            CircularProgressIndicator()
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(summary.lines()) { line ->
                    Text(line, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}
