package com.ks.taskflow.domain.usecase.task

import com.ks.taskflow.domain.model.Task
import com.ks.taskflow.domain.repository.TaskRepository
import javax.inject.Inject

/**
 * Use case for retrieving a single task by its ID.
 */
class GetTaskByIdUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    /**
     * Gets a task by its ID.
     *
     * @param id The ID of the task to retrieve.
     * @return The task if found, or null if not found.
     */
    suspend operator fun invoke(id: String): Task? {
        return taskRepository.getTaskById(id)
    }
} 