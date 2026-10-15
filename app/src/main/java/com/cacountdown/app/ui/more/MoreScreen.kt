package com.cacountdown.app.ui.more

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

private data class MoreItem(val label: String, val subtitle: String, val icon: ImageVector, val route: String)

private val MORE_ITEMS = listOf(
    MoreItem("Timetable", "Plan your own weekly study schedule", Icons.Filled.CalendarMonth, "timetable"),
    MoreItem("Schedule", "Plan revisions, mock tests, tomorrow's study", Icons.Filled.EditCalendar, "schedule"),
    MoreItem("Analysis", "Charts: syllabus, revision, urgency", Icons.Filled.BarChart, "analysis"),
    MoreItem("Notes", "Jot down anything, per subject or general", Icons.Filled.EditNote, "notes"),
    MoreItem("Checklist", "Quick to-dos", Icons.AutoMirrored.Filled.List, "checklist"),
    MoreItem("Alarms", "Set your own study alarms", Icons.Filled.Alarm, "alarms"),
    MoreItem("Goals & targets", "Personal goals and subject targets", Icons.Filled.TrackChanges, "goals"),
    MoreItem("Settings", "Profile, course and attempt", Icons.Filled.Settings, "settings"),
    MoreItem("Help & support", "FAQs and contact", Icons.Filled.Help, "support"),
)

@Composable
fun MoreScreen(onNavigate: (String) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = 16.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        item {
            Text(
                "More",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            )
        }
        items(MORE_ITEMS) { item ->
            ListItem(
                headlineContent = { Text(item.label) },
                supportingContent = { Text(item.subtitle) },
                leadingContent = { Icon(item.icon, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().clickable { onNavigate(item.route) },
            )
        }
    }
}
