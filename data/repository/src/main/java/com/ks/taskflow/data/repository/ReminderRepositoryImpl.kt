package com.ks.taskflow.data.repository

import com.ks.taskflow.data.local.dao.ReminderDao
import com.ks.taskflow.data.local.entity.ReminderEntity
import com.ks.taskflow.domain.model.Reminder
import com.ks.taskflow.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * Implementation of the ReminderRepository interface.
 */
class ReminderRepositoryImpl @Inject constructor(
    private val reminderDao: ReminderDao
) : ReminderRepository {
    
    private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    
    override fun getReminders(): Flow<List<Reminder>> {
        return reminderDao.getAllReminders().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override suspend fun getReminderById(id: String): Reminder? {
        return reminderDao.getReminderById(id)?.toDomain()
    }
    
    override fun getReminderByIdFlow(id: String): Flow<Reminder?> {
        return reminderDao.getReminderByIdFlow(id).map { it?.toDomain() }
    }
    
    override fun getRemindersByTaskId(taskId: String): Flow<List<Reminder>> {
        return reminderDao.getRemindersByTaskId(taskId).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun getUpcomingReminders(fromTime: LocalDateTime, toTime: LocalDateTime): Flow<List<Reminder>> {
        val fromTimeStr = fromTime.format(dateTimeFormatter)
        val toTimeStr = toTime.format(dateTimeFormatter)
        return reminderDao.getUpcomingReminders(fromTimeStr, toTimeStr).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun getRemindersForNext24Hours(): Flow<List<Reminder>> {
        val now = LocalDateTime.now()
        val tomorrow = now.plusDays(1)
        return getUpcomingReminders(now, tomorrow)
    }
    
    override fun getRemindersForNextWeek(): Flow<List<Reminder>> {
        val now = LocalDateTime.now()
        val nextWeek = now.plusDays(7)
        return getUpcomingReminders(now, nextWeek)
    }
    
    override suspend fun saveReminder(reminder: Reminder) {
        val reminderEntity = ReminderEntity.fromDomain(reminder)
        reminderDao.insertReminder(reminderEntity)
    }
    
    override suspend fun updateReminderStatus(reminderId: String, isEnabled: Boolean) {
        reminderDao.updateReminderStatus(reminderId, isEnabled)
    }
    
    override suspend fun deleteReminder(reminderId: String) {
        reminderDao.deleteReminderById(reminderId)
    }
    
    override suspend fun deleteRemindersByTaskId(taskId: String) {
        reminderDao.deleteRemindersByTaskId(taskId)
    }
} 