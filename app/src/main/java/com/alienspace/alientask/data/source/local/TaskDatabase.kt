package com.alienspace.alientask.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.alienspace.alientask.data.Task

@Database(version = 1, exportSchema = false,
    entities = [LocalTask::class])
abstract class TaskDatabase:RoomDatabase() {

    abstract fun taskDao():TaskDao
}