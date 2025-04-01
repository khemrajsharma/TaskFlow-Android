package com.ks.taskflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ks.taskflow.core.navigation.TaskFlowDestinations
import com.ks.taskflow.core.navigation.TaskFlowNavHost
import com.ks.taskflow.core.navigation.TaskFlowNavigationActions
import com.ks.taskflow.core.ui.components.TaskFlowScaffold
import com.ks.taskflow.core.ui.theme.TaskFlowTheme
import com.ks.taskflow.features.reminders.list.RemindersScreen
import com.ks.taskflow.features.settings.SettingsScreen
import com.ks.taskflow.features.tasks.detail.TaskDetailScreen
import com.ks.taskflow.features.tasks.edit.TaskEditScreen
import com.ks.taskflow.features.tasks.list.TasksScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaskFlowApp()
        }
    }
}

/**
 * Main composable for the TaskFlow app.
 */
@Composable
fun TaskFlowApp() {
    TaskFlowTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val navController = rememberNavController()
            val navigationActions = remember(navController) {
                TaskFlowNavigationActions(navController)
            }
            
            // Track current route for bottom navigation visibility
            var currentRoute by remember { mutableStateOf("") }
            // Update current route when navigation changes
            val currentBackStackEntry by navController.currentBackStackEntryAsState()
            currentRoute = currentBackStackEntry?.destination?.route ?: ""
            
            // Determine whether to show the bottom navigation
            val showBottomNav = when (currentRoute) {
                TaskFlowDestinations.TASKS_ROUTE, 
                TaskFlowDestinations.REMINDERS_ROUTE, 
                TaskFlowDestinations.SETTINGS_ROUTE -> true
                else -> false
            }
            
            TaskFlowScaffold(
                navController = navController,
                showBottomNav = showBottomNav
            ) { paddingValues ->
                TaskFlowNavHost(
                    navController = navController,
                    onTasksScreen = {
                        TasksScreen(
                            onAddTask = { navigationActions.navigateToTaskEdit() },
                            onTaskClick = { taskId -> navigationActions.navigateToTaskDetail(taskId) }
                        )
                    },
                    onTaskDetailScreen = { taskId ->
                        TaskDetailScreen(
                            onNavigateBack = { navigationActions.navigateBack() },
                            onEditTask = { navigationActions.navigateToTaskEdit(it) },
                            onAddReminder = { navigationActions.navigateToReminderEdit(taskId = it) }
                        )
                    },
                    onTaskEditScreen = { taskId ->
                        TaskEditScreen(
                            onNavigateBack = { navigationActions.navigateBack() }
                        )
                    },
                    onRemindersScreen = {
                        RemindersScreen(
                            onAddReminder = { navigationActions.navigateToReminderEdit() },
                            onReminderClick = { reminderId -> navigationActions.navigateToReminderDetail(reminderId) }
                        )
                    },
                    onReminderDetailScreen = { reminderId ->
                        // Will be implemented
                        Text("Reminder Detail Screen")
                    },
                    onReminderEditScreen = { reminderId, taskId ->
                        // Will be implemented
                        Text("Reminder Edit Screen")
                    },
                    onSettingsScreen = {
                        SettingsScreen()
                    }
                )
            }
        }
    }
} 