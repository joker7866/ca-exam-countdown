package com.cacountdown.app.ui.setup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cacountdown.app.domain.model.Exam
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun SetupScreen(
    onSetupComplete: () -> Unit,
    viewModel: SetupViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val dayMonthFormatter = DateTimeFormatter.ofPattern("dd MMM", Locale.ENGLISH)

    Scaffold(
        bottomBar = {
            Column(Modifier.padding(16.dp)) {
                Button(
                    onClick = { viewModel.startCountdown(onSetupComplete) },
                    enabled = state.canStart,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Start my countdown")
                }
            }
        },
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            item {
                Text("STEP 1 OF 2", style = androidx.compose.material3.MaterialTheme.typography.labelMedium)
                Text("Set up your exams", style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)
                Text(
                    "Pick your attempt and groups. We'll load the exam dates for you.",
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                )
            }

            item {
                Text("Exam attempt", style = androidx.compose.material3.MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AVAILABLE_ATTEMPTS.forEach { attempt ->
                        FilterChip(
                            selected = state.selectedAttempt.id == attempt.id,
                            onClick = { viewModel.selectAttempt(attempt) },
                            label = { Text(attempt.label) },
                        )
                    }
                }
            }

            item {
                Text("Groups", style = androidx.compose.material3.MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    GroupChoice.entries.forEach { group ->
                        FilterChip(
                            selected = state.selectedGroups == group,
                            onClick = { viewModel.selectGroups(group) },
                            label = { Text(groupLabel(group)) },
                            modifier = Modifier.selectable(selected = state.selectedGroups == group, onClick = {}),
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Exam dates loaded", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                    if (state.isLoading) CircularProgressIndicator(modifier = Modifier.size(16.dp))
                }
            }

            if (state.loadedExams.isEmpty() && !state.isLoading) {
                item {
                    Text("No official timetable bundled for this attempt yet.")
                }
            }

            items(state.loadedExams, key = Exam::subjectCode) { exam ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text(exam.examDate.format(dayMonthFormatter))
                        }
                        Column(Modifier.padding(start = 12.dp)) {
                            Text(exam.subjectName, style = androidx.compose.material3.MaterialTheme.typography.titleSmall)
                            Text("${groupLabelShort(exam.group)} · ${viewModel.daysAway(exam)} days away")
                        }
                    }
                }
            }
        }
    }
}

private fun groupLabel(choice: GroupChoice) = when (choice) {
    GroupChoice.GROUP_1 -> "Group I"
    GroupChoice.GROUP_2 -> "Group II"
    GroupChoice.BOTH -> "Both"
}

private fun groupLabelShort(group: com.cacountdown.app.domain.model.ExamGroup) = when (group) {
    com.cacountdown.app.domain.model.ExamGroup.GROUP_1 -> "Group I"
    com.cacountdown.app.domain.model.ExamGroup.GROUP_2 -> "Group II"
}
