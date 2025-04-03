package com.ks.taskflow.features.reminders.edit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ks.taskflow.domain.model.RepeatInterval
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Screen for creating and editing reminders.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderEditScreen(
    onNavigateBack: () -> Unit,
    viewModel: ReminderEditViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    // Dialog states
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showRepeatOptions by remember { mutableStateOf(false) }
    
    // Time picker state
    val currentTime = uiState.time ?: LocalDateTime.now()
    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.hour,
        initialMinute = currentTime.minute
    )
    
    // Date picker state
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = currentTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )
    
    // Navigate back if saved successfully
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onNavigateBack()
        }
    }
    
    // Show error messages
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { error ->
            scope.launch {
                snackbarHostState.showSnackbar(error)
                viewModel.clearError()
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
                            
                            val currentDateTime = uiState.time ?: LocalDateTime.now()
                            val newDateTime = LocalDateTime.of(
                                selectedDate,
                                currentDateTime.toLocalTime()
                            )
                            
                            viewModel.updateTime(newDateTime)
                        }
                        showDatePicker = false
                        showTimePicker = true // Move to time selection after date
                    }
                ) {
                    Text("OK")
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
        androidx.compose.material3.AlertDialog(
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
                        val selectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                        val currentDateTime = uiState.time ?: LocalDateTime.now()
                        val newDateTime = LocalDateTime.of(
                            currentDateTime.toLocalDate(),
                            selectedTime
                        )
                        
                        viewModel.updateTime(newDateTime)
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
    
    // Repeat options dialog
    if (showRepeatOptions) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showRepeatOptions = false },
            title = { Text("Repeat Options") },
            text = {
                Column {
                    RepeatInterval.values().forEach { interval ->
                        androidx.compose.material3.RadioButton(
                            selected = uiState.repeatInterval == interval,
                            onClick = {
                                viewModel.updateRepeatSettings(true, interval)
                                showRepeatOptions = false
                            }
                        )
                        Text(
                            text = interval.name,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showRepeatOptions = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(if (uiState.isNewReminder) "Create Reminder" else "Edit Reminder") 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Navigate Back"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Title field
                    OutlinedTextField(
                        value = uiState.title,
                        onValueChange = { viewModel.updateTitle(it) },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Message field
                    OutlinedTextField(
                        value = uiState.message,
                        onValueChange = { viewModel.updateMessage(it) },
                        label = { Text("Message") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Date & Time selection
                    Text(
                        text = "Reminder Time",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.weight(1f))
                        
                        Text(
                            text = uiState.time?.format(
                                DateTimeFormatter.ofPattern("MMM dd, yyyy - HH:mm")
                            ) ?: "Select date and time",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        
                        Spacer(modifier = Modifier.weight(1f))
                        
                        Button(onClick = { showDatePicker = true }) {
                            Text("Change")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Repeat settings
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Repeating Reminder",
                            style = MaterialTheme.typography.titleMedium
                        )
                        
                        Spacer(modifier = Modifier.weight(1f))
                        
                        Switch(
                            checked = uiState.isRepeating,
                            onCheckedChange = { isRepeating ->
                                viewModel.updateRepeatSettings(isRepeating)
                                if (isRepeating) {
                                    showRepeatOptions = true
                                }
                            }
                        )
                    }
                    
                    if (uiState.isRepeating) {
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Repeat,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            
                            Spacer(modifier = Modifier.weight(0.2f))
                            
                            Text(
                                text = "Repeats: ${uiState.repeatInterval?.name ?: "Not set"}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            
                            Spacer(modifier = Modifier.weight(1f))
                            
                            TextButton(onClick = { showRepeatOptions = true }) {
                                Text("Change")
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Enabled setting
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Enabled",
                            style = MaterialTheme.typography.titleMedium
                        )
                        
                        Spacer(modifier = Modifier.weight(1f))
                        
                        Switch(
                            checked = uiState.isEnabled,
                            onCheckedChange = { viewModel.updateEnabledState(it) }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Save button
                    Button(
                        onClick = { viewModel.saveReminder() },
                        enabled = uiState.isValid && !uiState.isSaving,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save Reminder")
                    }
                }
            }
            
            // Show loading spinner when saving
            if (uiState.isSaving) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
} 