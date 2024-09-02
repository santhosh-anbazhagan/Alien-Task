package com.alienspace.alientask.di

import android.content.Context
import androidx.room.Room
import com.alienspace.alientask.data.TaskRepository
import com.alienspace.alientask.data.TaskRepositoryImpl
import com.alienspace.alientask.data.source.local.TaskDao
import com.alienspace.alientask.data.source.local.TaskDatabase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class RepoModule {

    @Singleton
    @Binds
    abstract fun bindtaskRepo(taskRepository: TaskRepositoryImpl): TaskRepository
}


@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Singleton
    @Provides
    fun provideDatabse(@ApplicationContext context: Context): TaskDatabase {
        return Room.databaseBuilder(context = context.applicationContext, TaskDatabase::class.java,
            "Task.db")
            .build()
    }

    @Provides
    fun provideTaskDao(taskDatabase: TaskDatabase):TaskDao = taskDatabase.taskDao()
}