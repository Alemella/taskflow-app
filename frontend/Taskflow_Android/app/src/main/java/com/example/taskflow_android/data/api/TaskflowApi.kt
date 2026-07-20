package com.example.taskflow_android.data.api

import com.example.taskflow_android.data.models.AuthRequest
import com.example.taskflow_android.data.models.AuthResponse
import com.example.taskflow_android.data.models.CreateTaskRequest
import com.example.taskflow_android.data.models.Task
import com.example.taskflow_android.data.models.UpdateTaskRequest
import com.example.taskflow_android.data.models.UserResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TaskDaysApi {
    // Auth endpoints (No requieren token)
    @POST("auth/register")
    suspend fun register(@Body request: AuthRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body request: AuthRequest): AuthResponse

    // Task endpoints (El token se añade automáticamente por el interceptor)
    @GET("tasks")
    suspend fun getTasks(): List<Task>

    @POST("tasks")
    suspend fun createTask(@Body request: CreateTaskRequest): Task

    @GET("tasks/{id}")
    suspend fun getTaskById(@Path("id") taskId: Long): Task

    @PUT("tasks/{id}")
    suspend fun updateTask(
        @Path("id") taskId: Long,
        @Body request: UpdateTaskRequest
    ): Task

    @PATCH("tasks/{id}/complete")
    suspend fun completeTask(@Path("id") taskId: Long): Task

    @DELETE("tasks/{id}")
    suspend fun deleteTask(@Path("id") taskId: Long)

    // User endpoints
    @GET("users")
    suspend fun getUsers(): List<UserResponse>

    @GET("users/me") // Ajustado a un endpoint típico de "mi perfil"
    suspend fun getCurrentUser(): UserResponse
}
