package com.todoai.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoai.domain.model.Task
import com.todoai.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ListState(
    val tasks: List<Task> = emptyList(),
    val categories: List<String> = emptyList(),
    val query: String = "",
    val priorityFilter: TaskPriorityFilter = TaskPriorityFilter.ALL,
    val selectedCategory: String = "All"
)

@HiltViewModel
class ListViewModel @Inject constructor(
    private val getFilteredTasks: GetFilteredTasksUseCase,
    private val toggleCompleteUseCase: ToggleCompleteUseCase,
    private val deleteTask: DeleteTaskUseCase,
    private val getCategories: GetCategoriesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ListState())
    val state: StateFlow<ListState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            getFilteredTasks().collect { list ->
                _state.value = _state.value.copy(tasks = list)
            }
        }
        viewModelScope.launch {
            getCategories().collect { cats ->
                _state.value = _state.value.copy(categories = listOf("All") + cats)
            }
        }
    }

    fun setQuery(q: String) {
        _state.value = _state.value.copy(query = q)
        refresh()
    }

    fun setPriorityFilter(filter: TaskPriorityFilter) {
        _state.value = _state.value.copy(priorityFilter = filter)
        refresh()
    }

    fun setCategory(category: String) {
        _state.value = _state.value.copy(selectedCategory = category)
        refresh()
    }


//    private val toggleCompleteUseCase : ToggleCompleteUseCase

    fun toggleComplete(task: Task) = viewModelScope.launch { toggleCompleteUseCase(task) }

    fun delete(task: Task) = viewModelScope.launch { deleteTask(task) }

    private fun refresh() {
        val s = _state.value
        viewModelScope.launch {
            getFilteredTasks(s.query, s.priorityFilter, s.selectedCategory).collect { list ->
                _state.value = s.copy(tasks = list)
            }
        }
    }
}
