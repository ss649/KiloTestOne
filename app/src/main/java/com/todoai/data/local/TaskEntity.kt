package com.todoai.data.local

import androidx.room.*
import com.todoai.domain.model.Priority
import com.todoai.domain.model.Task

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val isCompleted: Boolean = false,
    val priority: String = "MEDIUM",
    val category: String = "General",
    val dueAtMillis: Long? = null,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val recurrence: String? = null
) {
    fun toDomain(): Task = Task(
        id = id,
        title = title,
        description = description,
        isCompleted = isCompleted,
        priority = Priority.valueOf(priority),
        category = category,
        dueAtMillis = dueAtMillis,
        createdAtMillis = createdAtMillis,
        recurrence = recurrence?.let { com.todoai.domain.model.Recurrence.valueOf(it) }
    )
}

fun Task.toEntity(): TaskEntity = TaskEntity(
    id = id,
    title = title,
    description = description,
    isCompleted = isCompleted,
    priority = priority.name,
    category = category,
    dueAtMillis = dueAtMillis,
    createdAtMillis = createdAtMillis,
    recurrence = recurrence?.name
)
