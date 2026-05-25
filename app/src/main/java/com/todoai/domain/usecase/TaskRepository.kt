package com.todoai.domain.usecase

import com.todoai.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun observeAll(): Flow<List<Task>>
    fun observeFiltered(
        query: String,
        priority: TaskPriorityFilter,
        category: String
    ): Flow<List<Task>>

    fun observeCategories(): Flow<List<String>>
    suspend fun insert(task: Task): Long
    suspend fun update(task: Task)
    suspend fun delete(task: Task)
    suspend fun getById(id: Long): Task?
}
