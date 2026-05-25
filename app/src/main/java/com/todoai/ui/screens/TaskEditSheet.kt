package com.todoai.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.todoai.domain.model.Priority
import com.todoai.ui.viewmodel.TaskDetailViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditSheet(taskId: Long, viewModel: TaskDetailViewModel, onDismiss: () -> Unit) {
    val task by viewModel.task.collectAsState()
    var title by remember(task) { mutableStateOf(task?.title ?: "") }
    var desc by remember(task) { mutableStateOf(task?.description ?: "") }
    var priority by remember(task) { mutableStateOf(task?.priority ?: Priority.MEDIUM) }
    var saving by remember { mutableStateOf(false) }
    var scope = rememberCoroutineScope()
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Edit task", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            OutlinedTextField(title, { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(desc, { desc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), minLines = 3)

            val options = Priority.entries
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded, { expanded = it }) {
                OutlinedTextField(
                    value = priority.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Priority") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded, onDismissRequest = { expanded = false }) {
                    options.forEach { p ->
                        DropdownMenuItem(
                            text = { Text(p.name) },
                            onClick = { priority = p; expanded = false }
                        )
                    }
                }
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onDismiss) { Text("Cancel") }
                Spacer(Modifier.width(8.dp))
                Button(onClick = {
                    viewModel.update(taskId, title, desc, priority)
                    saving = true
                    scope.launch {
                        kotlinx.coroutines.delay(400)
                        onDismiss()
                    }
                }) { Text(if (saving) "Saving…" else "Save") }
            }
        }
    }
}
