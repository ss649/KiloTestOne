package com.todoai.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoai.agent.tools.SummaryAgent
import com.todoai.domain.model.Task
import com.todoai.domain.usecase.GetAllTasksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.first

@HiltViewModel
class SummaryViewModel @Inject constructor(
    private val getAllTasks: GetAllTasksUseCase,
    private val summaryAgent: SummaryAgent
) : ViewModel() {

    private val _summary = MutableStateFlow("")
    val summary: StateFlow<String> = _summary

    init {
        viewModelScope.launch {
            delay(200)
            val tasks = getAllTasks().first()
            val completed = tasks.filter { it.isCompleted }
            if (completed.isEmpty()) { _summary.value = "No completed tasks yet. Start crushing them!"; return@launch }
            val blob = completed.joinToString("\n") { "${it.title}|${it.priority.name}|${it.createdAtMillis}" }
            _summary.value = summaryAgent.call(mapOf("completedJson" to blob))
        }
    }
}
