package com.alienspace.alientask.data

import com.alienspace.alientask.data.source.local.TaskDao
import com.alienspace.alientask.di.ApplicationScope
import com.alienspace.alientask.di.DefaultDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

interface TaskRepository {

    suspend fun createTask(title: String, description: String): String
    suspend fun getTasks(): List<Task>
    suspend fun getTask(taskId: String?): Task?
    fun getTasksStream(): Flow<List<Task>>
    fun getTaskStream(taskId: String): Flow<Task>
    suspend fun completeTask(taskId: String, complete: Boolean)
    suspend fun clearCompleteTask()
    suspend fun updateTask(taskId: String, title: String, description: String)
    suspend fun deleteTask(taskId: String)

}


class TaskRepositoryImpl @Inject constructor(
    private val dao: TaskDao,
    @DefaultDispatcher val dispatcher: CoroutineDispatcher,
    @ApplicationScope val scope: CoroutineScope,
) : TaskRepository {

    override suspend fun createTask(title: String, description: String): String {
        val taskId = withContext(dispatcher) {
            UUID.randomUUID().toString()
        }
        val task = Task(
            title = title,
            description = description,
            id = taskId
        )
        dao.upsert(task = task.toLocal())
        return taskId

    }

    override suspend fun getTasks(): List<Task> {
//        if (forceUpdate) {
//            refresh()
//        }
        return withContext(dispatcher) {
                dao.getAll().toExternal()
            }

    }

    override suspend fun getTask(taskId: String?): Task? {
        return dao.getTaskById(taskId)?.toExternal()
    }

    override fun getTasksStream(): Flow<List<Task>> {
        return dao.observeAll().map {
            withContext(dispatcher) {
                it.toExternal()
            }
        }.catch { Timber.d("Error.................") }
    }

    override fun getTaskStream(taskId: String): Flow<Task> {
        return dao.observeTask(taskId).map {
            it.toExternal()
        }.catch { Timber.e("Error While Fetching the task For Details...") }
    }

    override suspend fun completeTask(taskId: String, complete: Boolean) {
        dao.updateCompletedTask(taskId, complete)
    }

    override suspend fun clearCompleteTask() {
        dao.clearCompletedTask()
    }

    override suspend fun updateTask(taskId: String, title: String, description: String) {
        val task = dao.getTaskById(taskId)?.copy(
            title = title,
            description = description
        ) ?: throw Exception("Task ID : $taskId is not Found")
        dao.upsert(task)

    }

    override suspend fun deleteTask(taskId: String) {
        dao.deleteTask(taskId)
    }

}