package com.example.dailytaskmanager

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

class TaskDao {

    @Dao
    interface TaskDao {

        @Insert
        suspend fun insertTask(task: TaskEntity)

        @Update
        suspend fun updateTask(task: TaskEntity)

        @Delete
        suspend fun deleteTask(task: TaskEntity)

        @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, dueDate ASC")
        suspend fun getAllTasks(): List<TaskEntity>
    }

}