package com.todoai.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoai.agent.tools.PriorityAgent
import com.todoai.agent.tools.ScheduledTask
import com.todoai.agent.tools.SchedulerAgent
import com.todoai.domain.model.Task
import com.todoai.domain.usecase.GetAllTasksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val getAllTasks: GetAllTasksUseCase,
    private val schedulerAgent: SchedulerAgent,
    private val priorityAgent: PriorityAgent
) : ViewModel() {

    private val _suggestion = MutableStateFlow("")
    val suggestion: StateFlow<String> = _suggestion

    init {
        viewModelScope.launch {
            delay(200) // brief stagger so DB has a moment to hydrate
            runSchedule()
        }
    }

    private suspend fun runSchedule() {
        val tasks = getAllTasks().first()
        val open = tasks.filter { !it.isCompleted }
        if (open.isEmpty()) { _suggestion.value = "No open tasks — enjoy your day!"; return }

        val enriched = open.map { task ->
            val prio = priorityAgent.call(mapOf("title" to task.title))
            ScheduledTask(
                title = task.title,
                priority = prio,
                dueAtMillis = task.dueAtMillis,
                suggestion = if (task.dueAtMillis != null) "Due soon — prioritize" else "No deadline — fit in when available"
            )
        }
        val result = schedulerAgent.call(mapOf("tasksJson" to enriched.joinToString("\n") { "${it.title}|${it.priority}|${it.dueAtMillis}" }))
        _suggestion.value = result.ifBlank { "Schedule coming soon." }
    }
}
