package com.cacountdown.app.ui.checklist

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
import androidx.compose.material3.Checkbox
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
import com.cacountdown.app.data.local.entity.ChecklistItemEntity
import com.cacountdown.app.domain.model.Exam
import com.cacountdown.app.ui.common.SubjectDropdown

@Composable
fun ChecklistScreen(viewModel: ChecklistViewModel = hiltViewModel()) {
    val items by viewModel.items.collectAsState()
    val subjects by viewModel.subjects.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add to-do")
            }
        },
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxSize().padding(padding),
        ) {
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Checklist", style = MaterialTheme.typography.headlineSmall)
                    TextButton(onClick = viewModel::clearCompleted) { Text("Clear done") }
                }
            }

            if (items.isEmpty()) {
                item { Text("Nothing here yet — add your first to-do above.") }
            }

            items(items, key = ChecklistItemEntity::id) { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = item.isDone, onCheckedChange = { viewModel.toggleDone(item) })
                        Column {
                            Text(
                                item.text,
                                textDecoration = if (item.isDone) TextDecoration.LineThrough else TextDecoration.None,
                            )
                            viewModel.subjectName(item.subjectCode)?.let {
                                Text(it, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                    IconButton(onClick = { viewModel.deleteItem(item) }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete")
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddChecklistItemDialog(
            subjects = subjects,
            onDismiss = { showAddDialog = false },
            onSave = { text, subjectCode ->
                viewModel.addItem(text, subjectCode)
                showAddDialog = false
            },
        )
    }
}

@Composable
private fun AddChecklistItemDialog(subjects: List<Exam>, onDismiss: () -> Unit, onSave: (String, String?) -> Unit) {
    var text by remember { mutableStateOf("") }
    var selectedSubject by remember { mutableStateOf<Exam?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New to-do") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = text, onValueChange = { text = it }, label = { Text("To-do") }, singleLine = true)
                SubjectDropdown(subjects = subjects, selected = selectedSubject, onSelect = { selectedSubject = it })
            }
        },
        confirmButton = { TextButton(onClick = { onSave(text, selectedSubject?.subjectCode) }) { Text("Add") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}
