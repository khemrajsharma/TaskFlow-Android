package com.ks.taskflow.domain.usecase.reminder

import com.ks.taskflow.domain.repository.ReminderRepository
import javax.inject.Inject

/**
 * Use case for enabling or disabling a reminder.
 */
class UpdateReminderStatusUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository
) {
    /**
     * Updates the enabled status of a reminder.
     *
     * @param id The ID of the reminder to update.
     * @param isEnabled The new enabled status.
     */
    suspend operator fun invoke(id: String, isEnabled: Boolean) {
        reminderRepository.updateReminderStatus(id, isEnabled)
    }
} 