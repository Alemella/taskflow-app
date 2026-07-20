package com.example.taskflow_android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskflow_android.data.models.Task
import com.example.taskflow_android.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TaskListUiState(
    val isLoading: Boolean = false,
    val tasks: List<Task> = emptyList(),
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class TaskListViewModel(
    private val taskRepository: TaskRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(TaskListUiState())
    val uiState: StateFlow<TaskListUiState> = _uiState.asStateFlow()

    init {
        loadTasks()
    }

    fun loadTasks() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = taskRepository.getTasks()
            result.onSuccess { tasks ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    tasks = tasks
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Error al cargar tareas"
                )
            }
        }
    }

    fun createTask(title: String, description: String?, priority: String = "MEDIUM", status: String = "PENDING", dueDate: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = taskRepository.createTask(title, description, priority, status, dueDate)
            result.onSuccess { newTask ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    tasks = _uiState.value.tasks + newTask,
                    successMessage = "Tarea creada exitosamente"
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Error al crear tarea"
                )
            }
        }
    }

    fun updateTask(taskId: Long, title: String?, description: String?, priority: String?, status: String?, dueDate: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = taskRepository.updateTask(taskId, title, description, priority, status, dueDate)
            result.onSuccess { updatedTask ->
                val updatedList = _uiState.value.tasks.map {
                    if (it.id == taskId) updatedTask else it
                }
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    tasks = updatedList,
                    successMessage = "Tarea actualizada"
                )
            }.onFailure {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Error al actualizar")
            }
        }
    }

    fun updateTaskStatus(taskId: Long, newStatus: String) {
        viewModelScope.launch {
            val currentTask = _uiState.value.tasks.find { it.id == taskId }
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = if (newStatus == "COMPLETED") {
                taskRepository.completeTask(taskId)
            } else {
                taskRepository.updateTask(
                    taskId = taskId,
                    title = currentTask?.title,
                    priority = currentTask?.priority,
                    status = newStatus
                )
            }

            result.onSuccess { updatedTask ->
                val updatedList = _uiState.value.tasks.map {
                    if (it.id == taskId) updatedTask else it
                }
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    tasks = updatedList,
                    successMessage = "Estado actualizado a ${if (newStatus == "COMPLETED") "Finalizado" else "Pendiente"}"
                )
            }.onFailure {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Error al actualizar el estado")
            }
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            val result = taskRepository.deleteTask(taskId)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    tasks = _uiState.value.tasks.filter { it.id != taskId },
                    successMessage = "Tarea eliminada"
                )
            }.onFailure {
                _uiState.value = _uiState.value.copy(errorMessage = "Error al eliminar")
            }
        }
    }

    fun clearTasks() {
        _uiState.value = TaskListUiState()
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }
}
