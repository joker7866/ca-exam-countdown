package com.cacountdown.app.ui.schedule

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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cacountdown.app.data.local.entity.ActivityType
import com.cacountdown.app.data.local.entity.PlannedActivityEntity
import com.cacountdown.app.domain.model.Exam
import com.cacountdown.app.ui.common.SubjectDropdown
import com.cacountdown.app.ui.common.TimeOfDayPicker
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun ScheduleScreen(viewModel: ScheduleViewModel = hiltViewModel()) {
    val activities by viewModel.activities.collectAsState()
    val subjects by viewModel.subjects.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    val today = viewModel.today()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Schedule an activity")
            }
        },
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize().padding(padding),
        ) {
            item {
                Text("Schedule", style = MaterialTheme.typography.headlineSmall)
                Text("Revisions, mock tests, and tomorrow's study plan", style = MaterialTheme.typography.bodyMedium)
            }

            if (activities.isEmpty()) {
                item { Text("Nothing scheduled yet. Tap + to plan a session.") }
            }

            val grouped = activities.sortedBy { it.date }.groupBy { it.date }
            grouped.forEach { (date, dayActivities) ->
                item { Text(dateLabel(date, today), style = MaterialTheme.typography.titleMedium) }
                items(dayActivities, key = PlannedActivityEntity::id) { activity ->
                    Card(Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = activity.isDone, onCheckedChange = { viewModel.toggleDone(activity) })
                                Column {
                                    Text(
                                        "${typeLabel(activity.type)} · ${activity.title}",
                                        style = MaterialTheme.typography.titleSmall,
                                        textDecoration = if (activity.isDone) TextDecoration.LineThrough else TextDecoration.None,
                                    )
                                    Text("${activity.startTime} – ${activity.endTime}", style = MaterialTheme.typography.bodySmall)
                                    viewModel.subjectName(activity.subjectCode)?.let {
                                        Text(it, style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            }
                            IconButton(onClick = { viewModel.deleteActivity(activity) }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddActivityDialog(
            subjects = subjects,
            today = today,
            onDismiss = { showAddDialog = false },
            onSave = { title, type, subjectCode, date, start, end ->
                viewModel.addActivity(title, type, subjectCode, date, start, end)
                showAddDialog = false
            },
        )
    }
}

@Composable
private fun AddActivityDialog(
    subjects: List<Exam>,
    today: LocalDate,
    onDismiss: () -> Unit,
    onSave: (String, ActivityType, String?, LocalDate, LocalTime, LocalTime) -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(ActivityType.STUDY) }
    var selectedSubject by remember { mutableStateOf<Exam?>(null) }
    var daysFromToday by remember { mutableStateOf(1) }
    var start by remember { mutableStateOf(LocalTime.of(18, 0)) }
    var end by remember { mutableStateOf(LocalTime.of(19, 0)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Schedule an activity") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    ActivityType.entries.forEach { t ->
                        FilterChip(selected = type == t, onClick = { type = t }, label = { Text(typeLabel(t)) })
                    }
                }
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(placeholderFor(type)) },
                    singleLine = true,
                )
                SubjectDropdown(subjects = subjects, selected = selectedSubject, onSelect = { selectedSubject = it })

                Text("When", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf(0 to "Today", 1 to "Tomorrow", 2 to "+2d", 3 to "+3d", 7 to "+1wk").forEach { (offset, label) ->
                        FilterChip(selected = daysFromToday == offset, onClick = { daysFromToday = offset }, label = { Text(label) })
                    }
                }
                Text(today.plusDays(daysFromToday.toLong()).toString(), style = MaterialTheme.typography.bodySmall)

                Text("Start time", style = MaterialTheme.typography.labelLarge)
                TimeOfDayPicker(time = start, onChange = { start = it })
                Text("End time", style = MaterialTheme.typography.labelLarge)
                TimeOfDayPicker(time = end, onChange = { end = it })
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(
                    title.ifBlank { placeholderFor(type) },
                    type,
                    selectedSubject?.subjectCode,
                    today.plusDays(daysFromToday.toLong()),
                    start,
                    end,
                )
            }) { Text("Schedule") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

private fun typeLabel(type: ActivityType): String = when (type) {
    ActivityType.STUDY -> "Study"
    ActivityType.REVISION -> "Revision"
    ActivityType.MOCK_TEST -> "Mock test"
}

private fun placeholderFor(type: ActivityType): String = when (type) {
    ActivityType.STUDY -> "What will you study?"
    ActivityType.REVISION -> "What are you revising?"
    ActivityType.MOCK_TEST -> "Which mock test?"
}

private fun dateLabel(date: LocalDate, today: LocalDate): String = when {
    date.isEqual(today) -> "Today · ${date.format(DateTimeFormatter.ofPattern("d MMM"))}"
    date.isEqual(today.plusDays(1)) -> "Tomorrow · ${date.format(DateTimeFormatter.ofPattern("d MMM"))}"
    date.isBefore(today) -> "Overdue · ${date.format(DateTimeFormatter.ofPattern("d MMM"))}"
    else -> "${date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)} · ${date.format(DateTimeFormatter.ofPattern("d MMM"))}"
}
