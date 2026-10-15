package com.cacountdown.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cacountdown.app.ui.alarms.AlarmsScreen
import com.cacountdown.app.ui.alerts.AlertsScreen
import com.cacountdown.app.ui.analysis.AnalysisScreen
import com.cacountdown.app.ui.checklist.ChecklistScreen
import com.cacountdown.app.ui.goals.GoalsScreen
import com.cacountdown.app.ui.home.HomeScreen
import com.cacountdown.app.ui.more.MoreScreen
import com.cacountdown.app.ui.notes.NotesScreen
import com.cacountdown.app.ui.plan.PlanScreen
import com.cacountdown.app.ui.schedule.ScheduleScreen
import com.cacountdown.app.ui.scheduleupdate.ScheduleUpdateScreen
import com.cacountdown.app.ui.settings.SettingsScreen
import com.cacountdown.app.ui.setup.SetupScreen
import com.cacountdown.app.ui.subjects.SubjectsScreen
import com.cacountdown.app.ui.support.SupportScreen
import com.cacountdown.app.ui.timetable.TimetableScreen
import com.cacountdown.app.ui.topics.TopicsScreen
import com.cacountdown.app.ui.welcome.WelcomeScreen

private val TAB_ROUTES = setOf(
    CaDestination.Home.route,
    CaDestination.Subjects.route,
    CaDestination.Plan.route,
    CaDestination.Alerts.route,
    CaDestination.More.route,
)

@Composable
fun CaNavHost(startDestination: String) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val showBottomBar = backStackEntry?.destination?.route in TAB_ROUTES

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    BOTTOM_TABS.forEach { tab ->
                        val selected = backStackEntry?.destination?.hierarchy
                            ?.any { it.route == tab.destination.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                val route = if (tab.destination is CaDestination.Plan) {
                                    CaDestination.Plan.routeFor("next")
                                } else {
                                    tab.destination.route
                                }
                                navController.navigate(route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { androidx.compose.material3.Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) },
                        )
                    }
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = androidx.compose.ui.Modifier.padding(padding),
        ) {
            composable(CaDestination.Welcome.route) {
                WelcomeScreen(onGetStarted = { navController.navigate(CaDestination.Setup.route) })
            }
            composable(CaDestination.Setup.route) {
                SetupScreen(onSetupComplete = {
                    navController.navigate(CaDestination.Home.route) {
                        popUpTo(CaDestination.Welcome.route) { inclusive = true }
                    }
                })
            }
            composable(CaDestination.Home.route) {
                HomeScreen(
                    onOpenSubject = { code -> navController.navigate(CaDestination.Plan.routeFor(code)) },
                    onOpenScheduleUpdate = { navController.navigate(CaDestination.ScheduleUpdate.route) },
                    onOpenSchedule = { navController.navigate(CaDestination.Schedule.route) },
                    onOpenAnalysis = { navController.navigate(CaDestination.Analysis.route) },
                )
            }
            composable(CaDestination.Subjects.route) {
                SubjectsScreen(
                    onOpenSubject = { code -> navController.navigate(CaDestination.Plan.routeFor(code)) },
                    onOpenTopics = { code -> navController.navigate(CaDestination.Topics.routeFor(code)) },
                )
            }
            composable(CaDestination.Plan.route) {
                PlanScreen(
                    onOpenTopics = { code -> navController.navigate(CaDestination.Topics.routeFor(code)) },
                    onScheduleRevision = { navController.navigate(CaDestination.Schedule.route) },
                )
            }
            composable(CaDestination.Topics.route) {
                TopicsScreen()
            }
            composable(CaDestination.Alerts.route) {
                AlertsScreen()
            }
            composable(CaDestination.More.route) {
                MoreScreen(onNavigate = { route -> navController.navigate(route) })
            }
            composable(CaDestination.ScheduleUpdate.route) {
                ScheduleUpdateScreen(onDone = { navController.popBackStack() })
            }
            composable(CaDestination.Notes.route) {
                NotesScreen()
            }
            composable(CaDestination.Checklist.route) {
                ChecklistScreen()
            }
            composable(CaDestination.Timetable.route) {
                TimetableScreen()
            }
            composable(CaDestination.Alarms.route) {
                AlarmsScreen()
            }
            composable(CaDestination.Goals.route) {
                GoalsScreen()
            }
            composable(CaDestination.Settings.route) {
                SettingsScreen(onOpenSupport = { navController.navigate(CaDestination.Support.route) })
            }
            composable(CaDestination.Support.route) {
                SupportScreen()
            }
            composable(CaDestination.Schedule.route) {
                ScheduleScreen()
            }
            composable(CaDestination.Analysis.route) {
                AnalysisScreen()
            }
        }
    }
}
