package com.ks.taskflow.core.permission.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Dialog to request notification permission from the user.
 * 
 * @param onDismiss Callback when the dialog is dismissed without requesting permission.
 * @param onRequestPermission Callback when the user wants to request permission.
 * @param modifier Modifier for the dialog.
 */
@Composable
fun NotificationPermissionDialog(
    onDismiss: () -> Unit,
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Enable Notifications") },
        text = { 
            Text(
                text = "TaskFlow needs notification permission to remind you about your tasks. " +
                        "Would you like to enable notifications?"
            )
        },
        confirmButton = {
            TextButton(onClick = onRequestPermission) {
                Text("Enable")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Not Now")
            }
        },
        modifier = modifier
    )
} 