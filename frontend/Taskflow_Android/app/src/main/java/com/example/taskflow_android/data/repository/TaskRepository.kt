package com.example.taskflow_android.data.repository

import com.example.taskflow_android.data.api.RetrofitClient
import com.example.taskflow_android.data.models.CreateTaskRequest
import com.example.taskflow_android.data.models.Task
import com.example.taskflow_android.data.models.UpdateTaskRequest

class TaskRepository {
    private val api = RetrofitClient.api

    suspend fun getTasks(): Result<List<Task>> {
        return try {
            val response = api.getTasks()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createTask(
        title: String,
        description: String?,
        priority: String = "MEDIUM",
        status: String = "PENDING",
        dueDate: String? = null
    ): Result<Task> {
        return try {
            val request = CreateTaskRequest(title, description, priority, status, dueDate)
            val response = api.createTask(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTaskById(taskId: Long): Result<Task> {
        return try {
            val response = api.getTaskById(taskId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateTask(
        taskId: Long,
        title: String? = null,
        description: String? = null,
        priority: String? = null,
        status: String? = null,
        dueDate: String? = null
    ): Result<Task> {
        return try {
            val request = UpdateTaskRequest(title, description, priority, status, dueDate)
            val response = api.updateTask(taskId, request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun completeTask(taskId: Long): Result<Task> {
        return try {
            val response = api.completeTask(taskId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTask(taskId: Long): Result<Unit> {
        return try {
            api.deleteTask(taskId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
