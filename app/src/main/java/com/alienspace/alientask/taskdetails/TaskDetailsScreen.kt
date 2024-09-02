package com.alienspace.alientask.taskdetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alienspace.alientask.R
import com.alienspace.alientask.data.Task
import com.alienspace.alientask.util.TaskDetailTopAppBar
import timber.log.Timber


@Composable
fun TaskDetailsScreen(
    onEditTask: (String?) -> Unit,
    onBack: () -> Unit,
    onDeleteTask: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TaskDetailViewModel = hiltViewModel(),
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TaskDetailTopAppBar(
                onBack = onBack,
                onDeleteTask = viewModel::deleteTask
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onEditTask(viewModel.taskId) }) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = ""
                )
            }
        }
    ) { paddingValues ->
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        Timber.d("Task Detail : ${uiState.task}")

        TaskDetailContent(
            modifier = modifier
                .padding(paddingValues),
            task = uiState.task,
            onTaskCheckedChange = viewModel::completeTask

        )

        LaunchedEffect(uiState.isTaskDeleted) {
            if (uiState.isTaskDeleted){
                onDeleteTask()
            }
        }

    }
}

@Composable
private fun TaskDetailContent(
    modifier: Modifier = Modifier,
    task: Task?,
    onTaskCheckedChange: (Boolean) -> Unit,

    ) {
    val screenPadding = Modifier.padding(
        horizontal = dimensionResource(id = R.dimen.horizontal_margin),
        vertical = dimensionResource(id = R.dimen.vertical_margin)
    )

    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        Row(
            Modifier
                .fillMaxWidth()
                .then(screenPadding)
        ) {
            if (task != null) {
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = onTaskCheckedChange
                )
                Column {
                    Text(text = task.title, style = MaterialTheme.typography.headlineSmall)
                    Text(text = task.description, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }

}


@Composable
@Preview
private fun TaskDetailScreenPreview() {
    Surface(modifier = Modifier.wrapContentSize()) {
        TaskDetailContent(
            task = Task(
                title = "Hello1",
                description = "This is task 1",
                isCompleted = false,
                id = "ID1"
            )
        ) {

        }
    }
}
