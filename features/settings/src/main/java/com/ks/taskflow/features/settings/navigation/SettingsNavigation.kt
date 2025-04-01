package com.ks.taskflow.features.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.ks.taskflow.features.settings.SettingsScreen

const val settingsRoute = "settings_route"

/**
 * Navigate to the settings screen.
 */
fun NavController.navigateToSettings(navOptions: NavOptions? = null) {
    this.navigate(settingsRoute, navOptions)
}

/**
 * Settings screen navigation graph.
 */
fun NavGraphBuilder.settingsScreen() {
    composable(route = settingsRoute) {
        SettingsScreen()
    }
} 