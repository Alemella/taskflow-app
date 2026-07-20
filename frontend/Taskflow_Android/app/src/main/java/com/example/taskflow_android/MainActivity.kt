package com.example.taskflow_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.taskflow_android.data.datastore.SessionManager
import com.example.taskflow_android.data.repository.AuthRepository
import com.example.taskflow_android.data.repository.TaskRepository
import com.example.taskflow_android.navigation.AppNavigation
import com.example.taskflow_android.ui.theme.TaskDaysTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sessionManager = SessionManager(this)
        com.example.taskflow_android.data.api.RetrofitClient.init(sessionManager)
        val authRepository = AuthRepository(sessionManager)
        val taskRepository = TaskRepository()

        setContent {
            TaskDaysTheme {
                AppNavigation(
                    sessionManager = sessionManager,
                    authRepository = authRepository,
                    taskRepository = taskRepository
                )
            }
        }
    }
}
