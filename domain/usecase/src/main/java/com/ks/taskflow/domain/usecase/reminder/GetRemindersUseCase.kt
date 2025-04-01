package com.ks.taskflow.domain.usecase.reminder

import com.ks.taskflow.domain.model.Reminder
import com.ks.taskflow.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * Use case for retrieving reminders.
 */
class GetRemindersUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository
) {
    /**
     * Gets all reminders.
     */
    operator fun invoke(): Flow<List<Reminder>> {
        return reminderRepository.getAllReminders()
    }
    
    /**
     * Gets reminders for a specific task.
     */
    operator fun invoke(taskId: String): Flow<List<Reminder>> {
        return reminderRepository.getRemindersForTask(taskId)
    }
    
    /**
     * Gets upcoming reminders within a specified time range.
     */
    fun getUpcoming(days: Int): Flow<List<Reminder>> {
        val now = LocalDateTime.now()
        val future = now.plusDays(days.toLong())
        return reminderRepository.getUpcomingReminders(now, future)
    }
} 