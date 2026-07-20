package com.example.taskflow_android.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.taskflow_android.data.models.Task
import com.example.taskflow_android.ui.viewmodel.TaskListUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    uiState: TaskListUiState,
    username: String,
    onAddTaskClick: () -> Unit,
    onTaskClick: (Long) -> Unit,
    onStatusChange: (Long, String) -> Unit,
    onDeleteTask: (Long) -> Unit,
    onRefresh: () -> Unit,
    onProfileClick: () -> Unit,
    onClearMessages: () -> Unit,
    themeColor: Color = Color(0xFF304471),
    profileImageUri: String? = null
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedStatus by remember { mutableStateOf("PENDING") } // Default to "PENDING"
    var selectedPriority by remember { mutableStateOf<String?>(null) } // null, "HIGH", "MEDIUM", "LOW"

    val filteredTasks = remember(uiState.tasks, selectedStatus, selectedPriority) {
        uiState.tasks.filter { task ->
            val matchesStatus = when (selectedStatus) {
                "PENDING" -> task.status == "PENDING"
                "COMPLETED" -> task.status == "COMPLETED"
                else -> true
            }
            val matchesPriority = if (selectedPriority == null) true else task.priority == selectedPriority
            matchesStatus && matchesPriority
        }
    }

    LaunchedEffect(Unit) {
        onRefresh()
    }

    // Solo mostrar errores que no sean 403 y ocultar mensajes de éxito
    if (uiState.errorMessage != null && !uiState.errorMessage.contains("403")) {
        AlertDialog(
            onDismissRequest = { onClearMessages() },
            title = {
                Text(
                    text = "Aviso",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(text = uiState.errorMessage)
            },
            confirmButton = {
                TextButton(onClick = { onClearMessages() }) {
                    Text("Aceptar")
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTaskClick,
                containerColor = themeColor,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar tarea", tint = Color.White)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .background(Color(0xFFF8F9FA))
        ) {
            // Header ultra-compacto y pegado arriba
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(start = 24.dp, end = 24.dp, top = 4.dp, bottom = 0.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Bienvenido, $username",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1A1A1A)
                        )
                        Text(
                            text = buildAnnotatedString {
                                append("Tienes ")
                                withStyle(style = SpanStyle(color = themeColor, fontWeight = FontWeight.Bold)) {
                                    append("${uiState.tasks.size}")
                                }
                                append(" ${if (uiState.tasks.size == 1) "tarea" else "tareas"} en total")
                            },
                            fontSize = 11.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Botón de Perfil con imagen y contorno
                    Surface(
                        onClick = onProfileClick,
                        color = Color.White,
                        shape = CircleShape,
                        modifier = Modifier
                            .size(40.dp)
                            .border(2.dp, themeColor, CircleShape)
                    ) {
                        if (profileImageUri != null) {
                            AsyncImage(
                                model = profileImageUri,
                                contentDescription = "Perfil",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        } else {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    username.firstOrNull()?.uppercase() ?: "U",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = themeColor
                                )
                            }
                        }
                    }
                }
            }

            // Filtros agrupados para ahorrar espacio
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(bottom = 4.dp)
            ) {
                // Filter Tabs (Estado)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 0.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val statusOptions = listOf(
                        "Pendientes" to "PENDING",
                        "Completados" to "COMPLETED",
                        "Todos" to "ALL"
                    )
                    statusOptions.forEach { (label, value) ->
                        FilterChip(
                            selected = selectedStatus == value,
                            onClick = { selectedStatus = value },
                            label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = themeColor,
                                selectedLabelColor = Color.White,
                                labelColor = Color.Gray,
                                containerColor = Color.Transparent
                            ),
                            border = null,
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                // Filtros de Prioridad
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, top = 0.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PriorityLegendItem(
                        color = Color(0xFFEF5350), 
                        label = "Alta",
                        isSelected = selectedPriority == "HIGH",
                        onClick = { selectedPriority = if (selectedPriority == "HIGH") null else "HIGH" }
                    )
                    PriorityLegendItem(
                        color = Color(0xFFFFCA28), 
                        label = "Media",
                        isSelected = selectedPriority == "MEDIUM",
                        onClick = { selectedPriority = if (selectedPriority == "MEDIUM") null else "MEDIUM" }
                    )
                    PriorityLegendItem(
                        color = Color(0xFFBDBDBD), 
                        label = "Baja",
                        isSelected = selectedPriority == "LOW",
                        onClick = { selectedPriority = if (selectedPriority == "LOW") null else "LOW" }
                    )
                }
            }

            if (uiState.isLoading && uiState.tasks.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (filteredTasks.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    val message = when {
                        selectedPriority != null -> "No hay tareas con esta prioridad"
                        selectedStatus == "PENDING" -> "¡Genial! No tienes pendientes"
                        selectedStatus == "COMPLETED" -> "Aún no has completado tareas"
                        else -> "No hay tareas registradas"
                    }
                    Text(
                        text = message,
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, top = 4.dp, end = 16.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredTasks, key = { it.id!! }) { task ->
                        SwipeableTaskItem(
                            task = task,
                            onTaskClick = { onTaskClick(task.id!!) },
                            onStatusChange = { newStatus -> onStatusChange(task.id!!, newStatus) },
                            onDeleteClick = { onDeleteTask(task.id!!) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PriorityLegendItem(color: Color, label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = if (isSelected) color.copy(alpha = 0.12f) else Color.Transparent,
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically, 
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
            Text(
                text = label, 
                fontSize = 11.sp,
                color = if (isSelected) color else Color.DarkGray, 
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableTaskItem(
    task: Task,
    onTaskClick: () -> Unit,
    onStatusChange: (String) -> Unit,
    onDeleteClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onDeleteClick()
                true
            } else {
                false
            }
        }
    )

    val isDismissing = dismissState.targetValue == SwipeToDismissBoxValue.EndToStart
    
    val backgroundColor by animateColorAsState(
        targetValue = if (isDismissing) Color(0xFFFEE2E2) else Color.Transparent,
        label = "swipeBackgroundColor"
    )
    
    val iconScale by animateFloatAsState(
        targetValue = if (isDismissing) 1.2f else 0.8f,
        label = "iconScale"
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 4.dp)
                    .background(backgroundColor, RoundedCornerShape(24.dp))
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = if (isDismissing) Color(0xFFEF4444) else Color.Gray.copy(alpha = 0.5f),
                    modifier = Modifier
                        .size(28.dp)
                        .scale(iconScale)
                )
            }
        }
    ) {
        TaskItem(
            task = task,
            onTaskClick = onTaskClick,
            onStatusChange = onStatusChange
        )
    }
}

@Composable
fun TaskItem(
    task: Task,
    onTaskClick: () -> Unit,
    onStatusChange: (String) -> Unit
) {
    var showStatusMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTaskClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Indicador de prioridad vertical estilizado
            Box(
                modifier = Modifier
                    .width(10.dp)
                    .fillMaxHeight()
                    .background(
                        color = when (task.priority) {
                            "HIGH" -> Color(0xFFEF5350)
                            "MEDIUM" -> Color(0xFFFFCA28)
                            else -> Color(0xFFBDBDBD)
                        },
                        shape = RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp)
                    )
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                // Label de prioridad superior
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val priorityColor = when (task.priority) {
                        "HIGH" -> Color(0xFFEF5350)
                        "MEDIUM" -> Color(0xFFFFCA28)
                        else -> Color(0xFFBDBDBD)
                    }
                    Box(modifier = Modifier.size(8.dp).background(priorityColor, CircleShape))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = when(task.priority) {
                            "HIGH" -> "Prioridad Alta"
                            "MEDIUM" -> "Prioridad Media"
                            else -> "Prioridad Baja"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Icono de estado circular
                    Surface(
                        onClick = {
                            val newStatus = if (task.status == "COMPLETED") "PENDING" else "COMPLETED"
                            onStatusChange(newStatus)
                        },
                        shape = CircleShape,
                        color = if (task.status == "COMPLETED") Color(0xFFE8F5E9) else Color(0xFFF5F5F5),
                        modifier = Modifier
                            .size(42.dp)
                            .border(
                                width = 1.dp,
                                color = if (task.status == "COMPLETED") Color(0xFF43A047).copy(alpha = 0.5f) else Color(0xFFE0E0E0),
                                shape = CircleShape
                            )
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (task.status == "COMPLETED") Icons.Default.CheckCircle else Icons.Default.Circle,
                                contentDescription = null,
                                tint = if (task.status == "COMPLETED") Color(0xFF43A047) else Color(0xFFBDBDBD),
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }

                    // Texto de la tarea
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = task.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            textDecoration = if (task.status == "COMPLETED") TextDecoration.LineThrough else null,
                            color = if (task.status == "COMPLETED") Color.Gray else Color(0xFF2D3436),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (task.dueDate != null && task.dueDate.length >= 10) {
                                val parts = task.dueDate.take(10).split("-")
                                if (parts.size == 3) "${parts[2]}-${parts[1]}-${parts[0]}" else task.dueDate
                            } else "Sin fecha",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Badge de estado con Dropdown
                    Box {
                        StatusBadge(
                            status = task.status,
                            onClick = { showStatusMenu = true }
                        )
                        
                        DropdownMenu(
                            expanded = showStatusMenu,
                            onDismissRequest = { showStatusMenu = false },
                            modifier = Modifier
                                .background(Color.White, RoundedCornerShape(16.dp))
                                .padding(vertical = 4.dp)
                        ) {
                            DropdownMenuItem(
                                text = { StatusBadge(status = "PENDING", isCompact = false) },
                                onClick = {
                                    onStatusChange("PENDING")
                                    showStatusMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { StatusBadge(status = "COMPLETED", isCompact = false) },
                                onClick = {
                                    onStatusChange("COMPLETED")
                                    showStatusMenu = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String, isCompact: Boolean = true, onClick: (() -> Unit)? = null) {
    val isCompleted = status == "COMPLETED"
    val backgroundColor = if (isCompleted) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
    val textColor = if (isCompleted) Color(0xFF2E7D32) else Color(0xFFE65100)
    val label = if (isCompleted) "Finalizado" else "Pendiente"

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp),
        modifier = if (onClick != null) Modifier.clickable { onClick() } else Modifier
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(
                horizontal = if (isCompact) 12.dp else 16.dp,
                vertical = if (isCompact) 6.dp else 10.dp
            ),
            fontSize = if (isCompact) 12.sp else 14.sp,
            fontWeight = FontWeight.ExtraBold,
            color = textColor
        )
    }
}

