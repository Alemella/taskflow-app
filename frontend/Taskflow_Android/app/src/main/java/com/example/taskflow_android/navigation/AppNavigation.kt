package com.example.taskflow_android.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.taskflow_android.data.datastore.SessionManager
import com.example.taskflow_android.data.repository.AuthRepository
import com.example.taskflow_android.data.repository.TaskRepository
import com.example.taskflow_android.ui.screens.LoginScreen
import com.example.taskflow_android.ui.screens.ProfileScreen
import com.example.taskflow_android.ui.screens.RegisterScreen
import com.example.taskflow_android.ui.screens.SplashScreen
import com.example.taskflow_android.ui.screens.TaskFormScreen
import com.example.taskflow_android.ui.screens.TaskListScreen
import com.example.taskflow_android.ui.viewmodel.AuthViewModel
import com.example.taskflow_android.ui.viewmodel.TaskListViewModel

import androidx.compose.runtime.LaunchedEffect
import com.example.taskflow_android.ui.viewmodel.AuthUiState
import com.example.taskflow_android.ui.viewmodel.TaskListUiState

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object TaskList : Screen("task_list")
    object TaskForm : Screen("task_form")
    object TaskDetail : Screen("task_detail/{taskId}") {
        fun createRoute(taskId: Long) = "task_detail/$taskId"
    }
    object Profile : Screen("profile")
}

@Composable
fun AppNavigation(
    sessionManager: SessionManager,
    authRepository: AuthRepository,
    taskRepository: TaskRepository,
    navController: NavHostController = rememberNavController()
) {
    val authViewModel = remember { AuthViewModel(authRepository, sessionManager) }
    val taskListViewModel = remember { TaskListViewModel(taskRepository) }
    val authUiState by authViewModel.uiState.collectAsState()
    val taskListUiState by taskListViewModel.uiState.collectAsState()
    val token by sessionManager.tokenFlow.collectAsState(initial = null)
    val username by sessionManager.usernameFlow.collectAsState(initial = "")
    val email by sessionManager.emailFlow.collectAsState(initial = "")
    val rememberMe by sessionManager.rememberMeFlow.collectAsState(initial = false)
    val themeColorName by sessionManager.themeColorFlow.collectAsState(initial = "Azul")
    val profileImageUri by sessionManager.profileImageUriFlow.collectAsState(initial = null)
    val scope = rememberCoroutineScope()

    val themeColorMap = mapOf(
        "Azul" to Color(0xFF304471),
        "Verde" to Color(0xFF2E7D32),
        "Rojo" to Color(0xFFC62828),
        "Morado" to Color(0xFF6A1B9A),
        "Naranja" to Color(0xFFEF6C00),
        "Cian" to Color(0xFF00838F)
    )
    val selectedThemeColor = themeColorMap[themeColorName] ?: Color(0xFF304471)

    // Redirección automática al Login si hay un error 403 (Token expirado)
    LaunchedEffect(taskListUiState.errorMessage, authUiState.errorMessage) {
        val errorMsg = taskListUiState.errorMessage ?: authUiState.errorMessage
        if (errorMsg != null && errorMsg.contains("403")) {
            authViewModel.logout()
            taskListViewModel.clearTasks()
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    val startDestination = Screen.Splash.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToTasks = {
                    navController.navigate(Screen.TaskList.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                isAuthenticated = rememberMe && !token.isNullOrEmpty()
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                uiState = authUiState,
                onLoginClick = { email, password, rememberMe ->
                    authViewModel.login(email, password, rememberMe)
                },
                onRegisterClick = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToTasks = {
                    navController.navigate(Screen.TaskList.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onClearMessages = { authViewModel.clearMessages() },
                themeColor = selectedThemeColor,
                themeColorName = themeColorName,
                onThemeColorChange = { newColor ->
                    scope.launch {
                        sessionManager.saveThemeColor(newColor)
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                uiState = authUiState,
                onRegisterClick = { name, email, password, confirmPassword ->
                    authViewModel.register(name, email, password, confirmPassword)
                },
                onLoginClick = {
                    navController.popBackStack()
                },
                onNavigateToTasks = {
                    navController.navigate(Screen.TaskList.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onClearMessages = { authViewModel.clearMessages() },
                themeColor = selectedThemeColor
            )
        }

        composable(Screen.TaskList.route) {
            TaskListScreen(
                uiState = taskListUiState,
                username = username ?: "Usuario",
                profileImageUri = profileImageUri,
                onAddTaskClick = {
                    navController.navigate(Screen.TaskForm.route)
                },
                onTaskClick = { taskId ->
                    navController.navigate(Screen.TaskDetail.createRoute(taskId))
                },
                onStatusChange = { taskId: Long, newStatus: String ->
                    taskListViewModel.updateTaskStatus(taskId, newStatus)
                },
                onDeleteTask = { taskId ->
                    taskListViewModel.deleteTask(taskId)
                },
                onRefresh = {
                    taskListViewModel.loadTasks()
                },
                onProfileClick = {
                    navController.navigate(Screen.Profile.route)
                },
                onClearMessages = { taskListViewModel.clearMessages() },
                themeColor = selectedThemeColor
            )
        }

        composable(Screen.TaskForm.route) {
            TaskFormScreen(
                onSaveTask = { title, description, priority, status, dueDate ->
                    taskListViewModel.createTask(
                        title = title,
                        description = description,
                        priority = priority,
                        status = status,
                        dueDate = dueDate
                    )
                    navController.popBackStack()
                },
                onBackClick = {
                    navController.popBackStack()
                },
                onClearMessages = { taskListViewModel.clearMessages() },
                isLoading = taskListUiState.isLoading,
                errorMessage = taskListUiState.errorMessage,
                themeColor = selectedThemeColor
            )
        }

        composable(
            route = Screen.TaskDetail.route,
            arguments = listOf(navArgument("taskId") { type = NavType.LongType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId") ?: -1L
            val task = taskListUiState.tasks.find { it.id == taskId }
            
            TaskFormScreen(
                task = task,
                onSaveTask = { title, description, priority, status, dueDate ->
                    if (task != null) {
                        taskListViewModel.updateTask(
                            taskId = taskId,
                            title = title,
                            description = description,
                            priority = priority,
                            status = status,
                            dueDate = dueDate
                        )
                    }
                    navController.popBackStack()
                },
                onBackClick = { navController.popBackStack() },
                onClearMessages = { taskListViewModel.clearMessages() },
                isLoading = taskListUiState.isLoading,
                errorMessage = taskListUiState.errorMessage,
                themeColor = selectedThemeColor
            )
        }

        composable(Screen.Profile.route) {
            val totalTasks = taskListUiState.tasks.size
            val completedTasks = taskListUiState.tasks.count { it.status == "COMPLETED" }
            
            ProfileScreen(
                username = username ?: "Usuario",
                email = email ?: "",
                totalTasks = totalTasks,
                completedTasks = completedTasks,
                profileImageUri = profileImageUri,
                onProfileImageChange = { uri ->
                    scope.launch {
                        sessionManager.saveProfileImageUri(uri)
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                },
                onLogoutClick = {
                    authViewModel.logout()
                    taskListViewModel.clearTasks()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                rememberMe = rememberMe,
                onRememberMeChange = { authViewModel.updateRememberMe(it) },
                themeColorName = themeColorName,
                onThemeColorChange = { newColor ->
                    scope.launch {
                        sessionManager.saveThemeColor(newColor)
                    }
                }
            )
        }
    }
}
