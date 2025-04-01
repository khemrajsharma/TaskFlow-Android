package com.ks.taskflow.domain.usecase.di

import com.ks.taskflow.domain.repository.ReminderRepository
import com.ks.taskflow.domain.repository.TaskRepository
import com.ks.taskflow.domain.usecase.demo.GenerateDummyDataUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    /**
     * Provides the GenerateDummyDataUseCase instance.
     */
    @Singleton
    @Provides
    fun provideGenerateDummyDataUseCase(
        taskRepository: TaskRepository,
        reminderRepository: ReminderRepository
    ): GenerateDummyDataUseCase {
        return GenerateDummyDataUseCase(taskRepository, reminderRepository)
    }
} 