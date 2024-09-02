package com.alienspace.alientask.addedittask

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.alienspace.alientask.AddEditTasksRoute
import com.alienspace.alientask.R
import com.alienspace.alientask.data.TaskRepository
import com.alienspace.snackbar.SnackBarController
import com.alienspace.snackbar.SnackBarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


data class AddEditTaskUiState(
    val title: String = "",
    val description: String = "",
    val isTaskCompleted: Boolean = false,
    val isLoading: Boolean = false,
    val userMessage: Int? = null,
    val isTaskSaved: Boolean = false,
)


@HiltViewModel
class AddEditTaskViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val taskRepo: TaskRepository,
) : ViewModel() {

    private val taskId: String? = savedStateHandle.toRoute<AddEditTasksRoute>().taskId

    private val _uiState = MutableStateFlow(AddEditTaskUiState())
    var uiState: StateFlow<AddEditTaskUiState> = _uiState

    init {
        if (taskId != null) {
            Timber.tag("AddEditTaskViewModel").d("Task ID : $taskId")
            loadTask(taskId)
        }
    }

    private fun loadTask(taskId: String) {
        _uiState.update {
            it.copy(isLoading = true)
        }
        viewModelScope.launch {
            taskRepo.getTask(taskId).let { task ->
                if (task != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            title = task.title,
                            isTaskCompleted = task.isCompleted,
                            description = task.description
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false)
                    }
                }
            }
        }
    }


    fun saveTask() {
        if (uiState.value.title.isEmpty() || uiState.value.description.isEmpty()) {
            _uiState.update {
                it.copy(userMessage = R.string.empty_task_message)
            }
            Timber.d("${uiState.value.userMessage}")
            return
        }
        if (taskId == null) {
            createNewtask()
        } else {
            updateTask()
        }

    }

    private fun createNewtask() = viewModelScope.launch {
        // todo create task
        Timber.d("Creating New task...")
        taskRepo.createTask(uiState.value.title, uiState.value.description)
        _uiState.update {
            it.copy(isTaskSaved = true)
        }
        SnackBarController.sendEvent(
            SnackBarEvent(
                msg = "Task Saved Successfully!"
            )
        )
    }

    private fun updateTask() {
        if (taskId == null) {
            throw RuntimeException("updateTask() was called but task is new.")
        }

        viewModelScope.launch {
            taskRepo.updateTask(
                taskId,
                title = uiState.value.title,
                description = uiState.value.description
            )
            _uiState.update {
                it.copy(
                    isTaskSaved = true
                )
            }
            SnackBarController.sendEvent(
                SnackBarEvent(
                    msg = "Task Updated Successfully!",
                    duration = SnackbarDuration.Short
                )
            )
        }
    }


    fun updateTitle(newTitle: String) {
        _uiState.update {
            it.copy(title = newTitle)
        }
    }

    fun updateDescription(newDescription: String) {
        _uiState.update {
            it.copy(description = newDescription)
        }
    }

}