package com.example.dailytaskmanager

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskDao {

    @Insert
    suspend fun insertTask(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    // (sorted + completed bottom)
    @Query("""
        SELECT * FROM tasks
        WHERE dueDate BETWEEN :start AND :end
        ORDER BY isCompleted ASC, dueDate ASC
    """)
    suspend fun getTodayTasks(start: Long, end: Long): List<TaskEntity>
}
