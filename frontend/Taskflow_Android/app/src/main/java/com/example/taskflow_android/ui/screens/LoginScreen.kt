package com.example.taskflow_android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taskflow_android.ui.viewmodel.AuthUiState

@Composable
fun LoginScreen(
    uiState: AuthUiState,
    onLoginClick: (username: String, password: String, rememberMe: Boolean) -> Unit,
    onRegisterClick: () -> Unit,
    onNavigateToTasks: () -> Unit,
    onClearMessages: () -> Unit,
    themeColor: Color = Color(0xFF304471),
    themeColorName: String = "Azul",
    onThemeColorChange: (String) -> Unit = {}
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            onNavigateToTasks()
        }
    }

    // Solo mostrar mensaje de error (que no sea 403), no el de éxito al logearse
    if (uiState.errorMessage != null && !uiState.errorMessage.contains("403")) {
        AlertDialog(
            onDismissRequest = { onClearMessages() },
            title = {
                Text(
                    text = "Error",
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

    val themeColorMap = mapOf(
        "Azul" to Color(0xFF304471),
        "Verde" to Color(0xFF2E7D32),
        "Rojo" to Color(0xFFC62828),
        "Morado" to Color(0xFF6A1B9A),
        "Naranja" to Color(0xFFEF6C00),
        "Cian" to Color(0xFF00838F)
    )

    if (showColorPicker) {
        AlertDialog(
            onDismissRequest = { showColorPicker = false },
            title = { Text("Personalizar Tema", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Selecciona un color para la aplicación", fontSize = 14.sp, color = Color.Gray)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        themeColorMap.forEach { (name, color) ->
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .clickable { 
                                        onThemeColorChange(name)
                                        showColorPicker = false
                                    }
                                    .border(
                                        width = if (themeColorName == name) 3.dp else 0.dp,
                                        color = Color.Black.copy(alpha = 0.3f),
                                        shape = CircleShape
                                    )
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(themeColor)
    ) {
        // Decorative circles
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(
                    Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(100.dp)
                )
                .align(Alignment.TopEnd)
                .offset(x = 100.dp, y = (-100).dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Card with rounded corners
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color.White,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Bienvenido",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    "Ingresa para gestionar tus tareas",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Text(
                    "Correo",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Start
                )

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    placeholder = { Text("tu@email.com") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !uiState.isLoading,
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Text(
                    "Contraseña",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, top = 8.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Start
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("•••••••") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    visualTransformation = PasswordVisualTransformation(),
                    enabled = !uiState.isLoading,
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        enabled = !uiState.isLoading,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        "Mantener sesión iniciada",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Button(
                    onClick = {
                        if (username.isNotBlank() && password.isNotBlank()) {
                            onLoginClick(username, password, rememberMe)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(top = 8.dp),
                    enabled = !uiState.isLoading && username.isNotBlank() && password.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = themeColor
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(20.dp)
                                .padding(end = 8.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    }
                    Text(
                        if (uiState.isLoading) "Cargando..." else "Iniciar sesión",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = onRegisterClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    enabled = !uiState.isLoading,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Crear cuenta",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = themeColor
                    )
                }

                Text(
                    "TaskDay v1.0",
                    fontSize = 12.sp,
                    color = Color.LightGray,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }

        // Botón para abrir selector de tema (al final para estar arriba)
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(16.dp)
                .size(40.dp)
                .background(Color.Black.copy(alpha = 0.2f), CircleShape)
                .clickable { showColorPicker = true },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Palette,
                contentDescription = "Cambiar color",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
