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
                title = "Train my cat to do taxes",
                description = "Mr. Whiskers needs to learn spreadsheets and tax code by April",
                dueDate = now.plusDays(2),
                completed = false,
                priority = Priority.HIGH,
                category = TaskCategory.PERSONAL,
                tags = listOf("Pet", "Finance", "Impossible"),
                createdAt = now.minusDays(1),
                modifiedAt = now
            ),
            Task(
                id = UUID.randomUUID().toString(),
                title = "Find the TV remote",
                description = "Last seen under couch cushions, possibly migrated to another dimension",
                dueDate = now.plusDays(1),
                completed = false,
                priority = Priority.MEDIUM,
                category = TaskCategory.PERSONAL,
                tags = listOf("Home", "Mystery", "Daily"),
                createdAt = now.minusDays(1),
                modifiedAt = now
            ),
            Task(
                id = UUID.randomUUID().toString(),
                title = "Convince plants I'm a good caretaker",
                description = "Apologize to surviving houseplants and promise to do better",
                dueDate = now.plusDays(5),
                completed = false,
                priority = Priority.MEDIUM,
                category = TaskCategory.PERSONAL,
                tags = listOf("Plants", "Guilt", "Water"),
                createdAt = now.minusDays(2),
                modifiedAt = now
            ),
            Task(
                id = UUID.randomUUID().toString(),
                title = "Practice dad jokes",
                description = "Need to improve eye-roll ratio from family members",
                dueDate = now.plusDays(7),
                completed = false,
                priority = Priority.HIGH,
                category = TaskCategory.PERSONAL,
                tags = listOf("Humor", "Family", "Cringe"),
                createdAt = now.minusDays(3),
                modifiedAt = now
            ),
            Task(
                id = UUID.randomUUID().toString(),
                title = "Pretend to understand crypto",
                description = "Memorize phrases like 'blockchain', 'to the moon', and 'HODL'",
                dueDate = now.plusDays(10),
                completed = false,
                priority = Priority.LOW,
                category = TaskCategory.FINANCE,
                tags = listOf("Crypto", "Confusion", "Trendy"),
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
                    title = "Hey you! Remember to: ${task.title}",
                    message = when (index) {
                        0 -> "Your cat's financial future depends on this!"
                        1 -> "Have you checked between the couch cushions for the 17th time?"
                        2 -> "Your philodendron is giving you side-eye again"
                        3 -> "Your family is waiting to not laugh at new material"
                        4 -> "Time to nod confidently while understanding nothing"
                        else -> "Don't forget this extremely important thing!"
                    },
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
                        title = "FINAL WARNING: ${task.title}",
                        message = when (index) {
                            0 -> "The IRS is less forgiving than your cat will be"
                            2 -> "Your plants are plotting revenge. Water them NOW!"
                            4 -> "Quick! Someone asked about NFTs! Deploy buzzwords!"
                            else -> "This is your last chance to do the thing!"
                        },
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