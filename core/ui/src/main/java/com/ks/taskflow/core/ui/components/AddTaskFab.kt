package com.ks.taskflow.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Floating action button for adding a new task.
 * 
 * @param onClick Action to perform when the FAB is clicked.
 * @param extended Whether to show the extended version with text.
 * @param modifier Modifier for customizing the layout.
 */
@Composable
fun AddTaskFab(
    onClick: () -> Unit,
    extended: Boolean = true,
    modifier: Modifier = Modifier
) {
    if (extended) {
        ExtendedFloatingActionButton(
            onClick = onClick,
            icon = {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Task"
                )
            },
            text = {
                Text(text = "Add Task")
            },
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            expanded = true,
            modifier = modifier.padding(bottom = 80.dp, end = 16.dp, start = 16.dp, top = 16.dp)
        )
    } else {
        FloatingActionButton(
            onClick = onClick,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = modifier.padding(bottom = 80.dp, end = 16.dp, start = 16.dp, top = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Task"
            )
        }
    }
} 