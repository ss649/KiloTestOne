package com.todoai.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoai.domain.model.Priority
import com.todoai.domain.model.Task
import com.todoai.domain.usecase.ToggleCompleteUseCase
import com.todoai.domain.usecase.UpdateTaskUseCase
import com.todoai.domain.usecase.GetTaskByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val getTaskById: GetTaskByIdUseCase,
    private val toggleComplete: ToggleCompleteUseCase,
    private val updateTask: UpdateTaskUseCase
) : ViewModel() {

    private val _task = MutableStateFlow<Task?>(null)
    val task: StateFlow<Task?> = _task

    fun load(id: Long) = viewModelScope.launch {
        _task.value = getTaskById(id)
    }

    fun toggle(task: Task) = viewModelScope.launch { toggleComplete(task) }

    fun update(id: Long, title: String, description: String, priority: Priority) = viewModelScope.launch {
        val current = _task.value ?: return@launch
        updateTask(current.copy(title = title, description = description, priority = priority))
    }

    fun delete(task: Task) = viewModelScope.launch { toggleComplete(task) }
}
