package com.todoai.domain.usecase

import com.todoai.domain.model.Task
import javax.inject.Inject

class GetAllTasksUseCase @Inject constructor(private val repository: TaskRepository) {
    operator fun invoke() = repository.observeAll()
}

class GetFilteredTasksUseCase @Inject constructor(private val repository: TaskRepository) {
    operator fun invoke(
        query: String = "",
        priority: TaskPriorityFilter = TaskPriorityFilter.ALL,
        category: String = "All"
    ) = repository.observeFiltered(query, priority, category)
}

enum class TaskPriorityFilter { ALL, LOW, MEDIUM, HIGH }

class AddTaskUseCase @Inject constructor(private val repository: TaskRepository) {
    suspend operator fun invoke(task: Task) = repository.insert(task)
}

class DeleteTaskUseCase @Inject constructor(private val repository: TaskRepository) {
    suspend operator fun invoke(task: Task) = repository.delete(task)
}

class ToggleCompleteUseCase @Inject constructor(private val repository: TaskRepository) {
    suspend operator fun invoke(task: Task) =
        repository.update(task.copy(isCompleted = !task.isCompleted))
}

class UpdateTaskUseCase @Inject constructor(private val repository: TaskRepository) {
    suspend operator fun invoke(task: Task) = repository.update(task)
}

class GetTaskByIdUseCase @Inject constructor(private val repository: TaskRepository) {
    suspend operator fun invoke(id: Long) = repository.getById(id)
}

class GetCategoriesUseCase @Inject constructor(private val repository: TaskRepository) {
    operator fun invoke() = repository.observeCategories()
}
