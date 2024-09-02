package com.alienspace.alientask.tasks

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alienspace.alientask.R
import com.alienspace.alientask.data.Task
import com.alienspace.alientask.util.LoadingContent
import com.alienspace.alientask.util.TasksTopAppBar
import timber.log.Timber

@Composable
fun TaskScreen(
    modifier: Modifier = Modifier,
    viewModel: TaskViewModel = hiltViewModel<TaskViewModel>(),
    onAddTask: (String) -> Unit,
    onTaskClick: (Task) -> Unit,
) {
    Scaffold(
        topBar = {
            TasksTopAppBar(
                onClearCompletedTask = viewModel::clearCompletedTask,
                onFilterAllTasks = { viewModel.setFilteringTask(TaskFilterTypes.ALL_TASKS) },
                onFilterActiveTasks = { viewModel.setFilteringTask(TaskFilterTypes.ACTIVE_TASKS) },
                onFilterCompletedTasks = { viewModel.setFilteringTask(TaskFilterTypes.COMPLETED_TASKS) },
                onRefresh = viewModel::refresh
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                Timber.d("Task Add Clicked...")
                onAddTask("123")
            }) {
                Icon(Icons.Filled.Add, stringResource(id = R.string.add_task))
            }
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        Timber.d("State: $uiState")
        TaskScreenContent(
            tasks = uiState.items,
            onTaskClick = onTaskClick,
            onTaskCheckedChange = viewModel::completeTask,
            modifier = Modifier.padding(paddingValues),
            loading = uiState.isLoading,
            currentUiInfo = uiState.filteringUiInfo,
            onClickCompletedTask = { viewModel.setFilteringTask(TaskFilterTypes.COMPLETED_TASKS) },
            onClickAllTask = { viewModel.setFilteringTask(TaskFilterTypes.ALL_TASKS) },
            onClickActiveTask = { viewModel.setFilteringTask(TaskFilterTypes.ACTIVE_TASKS) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskScreenContent(
    tasks: List<Task>,
    onTaskClick: (Task) -> Unit,
    onClickAllTask: () -> Unit,
    onClickActiveTask: () -> Unit,
    onClickCompletedTask: () -> Unit,
    onTaskCheckedChange: (Task, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    loading: Boolean,
    currentUiInfo: FilteringUiInfo,
) {
    LoadingContent(loading = loading,
        modifier = modifier,
        content = {
            Column(
                modifier = Modifier
                    .padding(horizontal = dimensionResource(id = R.dimen.list_item_padding))
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    InputChip(
                        selected = currentUiInfo.allTask,
                        onClick = onClickAllTask,
                        label = { Text(text = TaskFilterTypes.ALL_TASKS.str) })
                    InputChip(selected = currentUiInfo.activeTask,
                        onClick = onClickActiveTask,
                        label = { Text(text = TaskFilterTypes.ACTIVE_TASKS.str) })
                    InputChip(selected = currentUiInfo.completedTask,
                        onClick = onClickCompletedTask,
                        label = { Text(text = TaskFilterTypes.COMPLETED_TASKS.str) })
                }
                Spacer(modifier = Modifier.height(10.dp))
                if (tasks.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            modifier = Modifier
                                .wrapContentSize()
                                .alpha(.5f),
                            alignment = Alignment.TopCenter,
                            painter = painterResource(id = R.drawable.bin),
                            contentDescription = ""
                        )
                        Spacer(modifier = Modifier.padding(20.dp))
                        Text(
                            text = stringResource(id = R.string.no_tasks_all),
                            style = MaterialTheme.typography.titleLarge,

                            )
                    }
                } else {
                    LazyColumn {
                        items(tasks) { task ->
                            TaskItem(
                                task = task,
                                onTaskClick = onTaskClick,
                                onCheckedChange = { onTaskCheckedChange(task, it) })
                        }
                    }
                }


            }
        })

}

@Composable
private fun TaskItem(
    task: Task,
    onTaskClick: (Task) -> Unit,
    onCheckedChange: (Boolean) -> Unit,
) {
    Card(
        onClick = { onTaskClick(task) },
        colors = CardDefaults.cardColors(),
        modifier = Modifier
            .padding(
                horizontal = dimensionResource(id = R.dimen.list_item_padding),
                vertical = dimensionResource(id = R.dimen.list_item_padding)
            )
            .wrapContentSize(),
        elevation = CardDefaults.elevatedCardElevation(2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)

        ) {
            Checkbox(
                modifier = Modifier,
                checked = task.isCompleted,
                onCheckedChange = onCheckedChange
            )
            Text(
                modifier = Modifier,
                text = task.titleForList,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textDecoration = if (task.isCompleted) {
                    TextDecoration.LineThrough
                } else {
                    null
                }
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
private fun PreviewTaskItem() {
    Surface {
        TaskItem(
            task = Task(
                title = "Hello",
                description = "This is task 1",
                isCompleted = false,
                id = "ID"
            ),
            onTaskClick = {}, onCheckedChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTaskItemCompleted() {
    Surface {
        TaskItem(
            task = Task(
                title = "Hello",
                description = "This is task 1",
                isCompleted = true,
                id = "ID"
            ),
            onTaskClick = {}, onCheckedChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTaskContent() {
    Surface {
        TaskScreenContent(
            tasks = listOf(
                Task(title = "Alien1", description = "Des-1", isCompleted = false, id = "ID1"),
                Task(title = "Alien2", description = "Des-2", isCompleted = true, id = "ID2"),
                Task(title = "Alien3", description = "Des-3", isCompleted = false, id = "ID3")
            ),
            onTaskClick = {},
            onTaskCheckedChange = { _, _ -> },
            loading = true,
            onClickCompletedTask = {},
            onClickAllTask = {},
            onClickActiveTask = {},
            currentUiInfo = FilteringUiInfo()

        )
    }
}