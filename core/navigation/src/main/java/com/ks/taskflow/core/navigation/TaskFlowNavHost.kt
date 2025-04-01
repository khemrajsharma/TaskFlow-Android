package com.ks.taskflow.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

/**
 * Navigation functions for the different screens.
 */
class TaskFlowNavigationActions(private val navController: NavHostController) {
    fun navigateToTasks() {
        navController.navigate(TaskFlowDestinations.TASKS_ROUTE) {
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
    
    fun navigateToTaskDetail(taskId: String) {
        navController.navigate("${TaskFlowDestinations.TASK_DETAIL_ROUTE}/$taskId")
    }
    
    fun navigateToTaskEdit(taskId: String = "") {
        val route = if (taskId.isBlank()) {
            TaskFlowDestinations.TASK_EDIT_ROUTE
        } else {
            "${TaskFlowDestinations.TASK_EDIT_ROUTE}/$taskId"
        }
        navController.navigate(route)
    }
    
    fun navigateToReminders() {
        navController.navigate(TaskFlowDestinations.REMINDERS_ROUTE) {
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
    
    fun navigateToReminderDetail(reminderId: String) {
        navController.navigate("${TaskFlowDestinations.REMINDER_DETAIL_ROUTE}/$reminderId")
    }
    
    fun navigateToReminderEdit(reminderId: String = "", taskId: String = "") {
        val route = if (reminderId.isBlank()) {
            if (taskId.isBlank()) {
                TaskFlowDestinations.REMINDER_EDIT_ROUTE
            } else {
                "${TaskFlowDestinations.REMINDER_EDIT_ROUTE}?taskId=$taskId"
            }
        } else {
            "${TaskFlowDestinations.REMINDER_EDIT_ROUTE}/$reminderId"
        }
        navController.navigate(route)
    }
    
    fun navigateToSettings() {
        navController.navigate(TaskFlowDestinations.SETTINGS_ROUTE)
    }
    
    fun navigateBack() {
        navController.popBackStack()
    }
}

/**
 * Main navigation setup for the app.
 */
@Composable
fun TaskFlowNavHost(
    navController: NavHostController,
    onTasksScreen: @Composable () -> Unit,
    onTaskDetailScreen: @Composable (String) -> Unit,
    onTaskEditScreen: @Composable (String) -> Unit,
    onRemindersScreen: @Composable () -> Unit,
    onReminderDetailScreen: @Composable (String) -> Unit,
    onReminderEditScreen: @Composable (String, String) -> Unit,
    onSettingsScreen: @Composable () -> Unit,
    startDestination: String = TaskFlowDestinations.TASKS_ROUTE
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(TaskFlowDestinations.TASKS_ROUTE) {
            onTasksScreen()
        }
        
        composable(
            route = TaskFlowDestinations.TASK_DETAIL_FULL_ROUTE,
            arguments = listOf(
                navArgument(TaskFlowDestinations.TASK_DETAIL_ARG_TASK_ID) {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            val taskId = entry.arguments?.getString(TaskFlowDestinations.TASK_DETAIL_ARG_TASK_ID) ?: ""
            onTaskDetailScreen(taskId)
        }
        
        composable(
            route = TaskFlowDestinations.TASK_EDIT_ROUTE
        ) {
            onTaskEditScreen("")
        }
        
        composable(
            route = TaskFlowDestinations.TASK_EDIT_FULL_ROUTE,
            arguments = listOf(
                navArgument(TaskFlowDestinations.TASK_EDIT_ARG_TASK_ID) {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            val taskId = entry.arguments?.getString(TaskFlowDestinations.TASK_EDIT_ARG_TASK_ID) ?: ""
            onTaskEditScreen(taskId)
        }
        
        composable(TaskFlowDestinations.REMINDERS_ROUTE) {
            onRemindersScreen()
        }
        
        composable(
            route = TaskFlowDestinations.REMINDER_DETAIL_FULL_ROUTE,
            arguments = listOf(
                navArgument(TaskFlowDestinations.REMINDER_DETAIL_ARG_REMINDER_ID) {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            val reminderId = entry.arguments?.getString(TaskFlowDestinations.REMINDER_DETAIL_ARG_REMINDER_ID) ?: ""
            onReminderDetailScreen(reminderId)
        }
        
        composable(
            route = TaskFlowDestinations.REMINDER_EDIT_ROUTE
        ) {
            onReminderEditScreen("", "")
        }
        
        composable(
            route = "${TaskFlowDestinations.REMINDER_EDIT_ROUTE}?taskId={${TaskFlowDestinations.REMINDER_EDIT_ARG_TASK_ID}}",
            arguments = listOf(
                navArgument(TaskFlowDestinations.REMINDER_EDIT_ARG_TASK_ID) {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                }
            )
        ) { entry ->
            val taskId = entry.arguments?.getString(TaskFlowDestinations.REMINDER_EDIT_ARG_TASK_ID) ?: ""
            onReminderEditScreen("", taskId)
        }
        
        composable(
            route = TaskFlowDestinations.REMINDER_EDIT_FULL_ROUTE,
            arguments = listOf(
                navArgument(TaskFlowDestinations.REMINDER_EDIT_ARG_REMINDER_ID) {
                    type = NavType.StringType
                },
                navArgument(TaskFlowDestinations.REMINDER_EDIT_ARG_TASK_ID) {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                }
            )
        ) { entry ->
            val reminderId = entry.arguments?.getString(TaskFlowDestinations.REMINDER_EDIT_ARG_REMINDER_ID) ?: ""
            val taskId = entry.arguments?.getString(TaskFlowDestinations.REMINDER_EDIT_ARG_TASK_ID) ?: ""
            onReminderEditScreen(reminderId, taskId)
        }
        
        composable(TaskFlowDestinations.SETTINGS_ROUTE) {
            onSettingsScreen()
        }
    }
} 