package com.cacountdown.app.ui.subjects

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cacountdown.app.domain.model.ExamState
import com.cacountdown.app.domain.model.UrgencyStatus
import com.cacountdown.app.ui.home.HomeViewModel
import com.cacountdown.app.ui.theme.AttentionAmber
import com.cacountdown.app.ui.theme.BehindOrange
import com.cacountdown.app.ui.theme.OnTrackGreen
import com.cacountdown.app.ui.theme.UrgentRed

@Composable
fun SubjectsScreen(
    onOpenSubject: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        item {
            Text("Subjects", style = MaterialTheme.typography.headlineSmall)
            Text("${state.rows.size} papers · ${state.attempt}", style = MaterialTheme.typography.bodyMedium)
        }

        items(state.rows, key = { it.exam.subjectCode }) { row ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(row.exam.subjectName, style = MaterialTheme.typography.titleMedium)
                        urgencyColor(row.urgency).let { (label, _) -> Text(label) }
                    }
                    val daysText = when (val s = row.state) {
                        is ExamState.DaysLeft -> "${s.days} days left"
                        ExamState.ExamToday -> "Today"
                        ExamState.Completed -> "Completed"
                    }
                    Text("Exam: ${row.exam.examDate} · $daysText", style = MaterialTheme.typography.bodySmall)
                    LinearProgressIndicator(
                        progress = { row.progress.syllabusFraction.toFloat() },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Text(
                        "Syllabus ${(row.progress.syllabusFraction * 100).toInt()}% · " +
                            "Revision ${(row.progress.revisionFraction * 100).toInt()}% · " +
                            "Recommended ${row.dailyTarget.requiredMinutesPerDay} min/day",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }
}

private fun urgencyColor(urgency: UrgencyStatus) = when (urgency) {
    UrgencyStatus.ON_TRACK -> "On track" to OnTrackGreen
    UrgencyStatus.ATTENTION -> "Attention" to AttentionAmber
    UrgencyStatus.BEHIND -> "Behind" to BehindOrange
    UrgencyStatus.URGENT -> "Urgent" to UrgentRed
}
