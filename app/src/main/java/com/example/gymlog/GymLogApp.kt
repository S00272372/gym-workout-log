package com.example.gymlog

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
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

//describes one entry in the bottom navigation bar or the navigation rail
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
    windowWidthSizeClass: WindowWidthSizeClass = WindowWidthSizeClass.Compact,
    navController: NavHostController = rememberNavController()
) {
    //compact windows such as a phone in portrait use a bottom bar
    //wider windows such as a phone in landscape use a navigation rail and two column layouts
    val useBottomBar = windowWidthSizeClass == WindowWidthSizeClass.Compact
    val isWide = !useBottomBar

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

    //shared by the bottom bar and the navigation rail
    val goToTopLevel: (GymLogScreen) -> Unit = { screen ->
        navController.navigate(screen.name) {
            //keep one copy of each top level screen
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
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
            //the bottom bar is only shown on the three top level screens and only in compact windows
            if (isTopLevel && useBottomBar) {
                NavigationBar {
                    topLevelDestinations.forEach { destination ->
                        NavigationBarItem(
                            selected = currentScreen == destination.screen,
                            onClick = { goToTopLevel(destination.screen) },
                            icon = { Icon(destination.icon, contentDescription = null) },
                            label = { Text(stringResource(destination.label)) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Row(modifier = Modifier.padding(innerPadding)) {
            //the navigation rail sits on the left in wider windows
            if (isTopLevel && !useBottomBar) {
                //the scaffold already added the insets so the rail does not add them again
                NavigationRail(windowInsets = WindowInsets(0, 0, 0, 0)) {
                    topLevelDestinations.forEach { destination ->
                        NavigationRailItem(
                            selected = currentScreen == destination.screen,
                            onClick = { goToTopLevel(destination.screen) },
                            icon = { Icon(destination.icon, contentDescription = null) },
                            label = { Text(stringResource(destination.label)) }
                        )
                    }
                }
            }
            NavHost(
                navController = navController,
                startDestination = GymLogScreen.Home.name,
                modifier = Modifier.weight(1f)
            ) {
                composable(route = GymLogScreen.Home.name) {
                    HomeScreen(
                        onStartWorkout = { navController.navigate(GymLogScreen.LiveSession.name) },
                        onAddWorkout = { navController.navigate(GymLogScreen.AddEdit.name) },
                        onOpenWorkout = openDetail,
                        isWide = isWide
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
                        onEditWorkout = { workoutId ->
                            navController.navigate("${GymLogScreen.AddEdit.name}?workoutId=$workoutId")
                        },
                        onAddPhoto = { navController.navigate(GymLogScreen.Camera.name) },
                        onDeleted = { navController.popBackStack() },
                        isWide = isWide
                    )
                }
                //workoutId is optional: -1 means add a new workout and anything else means edit
                composable(
                    route = "${GymLogScreen.AddEdit.name}?workoutId={workoutId}",
                    arguments = listOf(
                        navArgument("workoutId") {
                            type = NavType.LongType
                            defaultValue = -1L
                        }
                    )
                ) {
                    AddEditWorkoutScreen(
                        onSave = { navController.popBackStack() },
                        onCancel = { navController.popBackStack() },
                        isWide = isWide
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
}