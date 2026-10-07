package com.cacountdown.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.ui.graphics.vector.ImageVector

sealed class CaDestination(val route: String) {
    data object Setup : CaDestination("setup")
    data object Home : CaDestination("home")
    data object Subjects : CaDestination("subjects")
    data object Plan : CaDestination("plan/{subjectCode}") {
        fun routeFor(subjectCode: String) = "plan/$subjectCode"
    }
    data object Alerts : CaDestination("alerts")
    data object ScheduleUpdate : CaDestination("schedule_update")
}

data class BottomTab(val destination: CaDestination, val label: String, val icon: ImageVector)

val BOTTOM_TABS = listOf(
    BottomTab(CaDestination.Home, "Home", Icons.Filled.Home),
    BottomTab(CaDestination.Subjects, "Subjects", Icons.Outlined.Assignment),
    BottomTab(CaDestination.Plan, "Plan", Icons.Filled.TrackChanges),
    BottomTab(CaDestination.Alerts, "Alerts", Icons.Filled.Notifications),
)
