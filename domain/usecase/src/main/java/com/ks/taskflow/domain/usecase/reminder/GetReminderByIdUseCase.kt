package com.ks.taskflow.domain.usecase.reminder

import com.ks.taskflow.domain.model.Reminder
import com.ks.taskflow.domain.repository.ReminderRepository
import javax.inject.Inject

/**
 * Use case for retrieving a single reminder by its ID.
 */
class GetReminderByIdUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository
) {
    /**
     * Gets a reminder by its ID.
     *
     * @param id The ID of the reminder to retrieve.
     * @return The reminder if found, or null if not found.
     */
    suspend operator fun invoke(id: String): Reminder? {
        return reminderRepository.getReminderById(id)
    }
} 