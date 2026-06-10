package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.ChecklistsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FindingsScreen
import com.example.ui.screens.LawsScreen
import com.example.ui.theme.SafetyOrange
import com.example.viewmodel.SecurityViewModel

sealed class Screen(val route: String, val title: String, val icon: @Composable () -> Unit) {
    object Dashboard : Screen(
        route = "dashboard",
        title = "Inicio",
        icon = { Icon(Icons.Default.Dashboard, contentDescription = "Inicio") }
    )
    object Laws : Screen(
        route = "laws",
        title = "Leyes",
        icon = { Icon(Icons.Default.Gavel, contentDescription = "Leyes") }
    )
    object Checklists : Screen(
        route = "checklists",
        title = "Auditoría",
        icon = { Icon(Icons.Default.AssignmentTurnedIn, contentDescription = "Auditoría") }
    )
    object Findings : Screen(
        route = "findings",
        title = "Hallazgos",
        icon = { Icon(Icons.Default.Warning, contentDescription = "Hallazgos") }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AppNavigation(viewModel: SecurityViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val navigationItems = listOf(
        Screen.Dashboard,
        Screen.Laws,
        Screen.Checklists,
        Screen.Findings
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .testTag("app_navigation_bar")
                    .windowInsetsPadding(WindowInsets.navigationBars),
                tonalElevation = 8.dp
            ) {
                navigationItems.forEach { screen ->
                    val isSelected = currentRoute == screen.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    // Pop up to the start destination of the graph to
                                    // avoid building up a large stack of destinations
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    // Avoid multiple copies of the same destination when
                                    // reselecting the same item
                                    launchSingleTop = true
                                    // Restore state when reselecting a previously selected item
                                    restoreState = true
                                }
                            }
                        },
                        icon = screen.icon,
                        label = { Text(screen.title) },
                        modifier = Modifier.testTag("nav_item_${screen.route}"),
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedTextColor = SafetyOrange,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToChecklists = {
                        navController.navigate(Screen.Checklists.route) {
                            popUpTo(Screen.Dashboard.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToFindings = {
                        navController.navigate(Screen.Findings.route) {
                            popUpTo(Screen.Dashboard.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToLaws = {
                        navController.navigate(Screen.Laws.route) {
                            popUpTo(Screen.Dashboard.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            composable(Screen.Laws.route) {
                LawsScreen(viewModel = viewModel)
            }

            composable(Screen.Checklists.route) {
                ChecklistsScreen(
                    viewModel = viewModel,
                    onNavigateToFindings = {
                        navController.navigate(Screen.Findings.route) {
                            popUpTo(Screen.Dashboard.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            composable(Screen.Findings.route) {
                FindingsScreen(viewModel = viewModel)
            }
        }
    }
}
