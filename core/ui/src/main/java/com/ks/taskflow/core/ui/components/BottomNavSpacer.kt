package com.ks.taskflow.core.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Utility composable to add bottom spacing in scrollable content
 * to ensure content is not hidden behind the bottom navigation bar.
 */
@Composable
fun BottomNavSpacer() {
    Spacer(modifier = Modifier.height(100.dp))
} 