package com.alienspace.alientask.data.source.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Upsert
    suspend fun upsert(task: LocalTask)

    @Query("SELECT * FROM task")
    suspend fun getAll(): List<LocalTask>

    @Query("SELECT * FROM task where id = :taskId")
    suspend fun getTaskById(taskId:String?): LocalTask?

    @Query("SELECT * FROM task")
    fun observeAll(): Flow<List<LocalTask>>

    @Query("SELECT * FROM task where id = :taskId")
    fun observeTask(taskId: String): Flow<LocalTask>

    @Query("Update task Set isCompleted =:complete where id = :taskId ")
    suspend fun updateCompletedTask(taskId:String,complete:Boolean)

    @Query("delete from task where isCompleted = 1")
    suspend fun clearCompletedTask()

    @Query("delete from task where id =:taskId")
    suspend fun deleteTask(taskId:String)



}
