package com.ks.taskflow.core.permission

import android.Manifest
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.ks.taskflow.core.permission.ui.NotificationPermissionDialog

/**
 * Composable that handles notification permission request flow.
 * It checks if the permission is needed, shows a dialog, and requests the permission.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NotificationPermissionHandler(
    viewModel: NotificationPermissionViewModel = hiltViewModel()
) {
    val permissionState by viewModel.permissionState.collectAsState()
    
    // Only request permission on Android 13+ (API level 33)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val notificationPermissionState = rememberPermissionState(
            Manifest.permission.POST_NOTIFICATIONS
        )
        
        // Check permission status when the composable is first launched
        LaunchedEffect(true) {
            viewModel.checkNotificationPermission()
        }
        
        // Handle permission result changes
        LaunchedEffect(notificationPermissionState.status.isGranted) {
            viewModel.onPermissionResult(notificationPermissionState.status.isGranted)
        }
        
        // Show permission dialog if needed
        if (permissionState.shouldShowPermissionDialog) {
            NotificationPermissionDialog(
                onDismiss = {
                    viewModel.dismissPermissionDialog()
                },
                onRequestPermission = {
                    notificationPermissionState.launchPermissionRequest()
                }
            )
        }
    }
} 