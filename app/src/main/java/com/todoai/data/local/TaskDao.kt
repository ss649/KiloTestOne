package com.todoai.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY dueAtMillis ASC, createdAtMillis DESC")
    fun observeAll(): Flow<List<TaskEntity>>

    @Query("""
        SELECT * FROM tasks
        WHERE (:query = '' OR title LIKE '%' || :query || '%'
               OR description LIKE '%' || :query || '%')
          AND (:priority = 'ALL' OR priority = :priority)
          AND (:category = 'All' OR category = :category)
        ORDER BY dueAtMillis ASC, createdAtMillis DESC
    """)
    fun observeFiltered(
        query: String,
        priority: String,
        category: String
    ): Flow<List<TaskEntity>>

    @Query("SELECT DISTINCT category FROM tasks ORDER BY category ASC")
    fun observeCategories(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: TaskEntity): Long

    @Update
    suspend fun update(entity: TaskEntity)

    @Delete
    suspend fun delete(entity: TaskEntity)

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getById(id: Long): TaskEntity?
}
