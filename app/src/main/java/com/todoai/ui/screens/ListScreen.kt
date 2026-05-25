package com.todoai.ui.screens

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.todoai.ui.components.PriorityBadge
import com.todoai.ui.components.TaskCard
import com.todoai.ui.viewmodel.ListViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(navController: NavController) {
    val viewModel: ListViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* granted = handled in toasts */ }

    LaunchedEffect(Unit) {
        launcher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("TodoAI", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("add") }) {
                Icon(Icons.Default.Add, contentDescription = "Add task")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 12.dp)
        ) {
            OutlinedTextField(
                value = state.query,
                onValueChange = { viewModel.setQuery(it) },
                label = { Text("Search tasks…") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                FilterChip(
                    selected = state.priorityFilter == com.todoai.domain.usecase.TaskPriorityFilter.ALL,
                    onClick = { viewModel.setPriorityFilter(com.todoai.domain.usecase.TaskPriorityFilter.ALL) },
                    label = { Text("All") }
                )
                FilterChip(
                    selected = state.priorityFilter == com.todoai.domain.usecase.TaskPriorityFilter.HIGH,
                    onClick = { viewModel.setPriorityFilter(com.todoai.domain.usecase.TaskPriorityFilter.HIGH) },
                    label = { Text("High") }
                )
                FilterChip(
                    selected = state.priorityFilter == com.todoai.domain.usecase.TaskPriorityFilter.MEDIUM,
                    onClick = { viewModel.setPriorityFilter(com.todoai.domain.usecase.TaskPriorityFilter.MEDIUM) },
                    label = { Text("Medium") }
                )
                FilterChip(
                    selected = state.priorityFilter == com.todoai.domain.usecase.TaskPriorityFilter.LOW,
                    onClick = { viewModel.setPriorityFilter(com.todoai.domain.usecase.TaskPriorityFilter.LOW) },
                    label = { Text("Low") }
                )
            }

            Spacer(Modifier.height(8.dp))

            val displayList = state.tasks.filter {
                state.selectedCategory == "All" || it.category == state.selectedCategory
            }

            if (displayList.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No tasks yet — tap + to add one!", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(displayList, key = { it.id }) { task ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TaskCard(
                                task = task,
                                onChecked = { viewModel.toggleComplete(task) },
                                onClick = { navController.navigate("detail/${task.id}") },
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    viewModel.delete(task)

                                    scope.launch {
                                        snackbarHostState.showSnackbar("Task deleted — tap Undo to restore")
                                    }
                                }
                            ){
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}
