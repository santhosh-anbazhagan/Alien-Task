package com.alienspace.alientask

import androidx.navigation.NavHostController
import com.alienspace.alientask.TaskScreens.ADD_EDIT_TASK_SCREEN
import com.alienspace.alientask.TaskScreens.TASK_SCREEN
import com.alienspace.alientask.data.Task
import kotlinx.serialization.Serializable

private object TaskScreens {
    const val TASK_SCREEN = "tasks"
    const val ADD_EDIT_TASK_SCREEN = "addEditTask"
}

object TaskDestinations {
    const val TASK_ROUTE = TASK_SCREEN
    const val ADD_EDIT_TASK_ROUTE = ADD_EDIT_TASK_SCREEN
}

@Serializable
 object TasksRoute

@Serializable
 data class AddEditTasksRoute(val title:Int,val taskId: String?)

@Serializable
 data class TaskDetailRoute(val taskId: String?)

class TaskNavigations(private val navController: NavHostController) {

    fun navigateToAddEditTask(title: Int,taskId: String?) {
        navController.navigate(AddEditTasksRoute(title = title, taskId=taskId))
    }

    fun navigateToTasks(result : Int=0) {
        navController.navigate(TasksRoute){
            popUpTo(TasksRoute){
            }
            launchSingleTop = true
            restoreState =true
        }
    }

    fun navigateToTaskDetails(task: String) {
        navController.navigate(TaskDetailRoute(task))
    }

}