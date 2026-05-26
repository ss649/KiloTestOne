package com.todoai.ui.screens

import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.todoai.agent.ParsedTask
import com.todoai.agent.TaskParserAgent
import com.todoai.domain.model.Priority
import com.todoai.ui.viewmodel.AddTaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(navController: NavController) {
    val viewModel: AddTaskViewModel = hiltViewModel()
    val parser = remember { TaskParserAgent() }
    val state by viewModel.state.collectAsState()

    var naturalInput by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var parsing by remember { mutableStateOf(false) }

    var expandedPriority by remember { mutableStateOf(false) }
    var expandedCategory by remember { mutableStateOf(false) }

    val speechLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val spoken = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
        spoken?.let { naturalInput = it}
    }

    fun parseFromInput(input: String, agent: TaskParserAgent, onParsed: (ParsedTask) -> Unit) {
        parsing = true
        val parsed = agent.parse(input)
        parsing = false
        if (parsed.title.isNotBlank()) {
            title = parsed.title
            description = parsed.description
            onParsed(parsed)
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Add Task", fontWeight = FontWeight.Bold) }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── AI natural-language field ─────────────────────────────────────
            OutlinedTextField(
                value = naturalInput,
                onValueChange = { value ->
                    naturalInput = value
                    if (value.length > 5) parseFromInput(value, parser) { }
                },
                label = { Text("Ask AI: \"Buy milk tomorrow #groceries !high\"") },
                trailingIcon = {
                    IconButton(onClick = {
                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                        }
                        speechLauncher.launch(intent)
                    }) {
                            Icon(Icons.Filled.Mic, contentDescription = "Voice input")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            if (parsing) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())

            // ── Structured fields ─────────────────────────────────────────────
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // Priority
            ExposedDropdownMenuBox(
                expanded = expandedPriority,
                onExpandedChange = { expandedPriority = it }
            ) {
                OutlinedTextField(
                    value = state.selectedPriority?.name ?: Priority.MEDIUM.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Priority") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedPriority) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expandedPriority, onDismissRequest = { expandedPriority = false }) {
                    Priority.entries.forEach { p ->
                        DropdownMenuItem(
                            text = { Text(p.name) },
                            onClick = {
                                viewModel.setPriority(p)
                                expandedPriority = false
                            }
                        )
                    }
                }
            }

            // Category
            ExposedDropdownMenuBox(
                expanded = expandedCategory,
                onExpandedChange = { expandedCategory = it }
            ) {
                OutlinedTextField(
                    value = state.selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedCategory) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expandedCategory, onDismissRequest = { expandedCategory = false }) {
                    state.categories.forEach { c ->
                        DropdownMenuItem(
                            text = { Text(c) },
                            onClick = {
                                viewModel.setCategory(c)
                                expandedCategory = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { navController.popBackStack() }) { Text("Cancel") }
                Spacer(Modifier.width(8.dp))
                Button(
                    enabled = state.selectedPriority != null,
                    onClick = {
                        viewModel.addTask(
                            title,
                            description,
                            state.selectedPriority!!,
                            state.selectedCategory
                        )
                        navController.popBackStack()
                    }
                ) {
                    if (state.isSaving) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                    else Text("Save task")
                }
            }

            if (state.error != null) {
                Text(state.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
            if (state.success) {
                Text("Task saved ✓", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
