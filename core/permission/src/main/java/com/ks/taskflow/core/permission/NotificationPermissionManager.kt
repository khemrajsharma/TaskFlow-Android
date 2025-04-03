package com.ks.taskflow.core.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager for handling notification permissions.
 */
@Singleton
class NotificationPermissionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /**
     * Check if notification permission is granted.
     * On Android 13+ (API level 33), this checks for POST_NOTIFICATIONS permission.
     * On older versions, this returns true as notifications don't require runtime permission.
     * @return true if permission is granted, false otherwise.
     */
    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // For older versions, permission was granted at install time
            true
        }
    }

    /**
     * Determines if the app should show the notification permission rationale.
     * @return true if the app should show the rationale, false otherwise.
     */
    fun shouldShowRationale(): Boolean {
        // This is usually called from an Activity context, not the Application context
        // This will be used in the ViewModel to determine if we need to show a rationale dialog
        return false
    }
} 