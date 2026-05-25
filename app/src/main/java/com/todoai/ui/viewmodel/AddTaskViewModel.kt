package com.todoai.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoai.domain.model.Priority
import com.todoai.domain.model.Task
import com.todoai.domain.usecase.AddTaskUseCase
import com.todoai.domain.usecase.GetCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddTaskState(
    val isSaving: Boolean = false,
    val success: Boolean = false,
    val error: String? = null,
    val categories: List<String> = emptyList(),
    val selectedCategory: String = "General",
    val selectedPriority: Priority? = null
)

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val addTask: AddTaskUseCase,
    private val getCategories: GetCategoriesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AddTaskState())
    val state: StateFlow<AddTaskState> = _state

    init {
        viewModelScope.launch {
            getCategories().collect { cats ->
                _state.value = _state.value.copy(categories = if (cats.isEmpty()) listOf("General") else cats)
            }
        }
    }

    fun setPriority(priority: Priority) {
        _state.value = _state.value.copy(selectedPriority = priority, error = null)
    }

    fun setCategory(category: String) {
        _state.value = _state.value.copy(selectedCategory = category)
    }

    fun addTask(title: String, description: String, priority: Priority, category: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true, error = null, success = false)
            try {
                addTask(
                    Task(
                        title = title.trim(),
                        description = description.trim(),
                        priority = priority,
                        category = category
                    )
                )
                _state.value = _state.value.copy(isSaving = false, success = true)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isSaving = false, error = e.message)
            }
        }
    }
}
