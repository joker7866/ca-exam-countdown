package com.cacountdown.app.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cacountdown.app.domain.model.ExamState
import com.cacountdown.app.domain.model.UrgencyStatus
import com.cacountdown.app.ui.theme.AttentionAmber
import com.cacountdown.app.ui.theme.BehindOrange
import com.cacountdown.app.ui.theme.Navy
import com.cacountdown.app.ui.theme.OnTrackGreen
import com.cacountdown.app.ui.theme.UrgentRed
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreen(
    onOpenSubject: (String) -> Unit,
    onOpenScheduleUpdate: () -> Unit,
    onOpenSchedule: () -> Unit = {},
    onOpenAnalysis: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    if (state.isLoading) {
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        item {
            Text("CA INTERMEDIATE", style = MaterialTheme.typography.labelMedium)
            Text(state.attempt, style = MaterialTheme.typography.titleLarge)
        }

        if (state.attemptComplete) {
            item { Text("Attempt complete — all papers done. Well done!", style = MaterialTheme.typography.titleMedium) }
        } else {
            state.nextExam?.let { next ->
                item { NextExamCard(next, onOpenSubject) }
            }
        }

        item { Text("Upcoming exams", style = MaterialTheme.typography.titleMedium) }

        items(state.rows, key = { it.exam.subjectCode }) { row ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .clickableSubjectRow { onOpenSubject(row.exam.subjectCode) },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(row.exam.subjectName, style = MaterialTheme.typography.titleSmall)
                        UrgencyBadge(row.urgency)
                    }
                    Text(daysLabel(row.state), style = MaterialTheme.typography.titleMedium)
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                androidx.compose.material3.OutlinedButton(onClick = onOpenSchedule, modifier = Modifier.weight(1f)) {
                    Text("Schedule tomorrow")
                }
                androidx.compose.material3.OutlinedButton(onClick = onOpenAnalysis, modifier = Modifier.weight(1f)) {
                    Text("Analysis")
                }
            }
        }

        item {
            Button(onClick = onOpenScheduleUpdate, modifier = Modifier.fillMaxWidth()) {
                Text("Check for schedule updates")
            }
        }
    }
}

@Composable
private fun NextExamCard(row: SubjectRow, onOpenSubject: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickableSubjectRow { onOpenSubject(row.exam.subjectCode) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Navy),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(Modifier.padding(20.dp)) {
            Text("NEXT EXAM", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.labelMedium)
            Text(row.exam.subjectName, color = Color.White, style = MaterialTheme.typography.headlineSmall)
            Text(
                row.exam.examDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)),
                color = Color.White.copy(alpha = 0.8f),
            )
            Text(daysLabel(row.state), color = Color.White, style = MaterialTheme.typography.displaySmall)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatTile("Syllabus", "${(row.progress.syllabusFraction * 100).toInt()}%")
                StatTile("Revision", "${(row.progress.revisionFraction * 100).toInt()}%")
                StatTile("Target", minutesLabel(row.dailyTarget.requiredMinutesPerDay))
            }
        }
    }
}

@Composable
private fun StatTile(label: String, value: String) {
    Column {
        Text(label, color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.labelSmall)
        Text(value, color = Color.White, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun UrgencyBadge(urgency: UrgencyStatus) {
    val (label, color) = when (urgency) {
        UrgencyStatus.ON_TRACK -> "On track" to OnTrackGreen
        UrgencyStatus.ATTENTION -> "Attention" to AttentionAmber
        UrgencyStatus.BEHIND -> "Behind" to BehindOrange
        UrgencyStatus.URGENT -> "Urgent" to UrgentRed
    }
    Text(label, color = color, style = MaterialTheme.typography.labelMedium)
}

private fun daysLabel(state: ExamState): String = when (state) {
    is ExamState.DaysLeft -> "${state.days} days"
    ExamState.ExamToday -> "Today"
    ExamState.Completed -> "Completed"
}

private fun minutesLabel(minutes: Int): String {
    val h = minutes / 60
    val m = minutes % 60
    return if (h > 0) "${h}h ${m}m" else "${m}m"
}

private fun Modifier.clickableSubjectRow(onClick: () -> Unit): Modifier =
    this.clickable { onClick() }
