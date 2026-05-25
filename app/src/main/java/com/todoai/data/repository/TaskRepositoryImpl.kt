package com.todoai.data.repository

import com.todoai.data.local.TaskDao
import com.todoai.data.local.TaskEntity
import com.todoai.data.local.toEntity
//import com.todoai.data.local.toDomain
import com.todoai.domain.model.Task
import com.todoai.domain.usecase.TaskPriorityFilter
import com.todoai.domain.usecase.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepositoryImpl(
    private val dao: TaskDao
) : TaskRepository {

    override fun observeAll(): Flow<List<Task>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeFiltered(
        query: String,
        priority: TaskPriorityFilter,
        category: String
    ): Flow<List<Task>> =
        dao.observeFiltered(
            query = query,
            priority = if (priority == TaskPriorityFilter.ALL) "ALL" else priority.name,
            category = if (category == "All") "All" else category
        ).map { list -> list.map { it.toDomain() } }

    override fun observeCategories(): Flow<List<String>> =
        dao.observeCategories()

    override suspend fun insert(task: Task): Long =
        dao.insert(task.toEntity()).toLong()

    override suspend fun update(task: Task) =
        dao.update(task.toEntity())

    override suspend fun delete(task: Task) =
        dao.delete(task.toEntity())

    override suspend fun getById(id: Long): Task? =
        dao.getById(id)?.toDomain()
}
