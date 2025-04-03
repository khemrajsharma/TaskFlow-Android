package com.ks.taskflow.core.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ks.taskflow.core.navigation.TaskFlowDestinations
import com.ks.taskflow.core.ui.R

/**
 * Main scaffold for the TaskFlow app with bottom navigation.
 */
@Composable
fun TaskFlowScaffold(
    navController: NavController,
    showBottomNav: Boolean = true,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                TaskFlowBottomNavigation(navController)
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        content(paddingValues)
    }
}

/**
 * Bottom navigation for the TaskFlow app.
 */
@Composable
private fun TaskFlowBottomNavigation(navController: NavController) {
    val items = listOf(
        BottomNavItem(
            route = TaskFlowDestinations.TASKS_ROUTE,
            titleResId = R.string.tasks,
            selectedIcon = Icons.Filled.CheckCircle,
            unselectedIcon = Icons.Outlined.CheckCircle
        ),
        BottomNavItem(
            route = TaskFlowDestinations.REMINDERS_ROUTE,
            titleResId = R.string.reminders,
            selectedIcon = Icons.Filled.Notifications,
            unselectedIcon = Icons.Outlined.Notifications
        ),
        BottomNavItem(
            route = TaskFlowDestinations.SETTINGS_ROUTE,
            titleResId = R.string.settings,
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings
        )
    )
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: ""
    
    NavigationBar {
        items.forEach { item ->
            val selected = currentRoute == item.route
            
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = stringResource(id = item.titleResId)
                    )
                },
                label = { Text(text = stringResource(id = item.titleResId)) },
                selected = selected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

/**
 * Data class for bottom navigation items.
 */
private data class BottomNavItem(
    val route: String,
    val titleResId: Int,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector
) 