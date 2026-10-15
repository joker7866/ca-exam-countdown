package com.cacountdown.app.ui.alarms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cacountdown.app.data.local.entity.AlarmEntity
import com.cacountdown.app.domain.model.Exam
import com.cacountdown.app.ui.common.SubjectDropdown
import com.cacountdown.app.ui.common.TimeOfDayPicker
import java.time.LocalTime

@Composable
fun AlarmsScreen(viewModel: AlarmsViewModel = hiltViewModel()) {
    val alarms by viewModel.alarms.collectAsState()
    val subjects by viewModel.subjects.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add alarm")
            }
        },
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize().padding(padding),
        ) {
            item { Text("Alarms", style = MaterialTheme.typography.headlineSmall) }

            if (alarms.isEmpty()) {
                item { Text("No alarms set. Tap + to add a study reminder.") }
            }

            items(alarms, key = AlarmEntity::id) { alarm ->
                Card(Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text("%02d:%02d".format(alarm.hour, alarm.minute), style = MaterialTheme.typography.headlineSmall)
                            Text(alarm.label, style = MaterialTheme.typography.bodyMedium)
                            Text(if (alarm.repeatDaily) "Repeats daily" else "One time", style = MaterialTheme.typography.bodySmall)
                            viewModel.subjectName(alarm.subjectCode)?.let {
                                Text(it, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Switch(checked = alarm.isEnabled, onCheckedChange = { viewModel.toggleEnabled(alarm) })
                            IconButton(onClick = { viewModel.deleteAlarm(alarm) }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete alarm")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddAlarmDialog(
            subjects = subjects,
            onDismiss = { showAddDialog = false },
            onSave = { label, time, repeatDaily, subjectCode ->
                viewModel.addAlarm(label, time, repeatDaily, subjectCode)
                showAddDialog = false
            },
        )
    }
}

@Composable
private fun AddAlarmDialog(subjects: List<Exam>, onDismiss: () -> Unit, onSave: (String, LocalTime, Boolean, String?) -> Unit) {
    var label by remember { mutableStateOf("") }
    var time by remember { mutableStateOf(LocalTime.of(7, 0)) }
    var repeatDaily by remember { mutableStateOf(true) }
    var selectedSubject by remember { mutableStateOf<Exam?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New alarm") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = label, onValueChange = { label = it }, label = { Text("Label") }, singleLine = true)
                SubjectDropdown(subjects = subjects, selected = selectedSubject, onSelect = { selectedSubject = it })
                TimeOfDayPicker(time = time, onChange = { time = it })
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = repeatDaily, onCheckedChange = { repeatDaily = it })
                    Text("Repeat daily")
                }
            }
        },
        confirmButton = { TextButton(onClick = { onSave(label, time, repeatDaily, selectedSubject?.subjectCode) }) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}
