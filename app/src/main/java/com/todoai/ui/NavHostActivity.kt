package com.todoai.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.todoai.ui.screens.AddTaskScreen
import com.todoai.ui.screens.ListScreen
import com.todoai.ui.screens.ProductivitySummaryScreen
import com.todoai.ui.screens.ScheduleScreen
import com.todoai.ui.screens.taskDetailScreen
import com.todoai.ui.viewmodel.ListViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun NavHostActivity() {
    val navController = rememberNavController()
    val listVm: ListViewModel = hiltViewModel()
    val state by listVm.state.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    MaterialTheme {
        Surface {
            Scaffold(
                bottomBar = {
                    NavigationBar {
                        val items = listOf(
                            Triple("list", "Tasks", Icons.Default.List),
                            Triple("schedule", "Schedule", Icons.Default.Schedule),
                            Triple("summary", "Summary", Icons.Default.BarChart),
                            Triple("add", "Add", Icons.Default.Add),
                        )
                        items.forEach { (route, label, icon) ->
                            NavigationBarItem(
                                icon = { Icon(icon, label) },
                                label = null,
                                selected = currentDestination?.hierarchy?.any { it.route == route } == true,
                                onClick = {
                                    navController.navigate(route) {
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = "list",
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable("list")      { ListScreen(navController) }
                    composable("schedule")  { ScheduleScreen() }
                    composable("summary")   { ProductivitySummaryScreen() }
                    composable("add")       { AddTaskScreen(navController) }
                    composable("detail/{id}") { backStackEntry ->
                        val id = backStackEntry.arguments?.getString("id")?.toLongOrNull() ?: 0L
                        taskDetailScreen(id, onBack = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}
