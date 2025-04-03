package com.ks.taskflow.features.reminders.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ks.taskflow.core.ui.components.EmptyState
import com.ks.taskflow.core.ui.components.ReminderCard
import com.ks.taskflow.core.ui.components.BottomNavSpacer
import kotlinx.coroutines.launch

/**
 * Screen displaying a list of reminders.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(
    onAddReminder: () -> Unit,
    onReminderClick: (String) -> Unit,
    viewModel: RemindersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var searchActive by remember { mutableStateOf(false) }
    var showFilterOptions by remember { mutableStateOf(false) }
    
    // Show error message if any
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { error ->
            scope.launch {
                snackbarHostState.showSnackbar(error)
                viewModel.clearError()
            }
        }
    }
    
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text("Reminders") },
                scrollBehavior = scrollBehavior,
                actions = {
                    if (uiState.hasFilters) {
                        IconButton(onClick = { viewModel.clearFilters() }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear Filters"
                            )
                        }
                    }
                    
                    IconButton(onClick = { showFilterOptions = !showFilterOptions }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter Reminders"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            androidx.compose.material3.FloatingActionButton(
                onClick = onAddReminder,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Reminder"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search bar
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = { query ->
                    viewModel.setSearchQuery(query)
                },
                onSearch = { query ->
                    viewModel.setSearchQuery(query)
                    searchActive = false
                },
                active = searchActive,
                onActiveChange = { searchActive = it },
                placeholder = { Text("Search reminders...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotBlank()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear Search"
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                // No suggestions needed
            }
            
            // Filter options
            AnimatedVisibility(
                visible = showFilterOptions,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Filter Reminders",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Status filters
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Status:",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        
                        FilterChip(
                            selected = uiState.filterEnabled == true,
                            onClick = {
                                viewModel.setEnabledFilter(
                                    if (uiState.filterEnabled == true) null else true
                                )
                            },
                            label = { Text("Active") }
                        )
                        
                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                        
                        FilterChip(
                            selected = uiState.filterEnabled == false,
                            onClick = {
                                viewModel.setEnabledFilter(
                                    if (uiState.filterEnabled == false) null else false
                                )
                            },
                            label = { Text("Disabled") }
                        )
                    }
                }
            }
            
            // Reminders list
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 100.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else if (uiState.isEmpty) {
                    EmptyState(
                        icon = Icons.Default.Notifications,
                        title = "No Reminders Found",
                        message = if (uiState.hasFilters) 
                            "Try adjusting your filters or search query" 
                        else 
                            "Tap the + button to add your first reminder",
                        actionLabel = if (uiState.hasFilters) "Clear Filters" else null,
                        onActionClick = if (uiState.hasFilters) {{ viewModel.clearFilters() }} else null
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = uiState.reminders,
                            key = { it.id }
                        ) { reminder ->
                            ReminderCard(
                                reminder = reminder,
                                onEnableChange = { isEnabled ->
                                    viewModel.toggleReminderStatus(reminder.id, isEnabled)
                                },
                                onDeleteClick = {
                                    viewModel.deleteReminder(reminder.id)
                                },
                                onClick = { onReminderClick(reminder.id) },
                                modifier = Modifier
                                    .padding(vertical = 4.dp)
                                    .fillMaxWidth()
                            )
                        }
                        // Add bottom space for FAB
                        item { BottomNavSpacer() }
                    }
                }
            }
        }
    }
} 