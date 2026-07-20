package com.example.taskflow_android.data.repository

import com.example.taskflow_android.data.api.RetrofitClient
import com.example.taskflow_android.data.datastore.SessionManager
import com.example.taskflow_android.data.models.AuthRequest
import com.example.taskflow_android.data.models.AuthResponse
import org.json.JSONObject
import retrofit2.HttpException

class AuthRepository(
    private val sessionManager: SessionManager
) {
    private val api = RetrofitClient.api

    suspend fun register(name: String, email: String, password: String): Result<AuthResponse> {
        return try {
            val request = AuthRequest(name = name, email = email, password = password)
            val response = api.register(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(Exception(parseError(e)))
        }
    }

    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val request = AuthRequest(email = email, password = password)
            val response = api.login(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(Exception(parseError(e)))
        }
    }

    private fun parseError(e: Exception): String {
        if (e is HttpException) {
            val response = e.response()
            try {
                val errorBody = response?.errorBody()?.string()
                if (!errorBody.isNullOrBlank()) {
                    val json = JSONObject(errorBody)
                    
                    // 1. Intentar obtener errores de validación si existen y no son nulos
                    if (json.has("validationErrors") && !json.isNull("validationErrors")) {
                        val errors = json.getJSONObject("validationErrors")
                        if (errors.length() > 0) {
                            val firstKey = errors.keys().next()
                            return errors.getString(firstKey)
                        }
                    }
                    
                    // 2. Buscar el campo "message" (donde viene "El email ya esta registrado")
                    if (json.has("message") && !json.isNull("message")) {
                        return json.getString("message")
                    }

                    // 3. Buscar campo "error" como último recurso en el JSON
                    if (json.has("error") && !json.isNull("error")) {
                        return json.getString("error")
                    }
                }
            } catch (ex: Exception) {
                // Si falla el parseo, usamos el código HTTP como guía
                return when (response?.code()) {
                    409 -> "El correo electrónico ya está registrado"
                    401 -> "Credenciales incorrectas"
                    403 -> "Sesión expirada"
                    else -> "Error del servidor (${response?.code()})"
                }
            }
        }
        return e.localizedMessage ?: "Error de conexión"
    }

    suspend fun saveSession(response: AuthResponse, rememberMe: Boolean) {
        sessionManager.saveSession(
            token = response.token,
            userId = response.id.toString(),
            username = response.name,
            email = response.email,
            rememberMe = rememberMe
        )
    }

    suspend fun logout() {
        sessionManager.clearSession()
    }

    fun getTokenFlow() = sessionManager.tokenFlow
    fun getUsernameFlow() = sessionManager.usernameFlow
    fun getRememberMeFlow() = sessionManager.rememberMeFlow
}
