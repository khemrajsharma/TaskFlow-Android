package com.ks.taskflow.domain.usecase.task

import com.ks.taskflow.domain.model.Task
import com.ks.taskflow.domain.repository.TaskRepository
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

/**
 * Use case for saving (creating or updating) a task.
 */
class SaveTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    /**
     * Saves a task, generating a new ID if it's a new task.
     *
     * @param task The task to save.
     * @return The ID of the saved task.
     */
    suspend operator fun invoke(task: Task): String {
        val taskToSave = if (task.id.isBlank()) {
            task.copy(
                id = UUID.randomUUID().toString(),
                createdAt = LocalDateTime.now(),
                modifiedAt = LocalDateTime.now()
            )
        } else {
            task.copy(modifiedAt = LocalDateTime.now())
        }
        
        taskRepository.saveTask(taskToSave)
        return taskToSave.id
    }
} 