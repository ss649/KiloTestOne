package com.todoai.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.todoai.ui.components.PriorityBadge
import com.todoai.ui.viewmodel.TaskDetailViewModel
import com.todoai.util.formatDueDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun taskDetailScreen(taskId: Long, onBack: () -> Unit) {
    val viewModel: TaskDetailViewModel = hiltViewModel()
    val task by viewModel.task.collectAsState()
    var confirmDelete by remember { mutableStateOf(false) }

    LaunchedEffect(taskId) { viewModel.load(taskId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Task details") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, null) } },
                actions = {
                    IconButton(onClick = { }) { Icon(Icons.Filled.Edit, null) }
                    IconButton(onClick = { confirmDelete = true }) { Icon(Icons.Filled.Delete, null) }
                }
            )
        }
    ) { padding ->
        task?.let { t ->
            Column(Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(t.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PriorityBadge(t.priority)
                    Text(t.category, style = MaterialTheme.typography.bodyLarge)
                }
                if (t.description.isNotBlank()) {
                    Text("Description", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    Text(t.description, style = MaterialTheme.typography.bodyMedium)
                }
                t.dueAtMillis?.let { Text("Due: ${formatDueDate(it)}", style = MaterialTheme.typography.bodyMedium) }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Checkbox(checked = t.isCompleted, onCheckedChange = { viewModel.toggle(t) })
                    Text(if (t.isCompleted) "Completed ✓" else "Mark as complete")
                }
            }
        } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Delete task?") },
            text = { Text("This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    task?.let { viewModel.delete(it) }
                    confirmDelete = false
                    onBack()
                }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("Cancel") } }
        )
    }
}
