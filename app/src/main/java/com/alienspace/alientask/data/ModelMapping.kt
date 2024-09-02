package com.alienspace.alientask.data

import com.alienspace.alientask.data.source.local.LocalTask


fun Task.toLocal() = LocalTask(
    title = title,
    id = id,
    description = description,
    isCompleted = isCompleted
)

fun LocalTask.toExternal()= Task(
    title = title,
    description = description,
    isCompleted = isCompleted,
    id = id
)

@JvmName("localToExternal")
fun List<LocalTask>.toExternal() = map(LocalTask::toExternal)