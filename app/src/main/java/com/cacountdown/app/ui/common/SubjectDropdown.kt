package com.cacountdown.app.ui.common

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.cacountdown.app.domain.model.Exam

/** A "General (no subject)" + one entry per exam dropdown, used anywhere a note/checklist/alarm/goal/schedule item can be subject-scoped. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectDropdown(
    subjects: List<Exam>,
    selected: Exam?,
    onSelect: (Exam?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }, modifier = modifier) {
        OutlinedTextField(
            value = selected?.subjectName ?: "General (no subject)",
            onValueChange = {},
            readOnly = true,
            label = { Text("Subject") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("General (no subject)") },
                onClick = { onSelect(null); expanded = false },
            )
            subjects.forEach { exam ->
                DropdownMenuItem(
                    text = { Text(exam.subjectName) },
                    onClick = { onSelect(exam); expanded = false },
                )
            }
        }
    }
}
