package com.cacountdown.app.ui.scheduleupdate

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Stub for spec §12. The real flow (ScheduleSyncWorker fetching a newer
 * timetable version, diffing it, and letting the student choose "Update
 * dates" or "Keep current") lands with the backend in a later phase — until
 * then this just confirms the bundled timetable is the one in force.
 */
@Composable
fun ScheduleUpdateScreen(onDone: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("You're on the latest timetable", style = MaterialTheme.typography.headlineSmall)
        Text(
            "Official schedule sync isn't live yet — exam dates come from the bundled timetable.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp),
        )
        Button(onClick = onDone) { Text("Close") }
    }
}
