package com.todoai.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.todoai.domain.model.Recurrence

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val priority: Priority = Priority.MEDIUM,
    val category: String = "General",
    val dueAtMillis: Long? = null,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val recurrence: Recurrence? = null
)
