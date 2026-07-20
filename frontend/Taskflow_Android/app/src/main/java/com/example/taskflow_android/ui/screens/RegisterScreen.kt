package com.example.taskflow_android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
fun RegisterScreen(
    uiState: AuthUiState,
    onRegisterClick: (username: String, email: String, password: String, confirmPassword: String) -> Unit,
    onLoginClick: () -> Unit,
    onNavigateToTasks: () -> Unit,
    onClearMessages: () -> Unit,
    themeColor: Color = Color(0xFF304471)
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            onNavigateToTasks()
        }
    }

    if (uiState.errorMessage != null || uiState.successMessage != null) {
        AlertDialog(
            onDismissRequest = { onClearMessages() },
            title = {
                Text(
                    text = if (uiState.errorMessage != null) "Error" else "Éxito",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(text = uiState.errorMessage ?: uiState.successMessage ?: "")
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Crear Cuenta",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = themeColor,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Usuario") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            enabled = !uiState.isLoading,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = themeColor,
                focusedLabelColor = themeColor
            )
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            enabled = !uiState.isLoading,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = themeColor,
                focusedLabelColor = themeColor
            )
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            visualTransformation = PasswordVisualTransformation(),
            enabled = !uiState.isLoading,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = themeColor,
                focusedLabelColor = themeColor
            )
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirmar contraseña") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            visualTransformation = PasswordVisualTransformation(),
            enabled = !uiState.isLoading,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = themeColor,
                focusedLabelColor = themeColor
            )
        )

        Button(
            onClick = {
                if (username.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                    onRegisterClick(username, email, password, confirmPassword)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            enabled = !uiState.isLoading && username.isNotBlank() && email.isNotBlank() &&
                    password.isNotBlank() && confirmPassword.isNotBlank(),
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
            Text(if (uiState.isLoading) "Registrando..." else "Registrarse")
        }

        TextButton(
            onClick = onLoginClick,
            enabled = !uiState.isLoading
        ) {
            Text("¿Ya tienes cuenta? Inicia sesión aquí", color = themeColor)
        }
    }
}

