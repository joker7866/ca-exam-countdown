package com.cacountdown.app.ui.alerts

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AlertsScreen(viewModel: AlertsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        item {
            Text("Exam reminders", style = MaterialTheme.typography.headlineSmall)
            Text("Fewer alerts when exams are far, more as they get close", style = MaterialTheme.typography.bodyMedium)
        }

        if (!state.isLoading) {
            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Milestones", style = MaterialTheme.typography.titleSmall)
                        Text("${state.daysRemaining} days to next exam", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("How often you'll hear from us", style = MaterialTheme.typography.titleSmall)
                        Text(state.frequencyStage?.description ?: "", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        item {
            ToggleRow("All exam reminders", "Master switch", state.allRemindersOn, viewModel::toggleAllReminders)
        }
        item {
            ToggleRow("Milestone alerts", "90, 60, 45 … 1 day, exam day", state.milestoneAlertsOn, viewModel::toggleMilestoneAlerts)
        }
        item {
            ToggleRow("Progress check-ins", "Frequency follows the stages above", state.progressCheckInsOn, viewModel::toggleProgressCheckIns)
        }
        item {
            ToggleRow("Daily study nudge", "Before your first session", state.dailyNudgeOn, viewModel::toggleDailyNudge)
        }
        item {
            ToggleRow("Quiet hours", "No alerts 10:00 PM – 7:00 AM", state.quietHoursOn, viewModel::toggleQuietHours)
        }

        items(state.milestoneDays) { days ->
            Text("• $days days before exam", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun ToggleRow(title: String, subtitle: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Text(subtitle, style = MaterialTheme.typography.bodySmall)
        }
        Switch(checked = checked, onCheckedChange = onChange)
    }
}
