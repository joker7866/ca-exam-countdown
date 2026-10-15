package com.cacountdown.app.ui.timetable

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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import com.cacountdown.app.data.local.entity.TimetableEntryEntity
import com.cacountdown.app.domain.model.Exam
import com.cacountdown.app.ui.common.SubjectDropdown
import com.cacountdown.app.ui.common.TimeOfDayPicker
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun TimetableScreen(viewModel: TimetableViewModel = hiltViewModel()) {
    val entries by viewModel.entries.collectAsState()
    val subjects by viewModel.subjects.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add timetable entry")
            }
        },
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize().padding(padding),
        ) {
            item { Text("My weekly timetable", style = MaterialTheme.typography.headlineSmall) }

            if (entries.isEmpty()) {
                item { Text("No study blocks yet. Tap + to plan your week.") }
            }

            for (day in 1..7) {
                val dayEntries = entries.filter { it.dayOfWeek == day }
                if (dayEntries.isNotEmpty()) {
                    item { Text(dayLabel(day), style = MaterialTheme.typography.titleMedium) }
                    items(dayEntries, key = TimetableEntryEntity::id) { entry ->
                        Card(Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column {
                                    Text(entry.title, style = MaterialTheme.typography.titleSmall)
                                    Text("${entry.startTime} – ${entry.endTime}", style = MaterialTheme.typography.bodySmall)
                                    viewModel.subjectName(entry.subjectCode)?.let {
                                        Text(it, style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                                IconButton(onClick = { viewModel.deleteEntry(entry) }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Delete")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddTimetableEntryDialog(
            subjects = subjects,
            onDismiss = { showAddDialog = false },
            onSave = { title, day, start, end, subjectCode ->
                viewModel.addEntry(title, day, start, end, subjectCode)
                showAddDialog = false
            },
        )
    }
}

@Composable
private fun AddTimetableEntryDialog(
    subjects: List<Exam>,
    onDismiss: () -> Unit,
    onSave: (String, Int, LocalTime, LocalTime, String?) -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var day by remember { mutableStateOf(1) }
    var start by remember { mutableStateOf(LocalTime.of(18, 0)) }
    var end by remember { mutableStateOf(LocalTime.of(19, 0)) }
    var selectedSubject by remember { mutableStateOf<Exam?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New study block") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("What are you studying?") }, singleLine = true)
                SubjectDropdown(subjects = subjects, selected = selectedSubject, onSelect = { selectedSubject = it })
                Text("Day", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    (1..7).forEach { d ->
                        FilterChip(selected = day == d, onClick = { day = d }, label = { Text(dayLabel(d).take(3)) })
                    }
                }
                Text("Start time", style = MaterialTheme.typography.labelLarge)
                TimeOfDayPicker(time = start, onChange = { start = it })
                Text("End time", style = MaterialTheme.typography.labelLarge)
                TimeOfDayPicker(time = end, onChange = { end = it })
            }
        },
        confirmButton = { TextButton(onClick = { onSave(title, day, start, end, selectedSubject?.subjectCode) }) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

private fun dayLabel(day: Int): String =
    DayOfWeek.of(day).getDisplayName(TextStyle.FULL, Locale.ENGLISH)
