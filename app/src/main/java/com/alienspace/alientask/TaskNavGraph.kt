package com.alienspace.alientask

import android.app.Activity
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.alienspace.alientask.addedittask.AddEditTaskScreen
import com.alienspace.alientask.taskdetails.TaskDetailsScreen
import com.alienspace.alientask.tasks.TaskScreen
import kotlinx.coroutines.CoroutineScope

const val DELETE_RESULT_OK = Activity.RESULT_FIRST_USER + 2

@Composable
fun TaskNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    startDestination: TasksRoute = TasksRoute,
    navigations: TaskNavigations = remember(navController) {
        TaskNavigations(navController)
    },
) {
    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentNavBackStackEntry?.destination?.route ?: startDestination

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {


        composable<TasksRoute> { entry ->
            TaskScreen(
                onAddTask = {
                    navigations.navigateToAddEditTask(title = R.string.add_task, taskId = null)
                },
                onTaskClick = {
                    navigations.navigateToTaskDetails(it.id)
                }
            )

        }

        composable<AddEditTasksRoute> { entry ->
            val addEditScreen: AddEditTasksRoute = entry.toRoute()
            AddEditTaskScreen(
                onTaskUpdate = {
                    navigations.navigateToTasks()
                },
                onBack = { navController.popBackStack() },
                addEditTasks = addEditScreen
            )
        }

        composable<TaskDetailRoute> { entry ->
            TaskDetailsScreen(
                onEditTask = { navigations.navigateToAddEditTask(title = R.string.edit_task, taskId = it) },
                onBack = { navController.popBackStack() },
                onDeleteTask = { navigations.navigateToTasks() },
            )
        }
    }
}