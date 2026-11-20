package com.example.gymlog

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gymlog.ui.screens.AddEditWorkoutScreen
import com.example.gymlog.ui.screens.CameraScreen
import com.example.gymlog.ui.screens.HistoryScreen
import com.example.gymlog.ui.screens.HomeScreen
import com.example.gymlog.ui.screens.LiveSessionScreen
import com.example.gymlog.ui.screens.SettingsScreen
import com.example.gymlog.ui.screens.WorkoutDetailScreen

//describes one entry in the bottom navigation bar
private data class TopLevelDestination(
    val screen: GymLogScreen,
    @StringRes val label: Int,
    val icon: ImageVector
)

//home, history and settings are the top level destinations
private val topLevelDestinations = listOf(
    TopLevelDestination(GymLogScreen.Home, R.string.nav_home, Icons.Filled.Home),
    TopLevelDestination(GymLogScreen.History, R.string.nav_history, Icons.Filled.History),
    TopLevelDestination(GymLogScreen.Settings, R.string.nav_settings, Icons.Filled.Settings)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GymLogApp(
    navController: NavHostController = rememberNavController()
) {
    //observe the current back stack entry so the app bar follows navigation
    val backStackEntry by navController.currentBackStackEntryAsState()
    //routes with arguments look like Detail/{workoutId} so cut off everything after the name
    val currentScreen = GymLogScreen.valueOf(
        (backStackEntry?.destination?.route ?: GymLogScreen.Home.name)
            .substringBefore("/")
            .substringBefore("?")
    )
    val isTopLevel = topLevelDestinations.any { it.screen == currentScreen }

    //opens the detail screen for one workout and passes its id in the route
    val openDetail: (Long) -> Unit = { workoutId ->
        navController.navigate("${GymLogScreen.Detail.name}/$workoutId")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(currentScreen.title)) },
                navigationIcon = {
                    //only show the up arrow on screens that are not top level
                    if (!isTopLevel) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back_button)
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            //the bottom bar is only shown on the three top level screens
            if (isTopLevel) {
                NavigationBar {
                    topLevelDestinations.forEach { destination ->
                        NavigationBarItem(
                            selected = currentScreen == destination.screen,
                            onClick = {
                                navController.navigate(destination.screen.name) {
                                    //keep one copy of each top level screen
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(destination.icon, contentDescription = null) },
                            label = { Text(stringResource(destination.label)) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = GymLogScreen.Home.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = GymLogScreen.Home.name) {
                HomeScreen(
                    onStartWorkout = { navController.navigate(GymLogScreen.LiveSession.name) },
                    onAddWorkout = { navController.navigate(GymLogScreen.AddEdit.name) },
                    onOpenWorkout = openDetail
                )
            }
            composable(route = GymLogScreen.History.name) {
                HistoryScreen(
                    onOpenWorkout = openDetail
                )
            }
            composable(
                route = "${GymLogScreen.Detail.name}/{workoutId}",
                arguments = listOf(navArgument("workoutId") { type = NavType.LongType })
            ) {
                WorkoutDetailScreen(
                    onEditWorkout = { navController.navigate(GymLogScreen.AddEdit.name) },
                    onAddPhoto = { navController.navigate(GymLogScreen.Camera.name) }
                )
            }
            composable(route = GymLogScreen.AddEdit.name) {
                AddEditWorkoutScreen(
                    onSave = { navController.popBackStack() },
                    onCancel = { navController.popBackStack() }
                )
            }
            composable(route = GymLogScreen.LiveSession.name) {
                LiveSessionScreen(
                    onFinishSession = { navController.popBackStack() }
                )
            }
            composable(route = GymLogScreen.Camera.name) {
                CameraScreen(
                    onDone = { navController.popBackStack() }
                )
            }
            composable(route = GymLogScreen.Settings.name) {
                SettingsScreen()
            }
        }
    }
}