package com.example.taskflow_android.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taskflow_android.data.models.Task
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Suppress("SpellCheckingInspection")
fun TaskFormScreen(
    task: Task? = null,
    onSaveTask: (title: String, description: String?, priority: String, status: String, dueDate: String?) -> Unit,
    onBackClick: () -> Unit,
    onClearMessages: () -> Unit,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    themeColor: Color = Color(0xFF304471)
) {
    var title by remember { mutableStateOf(task?.title ?: "") }
    var description by remember { mutableStateOf(task?.description ?: "") }
    var priority by remember { mutableStateOf(task?.priority ?: "MEDIUM") }
    var status by remember { mutableStateOf(task?.status ?: "PENDING") }
    var dueDate by remember { mutableStateOf(task?.dueDate ?: "") }
    
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    
    // Solo mostrar errores que no sean 403 y ocultar mensajes de éxito
    if (errorMessage != null && !errorMessage.contains("403")) {
        AlertDialog(
            onDismissRequest = { onClearMessages() },
            title = {
                Text(
                    text = "Aviso",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(text = errorMessage)
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

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault()) // Usar zona horaria del sistema para evitar desfases visuales
                            .toLocalDate()
                        // El backend espera LocalDateTime (ISO_LOCAL_DATE_TIME)
                        // Aseguramos el formato agregando explícitamente la hora 00:00:00
                        dueDate = date.atStartOfDay().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        containerColor = Color(0xFFF8F9FA)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header estilo Profile pero más compacto
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = themeColor
                    )
                    .padding(start = 24.dp, top = 16.dp, end = 24.dp, bottom = 32.dp)
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                        .size(36.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        if (task == null) "Nueva Tarea" else "Editar Tarea",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        if (task == null) "Organiza tu día con facilidad" else "Actualiza los detalles de tu tarea",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Sección: Detalles Básicos
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(24.dp),
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            "Detalles de la Tarea",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A)
                        )

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Título") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isLoading,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = themeColor,
                                focusedLabelColor = themeColor
                            )
                        )

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Descripción (opcional)") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            enabled = !isLoading,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = themeColor,
                                focusedLabelColor = themeColor
                            )
                        )

                        // Selector de Fecha estilizado
                        Surface(
                            onClick = { if (!isLoading) showDatePicker = true },
                            color = Color(0xFFF8F9FA),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        "Fecha",
                                        fontSize = 12.sp,
                                        color = Color.Gray,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        if (dueDate.isBlank()) "Seleccionar fecha" else {
                                            val datePart = dueDate.take(10)
                                            val parts = datePart.split("-")
                                            if (parts.size == 3) "${parts[2]}-${parts[1]}-${parts[0]}" else datePart
                                        },
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (dueDate.isBlank()) Color.Gray else Color.Black
                                    )
                                }
                                Icon(
                                    Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    tint = themeColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                // Sección: Prioridad
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(24.dp),
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "Prioridad",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TaskPrioritySelector(
                                label = "Baja",
                                color = Color(0xFFBDBDBD),
                                isSelected = priority == "LOW",
                                onSelect = { priority = "LOW" },
                                modifier = Modifier.weight(1f),
                                enabled = !isLoading
                            )
                            TaskPrioritySelector(
                                label = "Media",
                                color = Color(0xFFFFCA28),
                                isSelected = priority == "MEDIUM",
                                onSelect = { priority = "MEDIUM" },
                                modifier = Modifier.weight(1f),
                                enabled = !isLoading
                            )
                            TaskPrioritySelector(
                                label = "Alta",
                                color = Color(0xFFEF5350),
                                isSelected = priority == "HIGH",
                                onSelect = { priority = "HIGH" },
                                modifier = Modifier.weight(1f),
                                enabled = !isLoading
                            )
                        }
                    }
                }

                // Sección: Estado (Solo si se está editando)
                if (task != null) {
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(24.dp),
                        shadowElevation = 2.dp
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                "Estado",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A1A1A),
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                TaskStatusSelector(
                                    label = "Pendiente",
                                    isSelected = status == "PENDING",
                                    onSelect = { status = "PENDING" },
                                    modifier = Modifier.weight(1f),
                                    enabled = !isLoading,
                                    activeColor = themeColor
                                )
                                TaskStatusSelector(
                                    label = "Completada",
                                    isSelected = status == "COMPLETED",
                                    onSelect = { status = "COMPLETED" },
                                    modifier = Modifier.weight(1f),
                                    enabled = !isLoading,
                                    activeColor = themeColor
                                )
                            }
                        }
                    }
                }

                // Botón de Guardar
                Button(
                    onClick = {
                        val trimmedTitle = title.trim()
                        if (trimmedTitle.isNotBlank()) {
                            onSaveTask(
                                trimmedTitle,
                                description.trim().ifBlank { null },
                                priority,
                                status,
                                dueDate.ifBlank { null }
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = themeColor,
                        disabledContainerColor = themeColor.copy(alpha = 0.5f)
                    ),
                    enabled = !isLoading && title.isNotBlank()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            if (task == null) "Crear Tarea" else "Guardar Cambios",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
@Suppress("SpellCheckingInspection")
fun TaskPrioritySelector(
    label: String,
    color: Color,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Surface(
        onClick = { if (enabled) onSelect() },
        color = if (isSelected) color.copy(alpha = 0.12f) else Color.Transparent,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 2.dp,
            color = if (isSelected) color else Color(0xFFF0F0F0)
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(color, CircleShape)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) color else Color.Gray
            )
        }
    }
}

@Composable
@Suppress("SpellCheckingInspection")
fun TaskStatusSelector(
    label: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    activeColor: Color = Color(0xFF304471)
) {
    Surface(
        onClick = { if (enabled) onSelect() },
        color = if (isSelected) activeColor.copy(alpha = 0.12f) else Color.Transparent,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 2.dp,
            color = if (isSelected) activeColor else Color(0xFFF0F0F0)
        ),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) activeColor else Color.Gray
            )
        }
    }
}

