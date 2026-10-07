package com.cacountdown.app.ui.plan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cacountdown.app.domain.model.ExamState
import com.cacountdown.app.domain.model.UrgencyStatus

@Composable
fun PlanScreen(viewModel: PlanViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    if (state.isLoading || state.exam == null) {
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
        }
        return
    }

    val exam = state.exam!!
    val progress = state.progress!!
    val target = state.dailyTarget!!

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        item {
            Text(exam.subjectName, style = MaterialTheme.typography.headlineSmall)
            Text(urgencyLabel(state.urgency), style = MaterialTheme.typography.titleMedium)
            val daysText = when (val s = state.state) {
                is ExamState.DaysLeft -> "${s.days} DAYS REMAINING"
                ExamState.ExamToday -> "EXAM TODAY"
                ExamState.Completed -> "COMPLETED"
                null -> ""
            }
            Text(daysText, style = MaterialTheme.typography.displaySmall)
        }

        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Why it's marked this way", style = MaterialTheme.typography.titleSmall)
                    LabeledRow("Syllabus done", "${(progress.syllabusFraction * 100).toInt()}% (${state.expectedSyllabusPercent}% expected)")
                    LabeledRow("Revision", "${(progress.revisionFraction * 100).toInt()}%")
                    LabeledRow("Mock tests", "${progress.mocksDone} of ${progress.mocksPlanned}")
                    LabeledRow("Missed sessions this week", "${progress.missedSessionsThisWeek}")
                }
            }
        }

        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Your daily target", style = MaterialTheme.typography.titleSmall)
                    Text(
                        "${"%.1f".format(target.hoursLeft)} h left ÷ ${target.studyDaysLeft} days",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        "Recommended ${target.requiredMinutesPerDay} min / day",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                }
            }
        }

        item {
            Button(onClick = { viewModel.markChapterDone() }, modifier = Modifier.fillMaxWidth()) {
                Text("Mark a chapter done")
            }
        }
    }
}

@Composable
private fun LabeledRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

private fun urgencyLabel(urgency: UrgencyStatus?) = when (urgency) {
    UrgencyStatus.ON_TRACK -> "On track"
    UrgencyStatus.ATTENTION -> "Attention needed"
    UrgencyStatus.BEHIND -> "Behind schedule"
    UrgencyStatus.URGENT -> "Urgent"
    null -> ""
}
