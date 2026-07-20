package com.example.taskflow_android.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val id: Long? = null,
    val title: String = "",
    val description: String? = null,
    val priority: String = "MEDIUM", // LOW, MEDIUM, HIGH
    val status: String = "PENDING",   // PENDING, IN_PROGRESS, COMPLETED
    val dueDate: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

@Serializable
data class CreateTaskRequest(
    val title: String,
    val description: String? = null,
    val priority: String,
    val status: String = "PENDING",
    val dueDate: String? = null
)

@Serializable
data class UpdateTaskRequest(
    val title: String? = null,
    val description: String? = null,
    val priority: String? = null,
    val status: String? = null,
    val dueDate: String? = null
)
