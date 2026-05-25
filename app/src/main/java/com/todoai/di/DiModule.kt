package com.todoai.di

import android.content.Context
import androidx.room.Room
import com.todoai.data.local.AppDatabase
import com.todoai.data.local.TaskDao
import com.todoai.data.repository.TaskRepositoryImpl
import com.todoai.domain.usecase.*
import com.todoai.domain.usecase.TaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "todoai_db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides @Singleton
    fun provideTaskDao(db: AppDatabase): TaskDao = db.taskDao()
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides @Singleton
    fun provideTaskRepository(dao: TaskDao): TaskRepository =
        TaskRepositoryImpl(dao)
}

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides @Singleton
    fun provideGetAllTasks(repo: TaskRepository) = GetFilteredTasksUseCase(repo)

    @Provides @Singleton
    fun provideAddTask(repo: TaskRepository) = AddTaskUseCase(repo)

    @Provides @Singleton
    fun provideDeleteTask(repo: TaskRepository) = DeleteTaskUseCase(repo)

    @Provides @Singleton
    fun provideToggleComplete(repo: TaskRepository) = ToggleCompleteUseCase(repo)

    @Provides @Singleton
    fun provideUpdateTask(repo: TaskRepository) = UpdateTaskUseCase(repo)

    @Provides @Singleton
    fun provideGetTaskById(repo: TaskRepository) = GetTaskByIdUseCase(repo)

    @Provides @Singleton
    fun provideGetCategories(repo: TaskRepository) = GetCategoriesUseCase(repo)
}
