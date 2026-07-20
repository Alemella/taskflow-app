package com.example.taskflow_android.data.api

import com.example.taskflow_android.data.datastore.SessionManager
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalSerializationApi::class)
object RetrofitClient {

    private const val BASE_URL = "https://taskflow-app-1sl4.onrender.com/"
    private var sessionManager: SessionManager? = null

    // Método para inicializar el SessionManager desde la Application o Hilt
    fun init(manager: SessionManager) {
        sessionManager = manager
    }

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        encodeDefaults = false
    }

    private val httpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val token = runBlocking { sessionManager?.tokenFlow?.first() }
                val request = if (!token.isNullOrEmpty()) {
                    chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                } else {
                    chain.request()
                }
                chain.proceed(request)
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    val api: TaskDaysApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(TaskDaysApi::class.java)
    }
}
