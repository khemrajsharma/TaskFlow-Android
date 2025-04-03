package com.ks.taskflow.core.permission

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * ViewModel for managing notification permission state and actions.
 */
@HiltViewModel
class NotificationPermissionViewModel @Inject constructor(
    private val notificationPermissionManager: NotificationPermissionManager
) : ViewModel() {

    private val _permissionState = MutableStateFlow(NotificationPermissionState())
    val permissionState: StateFlow<NotificationPermissionState> = _permissionState.asStateFlow()

    /**
     * Check if notifications permission is required and update state accordingly.
     */
    fun checkNotificationPermission() {
        val hasPermission = notificationPermissionManager.hasNotificationPermission()
        _permissionState.update { state ->
            state.copy(
                shouldShowPermissionDialog = !hasPermission && !state.permissionDenied
            )
        }
    }

    /**
     * Handle the result of a permission request.
     * @param isGranted true if permission was granted, false otherwise.
     */
    fun onPermissionResult(isGranted: Boolean) {
        _permissionState.update { state ->
            state.copy(
                shouldShowPermissionDialog = false,
                permissionDenied = !isGranted
            )
        }
    }

    /**
     * Dismiss the permission dialog.
     */
    fun dismissPermissionDialog() {
        _permissionState.update { state ->
            state.copy(
                shouldShowPermissionDialog = false
            )
        }
    }
} 