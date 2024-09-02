package com.alienspace.alientask.util

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.alienspace.alientask.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksTopAppBar(
    onClearCompletedTask: () -> Unit,
    onFilterAllTasks: () -> Unit,
    onFilterActiveTasks: () -> Unit,
    onFilterCompletedTasks: () -> Unit,
    onRefresh: () -> Unit,
) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.app_name)) },
        modifier = Modifier.fillMaxWidth(),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        actions = {
            FilterTaskMenu(
                onFilterAllTasks = onFilterAllTasks,
                onFilterActiveTasks = onFilterActiveTasks,
                onFilterCompletedTasks = onFilterCompletedTasks
            )
            MoreTaskMenu(
                onClearCompletedTask = onClearCompletedTask,
                onRefresh = onRefresh
            )

        }
    )
}


@Composable
private fun FilterTaskMenu(
    onFilterAllTasks: () -> Unit,
    onFilterActiveTasks: () -> Unit,
    onFilterCompletedTasks: () -> Unit,
) {
    TopAppBarDropDownMenu(iconContent = {
        Icon(
            painter = painterResource(R.drawable.ic_filter_list),
            contentDescription = ""
        )
    }) { closeMenu ->
        DropdownMenuItem(
            text = { Text(text = stringResource(R.string.nav_all)) },
            onClick = { onFilterAllTasks(); closeMenu() }
        )
        DropdownMenuItem(
            text = { Text(text = stringResource(R.string.nav_active)) },
            onClick = { onFilterActiveTasks(); closeMenu() }
        )
        DropdownMenuItem(
            text = { Text(text = stringResource(R.string.nav_completed)) },
            onClick = { onFilterCompletedTasks(); closeMenu() }
        )
    }

}

@Composable
private fun MoreTaskMenu(
    onClearCompletedTask: () -> Unit,
    onRefresh: () -> Unit,
) {
    TopAppBarDropDownMenu(
        iconContent = {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = ""
            )
        },
        content = { closeMenu ->
            DropdownMenuItem(
                text = { Text(text = stringResource(id = R.string.menu_clear)) },
                onClick = { onClearCompletedTask(); closeMenu() }
            )
            DropdownMenuItem(
                text = { Text(text = stringResource(id = R.string.refresh)) },
                onClick = { onRefresh(); closeMenu() }
            )
        }
    )
}

@Composable
private fun TopAppBarDropDownMenu(
    iconContent: @Composable () -> Unit,
    content: @Composable ColumnScope.(() -> Unit) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
        IconButton(onClick = { expanded = !expanded }) {
            iconContent()
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }) {
            content {
                expanded = !expanded
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskTopAppBar(@StringRes title: Int, onBack: () -> Unit) {
    TopAppBar(
        title = { Text(text = stringResource(title)) },
        modifier = Modifier.fillMaxWidth(),
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(id = R.string.menu_back))
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TaskDetailTopAppBar(
    onBack: () -> Unit,
    onDeleteTask: () -> Unit,
) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.task_details)) },
        actions = {
            IconButton(onClick = onDeleteTask) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = ""
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = " "
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
    )
}


@Preview(showBackground = true)
@Composable
private fun TopAppBarDropDownMenuPreview() {
    Surface(modifier = Modifier.wrapContentSize()) {
        TasksTopAppBar(
            onClearCompletedTask = { /*TODO*/ },
            onFilterAllTasks = { /*TODO*/ },
            onFilterActiveTasks = { /*TODO*/ },
            onRefresh = {},
            onFilterCompletedTasks = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskDetailTopAppBarPreview() {
    Surface(modifier = Modifier.wrapContentSize()) {
        TaskDetailTopAppBar(
            onBack = {},
            onDeleteTask = {},
        )
    }
}