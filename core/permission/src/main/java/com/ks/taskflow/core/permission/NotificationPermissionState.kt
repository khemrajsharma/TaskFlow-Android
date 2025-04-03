package com.ks.taskflow.core.permission

/**
 * State for notification permission UI components.
 */
data class NotificationPermissionState(
    val shouldShowPermissionDialog: Boolean = false,
    val permissionDenied: Boolean = false
) 