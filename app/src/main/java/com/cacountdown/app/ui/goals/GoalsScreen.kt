package com.cacountdown.app.ui.goals

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
import androidx.compose.material3.LinearProgressIndicator
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
import com.cacountdown.app.data.local.entity.GoalEntity
import com.cacountdown.app.domain.model.Exam
import com.cacountdown.app.domain.model.GoalUnit
import com.cacountdown.app.ui.common.SubjectDropdown

@Composable
fun GoalsScreen(viewModel: GoalsViewModel = hiltViewModel()) {
    val goals by viewModel.goals.collectAsState()
    val subjects by viewModel.subjects.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add goal")
            }
        },
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize().padding(padding),
        ) {
            item {
                Text("Goals & targets", style = MaterialTheme.typography.headlineSmall)
                Text("Personal goals, or a target for one subject", style = MaterialTheme.typography.bodyMedium)
            }

            if (goals.isEmpty()) {
                item { Text("No goals yet. Tap + to set one.") }
            }

            items(goals, key = GoalEntity::id) { goal ->
                val unit = runCatching { GoalUnit.valueOf(goal.unit) }.getOrDefault(GoalUnit.HOURS)
                val progress = if (goal.targetValue > 0) (goal.currentValue / goal.targetValue).toFloat().coerceIn(0f, 1f) else 0f

                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(goal.title, style = MaterialTheme.typography.titleMedium)
                            IconButton(onClick = { viewModel.deleteGoal(goal) }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete")
                            }
                        }
                        LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                "${formatValue(goal.currentValue)} / ${formatValue(goal.targetValue)} ${unit.label}" +
                                    (goal.deadline?.let { " · by $it" } ?: ""),
                                style = MaterialTheme.typography.bodySmall,
                            )
                            if (goal.isAchieved) Text("Achieved 🎉", style = MaterialTheme.typography.labelMedium)
                        }
                        viewModel.subjectName(goal.subjectCode)?.let {
                            Text(it, style = MaterialTheme.typography.labelSmall)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextButton(onClick = { viewModel.addProgress(goal, 1.0) }) { Text("+1") }
                            TextButton(onClick = { viewModel.addProgress(goal, -1.0) }) { Text("-1") }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddGoalDialog(
            subjects = subjects,
            onDismiss = { showAddDialog = false },
            onSave = { title, unit, target, subjectCode ->
                viewModel.addGoal(title, unit, target, deadline = null, subjectCode = subjectCode)
                showAddDialog = false
            },
        )
    }
}

@Composable
private fun AddGoalDialog(subjects: List<Exam>, onDismiss: () -> Unit, onSave: (String, GoalUnit, Double, String?) -> Unit) {
    var title by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf(GoalUnit.HOURS) }
    var targetText by remember { mutableStateOf("") }
    var selectedSubject by remember { mutableStateOf<Exam?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New goal") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Goal") }, singleLine = true)
                SubjectDropdown(subjects = subjects, selected = selectedSubject, onSelect = { selectedSubject = it })
                OutlinedTextField(
                    value = targetText,
                    onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) targetText = it },
                    label = { Text("Target amount") },
                    singleLine = true,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    GoalUnit.entries.forEach { u ->
                        FilterChip(selected = unit == u, onClick = { unit = u }, label = { Text(u.label) })
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(title, unit, targetText.toDoubleOrNull() ?: 0.0, selectedSubject?.subjectCode) }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

private fun formatValue(value: Double): String =
    if (value == value.toLong().toDouble()) value.toLong().toString() else "%.1f".format(value)
