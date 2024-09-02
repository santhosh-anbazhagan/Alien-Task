package com.alienspace.alientask.taskdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.alienspace.alientask.TaskDetailRoute
import com.alienspace.alientask.data.Task
import com.alienspace.alientask.data.TaskRepository
import com.alienspace.alientask.util.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class TaskDetailUiState(
    val task: Task? = null,
    val isLoading: Boolean = false,
    val userMessage: Int? = null,
    val isTaskDeleted: Boolean = false,
)

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val repo: TaskRepository,
) : ViewModel() {

    val taskId: String = savedStateHandle.toRoute<TaskDetailRoute>().taskId!!

    private val _isLoading = MutableStateFlow(false)
    private val _isTaskDeleted = MutableStateFlow(false)
    private val _isTaskAsync = if (taskId != null) {
        repo.getTaskStream(taskId)
    } else {
        flow { }
    }

    private val _uiState = MutableStateFlow(TaskDetailUiState())
    val uiState = combine(_isLoading,_isTaskDeleted, _isTaskAsync) { isLoading,taskDeletd, task ->
        TaskDetailUiState(
            isLoading = isLoading,
            task = task,
            isTaskDeleted = taskDeletd
        )
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = TaskDetailUiState(isLoading = true)
    )

//    init {
//        if (taskId != null) {
//            loadTask(taskId)
//        }
//    }


    fun deleteTask() = viewModelScope.launch {
        repo.deleteTask(taskId)
        _isTaskDeleted.value = true

    }

    fun completeTask(isComplete: Boolean) = viewModelScope.launch {
        Timber.d("Calling On Complete Task in Task Details ViewModel....")
        repo.completeTask(taskId, isComplete)
    }


}