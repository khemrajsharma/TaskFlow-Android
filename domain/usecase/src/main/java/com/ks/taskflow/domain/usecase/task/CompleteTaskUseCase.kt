package com.ks.taskflow.domain.usecase.task

import com.ks.taskflow.domain.repository.TaskRepository
import javax.inject.Inject

/**
 * Use case for updating the completion status of a task.
 */
class CompleteTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    /**
     * Updates the completion status of a task.
     *
     * @param id The ID of the task to update.
     * @param completed The new completion status.
     */
    suspend operator fun invoke(id: String, completed: Boolean) {
        taskRepository.updateTaskCompletion(id, completed)
    }
} 