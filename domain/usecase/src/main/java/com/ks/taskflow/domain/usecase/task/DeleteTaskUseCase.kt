package com.ks.taskflow.domain.usecase.task

import com.ks.taskflow.domain.repository.ReminderRepository
import com.ks.taskflow.domain.repository.TaskRepository
import javax.inject.Inject

/**
 * Use case for deleting a task and its associated reminders.
 */
class DeleteTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val reminderRepository: ReminderRepository
) {
    /**
     * Deletes a task and its associated reminders.
     *
     * @param id The ID of the task to delete.
     */
    suspend operator fun invoke(id: String) {
        // Delete associated reminders first
        reminderRepository.deleteRemindersForTask(id)
        
        // Then delete the task
        taskRepository.deleteTask(id)
    }
} 