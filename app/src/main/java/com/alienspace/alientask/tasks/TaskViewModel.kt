package com.alienspace.alientask.tasks

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alienspace.alientask.R
import com.alienspace.alientask.data.Task
import com.alienspace.alientask.data.TaskRepository
import com.alienspace.alientask.util.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State for Task List Screen
 */
data class TasksUiState(
    val items: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val userMessage: Int? = null,
    val filteringUiInfo: FilteringUiInfo = FilteringUiInfo(),
)

data class FilteringUiInfo(
    val isSelected: TaskFilterTypes = TaskFilterTypes.ALL_TASKS,
    val currentFilteringLabel: Int = R.string.label_all,
    val noTasksLabel: Int = R.string.no_tasks_all,
    val noTaskIconRes: Int = R.drawable.logo_no_fill,
    val allTask: Boolean = false,
    val activeTask: Boolean = false,
    val completedTask: Boolean = false,
)


// Used to save the current filtering in SavedStateHandle.
const val TASKS_FILTER_SAVED_STATE_KEY = "TASKS_FILTER_SAVED_STATE_KEY"


@HiltViewModel
class TaskViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repo: TaskRepository,
) : ViewModel() {

    private val _savedFilterType =
        savedStateHandle.getStateFlow(TASKS_FILTER_SAVED_STATE_KEY, TaskFilterTypes.ALL_TASKS)
    private val _filteringUiInfo =
        _savedFilterType.map { getFilteredValues(it) }.distinctUntilChanged()


    private val _isLoading = MutableStateFlow(false)
    private val _filteredTaskList = repo.getTasksStream().combine(_savedFilterType) { tasks, type ->
        filterTask(tasks, type)
    }


    //    private val _uiState = MutableStateFlow(TasksUiState())
    var uiState: StateFlow<TasksUiState> = combine(
        _filteringUiInfo, _filteredTaskList, _isLoading, _filteredTaskList,
    ) { filteringUiInfo, filteredTaskList, isLoading, tasksAsync ->

        TasksUiState(
            isLoading = isLoading,
            items = tasksAsync,
            filteringUiInfo = filteringUiInfo,
        )
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = TasksUiState(isLoading = true)
    )

    private fun filterTask(tasks: List<Task>, type: TaskFilterTypes): List<Task> {
        val showTaskList = ArrayList<Task>()
        for (task in tasks) {
            when (type) {
                TaskFilterTypes.ALL_TASKS -> {
                    showTaskList.add(task)
                }

                TaskFilterTypes.ACTIVE_TASKS -> {
                    if (task.isActive) {
                        showTaskList.add(task)
                    }
                }

                TaskFilterTypes.COMPLETED_TASKS -> {
                    if (task.isCompleted) {
                        showTaskList.add(task)
                    }
                }
            }
        }
        return showTaskList
    }


    fun completeTask(task: Task, completed: Boolean) = viewModelScope.launch {
        repo.completeTask(task.id, completed)
    }

    fun setFilteringTask(filterType: TaskFilterTypes) {
        savedStateHandle[TASKS_FILTER_SAVED_STATE_KEY] = filterType
    }

    fun clearCompletedTask() = viewModelScope.launch {
        repo.clearCompleteTask()
    }

    fun refresh() {
        _isLoading.value = true
        viewModelScope.launch {
//            taskRepository.refresh()
            delay(2000)
            _isLoading.value = false
        }
    }

    private fun getFilteredValues(taskFilterTypes: TaskFilterTypes): FilteringUiInfo =
        when (taskFilterTypes) {
            TaskFilterTypes.ALL_TASKS -> {
                FilteringUiInfo(
                    noTasksLabel = R.string.no_tasks_all,
                    currentFilteringLabel = R.string.label_all,
                    noTaskIconRes = R.drawable.logo_no_fill,
                    isSelected = TaskFilterTypes.ALL_TASKS,
                    activeTask = false,
                    allTask = true,
                    completedTask = false
                )

            }

            TaskFilterTypes.ACTIVE_TASKS -> {
                FilteringUiInfo(
                    noTasksLabel = R.string.no_tasks_active,
                    currentFilteringLabel = R.string.label_active,
                    noTaskIconRes = R.drawable.logo_no_fill,
                    isSelected = TaskFilterTypes.ACTIVE_TASKS,
                    activeTask = true,
                    allTask = false,
                    completedTask = false
                )
            }

            TaskFilterTypes.COMPLETED_TASKS -> {
                FilteringUiInfo(
                    noTasksLabel = R.string.no_tasks_completed,
                    currentFilteringLabel = R.string.label_completed,
                    noTaskIconRes = R.drawable.logo_no_fill,
                    isSelected = TaskFilterTypes.COMPLETED_TASKS,
                    activeTask = false,
                    allTask = false,
                    completedTask = true
                )
            }
        }


}


