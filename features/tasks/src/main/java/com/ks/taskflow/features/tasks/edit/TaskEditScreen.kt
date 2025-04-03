package com.ks.taskflow.features.tasks.edit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Label
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ks.taskflow.domain.model.Priority
import com.ks.taskflow.domain.model.TaskCategory
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Screen for creating and editing tasks.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditScreen(
    onNavigateBack: () -> Unit,
    viewModel: TaskEditViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Navigate back after successful save
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onNavigateBack()
        }
    }
    
    // Show error message
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (uiState.isNewTask) "Create Task" else "Edit Task"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (!uiState.isLoading && uiState.isValid && !uiState.isSaving) {
                FloatingActionButton(
                    onClick = { viewModel.saveTask() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Save task"
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Title
                OutlinedTextField(
                    value = uiState.title,
                    onValueChange = { viewModel.updateTitle(it) },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = uiState.title.isBlank()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Description
                OutlinedTextField(
                    value = uiState.description,
                    onValueChange = { viewModel.updateDescription(it) },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Priority
                PriorityDropdown(
                    selected = uiState.priority,
                    onPrioritySelected = { viewModel.updatePriority(it) }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Category
                CategoryDropdown(
                    selected = uiState.category,
                    onCategorySelected = { viewModel.updateCategory(it) }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Due date
                DueDatePicker(
                    dueDate = uiState.dueDate,
                    onDateSelected = { viewModel.updateDueDate(it) }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Tags
                OutlinedTextField(
                    value = uiState.tags,
                    onValueChange = { viewModel.updateTags(it) },
                    label = { Text("Tags (comma-separated)") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Label,
                            contentDescription = null
                        )
                    }
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Save button for larger touch area
                Button(
                    onClick = { viewModel.saveTask() },
                    enabled = uiState.isValid && !uiState.isSaving,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Task")
                }
                
                if (uiState.isSaving) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PriorityDropdown(
    selected: Priority,
    onPrioritySelected: (Priority) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = getPriorityDisplayName(selected),
            onValueChange = {},
            readOnly = true,
            label = { Text("Priority") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Priority.values().forEach { priority ->
                DropdownMenuItem(
                    text = { Text(getPriorityDisplayName(priority)) },
                    onClick = {
                        onPrioritySelected(priority)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryDropdown(
    selected: TaskCategory,
    onCategorySelected: (TaskCategory) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = getCategoryDisplayName(selected),
            onValueChange = {},
            readOnly = true,
            label = { Text("Category") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            TaskCategory.values().forEach { category ->
                DropdownMenuItem(
                    text = { Text(getCategoryDisplayName(category)) },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DueDatePicker(
    dueDate: LocalDateTime?,
    onDateSelected: (LocalDateTime?) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")
    
    // Date picker state
    val currentTime = dueDate ?: LocalDateTime.now()
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = currentTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )
    
    // Time picker state
    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.hour,
        initialMinute = currentTime.minute
    )
    
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = dueDate?.format(formatter) ?: "No due date",
            onValueChange = {},
            readOnly = true,
            label = { Text("Due Date & Time") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null
                )
            },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Text("Set", style = MaterialTheme.typography.labelMedium)
                }
            }
        )
        
        if (dueDate != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { onDateSelected(null) },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Clear Date")
            }
        }
    }
    
    // Date picker dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { dateMillis ->
                            val selectedDate = Instant.ofEpochMilli(dateMillis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            
                            val newDateTime = LocalDateTime.of(
                                selectedDate,
                                LocalTime.of(currentTime.hour, currentTime.minute)
                            )
                            
                            // First update the date, then show time picker
                            onDateSelected(newDateTime)
                            showDatePicker = false
                            showTimePicker = true
                        }
                    }
                ) {
                    Text("Next")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    
    // Time picker dialog
    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Select Time") },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    TimePicker(state = timePickerState)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val currentDateTime = dueDate ?: LocalDateTime.now()
                        val selectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                        val newDateTime = LocalDateTime.of(
                            currentDateTime.toLocalDate(),
                            selectedTime
                        )
                        
                        onDateSelected(newDateTime)
                        showTimePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

private fun getPriorityDisplayName(priority: Priority): String {
    return when (priority) {
        Priority.LOW -> "Low"
        Priority.MEDIUM -> "Medium"
        Priority.HIGH -> "High"
    }
}

private fun getCategoryDisplayName(category: TaskCategory): String {
    return when (category) {
        TaskCategory.PERSONAL -> "Personal"
        TaskCategory.WORK -> "Work"
        TaskCategory.SHOPPING -> "Shopping"
        TaskCategory.HEALTH -> "Health"
        TaskCategory.FINANCE -> "Finance"
        TaskCategory.OTHER -> "Other"
    }
} 