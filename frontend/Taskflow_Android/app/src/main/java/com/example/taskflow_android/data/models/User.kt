package com.example.taskflow_android.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Long? = null,
    val name: String,
    val email: String,
    val role: String,
    val password: String? = null
)

@Serializable
data class AuthRequest(
    val email: String,
    val password: String,
    val name: String? = null
)

@Serializable
data class AuthResponse(
    val token: String,
    @SerialName("userId") // Esto mapea "userId" del JSON a "id" en Kotlin
    val id: Long,
    val name: String,
    val email: String,
    val role: String
)

@Serializable
data class UserResponse(
    val id: Long? = null,
    val name: String,
    val email: String,
    val role: String
)
