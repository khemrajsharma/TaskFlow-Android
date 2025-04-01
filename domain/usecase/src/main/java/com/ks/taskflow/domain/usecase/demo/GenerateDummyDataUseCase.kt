package com.ks.taskflow.domain.usecase.demo

import com.ks.taskflow.domain.model.Priority
import com.ks.taskflow.domain.model.Reminder
import com.ks.taskflow.domain.model.Task
import com.ks.taskflow.domain.model.TaskCategory
import com.ks.taskflow.domain.repository.ReminderRepository
import com.ks.taskflow.domain.repository.TaskRepository
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

/**
 * Use case to generate dummy tasks and reminders for demonstration purposes.
 */
class GenerateDummyDataUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val reminderRepository: ReminderRepository
) {
    /**
     * Generates a predefined set of dummy tasks and reminders.
     */
    suspend operator fun invoke() {
        val tasks = generateDummyTasks()
        val reminders = generateDummyReminders(tasks)
        
        // Save each task with its reminders
        tasks.forEach { task ->
            taskRepository.saveTask(task)
        }
        
        reminders.forEach { reminder ->
            reminderRepository.saveReminder(reminder)
        }
    }
    
    /**
     * Generates a list of dummy tasks.
     */
    private fun generateDummyTasks(): List<Task> {
        val now = LocalDateTime.now()
        
        return listOf(
            Task(
                id = UUID.randomUUID().toString(),
                title = "Complete project proposal",
                description = "Finalize the project proposal for the client meeting",
                dueDate = now.plusDays(2),
                completed = false,
                priority = Priority.HIGH,
                category = TaskCategory.WORK,
                tags = listOf("Project", "Client", "Deadline"),
                createdAt = now.minusDays(1),
                modifiedAt = now
            ),
            Task(
                id = UUID.randomUUID().toString(),
                title = "Grocery shopping",
                description = "Buy milk, eggs, bread, and vegetables",
                dueDate = now.plusDays(1),
                completed = false,
                priority = Priority.MEDIUM,
                category = TaskCategory.SHOPPING,
                tags = listOf("Groceries", "Food"),
                createdAt = now.minusDays(1),
                modifiedAt = now
            ),
            Task(
                id = UUID.randomUUID().toString(),
                title = "Schedule dentist appointment",
                description = "Call the dentist to schedule a cleaning appointment",
                dueDate = now.plusDays(5),
                completed = false,
                priority = Priority.MEDIUM,
                category = TaskCategory.HEALTH,
                tags = listOf("Health", "Appointment"),
                createdAt = now.minusDays(2),
                modifiedAt = now
            ),
            Task(
                id = UUID.randomUUID().toString(),
                title = "Pay utility bills",
                description = "Pay electricity, water and internet bills",
                dueDate = now.plusDays(7),
                completed = false,
                priority = Priority.HIGH,
                category = TaskCategory.FINANCE,
                tags = listOf("Bills", "Monthly"),
                createdAt = now.minusDays(3),
                modifiedAt = now
            ),
            Task(
                id = UUID.randomUUID().toString(),
                title = "Clean garage",
                description = "Sort out tools and donate unused items",
                dueDate = now.plusDays(10),
                completed = false,
                priority = Priority.LOW,
                category = TaskCategory.PERSONAL,
                tags = listOf("Home", "Cleaning"),
                createdAt = now.minusDays(2),
                modifiedAt = now
            )
        )
    }
    
    /**
     * Generates dummy reminders for the provided tasks.
     */
    private fun generateDummyReminders(tasks: List<Task>): List<Reminder> {
        val now = LocalDateTime.now()
        val reminders = mutableListOf<Reminder>()
        
        tasks.forEachIndexed { index, task ->
            // Add 1-2 reminders per task
            reminders.add(
                Reminder(
                    id = UUID.randomUUID().toString(),
                    taskId = task.id,
                    title = "Reminder for: ${task.title}",
                    message = "Don't forget to ${task.title.lowercase()}",
                    time = task.dueDate?.minusHours(2) ?: now.plusDays(index.toLong() + 1),
                    isEnabled = true,
                    createdAt = now
                )
            )
            
            // Add a second reminder for some tasks
            if (index % 2 == 0) {
                reminders.add(
                    Reminder(
                        id = UUID.randomUUID().toString(),
                        taskId = task.id,
                        title = "Final reminder: ${task.title}",
                        message = "Last reminder to ${task.title.lowercase()}",
                        time = task.dueDate?.minusHours(1) ?: now.plusDays(index.toLong() + 1).plusHours(1),
                        isEnabled = true,
                        createdAt = now
                    )
                )
            }
        }
        
        return reminders
    }
} 